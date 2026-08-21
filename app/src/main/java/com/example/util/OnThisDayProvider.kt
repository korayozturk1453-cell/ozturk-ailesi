package com.example.util

import com.example.data.model.MemoryEntry
import java.util.Calendar

data class OnThisDayResult(
    val memory: MemoryEntry,
    val isExactDayMatch: Boolean,
    val badgeLabel: String,
    val yearsAgo: Int
)

object OnThisDayProvider {

    /**
     * Finds memories that happened on the exact same day & month in previous years,
     * or selects a meaningful throwback memory for today.
     */
    fun getOnThisDayMemory(
        memories: List<MemoryEntry>,
        currentTimestamp: Long = System.currentTimeMillis()
    ): OnThisDayResult? {
        if (memories.isEmpty()) return null

        val currentCal = Calendar.getInstance().apply { timeInMillis = currentTimestamp }
        val currentDay = currentCal.get(Calendar.DAY_OF_MONTH)
        val currentMonth = currentCal.get(Calendar.MONTH)
        val currentYear = currentCal.get(Calendar.YEAR)

        // 1. Search for exact same month & day in previous years
        val exactMatches = memories.mapNotNull { memory ->
            val memCal = Calendar.getInstance().apply { timeInMillis = memory.timestamp }
            val memDay = memCal.get(Calendar.DAY_OF_MONTH)
            val memMonth = memCal.get(Calendar.MONTH)
            val memYear = memCal.get(Calendar.YEAR)

            if (memDay == currentDay && memMonth == currentMonth && memYear < currentYear) {
                val yearsAgo = currentYear - memYear
                val label = if (yearsAgo == 1) "Tam 1 Yıl Önce Bugün" else "Tam $yearsAgo Yıl Önce Bugün"
                OnThisDayResult(
                    memory = memory,
                    isExactDayMatch = true,
                    badgeLabel = label,
                    yearsAgo = yearsAgo
                )
            } else {
                null
            }
        }

        if (exactMatches.isNotEmpty()) {
            return exactMatches.first()
        }

        // 2. Search for exact same day of month in recent months
        val sameDayMatches = memories.mapNotNull { memory ->
            val memCal = Calendar.getInstance().apply { timeInMillis = memory.timestamp }
            val memDay = memCal.get(Calendar.DAY_OF_MONTH)
            val memYear = memCal.get(Calendar.YEAR)
            val memMonth = memCal.get(Calendar.MONTH)

            if (memDay == currentDay && (memYear != currentYear || memMonth != currentMonth)) {
                val diffDays = ((currentTimestamp - memory.timestamp) / (1000L * 60 * 60 * 24)).toInt()
                if (diffDays >= 28) {
                    val monthsAgo = (diffDays / 30).coerceAtLeast(1)
                    OnThisDayResult(
                        memory = memory,
                        isExactDayMatch = true,
                        badgeLabel = "$monthsAgo Ay Önce Bugün",
                        yearsAgo = (diffDays / 365)
                    )
                } else null
            } else null
        }

        if (sameDayMatches.isNotEmpty()) {
            return sameDayMatches.first()
        }

        // 3. Fallback: Daily Nostalgia Highlight (Günün Nostaljik Anısı)
        // Pick a stable memory based on day-of-year so it changes daily
        val dayOfYear = currentCal.get(Calendar.DAY_OF_YEAR)
        val candidateList = memories.filter { it.photoPath != null }.ifEmpty { memories }
        val selected = candidateList[dayOfYear % candidateList.size]

        val diffDays = ((currentTimestamp - selected.timestamp) / (1000L * 60 * 60 * 24)).toInt()
        val label = when {
            diffDays >= 365 -> "🌿 Geçmişten Bir Hatıra (${diffDays / 365} Yıl Önce)"
            diffDays >= 30 -> "✨ Günün Nostalji Anısı (${diffDays / 30} Ay Önce)"
            else -> "📸 Günün Öne Çıkan Anısı"
        }

        return OnThisDayResult(
            memory = selected,
            isExactDayMatch = false,
            badgeLabel = label,
            yearsAgo = diffDays / 365
        )
    }
}
