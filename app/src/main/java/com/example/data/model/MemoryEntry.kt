package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memories")
data class MemoryEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val story: String,
    val photoPath: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val location: String = "",
    val mood: String = "Mutlu",
    val category: String = "Genel",
    val isFavorite: Boolean = false,
    val isSecretLocked: Boolean = false,
    val tags: String = ""
)
