package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.FamilyBucketItem
import kotlinx.coroutines.flow.Flow

@Dao
interface FamilyBucketItemDao {

    @Query("SELECT * FROM family_bucket_items ORDER BY isCompleted ASC, id ASC")
    fun getAllBucketItems(): Flow<List<FamilyBucketItem>>

    @Query("SELECT * FROM family_bucket_items ORDER BY isCompleted ASC, id ASC")
    suspend fun getAllBucketItemsList(): List<FamilyBucketItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBucketItem(item: FamilyBucketItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<FamilyBucketItem>)

    @Update
    suspend fun updateBucketItem(item: FamilyBucketItem)

    @Delete
    suspend fun deleteBucketItem(item: FamilyBucketItem)

    @Query("DELETE FROM family_bucket_items WHERE id = :id")
    suspend fun deleteBucketItemById(id: Long)

    @Query("SELECT COUNT(*) FROM family_bucket_items")
    suspend fun getCount(): Int
}
