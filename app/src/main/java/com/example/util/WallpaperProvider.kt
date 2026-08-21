package com.example.util

import com.example.R

object WallpaperProvider {
    val dayWallpapers = listOf(
        R.drawable.bg_day_family_warm_1787295607145,
        R.drawable.bg_day_sunny_lake_1787295634824,
        R.drawable.banner_memories_1787293319077
    )

    val dayWallpaperNames = listOf(
        "Sıcak Aile Bahçesi (Sabah)",
        "Güneşli Göl Manzarası",
        "Klasik Hatıra Bahçesi"
    )

    val nightWallpapers = listOf(
        R.drawable.bg_night_moon_lake_1787297492598,
        R.drawable.bg_night_family_cozy_1787295621296,
        R.drawable.bg_night_city_lights_1787297505494,
        R.drawable.bg_night_aurora_camp_1787295648741
    )

    val nightWallpaperNames = listOf(
        "Ay Işıklı Göl & Yıldızlar",
        "Yıldızlı Akşam Terası & Fenerler",
        "Akşam Işıkları & Sıcak Bahçe",
        "Samanyolu & Kamp Ateşi"
    )

    fun getWallpaper(isDarkMode: Boolean, index: Int): Int {
        return if (isDarkMode) {
            val safeIdx = if (nightWallpapers.isEmpty()) R.drawable.bg_night_family_cozy_1787295621296 else nightWallpapers[index % nightWallpapers.size]
            safeIdx
        } else {
            val safeIdx = if (dayWallpapers.isEmpty()) R.drawable.bg_day_family_warm_1787295607145 else dayWallpapers[index % dayWallpapers.size]
            safeIdx
        }
    }

    fun getWallpaperName(isDarkMode: Boolean, index: Int): String {
        return if (isDarkMode) {
            nightWallpaperNames[index % nightWallpaperNames.size]
        } else {
            dayWallpaperNames[index % dayWallpaperNames.size]
        }
    }
}
