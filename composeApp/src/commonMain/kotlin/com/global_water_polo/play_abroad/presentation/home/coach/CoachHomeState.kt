package com.global_water_polo.play_abroad.presentation.home.coach

import com.global_water_polo.play_abroad.presentation.base.BaseError
import com.global_water_polo.play_abroad.presentation.base.BaseViewState

enum class ScoutFilter {
    ALL,
    DRIVERS,
    CENTERS,
    GOALKEEPERS,
    LEFT_HANDED
}

/** One sample athlete on the scouting board (the prototype's SCOUT rows). */
data class ScoutProfile(
    val id: String,
    val position: String,
    val leftHanded: Boolean,
    val classOf: String,
    val height: String,
    val free50: String,
    val program: String
)

data class CoachHomeState(
    override val isLoading: Boolean = false,
    override val error: BaseError? = null,
    val firstName: String = "",
    val looking: Set<String> = emptySet(),
    val filter: ScoutFilter = ScoutFilter.ALL,
    val requested: Set<String> = emptySet(),
    val profiles: List<ScoutProfile> = SAMPLE_PROFILES
) : BaseViewState(isLoading, error) {

    val visibleProfiles: List<ScoutProfile>
        get() = profiles.filter {
            when (filter) {
                ScoutFilter.ALL -> true
                ScoutFilter.DRIVERS -> it.position == "Driver"
                ScoutFilter.CENTERS -> it.position == "Center"
                ScoutFilter.GOALKEEPERS -> it.position == "Goalkeeper"
                ScoutFilter.LEFT_HANDED -> it.leftHanded
            }
        }

    /** Trip and tournament coaches get a note that a GWP trip lead will be in touch. */
    val showTripNote: Boolean get() = "trip" in looking || "tournament" in looking

    companion object {
        //TODO: Sample data until athletes share real profiles with coaches.
        val SAMPLE_PROFILES = listOf(
            ScoutProfile("JM", "Driver", true, "2027", "6'3\"", "24.9", "GWP Fall Semester"),
            ScoutProfile("AK", "Center", false, "2026", "6'5\"", "25.8", "GWP Elite Gap Year"),
            ScoutProfile("DR", "Goalkeeper", false, "2028", "6'2\"", "27.1", "GWP Explore"),
            ScoutProfile("TS", "Utility", true, "2027", "6'1\"", "24.4", "GWP Summer Abroad")
        )
    }
}
