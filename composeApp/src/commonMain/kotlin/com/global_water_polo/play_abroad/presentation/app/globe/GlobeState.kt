package com.global_water_polo.play_abroad.presentation.app.globe

import com.global_water_polo.play_abroad.domain.model.City
import com.global_water_polo.play_abroad.domain.model.Club
import com.global_water_polo.play_abroad.domain.model.Profile
import com.global_water_polo.play_abroad.domain.model.Trip
import com.global_water_polo.play_abroad.domain.model.WaterPoloMap
import com.global_water_polo.play_abroad.domain.model.hasWomen
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
    val draft: Trip? = null,
    /** Country filter; empty = all countries. */
    val country: String = "",
    /** "men", "women" or empty for both. */
    val sex: String = "",
    /** The club group pop-up open over the globe, by its index in WaterPoloMap.clubGroups. */
    val popup: Int? = null
) : BaseViewState(isLoading, error) {

    /**
     * What the lists show (the prototype's gItems): the chosen country, or the region when nothing narrows it;
     * a search matches city, country or club names; Men's or Women's keeps only cities with such clubs.
     */
    val cities: List<City>
        get() {
            val q = query.trim().lowercase()
            val narrowed = q.isNotEmpty() || country.isNotEmpty() || sex.isNotEmpty()
            return WaterPoloMap.cities.filter { city ->
                (country.isEmpty() || city.country == country) && (narrowed || city.region == region) &&
                    (cityMatches(city, q) || clubsOf(city, q).isNotEmpty()) && (sex.isEmpty() || clubsOf(city, "").isNotEmpty())
            }
        }

    val clubs: List<Club> get() = query.trim().lowercase().let { q -> cities.flatMap { clubsOf(it, q) } }

    private fun cityMatches(city: City, q: String) = q.isEmpty() || city.name.lowercase().contains(q) || city.country.lowercase().contains(q)

    /** The city's clubs that pass the Men's or Women's filter, and the search unless the city itself matches. */
    private fun clubsOf(city: City, q: String): List<Club> = WaterPoloMap.clubsIn(city.id).filter { club ->
        (sex != "women" || club.hasWomen) && (cityMatches(city, q) || club.name.lowercase().contains(q))
    }

    /** The trip already in the cart for [cityId] (a draft wins over a sent one), for the "In your trips" chip. */
    fun tripIn(cityId: String): Trip? = profile.trips.filter { it.cityId == cityId }.minByOrNull { it.sent }

    fun trip(id: Long): Trip? = profile.trips.find { it.id == id }
}
