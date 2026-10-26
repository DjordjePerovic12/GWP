package com.global_water_polo.play_abroad.core.utils

import kotlinx.datetime.LocalDate
import com.global_water_polo.play_abroad.domain.model.Program
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Free year, earliest start and program recommendation, checked against the GWP prototype. */
class PathwayCalculatorTest {

    private val today = LocalDate(2026, 10, 26)
    private val dob = LocalDate(2008, 10, 3)

    @Test
    fun graduatingBeforeTheClockLeavesAFreeYear() {
        val f = PathwayCalculator.freeYear(dob, gradMonth = 6, gradYear = 2027, inCollege = false, today = today)
        assertEquals(LocalDate(2027, 10, 3), f.turns19)
        assertEquals(2028, f.clockYear)
        assertEquals(2027, f.college)
        assertEquals(1, f.free)
    }

    @Test
    fun graduatingTheFallTheClockStartsLeavesNoFreeYear() {
        val f = PathwayCalculator.freeYear(dob, gradMonth = 6, gradYear = 2028, inCollege = false, today = today)
        assertEquals(0, f.free)
    }

    @Test
    fun alreadyInCollegeMeansTheClockIsRunning() {
        val f = PathwayCalculator.freeYear(dob, gradMonth = 5, gradYear = 2027, inCollege = true, today = today)
        assertTrue(f.running)
        assertEquals(0, f.free)
    }

    @Test
    fun juniorOlympicsPushesTheStartToAugust() {
        assertEquals(8, PathwayCalculator.suggestStart(6, 2027, "", today))
        assertEquals(9, PathwayCalculator.suggestStart(6, 2027, "jo", today))
        assertEquals(10, PathwayCalculator.suggestStart(6, 2027, "summer", today))
        assertEquals(LocalDate(2027, 8, 1), PathwayCalculator.monthAt(9, today))
    }

    @Test
    fun aFreeYearAndABigGoalRecommendTheGapYear() {
        val f = PathwayCalculator.freeYear(dob, 6, 2027, false, today)
        assertEquals(Program.GAP_YEAR, PathwayCalculator.recommend(f, "pro", setOf("looking"), 9, today))
        assertEquals(Program.FALL, PathwayCalculator.recommend(f, "college", setOf("looking"), 9, today))
        assertEquals(Program.EXPLORE, PathwayCalculator.recommend(f, "college", setOf("unsure"), 9, today))
    }

    @Test
    fun seasonalProgramsRunInTheNextMatchingSeason() {
        assertEquals(2027, PathwayCalculator.seasonYear(Program.FALL, 9, today))
        assertEquals(2028, PathwayCalculator.seasonYear(Program.SPRING, 9, today))
    }
}
