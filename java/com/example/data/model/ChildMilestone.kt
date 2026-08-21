package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "child_milestones")
data class ChildMilestone(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val childName: String, // "Zeyd" or "Esila" or "Ortak"
    val title: String,
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false,
    val completedDate: Long? = null,
    val photoPath: String? = null,
    val iconEmoji: String = "🌟"
)
