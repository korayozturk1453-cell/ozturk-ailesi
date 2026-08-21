package com.example.util

import androidx.compose.ui.graphics.Color

enum class QuoteCategory(val label: String, val emoji: String, val badgeColor: Color) {
    FAMILY_UNITY("Aile Birliği", "🏡", Color(0xFFF59E0B)),
    CHILDREN_JOY("Evlat & Neşe", "👶", Color(0xFF10B981)),
    WARM_HOME("Sıcak Yuvamız", "🌿", Color(0xFF6366F1)),
    FAMILY_WISDOM("Aile Bilgeliği", "✨", Color(0xFF8B5CF6))
}

data class QuoteItem(
    val id: String,
    val text: String,
    val category: QuoteCategory,
    val authorOrTag: String = "Ailemizin Notu"
)

object LoveAndFamilyQuotes {

    val allQuotes = listOf(
        // Aile, Birlik & Beraberlik Sözleri
        QuoteItem(
            id = "f1",
            text = "Aile; hayatın fırtınalarında sığınılacak en sıcak ve en güvenli limandır. 🏡✨",
            category = QuoteCategory.FAMILY_UNITY,
            authorOrTag = "Huzurlu Yuvamız"
        ),
        QuoteItem(
            id = "f2",
            text = "Gerçek zenginlik; akşam eve geldiğinde seni sevgiyle karşılayan ve birbirine sımsıkı sarılan bir ailedir. 💖👨‍👩‍👧‍👦",
            category = QuoteCategory.FAMILY_UNITY,
            authorOrTag = "Aile Zenginliği"
        ),
        QuoteItem(
            id = "f3",
            text = "Bir çocuğun içten kahkahası ve neşesi, evin en güzel melodisidir. 🎈😄",
            category = QuoteCategory.CHILDREN_JOY,
            authorOrTag = "Evimizin Neşesi"
        ),
        QuoteItem(
            id = "f4",
            text = "Aile; geçmişimizin sağlam kökleri, geleceğimizin kanatlarıdır. 🌳🕊️",
            category = QuoteCategory.FAMILY_WISDOM,
            authorOrTag = "Köklerimiz & Yarınlarımız"
        ),
        QuoteItem(
            id = "f5",
            text = "Birlikte paylaşılan sıcacık bir pazar kahvaltısı ve demli çay; paha biçilemez bir aile hazinesidir. ☕🥞",
            category = QuoteCategory.WARM_HOME,
            authorOrTag = "Bereketli Soframız"
        ),
        QuoteItem(
            id = "f6",
            text = "Kardeşlik; çocukluğun en sadık oyun arkadaşı, ömrün en güvenilir sırdaşıdır. 🤝❤️",
            category = QuoteCategory.CHILDREN_JOY,
            authorOrTag = "Kardeş Sevgisi"
        ),
        QuoteItem(
            id = "f7",
            text = "Ev, dört duvardan ibaret değildir; sevdiklerimizin birbirine duyduğu sevgi ve güvenin yankılandığı yerdir. 🏠💫",
            category = QuoteCategory.WARM_HOME,
            authorOrTag = "Yuvamızın Huzuru"
        ),
        QuoteItem(
            id = "f8",
            text = "Anne ve babanın sevgisi ile duası, evlatların ömründeki en parlak yol gösterici ışıktır. 🌟🤲",
            category = QuoteCategory.FAMILY_WISDOM,
            authorOrTag = "Büyüklerimizin Duası"
        ),
        QuoteItem(
            id = "f9",
            text = "Aile demek; birbirine sımsıkı sarılmak ve hiçbir zorlukta birbirinin elini bırakmamak demektir. 🤝🌟",
            category = QuoteCategory.FAMILY_UNITY,
            authorOrTag = "Sonsuz Birliktelik"
        ),
        QuoteItem(
            id = "f10",
            text = "Bugün birlikte paylaştığımız küçük bir tebessüm, yarın dönüp baktığımızda en kıymetli hatıramız olur. 📸✨",
            category = QuoteCategory.WARM_HOME,
            authorOrTag = "Anı Defterimiz"
        ),
        QuoteItem(
            id = "f11",
            text = "Evlat kokusu, cennet kokusudur; evi bereketle, kalbi sonsuz huzurla doldurur. 🌸👶",
            category = QuoteCategory.CHILDREN_JOY,
            authorOrTag = "Can Parelerimiz"
        ),
        QuoteItem(
            id = "f12",
            text = "Aile; sevginin başladığı, bağların güçlendiği ve asla son bulmadığı tek yerdir. 🌿❤️",
            category = QuoteCategory.FAMILY_UNITY,
            authorOrTag = "Sevgi Çemberi"
        ),
        QuoteItem(
            id = "f13",
            text = "Birbirimize verebileceğimiz en değerli hediye; birlikte geçirdiğimiz dopdolu ve neşeli zamandır. ⏳🎁",
            category = QuoteCategory.FAMILY_WISDOM,
            authorOrTag = "Kıymetli Vakitler"
        ),
        QuoteItem(
            id = "f14",
            text = "Birlik ve sevgi içinde olan bir aileyi dünyanın hiçbir fırtınası sarsamaz. 🛡️👨‍👩‍👧‍👦",
            category = QuoteCategory.FAMILY_UNITY,
            authorOrTag = "Güçlü Bağlar"
        ),
        QuoteItem(
            id = "f15",
            text = "Çocuklarımızla biriktirdiğimiz her güzel anı, geleceğe bırakılan en değerli mirastır. 🗺️🌈",
            category = QuoteCategory.CHILDREN_JOY,
            authorOrTag = "Geleceğe Miras"
        )
    )

    fun getRandomQuote(): QuoteItem {
        return allQuotes.random()
    }

    fun getQuoteForIndex(index: Int): QuoteItem {
        return allQuotes[index % allQuotes.size]
    }
}

