package com.global_water_polo.play_abroad.core.utils

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** v29's lane engine, checked against the prototype's rules. */
class LaneCalculatorTest {

    private val today = LocalDate(2026, 11, 16)

    @Test
    fun under16BeforeTheWindowIsEarly() {
        val info = LaneCalculator.laneOf(LocalDate(2012, 3, 12), enrollment = null, pro = false, today = today)
        assertEquals(LaneKind.EARLY, info.lane)
        assertEquals(2031, info.clock.year)
    }

    @Test
    fun seniorWithTheWindowAheadIsApproaching() {
        val info = LaneCalculator.laneOf(LocalDate(2008, 10, 3), enrollment = null, pro = false, today = today)
        assertEquals(LaneKind.APPROACHING, info.lane)
        assertEquals(2028, info.clock.year)
        assertEquals(0, info.yearN)
    }

    @Test
    fun anOpenWindowCountsItsYear() {
        val info = LaneCalculator.laneOf(LocalDate(2006, 3, 9), enrollment = null, pro = false, today = today)
        assertEquals(LaneKind.RUNNING, info.lane)
        assertEquals(2025, info.clock.year)
        assertEquals(2, info.yearN)
    }

    @Test
    fun goingProOverridesTheWindow() {
        val info = LaneCalculator.laneOf(LocalDate(2008, 10, 3), enrollment = null, pro = true, today = today)
        assertEquals(LaneKind.PRO, info.lane)
    }

    @Test
    fun anOldWindowIsClosed() {
        val info = LaneCalculator.laneOf(LocalDate(1999, 1, 20), enrollment = null, pro = false, today = today)
        assertEquals(LaneKind.CLOSED, info.lane)
    }

    @Test
    fun enrollingBefore19OpensTheWindowEarly() {
        // Turns 19 in 2027 (window Fall 2027) but enrolled full time in January 2026.
        val (clock, byEnrollment) = LaneCalculator.windowOf(LocalDate(2008, 3, 1), LocalDate(2026, 1, 1))
        assertTrue(byEnrollment)
        assertEquals(2025, clock.year)
        assertEquals(LocalDate(2026, 1, 1), clock.start)
        assertEquals(2030, clock.closes)
    }

    @Test
    fun enrollingAfterTheAgeRuleChangesNothing() {
        val (clock, byEnrollment) = LaneCalculator.windowOf(LocalDate(2006, 3, 1), LocalDate(2026, 9, 1))
        assertFalse(byEnrollment)
        assertEquals(2025, clock.year)
    }

    @Test
    fun graduationIsGuessedFromTheBirthday() {
        assertEquals(6 to 2026, LaneCalculator.gradGuess(LocalDate(2008, 3, 1)))
        assertEquals(6 to 2027, LaneCalculator.gradGuess(LocalDate(2008, 10, 3)))
    }

    @Test
    fun summerGapAfterJuniorOlympics() {
        val gap = LaneCalculator.summerGap(6, 2027, "jo")!!
        assertEquals(LocalDate(2027, 7, 28), gap.from)
        assertEquals(LocalDate(2027, 8, 10), gap.to)
        assertEquals(2, gap.weeks)
    }

    @Test
    fun summerGapFromGraduation() {
        assertEquals(8, LaneCalculator.summerGap(6, 2027, "grad")!!.weeks)
    }

    @Test
    fun straightIntoPreseasonHasNoGap() {
        assertEquals(0, LaneCalculator.summerGap(6, 2027, "preseason")!!.weeks)
        assertNull(LaneCalculator.summerGap(6, 2027, ""))
    }

    @Test
    fun birthdayChecksMatchTheAnswers() {
        val noAnswers = emptySet<String>()
        assertEquals(BirthdayCheck.TOO_YOUNG, LaneCalculator.check(2020, 1, 1, parent = true, noAnswers, noAnswers, today))
        assertEquals(BirthdayCheck.NEEDS_PARENT, LaneCalculator.check(2015, 1, 1, parent = false, noAnswers, noAnswers, today))
        assertNull(LaneCalculator.check(2015, 1, 1, parent = true, noAnswers, noAnswers, today))
        assertEquals(BirthdayCheck.TOO_OLD, LaneCalculator.check(1990, 1, 1, parent = false, noAnswers, noAnswers, today))
        assertEquals(BirthdayCheck.OLD_FOR_HIGH_SCHOOL, LaneCalculator.check(2004, 1, 1, parent = false, noAnswers, setOf("hs"), today))
        assertEquals(BirthdayCheck.YOUNG_FOR_COLLEGE, LaneCalculator.check(2012, 1, 1, parent = false, setOf("college"), noAnswers, today))
        assertEquals(BirthdayCheck.OLD_FOR_COMMITTED, LaneCalculator.check(2001, 1, 1, parent = false, setOf("committed"), noAnswers, today))
        assertEquals(BirthdayCheck.INVALID_DATE, LaneCalculator.check(2008, 2, 30, parent = false, noAnswers, noAnswers, today))
        assertNull(LaneCalculator.check(2008, 10, 3, parent = false, setOf("committed"), setOf("hs", "club"), today))
    }

    @Test
    fun birthYearsRunFrom8To30NewestFirst() {
        val years = LaneCalculator.birthYears(today)
        assertEquals(2018, years.first())
        assertEquals(1996, years.last())
    }
}
