package com.global_water_polo.play_abroad.core.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import com.global_water_polo.play_abroad.domain.model.BirthdayProblem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/** NCAA clock rules, checked against the GWP prototype's numbers. */
class NcaaClockCalculatorTest {

    private val today = LocalDate(2026, 10, 23)

    @Test
    fun bornBeforeSeptemberStartsTheFallTheyTurn19() {
        val clock = NcaaClockCalculator.clockOf(LocalDate(2008, 3, 1))
        assertEquals(2027, clock.year)
        assertEquals(LocalDate(2027, 8, 15), clock.start)
        assertEquals(2032, clock.closes)
    }

    @Test
    fun bornFromSeptemberStartsTheFallAfter() {
        assertEquals(2028, NcaaClockCalculator.clockOf(LocalDate(2008, 10, 3)).year)
        assertEquals(2027, NcaaClockCalculator.clockOf(LocalDate(2008, 8, 31)).year)
    }

    @Test
    fun firstFallMovesToNextYearOnceAugustBegins() {
        assertEquals(2027, NcaaClockCalculator.firstFall(LocalDate(2026, 8, 1)))
        assertEquals(2026, NcaaClockCalculator.firstFall(LocalDate(2026, 7, 31)))
    }

    @Test
    fun windowKeepsAllSeasonsWhenCollegeStartsBeforeTheClock() {
        // Sample athlete from the prototype: Oct 3, 2008, clock 2028, college fall 2027.
        val w = NcaaClockCalculator.window(LocalDate(2008, 10, 3), today)
        assertEquals(2027, w.collegeFall)
        assertEquals(5, w.seasonsStraight)
        assertEquals(5, w.seasonsAbroad)
    }

    @Test
    fun yearAbroadUsesASeasonOnceTheClockRuns() {
        val w = NcaaClockCalculator.window(LocalDate(2007, 1, 10), today)
        assertEquals(2026, w.clock.year)
        assertEquals(4, w.seasonsStraight)
        assertEquals(3, w.seasonsAbroad)
    }

    @Test
    fun birthdayProblemsMatchThePrototype() {
        assertEquals(BirthdayProblem.INVALID_DATE, NcaaClockCalculator.problem(2008, 2, 31, today))
        assertEquals(BirthdayProblem.TOO_YOUNG, NcaaClockCalculator.problem(2015, 1, 1, today))
        assertEquals(BirthdayProblem.TOO_OLD, NcaaClockCalculator.problem(1995, 1, 1, today))
        assertEquals(BirthdayProblem.IN_FUTURE, NcaaClockCalculator.problem(2027, 1, 1, today))
        assertNull(NcaaClockCalculator.problem(2008, 10, 3, today))
    }

    @Test
    fun countdownSplitsDaysAndTimeInTheGivenTimeZone() {
        val zone = TimeZone.of("Europe/Podgorica")
        val clock = NcaaClockCalculator.clockOf(LocalDate(2008, 10, 3))
        val now = clock.start.atStartOfDayIn(zone) - (10.days + 5.hours + 4.minutes + 3.seconds)
        val c = NcaaClockCalculator.countdown(clock, now, zone)
        assertFalse(c.running)
        assertEquals(10L, c.days)
        assertEquals(5, c.hours)
        assertEquals(4, c.minutes)
        assertEquals(3, c.seconds)
        assertEquals("10d 05:04:03", clockText(c))
    }

    @Test
    fun countdownRunsOnceTheClockHasStarted() {
        val zone = TimeZone.UTC
        val clock = NcaaClockCalculator.clockOf(LocalDate(2005, 1, 1))
        val c = NcaaClockCalculator.countdown(clock, clock.start.atStartOfDayIn(zone) + 2.days, zone)
        assertTrue(c.running)
        assertEquals(2L, c.days)
    }

    @Test
    fun timeZoneLabelsFollowTheFacingMeridian() {
        assertEquals("UTC-3", TimeZoneLabels.tzLabel(TimeZoneLabels.facing(-50f)))
        assertEquals("40°W", TimeZoneLabels.lonLabel(TimeZoneLabels.facing(-50f)))
        assertEquals("180°", TimeZoneLabels.lonLabel(180f))
        assertEquals("UTC", TimeZoneLabels.tzLabel(0f))
    }
}
