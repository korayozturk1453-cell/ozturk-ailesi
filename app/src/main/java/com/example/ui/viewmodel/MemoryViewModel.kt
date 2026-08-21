package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.MemoryEntry
import com.example.data.repository.MemoryRepository
import com.example.data.security.SecurityManager
import com.example.util.ImageUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MemoryFilterState(
    val category: String = "Tümü",
    val searchQuery: String = "",
    val selectedMood: String? = null,
    val onlyFavorites: Boolean = false
)

class MemoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MemoryRepository
    val securityManager: SecurityManager = SecurityManager(application)

    val isLocked: StateFlow<Boolean> = securityManager.isLocked
    val isPinSet: StateFlow<Boolean> = securityManager.isPinSet
    val appTitle: StateFlow<String> = securityManager.appTitle
    val appSubtitle: StateFlow<String> = securityManager.appSubtitle
    val categories: StateFlow<List<String>> = securityManager.categories
    val allowedMembers: StateFlow<List<com.example.data.security.FamilyMember>> = securityManager.allowedMembers
    val activeUser: StateFlow<com.example.data.security.FamilyMember?> = securityManager.activeUser
    val isDarkMode: StateFlow<Boolean> = securityManager.isDarkMode
    val dayWallpaperIndex: StateFlow<Int> = securityManager.dayWallpaperIndex
    val nightWallpaperIndex: StateFlow<Int> = securityManager.nightWallpaperIndex

    private val _filterState = MutableStateFlow(MemoryFilterState())
    val filterState: StateFlow<MemoryFilterState> = _filterState.asStateFlow()

    private val _selectedMemory = MutableStateFlow<MemoryEntry?>(null)
    val selectedMemory: StateFlow<MemoryEntry?> = _selectedMemory.asStateFlow()

    private val _isAddEditOpen = MutableStateFlow(false)
    val isAddEditOpen: StateFlow<Boolean> = _isAddEditOpen.asStateFlow()

    private val _editingMemory = MutableStateFlow<MemoryEntry?>(null)
    val editingMemory: StateFlow<MemoryEntry?> = _editingMemory.asStateFlow()

    private val _isSettingsOpen = MutableStateFlow(false)
    val isSettingsOpen: StateFlow<Boolean> = _isSettingsOpen.asStateFlow()

    private val _isInsightsOpen = MutableStateFlow(false)
    val isInsightsOpen: StateFlow<Boolean> = _isInsightsOpen.asStateFlow()

    private val _isAdventureMapOpen = MutableStateFlow(false)
    val isAdventureMapOpen: StateFlow<Boolean> = _isAdventureMapOpen.asStateFlow()

    private val _isSlideshowOpen = MutableStateFlow(false)
    val isSlideshowOpen: StateFlow<Boolean> = _isSlideshowOpen.asStateFlow()

    private val _isChildGrowthOpen = MutableStateFlow(false)
    val isChildGrowthOpen: StateFlow<Boolean> = _isChildGrowthOpen.asStateFlow()

    private val _isBucketListOpen = MutableStateFlow(false)
    val isBucketListOpen: StateFlow<Boolean> = _isBucketListOpen.asStateFlow()

    private val _isGridMode = MutableStateFlow(false)
    val isGridMode: StateFlow<Boolean> = _isGridMode.asStateFlow()

    private val _pinError = MutableStateFlow<String?>(null)
    val pinError: StateFlow<String?> = _pinError.asStateFlow()

    private val _isForgotPinOpen = MutableStateFlow(false)
    val isForgotPinOpen: StateFlow<Boolean> = _isForgotPinOpen.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = MemoryRepository(db.memoryDao(), db.childMilestoneDao(), db.familyBucketItemDao())

        // Insert sample starter memory, milestones and bucket list items if database is brand new
        viewModelScope.launch {
            repository.allMemories.collect { list ->
                if (list.isEmpty()) {
                    // Seed initial welcome memory
                    repository.insertMemory(
                        MemoryEntry(
                            title = "Anı Defterine Hoş Geldin ✨",
                            story = "Bu özel ve güvenli anı defteri Öztürk Ailesine özeldir. Fotoğraflarınızı ekleyebilir, hissettiklerinizi yazabilir, çocukların gelişimini takip edebilir ve hayallerinizi gerçekleştirebilirsiniz.",
                            location = "Bizim Yuva",
                            mood = "Huzurlu",
                            category = "Aile",
                            isFavorite = true,
                            tags = "#özel, #öztürkaylesi, #zeyd, #esila",
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
            }
        }

        viewModelScope.launch {
            if (repository.getMilestonesCount() == 0) {
                repository.insertMilestones(com.example.util.ChildGrowthHelper.getStarterMilestones())
            }
            if (repository.getBucketItemsCount() == 0) {
                repository.insertBucketItems(com.example.util.ChildGrowthHelper.getStarterBucketItems())
            }
        }
    }

    val allMilestones: StateFlow<List<com.example.data.model.ChildMilestone>> = repository.allMilestones.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allBucketItems: StateFlow<List<com.example.data.model.FamilyBucketItem>> = repository.allBucketItems.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allMemories: StateFlow<List<MemoryEntry>> = repository.allMemories.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val displayedMemories: StateFlow<List<MemoryEntry>> = combine(
        repository.allMemories,
        _filterState
    ) { memories, filter ->
        memories.filter { item ->
            val matchesCategory = when (filter.category) {
                "Tümü" -> true
                "Favoriler" -> item.isFavorite
                else -> item.category.equals(filter.category, ignoreCase = true)
            }
            val matchesFavorites = if (filter.onlyFavorites) item.isFavorite else true
            val matchesMood = filter.selectedMood == null || item.mood == filter.selectedMood
            val matchesSearch = if (filter.searchQuery.isBlank()) {
                true
            } else {
                val q = filter.searchQuery.trim()
                com.example.util.TagProvider.matchesTagOrQuery(item.tags, q) ||
                    com.example.util.TagProvider.matchesTagOrQuery(item.title, q) ||
                    com.example.util.TagProvider.matchesTagOrQuery(item.story, q) ||
                    com.example.util.TagProvider.matchesTagOrQuery(item.location, q) ||
                    com.example.util.TagProvider.matchesTagOrQuery(item.category, q)
            }
            matchesCategory && matchesFavorites && matchesMood && matchesSearch
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setCategory(category: String) {
        _filterState.value = _filterState.value.copy(category = category)
    }

    fun setSearchQuery(query: String) {
        _filterState.value = _filterState.value.copy(searchQuery = query)
    }

    fun setSelectedMood(mood: String?) {
        val newMood = if (_filterState.value.selectedMood == mood) null else mood
        _filterState.value = _filterState.value.copy(selectedMood = newMood)
    }

    fun toggleGridMode() {
        _isGridMode.value = !_isGridMode.value
    }

    // PIN & Security operations
    fun verifyPin(pin: String): Boolean {
        val success = securityManager.verifyPin(pin)
        if (!success) {
            _pinError.value = "Hatalı PIN! Lütfen tekrar deneyiniz."
        } else {
            _pinError.value = null
        }
        return success
    }

    fun unlockInstantly() {
        securityManager.unlockInstantly()
    }

    fun clearPinError() {
        _pinError.value = null
    }

    fun setupPin(pin: String, question: String, answer: String): Boolean {
        return securityManager.setupPin(pin, question, answer)
    }

    fun resetPinWithAnswer(newPin: String, answer: String): Boolean {
        val success = securityManager.resetPinWithAnswer(newPin, answer)
        if (success) {
            _isForgotPinOpen.value = false
        }
        return success
    }

    fun lockApp() {
        securityManager.lockApp()
    }

    fun openForgotPin() {
        _isForgotPinOpen.value = true
    }

    fun closeForgotPin() {
        _isForgotPinOpen.value = false
    }

    // Memory operations
    fun openAddMemory() {
        _editingMemory.value = null
        _isAddEditOpen.value = true
    }

    fun openEditMemory(memory: MemoryEntry) {
        _editingMemory.value = memory
        _isAddEditOpen.value = true
    }

    fun closeAddEdit() {
        _isAddEditOpen.value = false
        _editingMemory.value = null
    }

    fun selectMemory(memory: MemoryEntry?) {
        _selectedMemory.value = memory
    }

    fun saveMemory(
        title: String,
        story: String,
        photoPath: String?,
        location: String,
        mood: String,
        category: String,
        timestamp: Long,
        isFavorite: Boolean,
        isSecretLocked: Boolean,
        tags: String
    ) {
        viewModelScope.launch {
            val current = _editingMemory.value
            if (current != null) {
                val updated = current.copy(
                    title = title.trim(),
                    story = story.trim(),
                    photoPath = photoPath ?: current.photoPath,
                    location = location.trim(),
                    mood = mood,
                    category = category,
                    timestamp = timestamp,
                    isFavorite = isFavorite,
                    isSecretLocked = isSecretLocked,
                    tags = tags.trim()
                )
                repository.updateMemory(updated)
                if (_selectedMemory.value?.id == current.id) {
                    _selectedMemory.value = updated
                }
            } else {
                val newEntry = MemoryEntry(
                    title = title.trim(),
                    story = story.trim(),
                    photoPath = photoPath,
                    location = location.trim(),
                    mood = mood,
                    category = category,
                    timestamp = timestamp,
                    isFavorite = isFavorite,
                    isSecretLocked = isSecretLocked,
                    tags = tags.trim()
                )
                repository.insertMemory(newEntry)
            }
            closeAddEdit()
        }
    }

    fun deleteMemory(memory: MemoryEntry) {
        viewModelScope.launch {
            repository.deleteMemory(memory)
            if (_selectedMemory.value?.id == memory.id) {
                _selectedMemory.value = null
            }
        }
    }

    fun toggleFavorite(memory: MemoryEntry) {
        viewModelScope.launch {
            val newStatus = !memory.isFavorite
            repository.toggleFavorite(memory.id, memory.isFavorite)
            if (_selectedMemory.value?.id == memory.id) {
                _selectedMemory.value = memory.copy(isFavorite = newStatus)
            }
        }
    }

    fun savePickedImage(uri: Uri): String? {
        return ImageUtils.saveUriToInternalStorage(getApplication(), uri)
    }

    fun updateAppTitle(title: String) {
        securityManager.setAppTitle(title)
    }

    fun updateAppSubtitle(subtitle: String) {
        securityManager.setAppSubtitle(subtitle)
    }

    fun addCategory(category: String): Boolean {
        return securityManager.addCategory(category)
    }

    fun removeCategory(category: String): Boolean {
        return securityManager.removeCategory(category)
    }

    fun addFamilyMember(name: String, role: String, pin: String): Boolean {
        return securityManager.addFamilyMember(name, role, pin)
    }

    fun removeFamilyMember(id: String): Boolean {
        return securityManager.removeFamilyMember(id)
    }

    fun toggleDayNightMode(): Boolean {
        return securityManager.toggleDayNightMode()
    }

    fun rotateWallpaper() {
        securityManager.rotateWallpaper()
    }

    fun setDarkMode(enabled: Boolean) {
        securityManager.setDarkMode(enabled)
    }

    fun openSettings() {
        _isSettingsOpen.value = true
    }

    fun closeSettings() {
        _isSettingsOpen.value = false
    }

    fun openInsights() {
        _isInsightsOpen.value = true
    }

    fun closeInsights() {
        _isInsightsOpen.value = false
    }

    fun openAdventureMap() {
        _isAdventureMapOpen.value = true
    }

    fun closeAdventureMap() {
        _isAdventureMapOpen.value = false
    }

    fun openSlideshow() {
        _isSlideshowOpen.value = true
    }

    fun closeSlideshow() {
        _isSlideshowOpen.value = false
    }

    fun openChildGrowth() {
        _isChildGrowthOpen.value = true
    }

    fun closeChildGrowth() {
        _isChildGrowthOpen.value = false
    }

    fun openBucketList() {
        _isBucketListOpen.value = true
    }

    fun closeBucketList() {
        _isBucketListOpen.value = false
    }

    fun toggleMilestone(milestone: com.example.data.model.ChildMilestone) {
        viewModelScope.launch {
            val updated = milestone.copy(
                isCompleted = !milestone.isCompleted,
                completedDate = if (!milestone.isCompleted) System.currentTimeMillis() else null
            )
            repository.updateMilestone(updated)
        }
    }

    fun addMilestone(
        childName: String,
        title: String,
        description: String = "",
        iconEmoji: String = "🌟"
    ) {
        viewModelScope.launch {
            repository.insertMilestone(
                com.example.data.model.ChildMilestone(
                    childName = childName,
                    title = title,
                    description = description,
                    iconEmoji = iconEmoji,
                    timestamp = System.currentTimeMillis(),
                    isCompleted = false
                )
            )
        }
    }

    fun deleteMilestone(milestone: com.example.data.model.ChildMilestone) {
        viewModelScope.launch {
            repository.deleteMilestone(milestone)
        }
    }

    fun toggleBucketItem(item: com.example.data.model.FamilyBucketItem) {
        viewModelScope.launch {
            val updated = item.copy(
                isCompleted = !item.isCompleted,
                completedDate = if (!item.isCompleted) System.currentTimeMillis() else null
            )
            repository.updateBucketItem(updated)
        }
    }

    fun addBucketItem(
        title: String,
        category: String = "Genel",
        description: String = "",
        targetDate: String = "",
        iconEmoji: String = "✨"
    ) {
        viewModelScope.launch {
            repository.insertBucketItem(
                com.example.data.model.FamilyBucketItem(
                    title = title,
                    category = category,
                    description = description,
                    targetDate = targetDate,
                    iconEmoji = iconEmoji,
                    isCompleted = false
                )
            )
        }
    }

    fun deleteBucketItem(item: com.example.data.model.FamilyBucketItem) {
        viewModelScope.launch {
            repository.deleteBucketItem(item)
        }
    }
}
