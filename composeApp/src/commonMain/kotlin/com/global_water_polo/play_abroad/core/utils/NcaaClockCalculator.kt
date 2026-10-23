package com.global_water_polo.play_abroad.core.utils

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.todayIn
import com.global_water_polo.play_abroad.domain.model.BirthdayProblem
import com.global_water_polo.play_abroad.domain.model.ClockCountdown
import com.global_water_polo.play_abroad.domain.model.NcaaClock
import com.global_water_polo.play_abroad.domain.model.NcaaWindow
import kotlin.math.abs
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * NCAA five-year clock rules, ported 1:1 from the GWP prototype.
 *
 * The clock starts Aug 15 of the fall after the athlete turns 19 (born Jan-Aug: the year they turn 19,
 * Sep-Dec: the year after). Every date is read in the device's own time zone.
 */
object NcaaClockCalculator {

    private const val SEASONS = 5
    private const val MIN_AGE = 13
    private const val MAX_AGE = 26

    fun today(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDate =
        Clock.System.todayIn(timeZone)

    /** Birth years the wheel offers: 26 to 13 years ago, oldest first. */
    fun birthYears(today: LocalDate = today()): List<Int> = ((today.year - MAX_AGE)..(today.year - MIN_AGE)).toList()

    fun dateOrNull(year: Int, month: Int, day: Int): LocalDate? =
        runCatching { LocalDate(year, month, day) }.getOrNull()

    fun problem(year: Int, month: Int, day: Int, today: LocalDate = today()): BirthdayProblem? {
        val dob = dateOrNull(year, month, day) ?: return BirthdayProblem.INVALID_DATE
        if (dob > today) return BirthdayProblem.IN_FUTURE
        val age = ageOn(dob, today)
        if (age < MIN_AGE) return BirthdayProblem.TOO_YOUNG
        if (age > MAX_AGE) return BirthdayProblem.TOO_OLD
        return null
    }

    fun ageOn(dob: LocalDate, on: LocalDate): Int {
        var age = on.year - dob.year
        if (on.month.number < dob.month.number || (on.month == dob.month && on.day < dob.day)) age -= 1
        return age
    }

    fun clockOf(dob: LocalDate): NcaaClock {
        val year19 = dob.year + 19
        val year = if (dob.month.number <= 8) year19 else year19 + 1
        return NcaaClock(year = year, start = LocalDate(year, 8, 15), closes = year + SEASONS)
    }

    /** First fall the athlete could start college: this fall, or next fall once August has begun. */
    fun firstFall(today: LocalDate = today()): Int = if (today.month.number >= 8) today.year + 1 else today.year

    fun seasons(clock: NcaaClock, college: Int): Int {
        val start = if (college < clock.year) college else clock.year
        return (SEASONS - maxOf(0, college - start)).coerceIn(0, SEASONS)
    }

    fun window(dob: LocalDate, today: LocalDate = today()): NcaaWindow {
        val clock = clockOf(dob)
        val fall = firstFall(today)
        return NcaaWindow(
            clock = clock,
            collegeFall = fall,
            seasonsStraight = seasons(clock, fall),
            seasonsAbroad = seasons(clock, fall + 1)
        )
    }

    fun countdown(
        clock: NcaaClock,
        now: Instant = Clock.System.now(),
        timeZone: TimeZone = TimeZone.currentSystemDefault()
    ): ClockCountdown {
        val start = clock.start.atStartOfDayIn(timeZone)
        val totalSeconds = abs((start - now).inWholeSeconds)
        return ClockCountdown(
            running = start <= now,
            days = totalSeconds / 86_400,
            hours = ((totalSeconds % 86_400) / 3_600).toInt(),
            minutes = ((totalSeconds % 3_600) / 60).toInt(),
            seconds = (totalSeconds % 60).toInt()
        )
    }

    /** Latest valid day of the month, so the day wheel can grey out 30/31 in short months. */
    fun daysIn(year: Int, month: Int): Int {
        val next = if (month == 12) LocalDate(year + 1, 1, 1) else LocalDate(year, month + 1, 1)
        return next.minus(1, DateTimeUnit.DAY).day
    }
}
