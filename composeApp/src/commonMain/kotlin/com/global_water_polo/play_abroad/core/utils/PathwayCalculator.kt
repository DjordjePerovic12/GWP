package com.global_water_polo.play_abroad.core.utils

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import kotlinx.datetime.plus
import com.global_water_polo.play_abroad.domain.model.Pathway
import com.global_water_polo.play_abroad.domain.model.PathwayStep
import com.global_water_polo.play_abroad.domain.model.Profile
import com.global_water_polo.play_abroad.domain.model.PathwayTiming
import com.global_water_polo.play_abroad.domain.model.PathwayWhy
import com.global_water_polo.play_abroad.domain.model.Program
import kotlin.math.max

/** The free-year reveal: time between leaving school and the NCAA clock that costs no college season. */
data class FreeYear(
    val turns19: LocalDate,
    val clockYear: Int,
    val college: Int,
    val running: Boolean,
    val free: Int,
    val straight: Int,
    val abroad: Int
)

/**
 * Pathway rules ported from the GWP prototype (freeInfo, suggestStart, recommend).
 * Start dates are counted in months from the first day of next month, like the prototype's S.start.
 */
object PathwayCalculator {

    private val BIG_GOALS = setOf("pro", "nt", "oly")

    fun freeYear(
        dob: LocalDate,
        gradMonth: Int,
        gradYear: Int,
        inCollege: Boolean,
        today: LocalDate = NcaaClockCalculator.today()
    ): FreeYear {
        val clock = NcaaClockCalculator.clockOf(dob)
        val gradFall = if (gradMonth <= 8) gradYear else gradYear + 1
        val college = max(gradFall, NcaaClockCalculator.firstFall(today))
        val running = inCollege || clock.start <= today
        return FreeYear(
            turns19 = dob.plus(DatePeriod(years = 19)),
            clockYear = clock.year,
            college = college,
            running = running,
            free = if (running) 0 else max(0, clock.year - college),
            straight = NcaaClockCalculator.seasons(clock, college),
            abroad = NcaaClockCalculator.seasons(clock, college + 1)
        )
    }

    /** First day of next month: start offset 0. */
    fun firstStartMonth(today: LocalDate = NcaaClockCalculator.today()): LocalDate =
        LocalDate(today.year, today.month.number, 1).plus(DatePeriod(months = 1))

    fun monthAt(offset: Int, today: LocalDate = NcaaClockCalculator.today()): LocalDate =
        firstStartMonth(today).plus(DatePeriod(months = offset))

    fun offsetOf(date: LocalDate, today: LocalDate = NcaaClockCalculator.today()): Int {
        val base = firstStartMonth(today)
        return (date.year - base.year) * 12 + date.month.number - base.month.number
    }

    /** Earliest start: the month after graduation, or after Junior Olympics / summer league. */
    fun suggestStart(gradMonth: Int, gradYear: Int, club: String, today: LocalDate = NcaaClockCalculator.today()): Int {
        var start = LocalDate(gradYear, gradMonth, 1).plus(DatePeriod(months = 1))
        if (club == "jo") start = maxOf(start, LocalDate(gradYear, 8, 1))
        if (club == "summer") start = maxOf(start, LocalDate(gradYear, 9, 1))
        return max(0, offsetOf(start, today))
    }

    fun recommend(
        freeYear: FreeYear?,
        goal: String,
        now: Set<String>,
        startOffset: Int?,
        today: LocalDate = NcaaClockCalculator.today()
    ): Program {
        val big = goal in BIG_GOALS || "beyond" in now || "ready" in now
        if (freeYear != null && freeYear.free >= 1 && big) return Program.GAP_YEAR
        if ("college" in now) return Program.SUMMER
        if ("unsure" in now) return Program.EXPLORE
        if (startOffset != null) {
            return when (monthAt(startOffset, today).month.number) {
                6, 7 -> Program.SUMMER
                8, 9 -> Program.FALL
                10, 11, 12, 1 -> Program.SPRING
                else -> Program.EXPLORE
            }
        }
        return Program.EXPLORE
    }

    /**
     * "Build my pathway" (the prototype's computePath): one step per length picked, in date order.
     * [education] is "hs", "grad" or "college"; [timing] holds "weeks", "summer", "semester", "year" or "unsure".
     */
    fun buildPathway(
        education: String,
        timing: Set<String>,
        gradMonth: Int?,
        gradYear: Int?,
        club: String,
        freeYear: FreeYear?,
        today: LocalDate = NcaaClockCalculator.today()
    ): List<PathwayStep> {
        val y = today.year
        val m = today.month.number
        val gy = gradYear ?: (y + 1)
        val summerYear = if (m <= 5) y else y + 1
        val after = if (education == "hs") gy else if (m >= 8) y + 1 else y
        val want = timing.ifEmpty { setOf("unsure") }
        val steps = mutableListOf<PathwayStep>()
        if ("weeks" in want || "unsure" in want) {
            val next = LocalDate(y, m, 1).plus(DatePeriod(months = 2))
            steps += PathwayStep(
                Program.EXPLORE, next.year, next.month.number, PathwayTiming.NEXT_BREAK,
                if (education == "college") PathwayWhy.BREAK_COLLEGE else PathwayWhy.BREAK
            )
        }
        if ("summer" in want) {
            steps += PathwayStep(Program.SUMMER, summerYear, 6, PathwayTiming.SUMMER, if (club == "jo") PathwayWhy.AFTER_JO else PathwayWhy.BETWEEN_YEARS)
        }
        if ("semester" in want) {
            steps += if (education == "college") {
                PathwayStep(Program.SPRING, y + 1, 1, PathwayTiming.SPRING, PathwayWhy.CHECK_CREDITS)
            } else {
                PathwayStep(Program.FALL, after, 8, PathwayTiming.FALL, if (education == "hs") PathwayWhy.AFTER_GRADUATION else PathwayWhy.CLUB_BLOCK)
            }
        }
        if ("year" in want) {
            val fits = freeYear != null && !freeYear.running && freeYear.free >= 1
            steps += PathwayStep(Program.GAP_YEAR, after, 8, PathwayTiming.SEASON, if (fits) PathwayWhy.FITS_BEFORE_CLOCK else PathwayWhy.CONFIRM_NCAA)
        }
        return steps.sortedWith(compareBy({ it.year }, { it.month }, { it.program.ordinal }))
    }

    /**
     * v29's plan (buildPath28 + computePath): one step from the first step picked in onboarding or in
     * "Adjust my plan". "summer" (the default) is the summer after school, "year" the year abroad, "fall",
     * "winter" and "spring" a ten-week season after school. In college, the next summer or fall from [today].
     */
    fun planSteps(
        firstStep: String,
        availability: String,
        gradMonth: Int?,
        gradYear: Int?,
        inCollege: Boolean,
        freeYear: FreeYear?,
        today: LocalDate = NcaaClockCalculator.today()
    ): List<PathwayStep> {
        val y = today.year
        val m = today.month.number
        val gy = gradYear ?: (y + 1)
        val gm = gradMonth ?: 6
        val summer = if (inCollege) (if (m <= 5) y else y + 1) else if (gm <= 8) gy else gy + 1
        val fall = if (inCollege) (if (m >= 8) y + 1 else y) else if (gm <= 8) gy else gy + 1
        val fits = freeYear != null && !freeYear.running && freeYear.free >= 1
        val seasonWhy = if (fits) PathwayWhy.IN_FREE_YEAR else PathwayWhy.CONFIRM_NCAA
        val step = when (firstStep) {
            "year" -> PathwayStep(Program.GAP_YEAR, fall, 8, PathwayTiming.SEASON, if (fits) PathwayWhy.FITS_BEFORE_CLOCK else PathwayWhy.CONFIRM_NCAA)
            "fall" -> PathwayStep(Program.FALL, fall, 9, PathwayTiming.FALL, seasonWhy, pick = "fall")
            "winter" -> PathwayStep(Program.SPRING, fall, 12, PathwayTiming.WINTER, seasonWhy, pick = "winter")
            "spring" -> PathwayStep(Program.SPRING, fall + 1, 3, PathwayTiming.SPRING, seasonWhy, pick = "spring")
            else -> PathwayStep(Program.SUMMER, summer, 6, PathwayTiming.SUMMER, if (availability == "jo") PathwayWhy.AFTER_JO else PathwayWhy.BETWEEN_YEARS)
        }
        return listOf(step)
    }

    /** The whole plan from the saved answers (v29's buildPath28). */
    fun planOf(p: Profile): Pathway {
        val college = "college" in p.now
        val steps = planSteps(
            firstStep = p.firstStep,
            availability = p.availability,
            gradMonth = p.gradMonth,
            gradYear = p.gradYear,
            inCollege = college,
            freeYear = p.freeYear
        )
        val education = when {
            college -> "college"
            p.laneInfo()?.let { it.age >= 19 } == true -> "grad"
            else -> "hs"
        }
        return Pathway(
            goal = p.goal,
            level = p.level.firstOrNull().orEmpty(),
            education = education,
            timing = setOfNotNull(p.firstStep.ifEmpty { null }),
            destinations = emptySet(),
            steps = steps
        )
    }

    /** Year a seasonal program would run for a given start, null for flexible programs. */
    fun seasonYear(program: Program, startOffset: Int, today: LocalDate = NcaaClockCalculator.today()): Int? {
        val seasonMonth = program.seasonStartMonth ?: return null
        val start = monthAt(startOffset, today)
        val lateBy = if (program == Program.FALL) 1 else 0
        return if (start.month.number > seasonMonth + lateBy) start.year + 1 else start.year
    }
}
