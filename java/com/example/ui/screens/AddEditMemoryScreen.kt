package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.MemoryEntry
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberOnContainer
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.MoodExcited
import com.example.ui.theme.MoodGrateful
import com.example.ui.theme.MoodHappy
import com.example.ui.theme.MoodLove
import com.example.ui.theme.MoodNostalgic
import com.example.ui.theme.MoodPeaceful
import com.example.ui.theme.MoodProud
import com.example.util.ImageUtils
import com.example.util.LoveAndFamilyQuotes
import com.example.util.QuoteCategory
import com.example.util.TagProvider
import java.io.File

data class MoodOption(val name: String, val emoji: String, val color: Color)

val ALL_MOODS = listOf(
    MoodOption("Mutlu", "😊", MoodHappy),
    MoodOption("Huzurlu", "🌿", MoodPeaceful),
    MoodOption("Sevgi", "❤️", MoodLove),
    MoodOption("Heyecanlı", "✨", MoodExcited),
    MoodOption("Nostaljik", "🌅", MoodNostalgic),
    MoodOption("Minnettar", "🌸", MoodGrateful),
    MoodOption("Gururlu", "🏆", MoodProud)
)

val ALL_CATEGORIES = listOf(
    "Genel",
    "Seyahat",
    "Aile & Dostlar",
    "Özel Günler",
    "Doğa & Keşif",
    "Kişisel & Başarı"
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditMemoryScreen(
    memoryToEdit: MemoryEntry?,
    availableCategories: List<String> = ALL_CATEGORIES,
    onSave: (
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
    ) -> Unit,
    onSaveImage: (Uri) -> String?,
    onCancel: () -> Unit
) {
    val displayCategories = remember(availableCategories) {
        val filtered = availableCategories.filter { it != "Tümü" && it != "Favoriler" }
        if (filtered.isEmpty()) ALL_CATEGORIES else filtered
    }
    var title by remember { mutableStateOf(memoryToEdit?.title ?: "") }
    var story by remember { mutableStateOf(memoryToEdit?.story ?: "") }
    var photoPath by remember { mutableStateOf(memoryToEdit?.photoPath) }
    var location by remember { mutableStateOf(memoryToEdit?.location ?: "") }
    var selectedMood by remember { mutableStateOf(memoryToEdit?.mood ?: "Mutlu") }
    var selectedCategory by remember { mutableStateOf(memoryToEdit?.category ?: displayCategories.firstOrNull() ?: "Genel") }
    var timestamp by remember { mutableLongStateOf(memoryToEdit?.timestamp ?: System.currentTimeMillis()) }
    var isFavorite by remember { mutableStateOf(memoryToEdit?.isFavorite ?: false) }
    var isSecretLocked by remember { mutableStateOf(memoryToEdit?.isSecretLocked ?: false) }
    var tags by remember { mutableStateOf(memoryToEdit?.tags ?: "") }

    var showDatePicker by remember { mutableStateOf(false) }
    var titleError by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            val savedPath = onSaveImage(it)
            if (savedPath != null) {
                photoPath = savedPath
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (memoryToEdit != null) "Anıyı Düzenle" else "Yeni Anı Ekle",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onCancel,
                        modifier = Modifier.testTag("btn_close_add_edit")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { isFavorite = !isFavorite },
                        modifier = Modifier.testTag("btn_toggle_favorite_edit")
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorilere Ekle",
                            tint = if (isFavorite) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Photo Section
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (photoPath != null) 240.dp else 150.dp)
                    .clickable {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                    .testTag("card_photo_picker")
            ) {
                if (photoPath != null) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = File(photoPath!!),
                            contentDescription = "Anı Fotoğrafı",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                IconButton(
                                    onClick = { photoPath = null },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Fotoğrafı Kaldır",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = "Fotoğraf Ekle",
                            tint = AmberPrimary,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Fotoğraf Yüklemek İçin Dokunun",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Galeriden en güzel anı fotoğrafınızı seçin",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Title Field
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    if (it.isNotBlank()) titleError = false
                },
                label = { Text("Anı Başlığı *") },
                placeholder = { Text("Örn: Ege'de Gün Batımı ve Sohbet") },
                isError = titleError,
                supportingText = {
                    if (titleError) {
                        Text("Lütfen anınız için bir başlık yazın", color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_title"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Date Picker Row
            Surface(
                onClick = { showDatePicker = true },
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Tarih Seç",
                            tint = AmberPrimary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Anı Tarihi",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = ImageUtils.formatDate(timestamp),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    Text(
                        text = "Değiştir",
                        color = AmberPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Mood Selector
            Column {
                Text(
                    text = "Bu Anda Nasıl Hissettin?",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ALL_MOODS.forEach { mood ->
                        val isSelected = selectedMood == mood.name
                        Surface(
                            onClick = { selectedMood = mood.name },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) mood.color.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) mood.color else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.testTag("mood_${mood.name}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = mood.emoji, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = mood.name,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) mood.color else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Category Selector
            Column {
                Text(
                    text = "Kategori",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    displayCategories.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AmberContainer,
                                selectedLabelColor = AmberOnContainer
                            ),
                            modifier = Modifier.testTag("cat_$cat")
                        )
                    }
                }
            }

            // Location Field
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Mekan / Konum") },
                placeholder = { Text("Örn: İzmir, Çeşme Marina") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Konum",
                        tint = AmberPrimary
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_location"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Story / Notes Area
            OutlinedTextField(
                value = story,
                onValueChange = { story = it },
                label = { Text("Anı Hikayesi & Detaylar") },
                placeholder = { Text("Bu anı unutulmaz kılan neydi? Neler hissettin, kiminleydin, neler konuşuldu?...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .testTag("input_story"),
                shape = RoundedCornerShape(12.dp)
            )

            // Subtle Quote / Inspiration note helper
            var inspirationQuoteIndex by remember { mutableStateOf((0..LoveAndFamilyQuotes.allQuotes.size - 1).random()) }
            val currentInspiration = LoveAndFamilyQuotes.getQuoteForIndex(inspirationQuoteIndex)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "💡 \"${currentInspiration.text}\"",
                    fontSize = 11.5.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            val quoteText = "\n\n\"${currentInspiration.text}\""
                            story = if (story.isBlank()) "\"${currentInspiration.text}\"" else "$story$quoteText"
                        }
                )
                TextButton(
                    onClick = {
                        inspirationQuoteIndex = (inspirationQuoteIndex + 1) % LoveAndFamilyQuotes.allQuotes.size
                    },
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                ) {
                    Text("Değiştir", fontSize = 10.5.sp)
                }
            }

            // Tags
            OutlinedTextField(
                value = tags,
                onValueChange = { tags = it },
                label = { Text("Etiketler (Virgülle ayırın veya aşağıdan seçin)") },
                placeholder = { Text("#Zeyd, #Esila, #Köy, #Gezi, #İstanbul...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Tag,
                        contentDescription = "Etiketler",
                        tint = AmberPrimary
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_tags"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Suggested Ready Tags Chips Row
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "🏷️ Hazır Etiketler (Dokunarak Ekle/Çıkar):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TagProvider.suggestedTags.forEach { suggestedTag ->
                        val isSelected = TagProvider.isTagPresent(tags, suggestedTag)
                        Surface(
                            onClick = {
                                tags = TagProvider.toggleTagInString(tags, suggestedTag)
                            },
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) AmberPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 1.dp else 0.5.dp,
                                color = if (isSelected) AmberPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.testTag("suggested_tag_$suggestedTag")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text(
                                    text = "#$suggestedTag",
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Special Secrecy Switch
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSecretLocked) AmberContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSecretLocked) AmberPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (isSecretLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = null,
                            tint = if (isSecretLocked) AmberPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Ekstra Gizli Anı",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Önizlemede içeriği bulanıklaştırılır ve gizlenir",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = isSecretLocked,
                        onCheckedChange = { isSecretLocked = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AmberPrimary,
                            checkedTrackColor = AmberContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                ) {
                    Text("Vazgeç")
                }

                Button(
                    onClick = {
                        if (title.isBlank()) {
                            titleError = true
                        } else {
                            onSave(
                                title,
                                story,
                                photoPath,
                                location,
                                selectedMood,
                                selectedCategory,
                                timestamp,
                                isFavorite,
                                isSecretLocked,
                                tags
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("btn_save_memory")
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Anıyı Kaydet", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Material 3 Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = timestamp
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            timestamp = it
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Tamam", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("İptal")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
