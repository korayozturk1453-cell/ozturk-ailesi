package com.example.util

import java.util.Locale

object TagProvider {
    /**
     * Rich list of ready-to-use family tags as requested by the user:
     * Zeyd, Esila, Köy, Gezi, İstanbul, Deniz, Orman and more!
     */
    val suggestedTags = listOf(
        "Zeyd",
        "Esila",
        "Köy",
        "Gezi",
        "İstanbul",
        "Deniz",
        "Orman",
        "Aile",
        "Doğum Günü",
        "Okul",
        "Piknik",
        "Bebeklik",
        "Yaz",
        "Kış",
        "Bayram",
        "Park",
        "Yemek",
        "Oyun",
        "Karadeniz",
        "Tatil",
        "Kardeşler",
        "Büyükanne & Dede",
        "İlk Adımlar",
        "Kutlama",
        "Huzur"
    )

    private val turkishLocale = Locale.forLanguageTag("tr")

    /**
     * Checks if a target string contains the search query safely with Turkish character sensitivity and # trimming.
     */
    fun matchesTagOrQuery(target: String, query: String): Boolean {
        if (query.isBlank()) return true
        val cleanQuery = query.trim().removePrefix("#").trim()
        if (cleanQuery.isEmpty()) return true

        val qLower = cleanQuery.lowercase(Locale.ROOT)
        val qTr = cleanQuery.lowercase(turkishLocale)

        val tLower = target.lowercase(Locale.ROOT)
        val tTr = target.lowercase(turkishLocale)

        return tLower.contains(qLower) || tTr.contains(qTr)
    }

    /**
     * Formats tags string by adding or removing a specific tag name.
     */
    fun toggleTagInString(currentTags: String, tagToAddOrRemove: String): String {
        val cleanTagName = tagToAddOrRemove.trim().removePrefix("#").trim()
        val tagWithHash = "#$cleanTagName"
        val existingTags = currentTags.split(",", " ", ";")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .toMutableList()

        val foundIndex = existingTags.indexOfFirst {
            it.equals(tagWithHash, ignoreCase = true) || it.equals(cleanTagName, ignoreCase = true)
        }

        if (foundIndex >= 0) {
            existingTags.removeAt(foundIndex)
        } else {
            existingTags.add(tagWithHash)
        }

        return existingTags.joinToString(", ")
    }

    /**
     * Checks if a tag is present in the tags string.
     */
    fun isTagPresent(tagsString: String, tag: String): Boolean {
        val clean = tag.trim().removePrefix("#").trim()
        val tagWithHash = "#$clean"
        val tagsList = tagsString.split(",", " ", ";").map { it.trim() }
        return tagsList.any { it.equals(tagWithHash, ignoreCase = true) || it.equals(clean, ignoreCase = true) }
    }
}
