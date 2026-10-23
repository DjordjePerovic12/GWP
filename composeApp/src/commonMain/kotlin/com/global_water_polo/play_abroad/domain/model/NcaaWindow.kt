package com.global_water_polo.play_abroad.domain.model

import kotlinx.datetime.LocalDate

/**
 * The athlete's five-year NCAA window under the Division I and II age rule:
 * it opens on [start] (Aug 15 of [year]) and closes after Spring [closes].
 */
data class NcaaClock(
    val year: Int,
    val start: LocalDate,
    val closes: Int
)

/**
 * Planning result shown on the window screens.
 * [collegeFall] = the first fall the athlete could start college, [seasonsStraight] = seasons kept
 * going straight to college, [seasonsAbroad] = seasons kept after a year abroad first.
 */
data class NcaaWindow(
    val clock: NcaaClock,
    val collegeFall: Int,
    val seasonsStraight: Int,
    val seasonsAbroad: Int
)

enum class BirthdayProblem {
    INVALID_DATE,
    IN_FUTURE,
    TOO_YOUNG,
    TOO_OLD
}

/** Time until (or since) the clock starts, already split for display. */
data class ClockCountdown(
    val running: Boolean,
    val days: Long,
    val hours: Int,
    val minutes: Int,
    val seconds: Int
)
