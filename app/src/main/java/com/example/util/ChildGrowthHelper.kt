package com.example.util

import com.example.data.model.ChildMilestone
import com.example.data.model.FamilyBucketItem
import com.example.data.model.MemoryEntry
import java.util.Calendar

data class ChildProfileData(
    val name: String,
    val genderEmoji: String,
    val defaultBirthYear: Int,
    val defaultBirthMonth: Int, // 0-indexed (0 = Jan, 2 = Mar, etc.)
    val defaultBirthDay: Int
)

data class DetectedChildAge(
    val childName: String,
    val emoji: String,
    val ageString: String
)

object ChildGrowthHelper {

    val ZEYD = ChildProfileData(
        name = "Zeyd",
        genderEmoji = "👶",
        defaultBirthYear = 2021,
        defaultBirthMonth = 3, // Nisan
        defaultBirthDay = 12
    )

    val ESILA = ChildProfileData(
        name = "Esila",
        genderEmoji = "🌸",
        defaultBirthYear = 2023,
        defaultBirthMonth = 8, // Eylül
        defaultBirthDay = 20
    )

    /**
     * Converts year, month (0-indexed), day into epoch millis
     */
    fun getTimestampFromDate(year: Int, month: Int, day: Int): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, day)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    /**
     * Calculates precise human-friendly age string between birth timestamp and target timestamp
     */
    fun calculateAge(birthTimestamp: Long, targetTimestamp: Long = System.currentTimeMillis()): String {
        if (targetTimestamp < birthTimestamp) return "Doğum Öncesi ⏳"

        val birthCal = Calendar.getInstance().apply { timeInMillis = birthTimestamp }
        val targetCal = Calendar.getInstance().apply { timeInMillis = targetTimestamp }

        var years = targetCal.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR)
        var months = targetCal.get(Calendar.MONTH) - birthCal.get(Calendar.MONTH)
        var days = targetCal.get(Calendar.DAY_OF_MONTH) - birthCal.get(Calendar.DAY_OF_MONTH)

        if (days < 0) {
            months--
            days += 30
        }
        if (months < 0) {
            years--
            months += 12
        }

        return when {
            years >= 2 -> {
                if (months > 0) "$years Yaş $months Aylık" else "$years Yaşında"
            }
            years == 1 -> {
                if (months > 0) "1 Yaş $months Aylık" else "1 Yaşında"
            }
            months >= 1 -> {
                "$months Aylık"
            }
            else -> {
                "${days.coerceAtLeast(1)} Günlük"
            }
        }
    }

    /**
     * Detects if the memory mentions Zeyd, Esila or both, and provides their exact age at the time of memory!
     */
    fun detectChildrenIn(
        memory: MemoryEntry,
        zeydBirthDate: Long = getTimestampFromDate(ZEYD.defaultBirthYear, ZEYD.defaultBirthMonth, ZEYD.defaultBirthDay),
        esilaBirthDate: Long = getTimestampFromDate(ESILA.defaultBirthYear, ESILA.defaultBirthMonth, ESILA.defaultBirthDay)
    ): List<DetectedChildAge> {
        val fullText = "${memory.title} ${memory.story} ${memory.tags} ${memory.category}".lowercase()
        val results = mutableListOf<DetectedChildAge>()

        if (fullText.contains("zeyd")) {
            val age = calculateAge(zeydBirthDate, memory.timestamp)
            results.add(DetectedChildAge("Zeyd", "👶", age))
        }

        if (fullText.contains("esila")) {
            val age = calculateAge(esilaBirthDate, memory.timestamp)
            results.add(DetectedChildAge("Esila", "🌸", age))
        }

        return results
    }

    /**
     * Initial Starter Milestones for children
     */
    fun getStarterMilestones(): List<ChildMilestone> {
        val now = System.currentTimeMillis()
        val oneYearAgo = now - (365L * 24 * 60 * 60 * 1000)
        val twoYearsAgo = now - (730L * 24 * 60 * 60 * 1000)

        return listOf(
            ChildMilestone(
                childName = "Zeyd",
                title = "İlk Adımlarını Attı! 👣",
                description = "Salonda koltuktan masaya doğru tek başına 5 adım attı.",
                timestamp = twoYearsAgo + (100L * 24 * 60 * 60 * 1000),
                isCompleted = true,
                completedDate = twoYearsAgo + (100L * 24 * 60 * 60 * 1000),
                iconEmoji = "👣"
            ),
            ChildMilestone(
                childName = "Zeyd",
                title = "İlk 'Baba' ve 'Anne' Kelimeleri 💬",
                description = "İlk önce babasına bakıp gülümsedi, ardından annesine sarıldı.",
                timestamp = twoYearsAgo,
                isCompleted = true,
                completedDate = twoYearsAgo,
                iconEmoji = "💬"
            ),
            ChildMilestone(
                childName = "Zeyd",
                title = "İki Tekerlekli / Denge Bisikleti 🚲",
                description = "Parkta pedalsız denge bisikletini sürmeyi öğrendi.",
                timestamp = oneYearAgo,
                isCompleted = true,
                completedDate = oneYearAgo,
                iconEmoji = "🚲"
            ),
            ChildMilestone(
                childName = "Zeyd",
                title = "İlk Kreş / Okul Günü 🎒",
                description = "Sırt çantasıyla ilk okul deneyimi heyecanı.",
                timestamp = now - (30L * 24 * 60 * 60 * 1000),
                isCompleted = false,
                iconEmoji = "🎒"
            ),
            ChildMilestone(
                childName = "Esila",
                title = "İlk Diş Buğdayı & İlk İnci Diş 🦷",
                description = "İlk alt dişi pırıl pırıl belirdi! Ailecek kutladık.",
                timestamp = oneYearAgo + (60L * 24 * 60 * 60 * 1000),
                isCompleted = true,
                completedDate = oneYearAgo + (60L * 24 * 60 * 60 * 1000),
                iconEmoji = "🦷"
            ),
            ChildMilestone(
                childName = "Esila",
                title = "İlk Emekleme ve Keşifler 🧸",
                description = "Odanın her köşesine emekleyerek ulaşıp oyuncakları inceliyor.",
                timestamp = oneYearAgo + (120L * 24 * 60 * 60 * 1000),
                isCompleted = true,
                completedDate = oneYearAgo + (120L * 24 * 60 * 60 * 1000),
                iconEmoji = "🧸"
            ),
            ChildMilestone(
                childName = "Esila",
                title = "İlk Bağımsız Adımlar 👣",
                description = "Elini bırakıp neşeyle yürümeye başladı.",
                timestamp = now - (90L * 24 * 60 * 60 * 1000),
                isCompleted = true,
                completedDate = now - (90L * 24 * 60 * 60 * 1000),
                iconEmoji = "👣"
            ),
            ChildMilestone(
                childName = "Esila",
                title = "İlk Cümleler & Tatlı Masallar 📖",
                description = "Kendi kelimeleriyle gün boyu minik hikayeler anlatıyor.",
                timestamp = now,
                isCompleted = false,
                iconEmoji = "📖"
            )
        )
    }

    /**
     * Initial Starter Family Bucket List Items
     */
    fun getStarterBucketItems(): List<FamilyBucketItem> {
        val now = System.currentTimeMillis()
        val threeMonthsAgo = now - (90L * 24 * 60 * 60 * 1000)

        return listOf(
            FamilyBucketItem(
                title = "Kapadokya'da Balonları İzlemek 🎈",
                category = "Seyahat & Gezi",
                description = "Sabahın erken saatlerinde gökyüzündeki rengarenk balonları ailecek seyretmek.",
                targetDate = "İlkbahar / Yaz",
                isCompleted = false,
                iconEmoji = "🎈"
            ),
            FamilyBucketItem(
                title = "Ailecek Doğada Çadır Kampı Kurmak ⛺",
                category = "Doğa & Kamp",
                description = "Ateş başında çay demlemek, yıldızların altında uyumak.",
                targetDate = "Yaz Tatili",
                isCompleted = true,
                completedDate = threeMonthsAgo,
                iconEmoji = "⛺"
            ),
            FamilyBucketItem(
                title = "Birlikte Bir Aile Ağacı / Fidan Dikmek 🌱",
                category = "Doğa & Kamp",
                description = "Zeyd ve Esila ile birlikte köyde veya bahçede bir meyve fidanı dikip can suyu vermek.",
                targetDate = "Sonbahar",
                isCompleted = false,
                iconEmoji = "🌱"
            ),
            FamilyBucketItem(
                title = "Karadeniz Yaylalarını Gezmek 🌲",
                category = "Seyahat & Gezi",
                description = "Sislerin arasında horon ve yemyeşil yayla havası.",
                targetDate = "Ağustos",
                isCompleted = false,
                iconEmoji = "🌲"
            ),
            FamilyBucketItem(
                title = "1000 Parçalık Büyük Aile Yapbozunu Bitirmek 🧩",
                category = "Etkinlik & Eğlence",
                description = "Salon masasında hep beraber bir puzzle'ı tamamlayıp tablo yapmak.",
                targetDate = "Kış Akşamı",
                isCompleted = true,
                completedDate = now - (40L * 24 * 60 * 60 * 1000),
                iconEmoji = "🧩"
            ),
            FamilyBucketItem(
                title = "Köyde Dalından Taze Meyve Toplamak 🍎",
                category = "Gelenek & Aile",
                description = "Çocuklarla sepete elma ve incir doldurmak.",
                targetDate = "Yaz Sonu",
                isCompleted = true,
                completedDate = threeMonthsAgo + (20L * 24 * 60 * 60 * 1000),
                iconEmoji = "🍎"
            ),
            FamilyBucketItem(
                title = "Sahilde Ailecek Bisiklet Turu Yapmak 🚲",
                category = "Etkinlik & Eğlence",
                description = "Gün batımında deniz kenarında pedal çevirip dondurma yemek.",
                targetDate = "Hafta Sonu",
                isCompleted = false,
                iconEmoji = "🚲"
            ),
            FamilyBucketItem(
                title = "Yıldızları İzleyip Gelecek Hayalleri Kurmak ✨",
                category = "Gelenek & Aile",
                description = "Açık havada battaniyelere sarılıp gökyüzünü seyretmek.",
                targetDate = "Her Yaz",
                isCompleted = false,
                iconEmoji = "✨"
            )
        )
    }
}
