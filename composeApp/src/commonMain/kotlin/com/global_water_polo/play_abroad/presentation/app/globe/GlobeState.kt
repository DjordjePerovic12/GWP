package com.global_water_polo.play_abroad.presentation.app.globe

import com.global_water_polo.play_abroad.domain.model.City
import com.global_water_polo.play_abroad.domain.model.Club
import com.global_water_polo.play_abroad.domain.model.Profile
import com.global_water_polo.play_abroad.domain.model.Trip
import com.global_water_polo.play_abroad.domain.model.WaterPoloMap
import com.global_water_polo.play_abroad.presentation.base.BaseError
import com.global_water_polo.play_abroad.presentation.base.BaseViewState

/**
 * The page open over Globe+: a city with its clubs, one club, the experience builder with its date
 * picker and apartment tour, a trip's review, or the trips cart.
 */
sealed interface GlobeSheet {
    data class CityPage(val cityId: String) : GlobeSheet
    data class ClubPage(val clubId: String) : GlobeSheet
    data object Build : GlobeSheet
    data object Dates : GlobeSheet
    data object Tour : GlobeSheet
    data class Review(val tripId: Long) : GlobeSheet
    data object Trips : GlobeSheet
}

data class GlobeState(
    override val isLoading: Boolean = false,
    override val error: BaseError? = null,
    val profile: Profile = Profile(),
    val query: String = "",
    /** "eu", "am" or "world". */
    val region: String = "eu",
    val sheet: GlobeSheet? = null,
    /** The city the globe should turn to; changes every time a city or club is picked. */
    val focus: City? = null,
    /** The experience being built; saved to the profile's trips on Review. */
    val draft: Trip? = null
) : BaseViewState(isLoading, error) {

    /**
     * What the list shows (the prototype's gItems): a search matches city, country or club names anywhere;
     * without one, the cities of the chosen region.
     */
    val cities: List<City>
        get() {
            val q = query.trim().lowercase()
            return WaterPoloMap.cities.filter { city ->
                if (q.isEmpty()) city.region == region
                else city.name.lowercase().contains(q) || city.country.lowercase().contains(q) ||
                    WaterPoloMap.clubsIn(city.id).any { it.name.lowercase().contains(q) }
            }
        }

    val clubs: List<Club>
        get() {
            val q = query.trim().lowercase()
            val inCities = cities.map { it.id }.toSet()
            return WaterPoloMap.clubs.filter { club ->
                club.cityId in inCities && (q.isEmpty() || club.name.lowercase().contains(q) || WaterPoloMap.city(club.cityId)?.let {
                    it.name.lowercase().contains(q) || it.country.lowercase().contains(q)
                } == true)
            }
        }

    /** The trip already in the cart for [cityId] (a draft wins over a sent one), for the "In your trips" chip. */
    fun tripIn(cityId: String): Trip? = profile.trips.filter { it.cityId == cityId }.minByOrNull { it.sent }

    fun trip(id: Long): Trip? = profile.trips.find { it.id == id }
}
