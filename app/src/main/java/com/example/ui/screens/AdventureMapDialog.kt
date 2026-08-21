package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.MemoryEntry
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberOnContainer
import com.example.ui.theme.AmberPrimary
import com.example.util.ImageUtils
import java.io.File
import java.util.Locale

data class CityNode(
    val name: String,
    val region: String,
    val relX: Float, // 0.0 to 1.0 relative position on map
    val relY: Float,
    val iconEmoji: String = "📍"
)

object KnownLocations {
    val predefinedCities = listOf(
        CityNode("İstanbul", "Marmara", 0.22f, 0.32f, "🕌"),
        CityNode("Köy", "Özel & Doğa", 0.72f, 0.28f, "🏡"),
        CityNode("Trabzon", "Karadeniz", 0.78f, 0.25f, "🌲"),
        CityNode("Rize", "Karadeniz", 0.85f, 0.24f, "🍃"),
        CityNode("Karadeniz", "Karadeniz", 0.65f, 0.22f, "🌊"),
        CityNode("Ankara", "İç Anadolu", 0.44f, 0.45f, "🏛️"),
        CityNode("İzmir", "Ege", 0.12f, 0.62f, "🏖️"),
        CityNode("Bursa", "Marmara", 0.24f, 0.42f, "🚠"),
        CityNode("Antalya", "Akdeniz", 0.38f, 0.82f, "🌴"),
        CityNode("Muğla", "Ege/Akdeniz", 0.18f, 0.78f, "⛵"),
        CityNode("Deniz", "Sahil & Tatil", 0.28f, 0.88f, "🌊"),
        CityNode("Orman", "Doğa Gezisi", 0.52f, 0.30f, "🏕️"),
        CityNode("Kapadokya", "İç Anadolu", 0.55f, 0.58f, "🎈")
    )
}

@Composable
fun AdventureMapDialog(
    memories: List<MemoryEntry>,
    onDismiss: () -> Unit,
    onSelectMemory: (MemoryEntry) -> Unit,
    onAddNewMemoryAtLocation: (String) -> Unit = {}
) {
    var selectedLocation by remember { mutableStateOf<String?>(null) }

    // Aggregate location stats from actual user memories
    val locationMap = remember(memories) {
        val map = mutableMapOf<String, MutableList<MemoryEntry>>()
        memories.forEach { memory ->
            val loc = memory.location.trim()
            if (loc.isNotBlank()) {
                map.getOrPut(loc) { mutableListOf() }.add(memory)
            }
            // Also check tags for known locations
            memory.tags.split(",", " ", ";").map { it.trim().removePrefix("#").trim() }.forEach { tag ->
                if (tag.isNotBlank() && (tag.equals("Köy", true) || tag.equals("İstanbul", true) || tag.equals("Deniz", true) || tag.equals("Orman", true) || tag.equals("Karadeniz", true))) {
                    map.getOrPut(tag) { mutableListOf() }.add(memory)
                }
            }
        }
        map
    }

    val totalLocationsCount = locationMap.keys.size
    val totalPhotosCount = memories.count { it.photoPath != null }

    val filteredMemories = remember(selectedLocation, memories) {
        if (selectedLocation == null) {
            memories.filter { it.location.isNotBlank() }
        } else {
            val q = selectedLocation!!.trim().lowercase(Locale.forLanguageTag("tr"))
            memories.filter { m ->
                m.location.lowercase(Locale.forLanguageTag("tr")).contains(q) ||
                    m.tags.lowercase(Locale.forLanguageTag("tr")).contains(q) ||
                    m.title.lowercase(Locale.forLanguageTag("tr")).contains(q) ||
                    m.story.lowercase(Locale.forLanguageTag("tr")).contains(q)
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = AmberContainer,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Explore,
                                    contentDescription = null,
                                    tint = AmberPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Aile Macera Haritası",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Geçmişten Bugüne Ayak İzlerimiz 🗺️",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Kapat",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Stats Banner Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("📍 Ziyaret Edilen", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "$totalLocationsCount Lokasyon",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = AmberContainer.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("📸 Fotoğraf Sayısı", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "$totalPhotosCount Kare",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmberPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Stylized Interactive Visual Map Canvas
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Background stylized topographic terrain art
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            // Draw subtle decorative map grid lines
                            for (i in 1..5) {
                                val x = w * (i / 6f)
                                drawLine(
                                    color = Color.LightGray.copy(alpha = 0.15f),
                                    start = Offset(x, 0f),
                                    end = Offset(x, h),
                                    strokeWidth = 1f
                                )
                            }
                            for (i in 1..4) {
                                val y = h * (i / 5f)
                                drawLine(
                                    color = Color.LightGray.copy(alpha = 0.15f),
                                    start = Offset(0f, y),
                                    end = Offset(w, y),
                                    strokeWidth = 1f
                                )
                            }

                            // Stylized decorative coastal curves
                            val coastPath = Path().apply {
                                moveTo(w * 0.08f, h * 0.4f)
                                cubicTo(w * 0.25f, h * 0.25f, w * 0.65f, h * 0.2f, w * 0.92f, h * 0.35f)
                                cubicTo(w * 0.8f, h * 0.75f, w * 0.45f, h * 0.85f, w * 0.15f, h * 0.75f)
                                close()
                            }
                            drawPath(
                                path = coastPath,
                                color = AmberPrimary.copy(alpha = 0.06f)
                            )
                            drawPath(
                                path = coastPath,
                                color = AmberPrimary.copy(alpha = 0.25f),
                                style = Stroke(width = 1.5f)
                            )
                        }

                        // Map Pins
                        KnownLocations.predefinedCities.forEach { city ->
                            val hasMemories = locationMap.keys.any {
                                it.contains(city.name, ignoreCase = true)
                            }
                            val isSelected = selectedLocation?.equals(city.name, ignoreCase = true) == true

                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(
                                        start = (city.relX * 280).dp.coerceIn(8.dp, 290.dp),
                                        top = (city.relY * 130).dp.coerceIn(8.dp, 140.dp)
                                    )
                                    .clickable {
                                        selectedLocation = if (isSelected) null else city.name
                                    }
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = when {
                                        isSelected -> AmberPrimary
                                        hasMemories -> MaterialTheme.colorScheme.primaryContainer
                                        else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                                    },
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = if (isSelected || hasMemories) 1.2.dp else 0.5.dp,
                                        color = if (isSelected) AmberPrimary else if (hasMemories) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.4f)
                                    ),
                                    shadowElevation = if (hasMemories || isSelected) 4.dp else 1.dp
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = city.iconEmoji, fontSize = 10.sp)
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = city.name,
                                            fontSize = 10.sp,
                                            fontWeight = if (hasMemories || isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                        if (hasMemories) {
                                            val count = locationMap.entries.firstOrNull { it.key.contains(city.name, ignoreCase = true) }?.value?.size ?: 1
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Box(
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .background(if (isSelected) Color.White else AmberPrimary, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "$count",
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) AmberPrimary else Color.White
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Location Chips Carousel
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        onClick = { selectedLocation = null },
                        shape = RoundedCornerShape(12.dp),
                        color = if (selectedLocation == null) AmberPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = "🌍 Tüm Lokasyonlar (${memories.size})",
                            fontSize = 11.5.sp,
                            fontWeight = if (selectedLocation == null) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedLocation == null) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }

                    KnownLocations.predefinedCities.forEach { city ->
                        val isSelected = selectedLocation?.equals(city.name, ignoreCase = true) == true
                        Surface(
                            onClick = { selectedLocation = if (isSelected) null else city.name },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) AmberPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        ) {
                            Text(
                                text = "${city.iconEmoji} ${city.name}",
                                fontSize = 11.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Selected City Memories Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedLocation == null) "📸 Tüm Lokasyonlardaki Anılar" else "📍 $selectedLocation Hatıraları (${filteredMemories.size})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Memories List in Selected Location
                if (filteredMemories.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text("📍", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "$selectedLocation için henüz kayıtlı anı yok.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredMemories, key = { it.id }) { mem ->
                            Card(
                                onClick = {
                                    onDismiss()
                                    onSelectMemory(mem)
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Photo Thumbnail or Mood Emoji Box
                                    if (mem.photoPath != null && File(mem.photoPath).exists()) {
                                        AsyncImage(
                                            model = File(mem.photoPath),
                                            contentDescription = mem.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(54.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(54.dp)
                                                .background(AmberContainer, RoundedCornerShape(10.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = "📸", fontSize = 22.sp)
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = mem.title,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (mem.story.isNotBlank()) {
                                            Text(
                                                text = mem.story,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "🗓️ ${ImageUtils.formatShortDate(mem.timestamp)}",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            if (mem.location.isNotBlank()) {
                                                Text(
                                                    text = "• 📍 ${mem.location}",
                                                    fontSize = 11.sp,
                                                    color = AmberPrimary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
