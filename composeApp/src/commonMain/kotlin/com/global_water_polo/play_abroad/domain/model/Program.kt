package com.global_water_polo.play_abroad.domain.model

/**
 * GWP programs abroad, from the prototype's PROGRAMS list.
 * [seasonStartMonth] is the 1-based month a seasonal program starts (null = flexible dates).
 * Every program except Explore needs GWP Elite.
 */
enum class Program(
    val key: String,
    val months: Int,
    val flagship: Boolean = false,
    val seasonStartMonth: Int? = null
) {
    EXPLORE("explore", months = 1),
    FALL("fall", months = 4, seasonStartMonth = 8),
    SPRING("spring", months = 5, seasonStartMonth = 1),
    SUMMER("summer", months = 3, seasonStartMonth = 6),
    GAP_YEAR("gapyear", months = 10, flagship = true);

    val needsElite: Boolean get() = this != EXPLORE

    companion object {
        fun of(key: String): Program? = entries.firstOrNull { it.key == key }
    }
}
