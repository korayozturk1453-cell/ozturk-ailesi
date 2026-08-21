package com.example.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ImageUtils {

    fun saveUriToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val directory = File(context.filesDir, "memories")
            if (!directory.exists()) {
                directory.mkdirs()
            }

            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.getDefault()).format(Date())
            val destinationFile = File(directory, "IMG_$timeStamp.jpg")

            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(destinationFile)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            destinationFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("d MMMM yyyy, EEEE", Locale("tr", "TR"))
        return sdf.format(Date(timestamp))
    }

    fun formatShortDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("d MMM yyyy", Locale("tr", "TR"))
        return sdf.format(Date(timestamp))
    }

    fun isToday(timestamp: Long): Boolean {
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return sdf.format(Date(timestamp)) == sdf.format(Date())
    }
}
