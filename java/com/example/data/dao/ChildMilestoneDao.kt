package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ChildMilestone
import kotlinx.coroutines.flow.Flow

@Dao
interface ChildMilestoneDao {

    @Query("SELECT * FROM child_milestones ORDER BY timestamp DESC")
    fun getAllMilestones(): Flow<List<ChildMilestone>>

    @Query("SELECT * FROM child_milestones ORDER BY timestamp DESC")
    suspend fun getAllMilestonesList(): List<ChildMilestone>

    @Query("SELECT * FROM child_milestones WHERE childName = :childName ORDER BY timestamp DESC")
    fun getMilestonesForChild(childName: String): Flow<List<ChildMilestone>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestone(milestone: ChildMilestone): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(milestones: List<ChildMilestone>)

    @Update
    suspend fun updateMilestone(milestone: ChildMilestone)

    @Delete
    suspend fun deleteMilestone(milestone: ChildMilestone)

    @Query("DELETE FROM child_milestones WHERE id = :id")
    suspend fun deleteMilestoneById(id: Long)

    @Query("SELECT COUNT(*) FROM child_milestones")
    suspend fun getCount(): Int
}
