package com.global_water_polo.play_abroad.presentation.app.travel

import com.global_water_polo.play_abroad.domain.model.City
import com.global_water_polo.play_abroad.domain.model.Profile
import com.global_water_polo.play_abroad.domain.model.WaterPoloMap
import com.global_water_polo.play_abroad.presentation.base.BaseError
import com.global_water_polo.play_abroad.presentation.base.BaseViewState

data class FlightRadarState(
    override val isLoading: Boolean = false,
    override val error: BaseError? = null,
    val profile: Profile = Profile()
) : BaseViewState(isLoading, error) {

    val trips: List<City> get() = profile.trips.mapNotNull { WaterPoloMap.city(it) }

    /** The route on the radar: the next trip, or a sample route to Herceg Novi before there is one. */
    val destination: City get() = trips.firstOrNull() ?: WaterPoloMap.city("herceg-novi")!!

    val home: Pair<Float, Float> get() = HOME_AIRPORTS[profile.homeAirport] ?: HOME_AIRPORTS.getValue("LAX")

    companion object {
        /** US home airports and where they are, like the prototype's HOMES. */
        val HOME_AIRPORTS = linkedMapOf(
            "LAX" to (33.94f to -118.41f),
            "SFO" to (37.62f to -122.38f),
            "SAN" to (32.73f to -117.19f),
            "IAH" to (29.98f to -95.34f),
            "MIA" to (25.79f to -80.29f),
            "ORD" to (41.97f to -87.9f),
            "JFK" to (40.64f to -73.78f)
        )
        val CHECKS = listOf("pass", "visa", "ins", "pick")
    }
}
