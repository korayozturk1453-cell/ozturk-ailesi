package com.example.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Lightweight, pleasant ambient music synthesizer for the Slideshow/Story feature.
 * Generates soft acoustic chime and piano-like arpeggios (Cmaj9, Fmaj7, G6, Am7)
 * using native Android AudioTrack. No external mp3 files required!
 */
class AmbientAudioSynthesizer {

    private var audioTrack: AudioTrack? = null
    private var synthJob: Job? = null
    private var isPlaying = false

    private val sampleRate = 22050

    // Nostalgic, peaceful chord progression frequencies in Hz (C major / G major scale)
    private val chordSequences = listOf(
        // Cmaj9: C4, E4, G4, B4, D5
        listOf(261.63, 329.63, 392.00, 493.88, 587.33),
        // Am7: A3, C4, E4, G4, C5
        listOf(220.00, 261.63, 329.63, 392.00, 523.25),
        // Fmaj7: F3, A3, C4, E4, A4
        listOf(174.61, 220.00, 261.63, 329.63, 440.00),
        // G6: G3, B3, D4, E4, G4
        listOf(196.00, 246.94, 293.66, 329.63, 392.00)
    )

    fun start() {
        if (isPlaying) return
        isPlaying = true

        try {
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )

            val bufferSize = (minBufferSize * 2).coerceAtLeast(4096)

            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack?.play()

            synthJob = CoroutineScope(Dispatchers.Default).launch {
                var chordIndex = 0
                while (isActive && isPlaying) {
                    val currentChord = chordSequences[chordIndex % chordSequences.size]
                    chordIndex++

                    // Play gentle arpeggiated notes of the chord
                    for (freq in currentChord) {
                        if (!isActive || !isPlaying) break

                        val noteDuration = 0.55 // seconds
                        val numSamples = (noteDuration * sampleRate).toInt()
                        val buffer = ShortArray(numSamples)

                        for (i in 0 until numSamples) {
                            val time = i.toDouble() / sampleRate
                            // Soft bell/chime envelope with exponential decay and warm fundamental + harmonic
                            val envelope = exp(-3.8 * time)
                            val fundamental = sin(2.0 * PI * freq * time)
                            val harmonic1 = 0.35 * sin(2.0 * PI * (freq * 2) * time)
                            val harmonic2 = 0.15 * sin(2.0 * PI * (freq * 3) * time)
                            val warmSound = (fundamental + harmonic1 + harmonic2) * envelope * 0.45

                            val sampleVal = (warmSound * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                            buffer[i] = sampleVal.toShort()
                        }

                        audioTrack?.write(buffer, 0, buffer.size)
                        delay(280L)
                    }

                    // Brief breathing pause between chords
                    delay(350L)
                }
            }
        } catch (e: Exception) {
            Log.e("AmbientAudioSynth", "Error starting ambient sound", e)
        }
    }

    fun stop() {
        isPlaying = false
        synthJob?.cancel()
        synthJob = null
        try {
            audioTrack?.pause()
            audioTrack?.flush()
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {
            Log.e("AmbientAudioSynth", "Error stopping audioTrack", e)
        } finally {
            audioTrack = null
        }
    }
}
