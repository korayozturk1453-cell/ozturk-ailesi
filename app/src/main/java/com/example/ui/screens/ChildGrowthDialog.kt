package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Cake
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import coil.compose.AsyncImage
import com.example.data.model.ChildMilestone
import com.example.data.model.MemoryEntry
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberOnContainer
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.IndigoContainer
import com.example.ui.theme.IndigoOnContainer
import com.example.ui.theme.IndigoSecondary
import com.example.util.ChildGrowthHelper
import com.example.util.ImageUtils
import java.io.File

@Composable
fun ChildGrowthDialog(
    milestones: List<ChildMilestone>,
    memories: List<MemoryEntry>,
    onDismiss: () -> Unit,
    onToggleMilestone: (ChildMilestone) -> Unit,
    onAddMilestone: (childName: String, title: String, description: String, iconEmoji: String) -> Unit,
    onDeleteMilestone: (ChildMilestone) -> Unit,
    onSelectMemory: (MemoryEntry) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Zeyd, 1: Esila, 2: Tüm Dönüm Noktaları
    var showAddDialog by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .testTag("dialog_child_growth"),
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
                                Text("👶", fontSize = 22.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Çocuk Gelişimi & Zaman Çizelgesi",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Zeyd & Esila'nın Büyüme Hikayesi",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_child_growth")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Kapat",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = AmberPrimary
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "👶 Zeyd",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "🌸 Esila",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Text(
                                "🌟 Dönüm Noktaları",
                                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    )
                }

                // Tab Content
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (selectedTab) {
                        0 -> ChildProfileView(
                            childName = "Zeyd",
                            childEmoji = "👶",
                            birthYear = ChildGrowthHelper.ZEYD.defaultBirthYear,
                            birthMonth = ChildGrowthHelper.ZEYD.defaultBirthMonth,
                            birthDay = ChildGrowthHelper.ZEYD.defaultBirthDay,
                            milestones = milestones.filter { it.childName.equals("Zeyd", ignoreCase = true) },
                            memories = memories.filter {
                                val fullText = "${it.title} ${it.story} ${it.tags} ${it.category}".lowercase()
                                fullText.contains("zeyd")
                            },
                            onToggleMilestone = onToggleMilestone,
                            onDeleteMilestone = onDeleteMilestone,
                            onAddMilestoneClick = { showAddDialog = true },
                            onSelectMemory = onSelectMemory
                        )
                        1 -> ChildProfileView(
                            childName = "Esila",
                            childEmoji = "🌸",
                            birthYear = ChildGrowthHelper.ESILA.defaultBirthYear,
                            birthMonth = ChildGrowthHelper.ESILA.defaultBirthMonth,
                            birthDay = ChildGrowthHelper.ESILA.defaultBirthDay,
                            milestones = milestones.filter { it.childName.equals("Esila", ignoreCase = true) },
                            memories = memories.filter {
                                val fullText = "${it.title} ${it.story} ${it.tags} ${it.category}".lowercase()
                                fullText.contains("esila")
                            },
                            onToggleMilestone = onToggleMilestone,
                            onDeleteMilestone = onDeleteMilestone,
                            onAddMilestoneClick = { showAddDialog = true },
                            onSelectMemory = onSelectMemory
                        )
                        2 -> AllMilestonesView(
                            milestones = milestones,
                            onToggleMilestone = onToggleMilestone,
                            onDeleteMilestone = onDeleteMilestone,
                            onAddMilestoneClick = { showAddDialog = true }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddMilestoneDialog(
            defaultChildName = if (selectedTab == 1) "Esila" else "Zeyd",
            onDismiss = { showAddDialog = false },
            onConfirm = { childName, title, description, iconEmoji ->
                onAddMilestone(childName, title, description, iconEmoji)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun ChildProfileView(
    childName: String,
    childEmoji: String,
    birthYear: Int,
    birthMonth: Int,
    birthDay: Int,
    milestones: List<ChildMilestone>,
    memories: List<MemoryEntry>,
    onToggleMilestone: (ChildMilestone) -> Unit,
    onDeleteMilestone: (ChildMilestone) -> Unit,
    onAddMilestoneClick: () -> Unit,
    onSelectMemory: (MemoryEntry) -> Unit
) {
    val birthTimestamp = remember(birthYear, birthMonth, birthDay) {
        ChildGrowthHelper.getTimestampFromDate(birthYear, birthMonth, birthDay)
    }
    val currentAge = remember(birthTimestamp) {
        ChildGrowthHelper.calculateAge(birthTimestamp, System.currentTimeMillis())
    }

    val completedMilestonesCount = milestones.count { it.isCompleted }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Child Summary Hero Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (childName == "Zeyd") AmberContainer else IndigoContainer
                ),
                border = BorderStroke(
                    1.dp,
                    if (childName == "Zeyd") AmberPrimary.copy(alpha = 0.3f) else IndigoSecondary.copy(alpha = 0.3f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        modifier = Modifier.size(60.dp),
                        border = BorderStroke(2.dp, if (childName == "Zeyd") AmberPrimary else IndigoSecondary)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(childEmoji, fontSize = 32.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "$childName Öztürk",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (childName == "Zeyd") AmberOnContainer else IndigoOnContainer
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Cake,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp),
                                tint = if (childName == "Zeyd") AmberPrimary else IndigoSecondary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Şu an $currentAge",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (childName == "Zeyd") AmberPrimary else IndigoSecondary
                            )
                        }
                        Text(
                            text = "$completedMilestonesCount/${milestones.size} Dönüm Noktası • ${memories.size} Hatıra",
                            fontSize = 12.sp,
                            color = (if (childName == "Zeyd") AmberOnContainer else IndigoOnContainer).copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Section: Dönüm Noktaları (Milestones Checklist)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🏆 $childName'in Dönüm Noktaları",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                TextButton(onClick = onAddMilestoneClick) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Yeni Ekle", fontSize = 13.sp)
                }
            }
        }

        if (milestones.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Henüz dönüm noktası eklenmemiş.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(milestones, key = { it.id }) { milestone ->
                MilestoneItemCard(
                    milestone = milestone,
                    onToggle = { onToggleMilestone(milestone) },
                    onDelete = { onDeleteMilestone(milestone) }
                )
            }
        }

        // Section: Anılardaki Yaş Zaman Çizelgesi
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "📸 $childName'in Fotoğraflı Zaman Çizelgesi",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (memories.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$childName etiketli veya isimli hatıra eklendiğinde burada yaşıyla birlikte listelenir.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(memories, key = { it.id }) { mem ->
                val ageAtTime = ChildGrowthHelper.calculateAge(birthTimestamp, mem.timestamp)
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectMemory(mem) }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (mem.photoPath != null) {
                            AsyncImage(
                                model = File(mem.photoPath),
                                contentDescription = mem.title,
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(10.dp)),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        } else {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = AmberContainer,
                                modifier = Modifier.size(54.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(childEmoji, fontSize = 24.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = AmberPrimary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "$childEmoji $ageAtTime",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = mem.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${ImageUtils.formatShortDate(mem.timestamp)} • ${mem.location.ifBlank { mem.mood }}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MilestoneItemCard(
    milestone: ChildMilestone,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val checkColor by animateColorAsState(
        targetValue = if (milestone.isCompleted) Color(0xFF2E7D32) else MaterialTheme.colorScheme.outline
    )

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (milestone.isCompleted) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
            else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            if (milestone.isCompleted) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
            else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onToggle,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (milestone.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = if (milestone.isCompleted) "Tamamlandı" else "Tamamla",
                    tint = checkColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(milestone.iconEmoji, fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = milestone.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (milestone.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
                if (milestone.description.isNotBlank()) {
                    Text(
                        text = milestone.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (milestone.isCompleted && milestone.completedDate != null) {
                    Text(
                        text = "🎉 Gerçekleşti: ${ImageUtils.formatShortDate(milestone.completedDate)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
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
fun AllMilestonesView(
    milestones: List<ChildMilestone>,
    onToggleMilestone: (ChildMilestone) -> Unit,
    onDeleteMilestone: (ChildMilestone) -> Unit,
    onAddMilestoneClick: () -> Unit
) {
    val completed = milestones.count { it.isCompleted }
    val total = milestones.size

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AmberContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = AmberPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Ailenin Büyüme Başarıları",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberOnContainer
                        )
                        Text(
                            text = "$completed / $total Dönüm Noktası Tamamlandı",
                            fontSize = 13.sp,
                            color = AmberOnContainer.copy(alpha = 0.8f)
                        )
                    }
                    Button(
                        onClick = onAddMilestoneClick,
                        colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Ekle", fontSize = 12.sp, color = Color.White)
                    }
                }
            }
        }

        items(milestones, key = { it.id }) { milestone ->
            MilestoneItemCard(
                milestone = milestone,
                onToggle = { onToggleMilestone(milestone) },
                onDelete = { onDeleteMilestone(milestone) }
            )
        }
    }
}

@Composable
fun AddMilestoneDialog(
    defaultChildName: String,
    onDismiss: () -> Unit,
    onConfirm: (childName: String, title: String, description: String, iconEmoji: String) -> Unit
) {
    var childName by remember { mutableStateOf(defaultChildName) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf("🌟") }

    val emojis = listOf("🌟", "👣", "💬", "🦷", "🚲", "🎒", "🧸", "🏊", "🎨", "🎂", "🎈", "❤️")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yeni Dönüm Noktası Ekle", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Child Choice
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Zeyd", "Esila", "Ortak").forEach { name ->
                        Surface(
                            onClick = { childName = name },
                            shape = RoundedCornerShape(10.dp),
                            color = if (childName == name) AmberPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, if (childName == name) AmberPrimary else Color.Transparent)
                        ) {
                            Text(
                                text = name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (childName == name) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Emoji Picker Row
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
                    label = { Text("Dönüm Noktası Başlığı") },
                    placeholder = { Text("Örn: İlk Bisiklet Turu, İlk Kelime") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Açıklama / Tatlı Bir Not") },
                    placeholder = { Text("O an neler yaşandı, kimler vardı?") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(childName, title.trim(), description.trim(), selectedEmoji)
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary)
            ) {
                Text("Kaydet", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}
