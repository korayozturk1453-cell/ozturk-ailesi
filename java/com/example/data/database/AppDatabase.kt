package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.ChildMilestoneDao
import com.example.data.dao.FamilyBucketItemDao
import com.example.data.dao.MemoryDao
import com.example.data.model.ChildMilestone
import com.example.data.model.FamilyBucketItem
import com.example.data.model.MemoryEntry

@Database(entities = [MemoryEntry::class, ChildMilestone::class, FamilyBucketItem::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun memoryDao(): MemoryDao
    abstract fun childMilestoneDao(): ChildMilestoneDao
    abstract fun familyBucketItemDao(): FamilyBucketItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "memory_journal_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
