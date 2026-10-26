package com.global_water_polo.play_abroad.core.utils

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import kotlinx.datetime.plus
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

    /** Year a seasonal program would run for a given start, null for flexible programs. */
    fun seasonYear(program: Program, startOffset: Int, today: LocalDate = NcaaClockCalculator.today()): Int? {
        val seasonMonth = program.seasonStartMonth ?: return null
        val start = monthAt(startOffset, today)
        val lateBy = if (program == Program.FALL) 1 else 0
        return if (start.month.number > seasonMonth + lateBy) start.year + 1 else start.year
    }
}
