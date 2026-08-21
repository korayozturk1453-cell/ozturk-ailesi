package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.model.ChildMilestone
import com.example.data.model.FamilyBucketItem
import com.example.data.model.MemoryEntry
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BackupData(
    val memories: List<MemoryEntry>,
    val milestones: List<ChildMilestone>,
    val bucketItems: List<FamilyBucketItem>,
    val appTitle: String,
    val appSubtitle: String,
    val categories: List<String>
)

object FamilySyncHelper {

    fun exportBackupToJson(
        memories: List<MemoryEntry>,
        milestones: List<ChildMilestone>,
        bucketItems: List<FamilyBucketItem>,
        appTitle: String,
        appSubtitle: String,
        categories: List<String>
    ): String {
        val root = JSONObject()
        root.put("version", 1)
        root.put("exportDate", System.currentTimeMillis())
        root.put("appTitle", appTitle)
        root.put("appSubtitle", appSubtitle)

        // Categories
        val catsArray = JSONArray()
        categories.forEach { catsArray.put(it) }
        root.put("categories", catsArray)

        // Memories
        val memsArray = JSONArray()
        memories.forEach { mem ->
            val obj = JSONObject()
            obj.put("title", mem.title)
            obj.put("story", mem.story)
            obj.put("location", mem.location)
            obj.put("mood", mem.mood)
            obj.put("category", mem.category)
            obj.put("isFavorite", mem.isFavorite)
            obj.put("tags", mem.tags)
            obj.put("timestamp", mem.timestamp)
            obj.put("authorName", mem.authorName)
            obj.put("voiceNotePath", mem.voiceNotePath ?: "")
            obj.put("imagePath", mem.imagePath ?: "")
            memsArray.put(obj)
        }
        root.put("memories", memsArray)

        // Milestones
        val milesArray = JSONArray()
        milestones.forEach { m ->
            val obj = JSONObject()
            obj.put("childName", m.childName)
            obj.put("title", m.title)
            obj.put("description", m.description)
            obj.put("targetAgeMonths", m.targetAgeMonths)
            obj.put("achievedDate", m.achievedDate)
            obj.put("isCompleted", m.isCompleted)
            obj.put("iconEmoji", m.iconEmoji)
            milesArray.put(obj)
        }
        root.put("milestones", milesArray)

        // Bucket Items
        val bucketArray = JSONArray()
        bucketItems.forEach { b ->
            val obj = JSONObject()
            obj.put("title", b.title)
            obj.put("category", b.category)
            obj.put("description", b.description)
            obj.put("targetDate", b.targetDate)
            obj.put("isCompleted", b.isCompleted)
            obj.put("completedDate", b.completedDate)
            obj.put("iconEmoji", b.iconEmoji)
            bucketArray.put(obj)
        }
        root.put("bucketItems", bucketArray)

        return root.toString(2)
    }

    fun parseBackupJson(jsonString: String): BackupData {
        val root = JSONObject(jsonString)
        val appTitle = root.optString("appTitle", "Öztürk Ailesi")
        val appSubtitle = root.optString("appSubtitle", "Bizim Özel Anı Defterimiz")

        val categories = mutableListOf<String>()
        val catsArray = root.optJSONArray("categories")
        if (catsArray != null) {
            for (i in 0 until catsArray.length()) {
                categories.add(catsArray.getString(i))
            }
        }

        val memories = mutableListOf<MemoryEntry>()
        val memsArray = root.optJSONArray("memories")
        if (memsArray != null) {
            for (i in 0 until memsArray.length()) {
                val obj = memsArray.getJSONObject(i)
                memories.add(
                    MemoryEntry(
                        title = obj.optString("title", ""),
                        story = obj.optString("story", ""),
                        location = obj.optString("location", ""),
                        mood = obj.optString("mood", "Mutlu"),
                        category = obj.optString("category", "Aile"),
                        isFavorite = obj.optBoolean("isFavorite", false),
                        tags = obj.optString("tags", ""),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        authorName = obj.optString("authorName", "Aile"),
                        voiceNotePath = obj.optString("voiceNotePath").ifBlank { null },
                        imagePath = obj.optString("imagePath").ifBlank { null }
                    )
                )
            }
        }

        val milestones = mutableListOf<ChildMilestone>()
        val milesArray = root.optJSONArray("milestones")
        if (milesArray != null) {
            for (i in 0 until milesArray.length()) {
                val obj = milesArray.getJSONObject(i)
                milestones.add(
                    ChildMilestone(
                        childName = obj.optString("childName", "Zeyd"),
                        title = obj.optString("title", ""),
                        description = obj.optString("description", ""),
                        targetAgeMonths = obj.optInt("targetAgeMonths", 0),
                        achievedDate = obj.optLong("achievedDate", 0L),
                        isCompleted = obj.optBoolean("isCompleted", false),
                        iconEmoji = obj.optString("iconEmoji", "⭐")
                    )
                )
            }
        }

        val bucketItems = mutableListOf<FamilyBucketItem>()
        val bucketArray = root.optJSONArray("bucketItems")
        if (bucketArray != null) {
            for (i in 0 until bucketArray.length()) {
                val obj = bucketArray.getJSONObject(i)
                bucketItems.add(
                    FamilyBucketItem(
                        title = obj.optString("title", ""),
                        category = obj.optString("category", "Gelenek"),
                        description = obj.optString("description", ""),
                        targetDate = obj.optString("targetDate", ""),
                        isCompleted = obj.optBoolean("isCompleted", false),
                        completedDate = obj.optLong("completedDate", 0L),
                        iconEmoji = obj.optString("iconEmoji", "✨")
                    )
                )
            }
        }

        return BackupData(
            memories = memories,
            milestones = milestones,
            bucketItems = bucketItems,
            appTitle = appTitle,
            appSubtitle = appSubtitle,
            categories = categories
        )
    }

    fun shareBackupFile(context: Context, jsonContent: String) {
        val dateFormat = SimpleDateFormat("dd_MM_yyyy_HHmm", Locale.getDefault())
        val fileName = "ozturk_ailesi_yedek_${dateFormat.format(Date())}.ozturk"
        val file = File(context.cacheDir, fileName)
        file.writeText(jsonContent)

        val uri: Uri = try {
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
            Uri.fromFile(file)
        }

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_SUBJECT, "Öztürk Ailesi Anı Defteri Yedeği")
            putExtra(Intent.EXTRA_TEXT, "Öztürk Ailesi güncel anı defteri, çocuk gelişim notları ve aile hedefleri yedeğidir. Uygulamadan İçe Aktar diyerek açabilirsiniz.")
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(sendIntent, "Eşinizle / Aileyle Paylaş (WhatsApp)")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}
