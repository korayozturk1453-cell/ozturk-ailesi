package com.example.data.repository

import com.example.data.dao.ChildMilestoneDao
import com.example.data.dao.FamilyBucketItemDao
import com.example.data.dao.MemoryDao
import com.example.data.model.ChildMilestone
import com.example.data.model.FamilyBucketItem
import com.example.data.model.MemoryEntry
import kotlinx.coroutines.flow.Flow

class MemoryRepository(
    private val memoryDao: MemoryDao,
    private val childMilestoneDao: ChildMilestoneDao,
    private val familyBucketItemDao: FamilyBucketItemDao
) {

    val allMemories: Flow<List<MemoryEntry>> = memoryDao.getAllMemories()
    val favoriteMemories: Flow<List<MemoryEntry>> = memoryDao.getFavoriteMemories()
    val memoryCount: Flow<Int> = memoryDao.getMemoryCount()

    val allMilestones: Flow<List<ChildMilestone>> = childMilestoneDao.getAllMilestones()
    val allBucketItems: Flow<List<FamilyBucketItem>> = familyBucketItemDao.getAllBucketItems()

    fun getMilestonesForChild(childName: String): Flow<List<ChildMilestone>> {
        return childMilestoneDao.getMilestonesForChild(childName)
    }

    fun getMemoriesByCategory(category: String): Flow<List<MemoryEntry>> {
        return memoryDao.getMemoriesByCategory(category)
    }

    fun searchMemories(query: String): Flow<List<MemoryEntry>> {
        return memoryDao.searchMemories(query)
    }

    suspend fun getMemoryById(id: Long): MemoryEntry? {
        return memoryDao.getMemoryById(id)
    }

    suspend fun insertMemory(memory: MemoryEntry): Long {
        return memoryDao.insertMemory(memory)
    }

    suspend fun updateMemory(memory: MemoryEntry) {
        memoryDao.updateMemory(memory)
    }

    suspend fun deleteMemory(memory: MemoryEntry) {
        memoryDao.deleteMemory(memory)
    }

    suspend fun deleteMemoryById(id: Long) {
        memoryDao.deleteMemoryById(id)
    }

    suspend fun toggleFavorite(id: Long, currentStatus: Boolean) {
        memoryDao.updateFavoriteStatus(id, !currentStatus)
    }

    // Milestones
    suspend fun insertMilestones(milestones: List<ChildMilestone>) {
        childMilestoneDao.insertAll(milestones)
    }

    suspend fun insertMilestone(milestone: ChildMilestone): Long {
        return childMilestoneDao.insertMilestone(milestone)
    }

    suspend fun updateMilestone(milestone: ChildMilestone) {
        childMilestoneDao.updateMilestone(milestone)
    }

    suspend fun deleteMilestone(milestone: ChildMilestone) {
        childMilestoneDao.deleteMilestone(milestone)
    }

    suspend fun getMilestonesCount(): Int {
        return childMilestoneDao.getCount()
    }

    // Bucket List
    suspend fun insertBucketItems(items: List<FamilyBucketItem>) {
        familyBucketItemDao.insertAll(items)
    }

    suspend fun insertBucketItem(item: FamilyBucketItem): Long {
        return familyBucketItemDao.insertBucketItem(item)
    }

    suspend fun updateBucketItem(item: FamilyBucketItem) {
        familyBucketItemDao.updateBucketItem(item)
    }

    suspend fun deleteBucketItem(item: FamilyBucketItem) {
        familyBucketItemDao.deleteBucketItem(item)
    }

    suspend fun getBucketItemsCount(): Int {
        return familyBucketItemDao.getCount()
    }
}
