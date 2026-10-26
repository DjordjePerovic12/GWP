package com.global_water_polo.play_abroad.presentation.onboarding.tour.components

/** A stop on the globe tour: where GWP clubs and programs are. */
data class TourCity(val name: String, val country: String, val latitude: Float, val longitude: Float, val flag: String)

/** The prototype's TOUR list, in the order the globe visits them. Place names stay in English. */
val TOUR_CITIES = listOf(
    TourCity("Herceg Novi", "Montenegro", 42.45f, 18.54f, "🇲🇪"),
    TourCity("Split", "Croatia", 43.51f, 16.44f, "🇭🇷"),
    TourCity("Belgrade", "Serbia", 44.8f, 20.47f, "🇷🇸"),
    TourCity("Budapest", "Hungary", 47.5f, 19.04f, "🇭🇺"),
    TourCity("Bucharest", "Romania", 44.43f, 26.11f, "🇷🇴"),
    TourCity("Athens", "Greece", 37.98f, 23.73f, "🇬🇷"),
    TourCity("Recco", "Italy", 44.36f, 9.14f, "🇮🇹"),
    TourCity("Marseille", "France", 43.3f, 5.38f, "🇫🇷"),
    TourCity("Barcelona", "Spain", 41.39f, 2.16f, "🇪🇸"),
    TourCity("San Juan", "Puerto Rico", 18.47f, -66.11f, "🇵🇷"),
    TourCity("Miami", "United States", 25.76f, -80.19f, "🇺🇸"),
    TourCity("Texas", "United States", 29.76f, -95.37f, "🇺🇸"),
    TourCity("Chicago", "United States", 41.88f, -87.63f, "🇺🇸"),
    TourCity("Northern California", "United States", 37.77f, -122.42f, "🇺🇸"),
    TourCity("Southern California", "United States", 34.05f, -118.24f, "🇺🇸"),
    TourCity("Yokohama", "Japan", 35.43f, 139.65f, "🇯🇵"),
    TourCity("Singapore", "Singapore", 1.29f, 103.85f, "🇸🇬"),
    TourCity("Harare", "Zimbabwe", -17.83f, 31.05f, "🇿🇼")
)

/** Where the tour starts and ends: the middle of Europe's water polo map. */
const val TOUR_HOME_LONGITUDE = 15f
const val TOUR_HOME_LATITUDE = 43f
