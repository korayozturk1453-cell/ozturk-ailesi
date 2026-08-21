package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.InsertChart
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.MemoryEntry
import com.example.ui.components.DailyNoteLine
import com.example.ui.components.OnThisDayCard
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberOnContainer
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.IndigoContainer
import com.example.ui.theme.IndigoOnContainer
import com.example.ui.theme.IndigoSecondary
import com.example.util.ImageUtils
import com.example.util.OnThisDayProvider
import com.example.util.TagProvider
import com.example.util.WallpaperProvider
import kotlinx.coroutines.delay
import java.io.File

val HOME_CATEGORIES = listOf(
    "Tümü",
    "Favoriler",
    "Seyahat",
    "Aile & Dostlar",
    "Özel Günler",
    "Doğa & Keşif",
    "Kişisel & Başarı"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    memories: List<MemoryEntry>,
    allMemories: List<MemoryEntry>,
    selectedCategory: String,
    searchQuery: String,
    selectedMood: String?,
    isGridMode: Boolean,
    appTitle: String = "Öztürk Ailesi",
    appSubtitle: String = "Özel Aile Rehberi & Hatıra Defteri",
    categories: List<String> = HOME_CATEGORIES,
    activeUser: com.example.data.security.FamilyMember? = null,
    isDarkMode: Boolean = false,
    dayWallpaperIndex: Int = 0,
    nightWallpaperIndex: Int = 0,
    onToggleDayNightMode: () -> Unit = {},
    onRotateWallpaper: () -> Unit = {},
    onSelectCategory: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSelectMood: (String?) -> Unit,
    onToggleGridMode: () -> Unit,
    onSelectMemory: (MemoryEntry) -> Unit,
    onToggleFavorite: (MemoryEntry) -> Unit,
    onAddMemory: () -> Unit,
    onLockApp: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenInsights: () -> Unit,
    onOpenAdventureMap: () -> Unit = {},
    onOpenSlideshow: () -> Unit = {},
    onOpenChildGrowth: () -> Unit = {},
    onOpenBucketList: () -> Unit = {},
    onOpenSync: () -> Unit = {}
) {
    var isSearchExpanded by remember { mutableStateOf(false) }

    val activeWallpaperList = if (isDarkMode) WallpaperProvider.nightWallpapers else WallpaperProvider.dayWallpapers
    val baseIndex = if (isDarkMode) nightWallpaperIndex else dayWallpaperIndex

    // Automatic self-changing wallpaper rotation timer (every 10 seconds)
    var autoSlideOffset by remember { mutableIntStateOf(0) }
    LaunchedEffect(isDarkMode, activeWallpaperList.size) {
        autoSlideOffset = 0
        while (true) {
            delay(10000L) // Changes automatically every 10 seconds
            autoSlideOffset = (autoSlideOffset + 1) % activeWallpaperList.size
        }
    }

    val currentIndex = (baseIndex + autoSlideOffset) % activeWallpaperList.size
    val currentWallpaper = activeWallpaperList[currentIndex]

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Full-screen Page Wallpaper with Smooth Crossfade
        Crossfade(
            targetState = currentWallpaper,
            animationSpec = tween(1200),
            label = "FullScreenWallpaperTransition",
            modifier = Modifier.fillMaxSize()
        ) { bgRes ->
            Image(
                painter = painterResource(id = bgRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // 2. Translucent protective backdrop wash that preserves background photo visibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (isDarkMode) {
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0F172A).copy(alpha = 0.48f),
                                Color(0xFF0B1120).copy(alpha = 0.62f),
                                Color(0xFF020617).copy(alpha = 0.75f)
                            )
                        )
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFFFFBEB).copy(alpha = 0.60f),
                                Color(0xFFFEF3C7).copy(alpha = 0.68f),
                                Color(0xFFF8FAFC).copy(alpha = 0.78f)
                            )
                        )
                    }
                )
        )

        // 3. Main Scaffold with transparent container
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { onOpenSettings() }
                                .testTag("app_header_title_row")
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = appTitle,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Başlığı Değiştir",
                                        tint = AmberPrimary.copy(alpha = 0.6f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Text(
                                    text = if (activeUser != null) "👤 ${activeUser.name} (${activeUser.role})" else appSubtitle,
                                    fontSize = 11.sp,
                                    color = AmberPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    },
                    actions = {
                        // Slayt Gösterisi / Aile Sineması
                        IconButton(
                            onClick = onOpenSlideshow,
                            modifier = Modifier.testTag("btn_slideshow")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Slideshow,
                                contentDescription = "Slayt Gösterisi",
                                tint = AmberPrimary
                            )
                        }

                        // Aile Macera Haritası
                        IconButton(
                            onClick = onOpenAdventureMap,
                            modifier = Modifier.testTag("btn_adventure_map")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Explore,
                                contentDescription = "Macera Haritası",
                                tint = IndigoSecondary
                            )
                        }

                        // Day / Night Mode Quick Switcher
                        IconButton(
                            onClick = onToggleDayNightMode,
                            modifier = Modifier.testTag("btn_theme_toggle_appbar")
                        ) {
                            Icon(
                                imageVector = if (isDarkMode) Icons.Default.WbSunny else Icons.Default.NightsStay,
                                contentDescription = if (isDarkMode) "Gündüz Moduna Geç" else "Gece Moduna Geç",
                                tint = if (isDarkMode) Color(0xFFFBBF24) else IndigoSecondary
                            )
                        }

                        // Search toggle
                        IconButton(
                            onClick = {
                                isSearchExpanded = !isSearchExpanded
                                if (!isSearchExpanded) onSearchQueryChange("")
                            },
                            modifier = Modifier.testTag("btn_search_toggle")
                        ) {
                            Icon(
                                imageVector = if (isSearchExpanded) Icons.Default.Clear else Icons.Default.Search,
                                contentDescription = "Ara"
                            )
                        }

                        // Grid / List toggle
                        IconButton(
                            onClick = onToggleGridMode,
                            modifier = Modifier.testTag("btn_grid_toggle")
                        ) {
                            Icon(
                                imageVector = if (isGridMode) Icons.Default.ViewAgenda else Icons.Default.GridView,
                                contentDescription = "Görünümü Değiştir"
                            )
                        }

                        // Insights Button
                        IconButton(
                            onClick = onOpenInsights,
                            modifier = Modifier.testTag("btn_insights")
                        ) {
                            Icon(
                                imageVector = Icons.Default.InsertChart,
                                contentDescription = "İstatistikler",
                                tint = AmberPrimary
                            )
                        }

                        // Quick Lock Button
                        IconButton(
                            onClick = onLockApp,
                            modifier = Modifier.testTag("btn_quick_lock")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Uygulamayı Kilitle",
                                tint = IndigoSecondary
                            )
                        }

                        // Settings Button
                        IconButton(
                            onClick = onOpenSettings,
                            modifier = Modifier.testTag("btn_settings")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Ayarlar"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                    )
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = onAddMemory,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Anı Ekle", fontWeight = FontWeight.Bold) },
                    containerColor = AmberPrimary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("fab_add_memory")
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // 4. Clean frameless Daily Note line directly below the top menu
                DailyNoteLine()

                // Search field when expanded
                AnimatedVisibility(visible = isSearchExpanded) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = onSearchQueryChange,
                            placeholder = { Text("Anılarda, mekanlarda veya etiketlerde ara...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { onSearchQueryChange("") }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Temizle")
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_search")
                        )
                    }
                }

                // Categories Filter Scroll
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { category ->
                        val isSelected = selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectCategory(category) },
                            label = {
                                Text(
                                    text = category,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AmberContainer,
                                selectedLabelColor = AmberOnContainer
                            ),
                            modifier = Modifier.testTag("filter_cat_$category")
                        )
                    }
                }

                // Mood Filter Badges Scroll
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ALL_MOODS.forEach { mood ->
                        val isSelected = selectedMood == mood.name
                        Surface(
                            onClick = { onSelectMood(mood.name) },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) mood.color.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 1.5.dp else 0.5.dp,
                                color = if (isSelected) mood.color else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.testTag("filter_mood_${mood.name}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = mood.emoji, fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = mood.name,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) mood.color else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Ready Suggested Family Tags Filter Scroll
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🏷️",
                        fontSize = 12.sp,
                        modifier = Modifier.padding(end = 2.dp)
                    )
                    TagProvider.suggestedTags.forEach { suggestedTag ->
                        val isSelected = searchQuery.equals(suggestedTag, ignoreCase = true) ||
                            searchQuery.equals("#$suggestedTag", ignoreCase = true)
                        Surface(
                            onClick = {
                                if (isSelected) {
                                    onSearchQueryChange("")
                                } else {
                                    onSearchQueryChange(suggestedTag)
                                    isSearchExpanded = true
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) AmberPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 1.2.dp else 0.5.dp,
                                color = if (isSelected) AmberPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                            ),
                            modifier = Modifier.testTag("filter_tag_$suggestedTag")
                        ) {
                            Text(
                                text = "#$suggestedTag",
                                fontSize = 11.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Quick Feature Strip (Gelişim, Hedefler, Sinema, Harita)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Çocuk Gelişimi (Zeyd & Esila)
                    Surface(
                        onClick = onOpenChildGrowth,
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                        modifier = Modifier.testTag("btn_quick_child_growth")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("👶", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Zeyd & Esila Gelişimi",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    // Aile Hedefleri (Bucket List)
                    Surface(
                        onClick = onOpenBucketList,
                        shape = RoundedCornerShape(12.dp),
                        color = AmberContainer.copy(alpha = 0.85f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AmberPrimary.copy(alpha = 0.4f)),
                        modifier = Modifier.testTag("btn_quick_bucket_list")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🏆", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Aile Hedefleri",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmberOnContainer
                            )
                        }
                    }

                    // Aile Sineması (Slayt Gösterisi)
                    Surface(
                        onClick = onOpenSlideshow,
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AmberPrimary.copy(alpha = 0.35f)),
                        modifier = Modifier.testTag("btn_quick_slideshow")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Slideshow,
                                contentDescription = null,
                                tint = AmberPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Aile Sineması",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Macera Haritası
                    Surface(
                        onClick = onOpenAdventureMap,
                        shape = RoundedCornerShape(12.dp),
                        color = IndigoContainer.copy(alpha = 0.85f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, IndigoSecondary.copy(alpha = 0.4f)),
                        modifier = Modifier.testTag("btn_quick_map")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Explore,
                                contentDescription = null,
                                tint = IndigoSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Macera Haritası",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = IndigoOnContainer
                            )
                        }
                    }

                    // Eşinle Paylaş & Senkronize Et
                    Surface(
                        onClick = onOpenSync,
                        shape = RoundedCornerShape(12.dp),
                        color = AmberContainer.copy(alpha = 0.95f),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, AmberPrimary),
                        modifier = Modifier.testTag("btn_quick_sync")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = null,
                                tint = AmberPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "📲 Eşinle Paylaş",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmberOnContainer
                            )
                        }
                    }
                }

                // ⏳ Tarihte Bugün (Zaman Tüneli & Nostalji Kartı)
                val onThisDayResult = remember(allMemories) {
                    OnThisDayProvider.getOnThisDayMemory(allMemories)
                }
                if (onThisDayResult != null && selectedCategory == "Tümü" && searchQuery.isBlank() && selectedMood == null) {
                    OnThisDayCard(
                        result = onThisDayResult,
                        onOpenMemory = onSelectMemory,
                        onStartSlideshow = onOpenSlideshow
                    )
                }

                // Content: Empty State vs Memories List / Grid
                if (memories.isEmpty()) {
                    EmptyStateView(
                        isFilterActive = searchQuery.isNotBlank() || selectedCategory != "Tümü" || selectedMood != null,
                        onAddFirstMemory = onAddMemory
                    )
                } else {
                    if (isGridMode) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            memories.forEach { memory ->
                                item(key = memory.id) {
                                    MemoryGridCard(
                                        memory = memory,
                                        onClick = { onSelectMemory(memory) },
                                        onToggleFavorite = { onToggleFavorite(memory) },
                                        onTagClick = { tag ->
                                            onSearchQueryChange(tag)
                                            isSearchExpanded = true
                                        }
                                    )
                                }
                            }
                            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                                Spacer(modifier = Modifier.height(64.dp))
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // Flashback Highlight
                            if (selectedCategory == "Tümü" && searchQuery.isBlank() && selectedMood == null && allMemories.isNotEmpty()) {
                                item {
                                    FlashbackCard(
                                        memory = allMemories.last(),
                                        onClick = { onSelectMemory(allMemories.last()) }
                                    )
                                }
                            }

                            memories.forEach { memory ->
                                item(key = memory.id) {
                                    MemoryListCard(
                                        memory = memory,
                                        onClick = { onSelectMemory(memory) },
                                        onToggleFavorite = { onToggleFavorite(memory) },
                                        onTagClick = { tag ->
                                            onSearchQueryChange(tag)
                                            isSearchExpanded = true
                                        }
                                    )
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(72.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FlashbackCard(
    memory: MemoryEntry,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = IndigoContainer),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = IndigoSecondary,
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Günün Öne Çıkan Hatırası",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = IndigoSecondary
                )
                Text(
                    text = memory.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = IndigoOnContainer,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${ImageUtils.formatShortDate(memory.timestamp)} • ${memory.location.ifBlank { memory.mood }}",
                    fontSize = 12.sp,
                    color = IndigoOnContainer.copy(alpha = 0.75f)
                )
            }
        }
    }
}

@Composable
fun MemoryListCard(
    memory: MemoryEntry,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onTagClick: (String) -> Unit = {}
) {
    val moodOption = ALL_MOODS.find { it.name.equals(memory.mood, ignoreCase = true) }
        ?: ALL_MOODS.first()

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("memory_card_${memory.id}")
    ) {
        Column {
            // Photo Cover (if present)
            if (memory.photoPath != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    AsyncImage(
                        model = File(memory.photoPath),
                        contentDescription = memory.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .then(if (memory.isSecretLocked) Modifier.blur(14.dp) else Modifier)
                    )

                    // Top row indicators
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Mood Badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(moodOption.emoji, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = moodOption.name,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = moodOption.color
                                )
                            }
                        }

                        // Favorite Button
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            IconButton(onClick = onToggleFavorite, modifier = Modifier.size(36.dp)) {
                                Icon(
                                    imageVector = if (memory.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favori",
                                    tint = if (memory.isFavorite) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Info Content
            Column(modifier = Modifier.padding(14.dp)) {
                if (memory.photoPath == null) {
                    // Header when no photo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = moodOption.color.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(moodOption.emoji, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = moodOption.name,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = moodOption.color
                                )
                            }
                        }

                        IconButton(onClick = onToggleFavorite, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = if (memory.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favori",
                                tint = if (memory.isFavorite) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Text(
                    text = memory.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Child Age Badges (if memory mentions Zeyd or Esila)
                val detectedChildren = remember(memory) {
                    com.example.util.ChildGrowthHelper.detectChildrenIn(memory)
                }
                if (detectedChildren.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        detectedChildren.forEach { child ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (child.childName == "Zeyd") AmberContainer else IndigoContainer,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (child.childName == "Zeyd") AmberPrimary.copy(alpha = 0.3f) else IndigoSecondary.copy(alpha = 0.3f)
                                )
                            ) {
                                Text(
                                    text = "${child.emoji} ${child.childName} • ${child.ageString}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (child.childName == "Zeyd") AmberOnContainer else IndigoOnContainer,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                if (memory.story.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = memory.story,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp,
                        modifier = if (memory.isSecretLocked) Modifier.blur(6.dp) else Modifier
                    )
                }

                // Tags Display on Card
                if (memory.tags.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        memory.tags.split(",", " ", ";")
                            .map { it.trim() }
                            .filter { it.isNotBlank() }
                            .forEach { tagItem ->
                                val cleanTag = tagItem.removePrefix("#")
                                Surface(
                                    onClick = { onTagClick(cleanTag) },
                                    shape = RoundedCornerShape(8.dp),
                                    color = AmberContainer.copy(alpha = 0.6f)
                                ) {
                                    Text(
                                        text = "#$cleanTag",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = AmberOnContainer,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Footer: Date & Location
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = AmberPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = ImageUtils.formatShortDate(memory.timestamp),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (memory.location.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = memory.location,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MemoryGridCard(
    memory: MemoryEntry,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onTagClick: (String) -> Unit = {}
) {
    val moodOption = ALL_MOODS.find { it.name.equals(memory.mood, ignoreCase = true) }
        ?: ALL_MOODS.first()

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("memory_grid_card_${memory.id}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (memory.photoPath != null) {
                    AsyncImage(
                        model = File(memory.photoPath),
                        contentDescription = memory.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .then(if (memory.isSecretLocked) Modifier.blur(14.dp) else Modifier)
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(moodOption.emoji, fontSize = 32.sp)
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(30.dp)
                ) {
                    IconButton(onClick = onToggleFavorite, modifier = Modifier.size(30.dp)) {
                        Icon(
                            imageVector = if (memory.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favori",
                            tint = if (memory.isFavorite) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = memory.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = ImageUtils.formatShortDate(memory.timestamp),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (memory.tags.isNotBlank()) {
                        val firstTag = memory.tags.split(",", " ", ";").firstOrNull { it.isNotBlank() }?.removePrefix("#") ?: ""
                        if (firstTag.isNotBlank()) {
                            Text(
                                text = "#$firstTag",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmberPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.clickable { onTagClick(firstTag) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateView(
    isFilterActive: Boolean,
    onAddFirstMemory: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Aesthetic illustration banner
        Image(
            painter = painterResource(R.drawable.banner_memories_1787293319077),
            contentDescription = "Anı Rehberi",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(width = 220.dp, height = 130.dp)
                .clip(RoundedCornerShape(16.dp))
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = if (isFilterActive) "Eşleşen Anı Bulunamadı" else "Henüz Anı Eklenmedi",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = if (isFilterActive) "Farklı bir arama terimi veya kategori deneyin."
            else "Fotoğraflarını yükle, hikayeni yaz ve tüm güzel anlarını güvenle sakla.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        if (!isFilterActive) {
            Spacer(modifier = Modifier.height(16.dp))
            androidx.compose.material3.Button(
                onClick = onAddFirstMemory,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = AmberPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("İlk Anını Kaydet", fontWeight = FontWeight.Bold)
            }
        }
    }
}
