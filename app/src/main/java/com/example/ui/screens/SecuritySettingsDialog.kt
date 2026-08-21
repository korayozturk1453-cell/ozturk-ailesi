package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.security.FamilyMember
import com.example.data.security.SecurityManager
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberOnContainer
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.IndigoContainer
import com.example.ui.theme.IndigoOnContainer
import com.example.ui.theme.IndigoSecondary
import com.example.util.WallpaperProvider

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SecuritySettingsDialog(
    securityManager: SecurityManager,
    onDismiss: () -> Unit,
    onLockNow: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("🏷️ Başlıklar", "🎨 Tema & Fon", "👥 İzinler", "🔒 Güvenlik")

    val appTitle by securityManager.appTitle.collectAsStateWithLifecycle()
    val appSubtitle by securityManager.appSubtitle.collectAsStateWithLifecycle()
    val categories by securityManager.categories.collectAsStateWithLifecycle()
    val allowedMembers by securityManager.allowedMembers.collectAsStateWithLifecycle()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = AmberPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Özelleştirme & Aile İzinleri", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Tab Selection
                SecondaryTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Box(modifier = Modifier.height(380.dp)) {
                    when (selectedTab) {
                        0 -> TitleCustomizationTab(
                            currentTitle = appTitle,
                            currentSubtitle = appSubtitle,
                            categories = categories,
                            onSaveTitle = { newTitle -> securityManager.setAppTitle(newTitle) },
                            onSaveSubtitle = { newSub -> securityManager.setAppSubtitle(newSub) },
                            onAddCategory = { newCat -> securityManager.addCategory(newCat) },
                            onRemoveCategory = { cat -> securityManager.removeCategory(cat) }
                        )
                        1 -> ThemeCustomizationTab(
                            securityManager = securityManager
                        )
                        2 -> FamilyAccessControlTab(
                            allowedMembers = allowedMembers,
                            onAddMember = { name, role, pin ->
                                securityManager.addFamilyMember(name, role, pin)
                            },
                            onRemoveMember = { id ->
                                securityManager.removeFamilyMember(id)
                            }
                        )
                        3 -> SecurityAndLockTab(
                            securityManager = securityManager,
                            onDismiss = onDismiss,
                            onLockNow = onLockNow
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary)
            ) {
                Text("Tamam", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun ThemeCustomizationTab(
    securityManager: SecurityManager
) {
    val isDarkMode by securityManager.isDarkMode.collectAsStateWithLifecycle()
    val dayWallpaperIndex by securityManager.dayWallpaperIndex.collectAsStateWithLifecycle()
    val nightWallpaperIndex by securityManager.nightWallpaperIndex.collectAsStateWithLifecycle()

    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Text(
                text = "Görünüm Modu (Gece / Gündüz)",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    onClick = { securityManager.setDarkMode(false) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (!isDarkMode) AmberContainer else MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(if (!isDarkMode) 2.dp else 0.5.dp, if (!isDarkMode) AmberPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_theme_select_day")
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("☀️", fontSize = 24.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Gündüz Modu", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Aydınlık & Sıcak", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Surface(
                    onClick = { securityManager.setDarkMode(true) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isDarkMode) IndigoContainer else MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(if (isDarkMode) 2.dp else 0.5.dp, if (isDarkMode) IndigoSecondary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_theme_select_night")
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🌙", fontSize = 24.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Gece Modu", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Koyu & Gece Fonu", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        item {
            Text(
                text = "Gündüz Arka Fon Fotoğrafları",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                com.example.util.WallpaperProvider.dayWallpapers.forEachIndexed { index, resId ->
                    Surface(
                        onClick = {
                            securityManager.setDarkMode(false)
                            securityManager.setDayWallpaperIndex(index)
                        },
                        shape = RoundedCornerShape(10.dp),
                        color = if (dayWallpaperIndex == index) AmberContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(if (dayWallpaperIndex == index) 1.5.dp else 0.5.dp, if (dayWallpaperIndex == index) AmberPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = resId),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(width = 56.dp, height = 36.dp)
                                    .clip(RoundedCornerShape(6.dp))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = WallpaperProvider.dayWallpaperNames.getOrElse(index) { "Gündüz Fonu ${index + 1}" },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(text = "Gündüz teması arkaplanı", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (dayWallpaperIndex == index) {
                                Icon(Icons.Default.Check, contentDescription = "Seçili", tint = AmberPrimary, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Gece Arka Fon Fotoğrafları",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                WallpaperProvider.nightWallpapers.forEachIndexed { index, resId ->
                    Surface(
                        onClick = {
                            securityManager.setDarkMode(true)
                            securityManager.setNightWallpaperIndex(index)
                        },
                        shape = RoundedCornerShape(10.dp),
                        color = if (nightWallpaperIndex == index) IndigoContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(if (nightWallpaperIndex == index) 1.5.dp else 0.5.dp, if (nightWallpaperIndex == index) IndigoSecondary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = resId),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(width = 56.dp, height = 36.dp)
                                    .clip(RoundedCornerShape(6.dp))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = WallpaperProvider.nightWallpaperNames.getOrElse(index) { "Gece Fonu ${index + 1}" },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(text = "Gece teması arkaplanı", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (nightWallpaperIndex == index) {
                                Icon(Icons.Default.Check, contentDescription = "Seçili", tint = IndigoSecondary, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TitleCustomizationTab(
    currentTitle: String,
    currentSubtitle: String,
    categories: List<String>,
    onSaveTitle: (String) -> Unit,
    onSaveSubtitle: (String) -> Unit,
    onAddCategory: (String) -> Boolean,
    onRemoveCategory: (String) -> Boolean
) {
    var titleInput by remember { mutableStateOf(currentTitle) }
    var subtitleInput by remember { mutableStateOf(currentSubtitle) }
    var titleSavedMessage by remember { mutableStateOf(false) }

    var newCategoryInput by remember { mutableStateOf("") }
    var categoryError by remember { mutableStateOf<String?>(null) }

    val presetTitles = listOf("Öztürk Ailesi", "Öztürk Hatıra Defteri", "Bizim Aile Köşesi", "Öztürk Albümü")

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text(
                text = "Ana Başlık & Aile İsmi",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = titleInput,
                onValueChange = {
                    titleInput = it
                    titleSavedMessage = false
                },
                label = { Text("Uygulama / Aile Başlığı") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_custom_app_title")
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Quick presets
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                presetTitles.forEach { preset ->
                    Surface(
                        onClick = {
                            titleInput = preset
                            onSaveTitle(preset)
                            titleSavedMessage = true
                        },
                        shape = RoundedCornerShape(8.dp),
                        color = AmberContainer.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = preset,
                            fontSize = 11.sp,
                            color = AmberOnContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = subtitleInput,
                onValueChange = {
                    subtitleInput = it
                    titleSavedMessage = false
                },
                label = { Text("Alt Başlık / Slogan") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (titleSavedMessage) {
                    Text("✅ Başlıklar Kaydedildi!", color = Color(0xFF16A34A), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }
                Button(
                    onClick = {
                        onSaveTitle(titleInput)
                        onSaveSubtitle(subtitleInput)
                        titleSavedMessage = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary),
                    modifier = Modifier.testTag("btn_save_titles")
                ) {
                    Text("Başlığı Kaydet", fontSize = 12.sp)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Kategorileri Yönet",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newCategoryInput,
                    onValueChange = {
                        newCategoryInput = it
                        categoryError = null
                    },
                    placeholder = { Text("Yeni Kategori Ekle...") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (newCategoryInput.isNotBlank()) {
                            val ok = onAddCategory(newCategoryInput)
                            if (ok) {
                                newCategoryInput = ""
                            } else {
                                categoryError = "Bu kategori zaten mevcut!"
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Ekle")
                }
            }

            if (categoryError != null) {
                Text(categoryError ?: "", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEach { cat ->
                    val isLocked = cat == "Tümü" || cat == "Favoriler"
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(cat, fontSize = 12.sp)
                            if (!isLocked) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Sil",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clickable { onRemoveCategory(cat) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FamilyAccessControlTab(
    allowedMembers: List<FamilyMember>,
    onAddMember: (String, String, String) -> Boolean,
    onRemoveMember: (String) -> Boolean
) {
    var showAddMemberDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    var newRole by remember { mutableStateOf("Aile Üyesi") }
    var newPin by remember { mutableStateOf("") }
    var addError by remember { mutableStateOf<String?>(null) }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Informational Explanation Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = IndigoContainer.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = IndigoSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Sadece İzin Verilenler Nasıl Kullanır?",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = IndigoOnContainer
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "1. Bu listeden aile fertlerini ve onların 4 haneli özel giriş PIN kodlarını belirleyin.\n" +
                                   "2. Uygulamayı telefonlarında açtıklarında sadece kendi PIN'leri veya sizin Ana PIN'iniz ile giriş yapabilirler.\n" +
                                   "3. İstediğiniz zaman buradan erişim iznini tek tıkla kaldırabilirsiniz.",
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = IndigoOnContainer.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "İzin Verilen Aile Bireyleri (${allowedMembers.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Button(
                    onClick = { showAddMemberDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("btn_add_family_member")
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Yeni İzin Ekle", fontSize = 12.sp)
                }
            }
        }

        // Add Member Sub-Form
        if (showAddMemberDialog) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = AmberContainer.copy(alpha = 0.35f)),
                    border = BorderStroke(1.dp, AmberPrimary.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Yeni Aile Üyesi Tanımla", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = newName,
                            onValueChange = { newName = it },
                            label = { Text("İsim Soyisim (Örn: Ayşe Öztürk)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = newRole,
                            onValueChange = { newRole = it },
                            label = { Text("Rol / Yakınlık (Örn: Anne, Baba, Kızım, Eşim)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = newPin,
                            onValueChange = { if (it.length <= 4) newPin = it },
                            label = { Text("Üyenin 4 Haneli Özel PIN Kodu") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (addError != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(addError ?: "", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = {
                                showAddMemberDialog = false
                                newName = ""
                                newPin = ""
                                addError = null
                            }) {
                                Text("İptal")
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(
                                onClick = {
                                    if (newName.isBlank()) {
                                        addError = "Lütfen üye ismini giriniz."
                                    } else if (newPin.length < 4) {
                                        addError = "PIN 4 haneli olmalıdır."
                                    } else {
                                        val ok = onAddMember(newName, newRole, newPin)
                                        if (ok) {
                                            showAddMemberDialog = false
                                            newName = ""
                                            newPin = ""
                                            addError = null
                                        } else {
                                            addError = "Eklenirken hata oluştu."
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary)
                            ) {
                                Text("İzni Kaydet")
                            }
                        }
                    }
                }
            }
        }

        // List of Allowed Members
        items(allowedMembers, key = { it.id }) { member ->
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (member.isMaster) AmberContainer.copy(alpha = 0.5f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                border = BorderStroke(
                    0.5.dp,
                    if (member.isMaster) AmberPrimary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = if (member.isMaster) AmberPrimary else IndigoSecondary,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = member.name.take(1).uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = member.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                if (member.isMaster) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = AmberPrimary
                                    ) {
                                        Text(
                                            text = "Ana Yönetici",
                                            fontSize = 9.sp,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "${member.role} • PIN Korumalı Erişim",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (!member.isMaster) {
                        IconButton(
                            onClick = { onRemoveMember(member.id) },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "İzni Kaldır",
                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SecurityAndLockTab(
    securityManager: SecurityManager,
    onDismiss: () -> Unit,
    onLockNow: () -> Unit
) {
    var showChangePin by remember { mutableStateOf(false) }
    var oldPin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var changePinError by remember { mutableStateOf<String?>(null) }
    var changePinSuccess by remember { mutableStateOf(false) }
    var biometricEnabled by remember { mutableStateOf(securityManager.isBiometricEnabled()) }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Immediate Lock Button
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AmberContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = AmberOnContainer)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Uygulamayı Şimdi Kilitle",
                                fontWeight = FontWeight.Bold,
                                color = AmberOnContainer,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "PIN ekranına döner",
                                fontSize = 11.sp,
                                color = AmberOnContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                    Button(
                        onClick = {
                            onDismiss()
                            onLockNow()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary),
                        modifier = Modifier.testTag("btn_lock_now")
                    ) {
                        Text("Kilitle", fontSize = 12.sp)
                    }
                }
            }
        }

        // Biometric / Fast permission toggle
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.Fingerprint,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Hızlı İzin & Biyometrik",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Tek dokunuşla güvenli kilit açma",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Switch(
                    checked = biometricEnabled,
                    onCheckedChange = {
                        biometricEnabled = it
                        securityManager.setBiometricEnabled(it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = AmberPrimary,
                        checkedTrackColor = AmberContainer
                    )
                )
            }
        }

        // Change Master PIN section
        item {
            if (!showChangePin) {
                Button(
                    onClick = { showChangePin = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ana Yönetici PIN Kodunu Değiştir", fontSize = 12.sp)
                }
            } else {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Ana PIN Değiştirme", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = oldPin,
                            onValueChange = { if (it.length <= 4) oldPin = it },
                            label = { Text("Eski 4 Haneli PIN") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = newPin,
                            onValueChange = { if (it.length <= 4) newPin = it },
                            label = { Text("Yeni 4 Haneli PIN") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (changePinError != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(changePinError ?: "", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                        }
                        if (changePinSuccess) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("PIN başarıyla güncellendi!", color = MaterialTheme.colorScheme.primary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { showChangePin = false }) {
                                Text("İptal")
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(
                                onClick = {
                                    if (oldPin.length < 4 || newPin.length < 4) {
                                        changePinError = "PIN 4 haneli olmalıdır."
                                    } else {
                                        val ok = securityManager.changePin(oldPin, newPin)
                                        if (ok) {
                                            changePinSuccess = true
                                            changePinError = null
                                            oldPin = ""
                                            newPin = ""
                                        } else {
                                            changePinError = "Eski PIN hatalı!"
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary)
                            ) {
                                Text("Güncelle")
                            }
                        }
                    }
                }
            }
        }
    }
}
