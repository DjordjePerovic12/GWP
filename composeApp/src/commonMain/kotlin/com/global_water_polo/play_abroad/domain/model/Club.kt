package com.global_water_polo.play_abroad.domain.model

/** A water polo city on Globe+; [region] is "eu", "am" or "world", [airport] its nearest airport code. */
data class City(
    val id: String,
    val name: String,
    val country: String,
    val flag: String,
    val latitude: Float,
    val longitude: Float,
    val airport: String,
    val region: String
)

/** One public honour: when, what, and a short qualifier ("Men · winners"). */
data class Honour(val year: String, val title: String, val detail: String)

/** A club shown on Globe+. Public information only: not a GWP partner until it verifies its profile. */
data class Club(
    val id: String,
    val name: String,
    val cityId: String,
    val founded: String,
    val history: String,
    val honours: List<Honour>
)

/**
 * Cities and clubs from the GWP prototype. Place and club names are proper names and are not translated.
 * TODO: Sample data until Globe+ is served by the GWP backend.
 */
object WaterPoloMap {
    val cities: List<City> = listOf(
        City("herceg-novi", "Herceg Novi", "Montenegro", "🇲🇪", 42.45f, 18.54f, "TIV", "eu"),
        City("kotor", "Kotor", "Montenegro", "🇲🇪", 42.42f, 18.77f, "TIV", "eu"),
        City("budva", "Budva", "Montenegro", "🇲🇪", 42.29f, 18.84f, "TIV", "eu"),
        City("dubrovnik", "Dubrovnik", "Croatia", "🇭🇷", 42.64f, 18.11f, "DBV", "eu"),
        City("split", "Split", "Croatia", "🇭🇷", 43.51f, 16.44f, "SPU", "eu"),
        City("zagreb", "Zagreb", "Croatia", "🇭🇷", 45.81f, 15.98f, "ZAG", "eu"),
        City("rijeka", "Rijeka", "Croatia", "🇭🇷", 45.33f, 14.44f, "RJK", "eu"),
        City("belgrade", "Belgrade", "Serbia", "🇷🇸", 44.8f, 20.47f, "BEG", "eu"),
        City("kragujevac", "Kragujevac", "Serbia", "🇷🇸", 44.02f, 20.92f, "BEG", "eu"),
        City("sabac", "Šabac", "Serbia", "🇷🇸", 44.75f, 19.69f, "BEG", "eu"),
        City("budapest", "Budapest", "Hungary", "🇭🇺", 47.5f, 19.04f, "BUD", "eu"),
        City("szolnok", "Szolnok", "Hungary", "🇭🇺", 47.18f, 20.2f, "BUD", "eu"),
        City("barcelona", "Barcelona", "Spain", "🇪🇸", 41.39f, 2.16f, "BCN", "eu"),
        City("sabadell", "Sabadell", "Spain", "🇪🇸", 41.54f, 2.11f, "BCN", "eu"),
        City("terrassa", "Terrassa", "Spain", "🇪🇸", 41.57f, 2.02f, "BCN", "eu"),
        City("athens", "Athens", "Greece", "🇬🇷", 37.98f, 23.73f, "ATH", "eu"),
        City("piraeus", "Piraeus", "Greece", "🇬🇷", 37.94f, 23.65f, "ATH", "eu"),
        City("vouliagmeni", "Vouliagmeni", "Greece", "🇬🇷", 37.81f, 23.78f, "ATH", "eu"),
        City("nea-smyrni", "Nea Smyrni", "Greece", "🇬🇷", 37.95f, 23.71f, "ATH", "eu"),
        City("bucharest", "Bucharest", "Romania", "🇷🇴", 44.43f, 26.11f, "OTP", "eu"),
        City("oradea", "Oradea", "Romania", "🇷🇴", 47.05f, 21.92f, "OMR", "eu"),
        City("recco", "Recco", "Italy", "🇮🇹", 44.36f, 9.14f, "GOA", "eu"),
        City("savona", "Savona", "Italy", "🇮🇹", 44.31f, 8.48f, "GOA", "eu"),
        City("brescia", "Brescia", "Italy", "🇮🇹", 45.54f, 10.21f, "BGY", "eu"),
        City("syracuse", "Syracuse", "Italy", "🇮🇹", 37.08f, 15.29f, "CTA", "eu"),
        City("marseille", "Marseille", "France", "🇫🇷", 43.3f, 5.38f, "MRS", "eu"),
        City("noisy", "Noisy-le-Sec", "France", "🇫🇷", 48.89f, 2.46f, "CDG", "eu"),
        City("tourcoing", "Tourcoing", "France", "🇫🇷", 50.72f, 3.16f, "LIL", "eu"),
        City("valletta", "Valletta", "Malta", "🇲🇹", 35.9f, 14.5f, "MLA", "eu"),
        City("socal", "Southern California", "United States", "🇺🇸", 34.05f, -118.24f, "LAX", "am"),
        City("norcal", "Northern California", "United States", "🇺🇸", 37.77f, -122.42f, "SFO", "am"),
        City("texas", "Texas", "United States", "🇺🇸", 29.76f, -95.37f, "IAH", "am"),
        City("miami", "Miami", "United States", "🇺🇸", 25.76f, -80.19f, "MIA", "am"),
        City("chicago", "Chicago", "United States", "🇺🇸", 41.88f, -87.63f, "ORD", "am"),
        City("san-juan", "San Juan", "Puerto Rico", "🇵🇷", 18.47f, -66.11f, "SJU", "am"),
        City("harare", "Harare", "Zimbabwe", "🇿🇼", -17.83f, 31.05f, "HRE", "world"),
        City("yokohama", "Yokohama", "Japan", "🇯🇵", 35.43f, 139.65f, "HND", "world"),
        City("singapore", "Singapore", "Singapore", "🇸🇬", 1.29f, 103.85f, "SIN", "world")
    )

    val clubs: List<Club> = listOf(
        Club(
            "primorac", "Primorac", "kotor", "1922",
            "A club shaped by Kotor’s bay and seafaring identity. The 2009 European title remains a defining moment.",
            listOf(Honour("2009", "European champions", "Men · title winner"))
        ),
        Club(
            "vk-budva", "VK Budva", "budva", "1947",
            "Its roots lie in Mogren, the sports society formed in 1930; the post-war water polo section began in 1947.",
            listOf(Honour("2011", "Euroleague Final Four", "Men · historic appearance"))
        ),
        Club(
            "jug", "Jug", "dubrovnik", "1923",
            "Founded in 1923; first public match in June 1924. Euro Cup winners in 2000 and 2024.",
            listOf(Honour("2000 · 2024", "Euro Cup", "Men · winners"))
        ),
        Club(
            "jadran-split", "Jadran Split", "split", "1920",
            "European champions in 1992 and 1993; Croatian champions in 2023 and 2024.",
            listOf(Honour("1992 · 1993", "European champions", "Men · back-to-back titles"))
        ),
        Club(
            "havk-mladost", "HAVK Mladost", "zagreb", "1946",
            "Seven European championship titles, as recorded in the club’s official history.",
            listOf(Honour("7 titles", "European champions", "Men · official club history"))
        ),
        Club(
            "primorje", "Primorje", "rijeka", "1908",
            "Founded in 1908 as Viktorija. Won the Croatian league, cup and regional league in both 2013/14 and 2014/15.",
            listOf(Honour("2014 · 2015", "Domestic + regional treble", "Men · league, cup and regional league"))
        ),
        Club(
            "novi-beograd", "Novi Beograd", "belgrade", "2015",
            "Founded in 2015; won the Serbian Cup in 2024.",
            listOf(Honour("2024", "Serbian Cup", "Men · winners"))
        ),
        Club(
            "radnicki", "Radnički", "kragujevac", "2012",
            "Euro Cup winners in 2013.",
            listOf(Honour("2013", "Euro Cup", "Men · winners"))
        ),
        Club(
            "vk-sabac", "VK Šabac", "sabac", "1990",
            "The senior club won the Serbian league-and-cup double in 2018/19.",
            listOf(Honour("2018/19", "Serbian double", "Men · league and cup"))
        ),
        Club(
            "ferencvaros-ftc", "Ferencváros / FTC", "budapest", "1899",
            "Founded in 1899 as a multisport club, FTC won its first water polo league title in 1910.",
            listOf(Honour("2024", "Champions League", "Men · winners"))
        ),
        Club(
            "vasas", "Vasas", "budapest", "1945",
            "Men’s Euro Cup winners in 2023.",
            listOf(Honour("2023", "Euro Cup", "Men · winners"))
        ),
        Club(
            "bvsc-zuglo", "BVSC-Zugló", "budapest", "1911",
            "The official section history lists seven men’s league titles and six men’s Hungarian Cups.",
            listOf(Honour("7 league · 6 cup", "Hungarian honours", "Men · official section archive"))
        ),
        Club(
            "szolnoki-dozsa", "Szolnoki Dózsa", "szolnok", "1921",
            "Men’s Champions League winners in 2017, defeating Jug 10–5.",
            listOf(Honour("2017", "Champions League", "Men · won final 10–5"))
        ),
        Club(
            "cn-atletic-barceloneta", "CN Atlètic-Barceloneta", "barcelona", "1992",
            "Two waterfront traditions joined in 1992. CN Atlètic’s roots date to 1913, with its first water polo team in 1915.",
            listOf(Honour("2014", "European champions", "Men · milestone title"))
        ),
        Club(
            "cn-barcelona", "CN Barcelona", "barcelona", "1907",
            "Men’s LEN Cup winners in 1995 and 2004.",
            listOf(Honour("1995 · 2004", "LEN Cup", "Men · winners"))
        ),
        Club(
            "cn-sabadell", "CN Sabadell", "sabadell", "1916",
            "Women’s Champions League winners in 2024.",
            listOf(Honour("2024", "Champions League", "Women · winners"))
        ),
        Club(
            "cn-terrassa", "CN Terrassa", "terrassa", "1932",
            "Founded on 3 June 1932; a long-established aquatic institution in Terrassa.",
            listOf(Honour("Since 1932", "Aquatic heritage", "Honours archive to be completed"))
        ),
        Club(
            "olympiacos", "Olympiacos", "piraeus", "1925",
            "Men’s Champions League winners in 2018.",
            listOf(Honour("2018", "Champions League", "Men · winners"))
        ),
        Club(
            "no-vouliagmeni", "NO Vouliagmeni", "vouliagmeni", "1937",
            "Men’s European Cup Winners’ Cup winners in 1997; women’s European champions in 2009 and 2010.",
            listOf(Honour("1997", "Cup Winners’ Cup", "Men · winners"), Honour("2009 · 2010", "European champions", "Women · winners"))
        ),
        Club(
            "panathinaikos", "Panathinaikos", "athens", "1928",
            "The water polo section dates to 1928. The men’s team won the A2 title in 2021.",
            listOf(Honour("2021", "Greek A2 title", "Men · promotion season"))
        ),
        Club(
            "panionios", "Panionios", "nea-smyrni", "1985",
            "Men’s LEN Cup finalists in 2009 and 2011.",
            listOf(Honour("2009 · 2011", "LEN Cup", "Men · finalists"))
        ),
        Club(
            "dinamo-bucuresti", "Dinamo București", "bucharest", "1948",
            "Dinamo’s record of 32 national water polo titles was reported by AGERPRES in 2018.",
            listOf(Honour("32 titles", "Romanian championship", "Historical total reported in 2018"))
        ),
        Club(
            "steaua-bucuresti", "Steaua București", "bucharest", "1949",
            "Won the Romanian men’s championship in 2024.",
            listOf(Honour("2024", "Romanian championship", "Men · title winner"))
        ),
        Club(
            "csm-oradea", "CSM Oradea", "oradea", "2003",
            "First national title in 2007; nine consecutive domestic titles, and a Euro Cup final appearance.",
            listOf(Honour("2007–2015", "Nine consecutive titles", "Men · Romanian championship"), Honour("2017", "Euro Cup", "Men · finalists"))
        ),
        Club(
            "pro-recco", "Pro Recco", "recco", "1913",
            "Founded in 1913 as Rari Nantes Enotria; won the 2022 Champions League.",
            listOf(Honour("2022", "Champions League", "Men · winners"))
        ),
        Club(
            "an-brescia", "AN Brescia", "brescia", "1973",
            "Founded in 1973; won the 2021 Italian championship and the 2024 Coppa Italia.",
            listOf(Honour("2021", "Italian championship", "Men · winners"), Honour("2024", "Coppa Italia", "Men · winners"))
        ),
        Club(
            "rari-nantes-savona", "Rari Nantes Savona", "savona", "1948",
            "Men’s LEN Cup winners in 2005, 2011 and 2012.",
            listOf(Honour("2005 · 2011 · 2012", "LEN Cup", "Men · winners"))
        ),
        Club(
            "cc-ortigia", "CC Ortigia", "syracuse", "1928",
            "Founded in 1928; named after the island forming Syracuse’s historic centre.",
            listOf(Honour("Since 1928", "Siracusa tradition", "Honours archive to be completed"))
        ),
        Club(
            "cn-marseille", "CN Marseille", "marseille", "1921",
            "Won the Euro Cup in 2019; celebrated its 40th national title in 2023.",
            listOf(Honour("2019", "Euro Cup", "Men · winners"), Honour("2023", "40th French title", "Historical milestone"))
        ),
        Club(
            "cn-noisy-le-sec-cercle-93", "CN Noisy-le-Sec / Cercle 93", "noisy", "",
            "The Noisy-le-Sec tradition is represented here through Cercle 93.",
            listOf(Honour("2024", "French championship", "Men · finalists"))
        ),
        Club(
            "enfants-de-neptune-tourcoing", "Enfants de Neptune Tourcoing", "tourcoing", "1904",
            "The club history records a French Cup win in 1988 and a return to Europe in 2020.",
            listOf(Honour("1988", "French Cup", "Men · winners"))
        ),
        Club(
            "ikai-kingfisher74", "IKAI / Kingfisher74", "yokohama", "2016",
            "Previously All-Nippon Sport Science University, the team adopted the Kingfisher74 name in 2016.",
            listOf(Honour("2025", "Japanese championship", "Men · seventh successive title"))
        ),
        Club(
            "pacer-water-polo-academy-pdc", "Pacer Water Polo Academy / PDC", "singapore", "2012",
            "Pacer’s timeline begins with six players on 4 March 2012. PDC is its elite training arm.",
            listOf(Honour("Since 2012", "Player development", "Academy · honours not yet verified"))
        )
    )

    fun city(id: String): City? = cities.firstOrNull { it.id == id }
    fun club(id: String): Club? = clubs.firstOrNull { it.id == id }
    fun clubsIn(cityId: String): List<Club> = clubs.filter { it.cityId == cityId }

    /** The countries in list order, for the Globe+ country filter. */
    val countries: List<String> by lazy { cities.map { it.country }.distinct() }

    /**
     * Clubs whose cities sit within half a degree of each other share one badge on the globe (the prototype's
     * CLUB_GROUPS): Barcelona, Sabadell and Terrassa, or the four clubs around Athens.
     */
    val clubGroups: List<ClubGroup> by lazy {
        val groups = ArrayList<ClubGroup>()
        clubs.forEach { club ->
            val city = city(club.cityId) ?: return@forEach
            val i = groups.indexOfFirst { kotlin.math.hypot(it.latitude - city.latitude, it.longitude - city.longitude) < 0.5f }
            if (i < 0) {
                groups += ClubGroup(city.latitude, city.longitude, listOf(club), listOf(city.name))
            } else {
                val g = groups[i]
                groups[i] = g.copy(clubs = g.clubs + club, cities = if (city.name in g.cities) g.cities else g.cities + city.name)
            }
        }
        groups
    }

    /** Crest monogram: "CN Atlètic-Barceloneta" -> "AB", like the prototype's monog. */
    fun monogram(name: String): String =
        name.replace(Regex("^(CN|VK|PVK|HAVK|AN|CC|CSM|NO|IKAI)\\s+"), "")
            .split(Regex("[\\s/-]+")).filter { it.isNotBlank() }.take(2)
            .joinToString("") { it.first().uppercase() }
}

/** Nearby clubs shown as one badge: where it sits, its clubs and their cities. */
data class ClubGroup(val latitude: Float, val longitude: Float, val clubs: List<Club>, val cities: List<String>)

/**
 * Whether the club fields a women's team. TODO: The prototype's stand-in rule until GWP confirms: clubs in
 * Romania and Montenegro are men's only, every other club has both.
 */
val Club.hasWomen: Boolean get() = WaterPoloMap.city(cityId)?.country !in setOf("Romania", "Montenegro")
