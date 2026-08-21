package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "family_bucket_items")
data class FamilyBucketItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String = "Genel", // "Seyahat & Gezi", "Doğa & Kamp", "Etkinlik & Eğlence", "Gelenek & Aile", "Ev & Yaşam"
    val description: String = "",
    val targetDate: String = "",
    val isCompleted: Boolean = false,
    val completedDate: Long? = null,
    val photoPath: String? = null,
    val iconEmoji: String = "✨"
)
