package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AddEditMemoryScreen
import com.example.ui.screens.AdventureMapDialog
import com.example.ui.screens.ChildGrowthDialog
import com.example.ui.screens.FamilyBucketListDialog
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InsightsDialog
import com.example.ui.screens.LockScreen
import com.example.ui.screens.MemoryDetailScreen
import com.example.ui.screens.SecuritySettingsDialog
import com.example.ui.screens.StorySlideshowScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MemoryViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MemoryAppRoot()
    }
  }
}

@Composable
fun MemoryAppRoot(viewModel: MemoryViewModel = viewModel()) {
  val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()

  MyApplicationTheme(darkTheme = isDarkMode) {
    Surface(modifier = Modifier.fillMaxSize()) {
      MemoryApp(viewModel = viewModel, isDarkMode = isDarkMode)
    }
  }
}

@Composable
fun MemoryApp(viewModel: MemoryViewModel = viewModel(), isDarkMode: Boolean = false) {
  val isLocked by viewModel.isLocked.collectAsStateWithLifecycle()
  val isPinSet by viewModel.isPinSet.collectAsStateWithLifecycle()
  val displayedMemories by viewModel.displayedMemories.collectAsStateWithLifecycle()
  val allMemories by viewModel.allMemories.collectAsStateWithLifecycle()
  val filterState by viewModel.filterState.collectAsStateWithLifecycle()
  val isGridMode by viewModel.isGridMode.collectAsStateWithLifecycle()
  val selectedMemory by viewModel.selectedMemory.collectAsStateWithLifecycle()
  val isAddEditOpen by viewModel.isAddEditOpen.collectAsStateWithLifecycle()
  val editingMemory by viewModel.editingMemory.collectAsStateWithLifecycle()
  val isSettingsOpen by viewModel.isSettingsOpen.collectAsStateWithLifecycle()
  val isInsightsOpen by viewModel.isInsightsOpen.collectAsStateWithLifecycle()
  val isAdventureMapOpen by viewModel.isAdventureMapOpen.collectAsStateWithLifecycle()
  val isSlideshowOpen by viewModel.isSlideshowOpen.collectAsStateWithLifecycle()
  val isChildGrowthOpen by viewModel.isChildGrowthOpen.collectAsStateWithLifecycle()
  val isBucketListOpen by viewModel.isBucketListOpen.collectAsStateWithLifecycle()
  val allMilestones by viewModel.allMilestones.collectAsStateWithLifecycle()
  val allBucketItems by viewModel.allBucketItems.collectAsStateWithLifecycle()
  val appTitle by viewModel.appTitle.collectAsStateWithLifecycle()
  val appSubtitle by viewModel.appSubtitle.collectAsStateWithLifecycle()
  val categories by viewModel.categories.collectAsStateWithLifecycle()
  val activeUser by viewModel.activeUser.collectAsStateWithLifecycle()
  val dayWallpaperIndex by viewModel.dayWallpaperIndex.collectAsStateWithLifecycle()
  val nightWallpaperIndex by viewModel.nightWallpaperIndex.collectAsStateWithLifecycle()

  if (isLocked) {
    LockScreen(
      isPinSet = isPinSet,
      appTitle = appTitle,
      onUnlock = { pin -> viewModel.verifyPin(pin) },
      onSetupPin = { pin, question, answer -> viewModel.setupPin(pin, question, answer) },
      onResetPinWithAnswer = { pin, answer -> viewModel.resetPinWithAnswer(pin, answer) },
      securityQuestion = viewModel.securityManager.getSecurityQuestion(),
      onQuickUnlock = { viewModel.unlockInstantly() }
    )
  } else if (isSlideshowOpen) {
    StorySlideshowScreen(
      memories = allMemories,
      onClose = { viewModel.closeSlideshow() },
      onSelectMemory = { mem ->
        viewModel.closeSlideshow()
        viewModel.selectMemory(mem)
      },
      onToggleFavorite = { mem -> viewModel.toggleFavorite(mem) }
    )
  } else if (isAddEditOpen) {
    AddEditMemoryScreen(
      memoryToEdit = editingMemory,
      availableCategories = categories,
      onSave = { title, story, photoPath, location, mood, category, timestamp, isFavorite, isSecretLocked, tags ->
        viewModel.saveMemory(
          title = title,
          story = story,
          photoPath = photoPath,
          location = location,
          mood = mood,
          category = category,
          timestamp = timestamp,
          isFavorite = isFavorite,
          isSecretLocked = isSecretLocked,
          tags = tags
        )
      },
      onSaveImage = { uri -> viewModel.savePickedImage(uri) },
      onCancel = { viewModel.closeAddEdit() }
    )
  } else if (selectedMemory != null) {
    val currentSelected = selectedMemory!!
    MemoryDetailScreen(
      memory = currentSelected,
      onBack = { viewModel.selectMemory(null) },
      onEdit = { memory -> viewModel.openEditMemory(memory) },
      onDelete = { memory -> viewModel.deleteMemory(memory) },
      onToggleFavorite = { memory -> viewModel.toggleFavorite(memory) }
    )
  } else {
    HomeScreen(
      memories = displayedMemories,
      allMemories = allMemories,
      selectedCategory = filterState.category,
      searchQuery = filterState.searchQuery,
      selectedMood = filterState.selectedMood,
      isGridMode = isGridMode,
      appTitle = appTitle,
      appSubtitle = appSubtitle,
      categories = categories,
      activeUser = activeUser,
      isDarkMode = isDarkMode,
      dayWallpaperIndex = dayWallpaperIndex,
      nightWallpaperIndex = nightWallpaperIndex,
      onToggleDayNightMode = { viewModel.toggleDayNightMode() },
      onRotateWallpaper = { viewModel.rotateWallpaper() },
      onSelectCategory = { cat -> viewModel.setCategory(cat) },
      onSearchQueryChange = { q -> viewModel.setSearchQuery(q) },
      onSelectMood = { mood -> viewModel.setSelectedMood(mood) },
      onToggleGridMode = { viewModel.toggleGridMode() },
      onSelectMemory = { memory -> viewModel.selectMemory(memory) },
      onToggleFavorite = { memory -> viewModel.toggleFavorite(memory) },
      onAddMemory = { viewModel.openAddMemory() },
      onLockApp = { viewModel.lockApp() },
      onOpenSettings = { viewModel.openSettings() },
      onOpenInsights = { viewModel.openInsights() },
      onOpenAdventureMap = { viewModel.openAdventureMap() },
      onOpenSlideshow = { viewModel.openSlideshow() },
      onOpenChildGrowth = { viewModel.openChildGrowth() },
      onOpenBucketList = { viewModel.openBucketList() }
    )
  }

  // Dialogs
  if (isSettingsOpen) {
    SecuritySettingsDialog(
      securityManager = viewModel.securityManager,
      onDismiss = { viewModel.closeSettings() },
      onLockNow = { viewModel.lockApp() }
    )
  }

  if (isInsightsOpen) {
    InsightsDialog(
      memories = allMemories,
      onDismiss = { viewModel.closeInsights() }
    )
  }

  if (isAdventureMapOpen) {
    AdventureMapDialog(
      memories = allMemories,
      onDismiss = { viewModel.closeAdventureMap() },
      onSelectMemory = { mem ->
        viewModel.closeAdventureMap()
        viewModel.selectMemory(mem)
      }
    )
  }

  if (isChildGrowthOpen) {
    ChildGrowthDialog(
      milestones = allMilestones,
      memories = allMemories,
      onDismiss = { viewModel.closeChildGrowth() },
      onToggleMilestone = { milestone -> viewModel.toggleMilestone(milestone) },
      onAddMilestone = { childName, title, description, iconEmoji ->
        viewModel.addMilestone(childName, title, description, iconEmoji)
      },
      onDeleteMilestone = { milestone -> viewModel.deleteMilestone(milestone) },
      onSelectMemory = { mem ->
        viewModel.closeChildGrowth()
        viewModel.selectMemory(mem)
      }
    )
  }

  if (isBucketListOpen) {
    FamilyBucketListDialog(
      bucketItems = allBucketItems,
      onDismiss = { viewModel.closeBucketList() },
      onToggleBucketItem = { item -> viewModel.toggleBucketItem(item) },
      onAddBucketItem = { title, category, description, targetDate, iconEmoji ->
        viewModel.addBucketItem(title, category, description, targetDate, iconEmoji)
      },
      onDeleteBucketItem = { item -> viewModel.deleteBucketItem(item) }
    )
  }
}

