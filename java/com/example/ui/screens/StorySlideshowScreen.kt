package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.MemoryEntry
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberOnContainer
import com.example.ui.theme.AmberPrimary
import com.example.util.AmbientAudioSynthesizer
import com.example.util.ImageUtils
import com.example.util.TagProvider
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun StorySlideshowScreen(
    memories: List<MemoryEntry>,
    initialIndex: Int = 0,
    onClose: () -> Unit,
    onSelectMemory: (MemoryEntry) -> Unit,
    onToggleFavorite: (MemoryEntry) -> Unit
) {
    var selectedFilterTag by remember { mutableStateOf<String?>(null) }
    var onlyPhotos by remember { mutableStateOf(false) }

    val activePlaylist = remember(memories, selectedFilterTag, onlyPhotos) {
        var list = memories
        if (onlyPhotos) {
            list = list.filter { it.photoPath != null && File(it.photoPath).exists() }
        }
        if (selectedFilterTag != null) {
            list = list.filter { TagProvider.isTagPresent(it.tags, selectedFilterTag!!) || it.location.contains(selectedFilterTag!!, ignoreCase = true) }
        }
        if (list.isEmpty()) memories else list
    }

    var currentIndex by remember { mutableIntStateOf(initialIndex.coerceIn(0, (activePlaylist.size - 1).coerceAtLeast(0))) }
    var isPaused by remember { mutableStateOf(false) }
    var isMusicEnabled by remember { mutableStateOf(true) }

    val currentMemory = activePlaylist.getOrNull(currentIndex) ?: memories.firstOrNull()

    // Ambient Audio Synthesizer
    val audioSynth = remember { AmbientAudioSynthesizer() }

    DisposableEffect(Unit) {
        if (isMusicEnabled) {
            audioSynth.start()
        }
        onDispose {
            audioSynth.stop()
        }
    }

    LaunchedEffect(isMusicEnabled) {
        if (isMusicEnabled) {
            audioSynth.start()
        } else {
            audioSynth.stop()
        }
    }

    // Story Slide Progress (0f to 1f)
    var slideProgress by remember { mutableFloatStateOf(0f) }
    val slideDurationMs = 5000L

    LaunchedEffect(currentIndex, isPaused, activePlaylist.size) {
        if (activePlaylist.isEmpty()) return@LaunchedEffect
        slideProgress = 0f
        val stepMs = 50L
        val totalSteps = slideDurationMs / stepMs
        while (!isPaused && slideProgress < 1f) {
            delay(stepMs)
            slideProgress += (1f / totalSteps)
        }
        if (!isPaused && slideProgress >= 1f) {
            if (currentIndex < activePlaylist.size - 1) {
                currentIndex++
            } else {
                // Loop back to start or finish
                currentIndex = 0
            }
        }
    }

    // Ken Burns slow zoom transition
    val infiniteTransition = rememberInfiniteTransition(label = "KenBurns")
    val zoomScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ZoomAnim"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPaused = true
                        tryAwaitRelease()
                        isPaused = false
                    },
                    onTap = { offset ->
                        val screenWidth = size.width
                        if (offset.x < screenWidth * 0.33f) {
                            // Previous slide
                            if (currentIndex > 0) {
                                currentIndex--
                            } else {
                                currentIndex = activePlaylist.size - 1
                            }
                        } else {
                            // Next slide
                            if (currentIndex < activePlaylist.size - 1) {
                                currentIndex++
                            } else {
                                currentIndex = 0
                            }
                        }
                    }
                )
            }
    ) {
        // 1. Background Content (Photo with Ken Burns zoom or atmospheric card)
        if (currentMemory?.photoPath != null && File(currentMemory.photoPath).exists()) {
            AsyncImage(
                model = File(currentMemory.photoPath),
                contentDescription = currentMemory.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .scale(zoomScale)
            )
        } else {
            // Atmospheric Mood Gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                AmberPrimary.copy(alpha = 0.45f),
                                Color(0xFF1E1B4B),
                                Color(0xFF090A0F)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "🏡",
                        fontSize = 72.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = currentMemory?.title ?: "Aile Hatırası",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // 2. Translucent gradient shades for controls and caption readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.65f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f)
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )

        // 3. Top Section: Segmented Progress Bars & Controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 12.dp, end = 12.dp)
        ) {
            // Segmented Progress Bars
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                activePlaylist.forEachIndexed { idx, _ ->
                    val progress = when {
                        idx < currentIndex -> 1f
                        idx == currentIndex -> slideProgress
                        else -> 0f
                    }
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .weight(1f)
                            .height(3.5.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = AmberPrimary,
                        trackColor = Color.White.copy(alpha = 0.3f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Top Bar Icons & Tags Filter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.White.copy(alpha = 0.2f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🎬 ${currentIndex + 1}/${activePlaylist.size}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Music Synthesizer Mute Toggle
                    IconButton(
                        onClick = { isMusicEnabled = !isMusicEnabled },
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color.Black.copy(alpha = 0.45f), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isMusicEnabled) Icons.Default.MusicNote else Icons.Default.MusicOff,
                            contentDescription = "Müzik",
                            tint = if (isMusicEnabled) AmberPrimary else Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Play / Pause Toggle
                    IconButton(
                        onClick = { isPaused = !isPaused },
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color.Black.copy(alpha = 0.45f), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = "Durdur / Oynat",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Close Button
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Kapat",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Playlist Filter Tag Pills
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    onClick = {
                        selectedFilterTag = null
                        onlyPhotos = false
                        currentIndex = 0
                    },
                    shape = RoundedCornerShape(10.dp),
                    color = if (selectedFilterTag == null && !onlyPhotos) AmberPrimary else Color.Black.copy(alpha = 0.55f),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.White.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "Tümü",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                listOf("Zeyd", "Esila", "Köy", "İstanbul", "Gezi", "Deniz").forEach { tag ->
                    val isSel = selectedFilterTag.equals(tag, ignoreCase = true)
                    Surface(
                        onClick = {
                            selectedFilterTag = if (isSel) null else tag
                            currentIndex = 0
                        },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSel) AmberPrimary else Color.Black.copy(alpha = 0.55f),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.White.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "#$tag",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // 4. Bottom Section: Memory Details & Action Overlay
        if (currentMemory != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(start = 16.dp, end = 16.dp, bottom = 28.dp)
            ) {
                // Mood & Date Badges
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AmberContainer.copy(alpha = 0.9f)
                    ) {
                        Text(
                            text = "🗓️ ${ImageUtils.formatShortDate(currentMemory.timestamp)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberOnContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    if (currentMemory.location.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.6f),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.White.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "📍 ${currentMemory.location}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    if (currentMemory.mood.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.6f),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.White.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "✨ ${currentMemory.mood}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Title
                Text(
                    text = currentMemory.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Story
                if (currentMemory.story.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentMemory.story,
                        fontSize = 13.5.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            onClose()
                            onSelectMemory(currentMemory)
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInFull,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Anıyı Aç",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = { onToggleFavorite(currentMemory) },
                            modifier = Modifier
                                .size(42.dp)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (currentMemory.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favori",
                                tint = if (currentMemory.isFavorite) Color.Red else Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
