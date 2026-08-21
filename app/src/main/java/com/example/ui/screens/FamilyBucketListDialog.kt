package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.FamilyBucketItem
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberOnContainer
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.IndigoContainer
import com.example.ui.theme.IndigoOnContainer
import com.example.ui.theme.IndigoSecondary
import com.example.util.ImageUtils

@Composable
fun FamilyBucketListDialog(
    bucketItems: List<FamilyBucketItem>,
    onDismiss: () -> Unit,
    onToggleBucketItem: (FamilyBucketItem) -> Unit,
    onAddBucketItem: (title: String, category: String, description: String, targetDate: String, iconEmoji: String) -> Unit,
    onDeleteBucketItem: (FamilyBucketItem) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("Tümü") }
    var showAddDialog by remember { mutableStateOf(false) }

    val categories = listOf("Tümü", "Seyahat & Gezi", "Doğa & Kamp", "Etkinlik & Eğlence", "Gelenek & Aile", "Tamamlananlar")

    val completedCount = bucketItems.count { it.isCompleted }
    val totalCount = bucketItems.size
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "progress")

    val filteredItems = remember(bucketItems, selectedCategory) {
        when (selectedCategory) {
            "Tümü" -> bucketItems
            "Tamamlananlar" -> bucketItems.filter { it.isCompleted }
            else -> bucketItems.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .testTag("dialog_bucket_list"),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.background,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = AmberContainer,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🏆", fontSize = 22.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Aile Hedefleri & Hayal Listesi",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Birlikte Gerçekleştireceğimiz Güzel Anlar",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_bucket_list")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Kapat",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Progress Banner Card
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = AmberContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = AmberPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (progress >= 1f) "Muhteşem! Tüm Hedefler Başarıldı 🎉" else "$completedCount / $totalCount Hayal Gerçekleşti",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberOnContainer
                                )
                            }
                            Text(
                                text = "%${(progress * 100).toInt()}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = AmberPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = AmberPrimary,
                            trackColor = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }

                // Category Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AmberPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                // List Section Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hedefler (${filteredItems.size})",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Button(
                        onClick = { showAddDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("btn_add_bucket_item")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Yeni Hedef Ekle", fontSize = 12.sp, color = Color.White)
                    }
                }

                // Items List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (filteredItems.isEmpty()) {
                        item {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Bu kategoride henüz hedef yok.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    } else {
                        items(filteredItems, key = { it.id }) { item ->
                            BucketListItemCard(
                                item = item,
                                onToggle = { onToggleBucketItem(item) },
                                onDelete = { onDeleteBucketItem(item) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddBucketItemDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, category, description, targetDate, iconEmoji ->
                onAddBucketItem(title, category, description, targetDate, iconEmoji)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun BucketListItemCard(
    item: FamilyBucketItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val checkColor by animateColorAsState(
        targetValue = if (item.isCompleted) Color(0xFF2E7D32) else MaterialTheme.colorScheme.outline
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isCompleted) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
            else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            if (item.isCompleted) MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
            else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onToggle,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (item.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = if (item.isCompleted) "Tamamlandı" else "Tamamla",
                    tint = checkColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(item.iconEmoji, fontSize = 20.sp)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = AmberContainer.copy(alpha = 0.7f)
                    ) {
                        Text(
                            text = item.category,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberOnContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    if (item.targetDate.isNotBlank()) {
                        Text(
                            text = "🗓️ ${item.targetDate}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = item.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (item.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )

                if (item.description.isNotBlank()) {
                    Text(
                        text = item.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (item.isCompleted && item.completedDate != null) {
                    Text(
                        text = "🎉 Başarıldı: ${ImageUtils.formatShortDate(item.completedDate)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Sil",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun AddBucketItemDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, category: String, description: String, targetDate: String, iconEmoji: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Seyahat & Gezi") }
    var description by remember { mutableStateOf("") }
    var targetDate by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf("✨") }

    val categories = listOf("Seyahat & Gezi", "Doğa & Kamp", "Etkinlik & Eğlence", "Gelenek & Aile", "Ev & Yaşam")
    val emojis = listOf("✨", "🎈", "⛺", "🌱", "🌲", "🧩", "🍎", "🚲", "🏖️", "🎡", "🎨", "🚀", "🏔️", "❤️")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yeni Aile Hedefi / Hayali", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Category Choice
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        Surface(
                            onClick = { category = cat },
                            shape = RoundedCornerShape(8.dp),
                            color = if (category == cat) AmberPrimary else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = cat,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (category == cat) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                // Emoji Choice
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    emojis.forEach { emoji ->
                        Surface(
                            onClick = { selectedEmoji = emoji },
                            shape = CircleShape,
                            color = if (selectedEmoji == emoji) AmberContainer else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, if (selectedEmoji == emoji) AmberPrimary else Color.Transparent),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(emoji, fontSize = 18.sp)
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Hedef / Hayal Başlığı") },
                    placeholder = { Text("Örn: Karadeniz Yaylalarını Gezmek") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = targetDate,
                    onValueChange = { targetDate = it },
                    label = { Text("Hedef Zaman (Opsiyonel)") },
                    placeholder = { Text("Örn: Bu Yaz, 2026 İlkbahar") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Açıklama / Detay") },
                    placeholder = { Text("Bu hayali gerçekleştirdiğimizde neler yapacağız?") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title.trim(), category, description.trim(), targetDate.trim(), selectedEmoji)
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary)
            ) {
                Text("Listeye Ekle", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}
