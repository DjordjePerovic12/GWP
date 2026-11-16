package com.global_water_polo.play_abroad.core.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.datetime.number
import com.global_water_polo.play_abroad.domain.model.Lane
import com.global_water_polo.play_abroad.domain.model.NcaaClock
import com.global_water_polo.play_abroad.domain.model.Profile
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/** Where an athlete stands with the NCAA window (the prototype's lanes). */
enum class LaneKind {
    /** Under 16: the window is years away. */
    EARLY,

    /** 16 or older and the window hasn't opened: the countdown matters. */
    APPROACHING,

    /** The window is open: year 1 to 5. */
    RUNNING,

    /** Going pro: the NCAA window isn't part of the plan. */
    PRO,

    /** The window has closed. */
    CLOSED
}

/** The window as it applies to this athlete: opened by age or by an earlier full-time enrollment. */
data class LaneInfo(
    val lane: LaneKind,
    val age: Int,
    val clock: NcaaClock,
    val openedByEnrollment: Boolean,
    /** 1 to 5 while the window runs, else 0. */
    val yearN: Int
) {
    val running: Boolean get() = lane == LaneKind.RUNNING
}

/** What the summer between school and college looks like (the prototype's gapInfo). */
data class SummerGap(val from: LocalDate, val to: LocalDate, val weeks: Int)

/** Problems with a birthday given the other answers (the prototype's v29 problem()). */
enum class BirthdayCheck {
    INVALID_DATE,
    IN_FUTURE,
    /** Under 8: GWP programs start at 8. */
    TOO_YOUNG,
    /** An athlete under 13 needs a parent to sign up. */
    NEEDS_PARENT,
    /** Over 30. */
    TOO_OLD,
    /** 21 or older but playing high school water polo. */
    OLD_FOR_HIGH_SCHOOL,
    /** 15 or younger but in college. */
    YOUNG_FOR_COLLEGE,
    /** 24 or older but committed to or looking for a college program. */
    OLD_FOR_COMMITTED
}

/**
 * v29's lane engine: birthday, school status and the first full-time college enrollment decide what the NCAA
 * window means for this athlete, plus the summer gap and the suggested graduation. Ported from the prototype
 * (laneInfo, windowOf, gapInfo, gradGuess); dates are read in the device's time zone.
 */
object LaneCalculator {

    const val MIN_AGE = 8
    const val MAX_AGE = 30
    private const val PARENT_AGE = 13

    /** Birth years the wheel offers: 8 to 30 years ago, newest first so most people barely scroll. */
    fun birthYears(today: LocalDate = NcaaClockCalculator.today()): List<Int> =
        ((today.year - MIN_AGE) downTo (today.year - MAX_AGE)).toList()

    /**
     * Second look at a birthday: out of range, or an age that doesn't fit where they said they are.
     * [parent] = a parent entering their athlete's birthday.
     */
    fun check(
        year: Int,
        month: Int,
        day: Int,
        parent: Boolean,
        now: Set<String>,
        level: Set<String>,
        today: LocalDate = NcaaClockCalculator.today()
    ): BirthdayCheck? {
        val dob = NcaaClockCalculator.dateOrNull(year, month, day) ?: return BirthdayCheck.INVALID_DATE
        if (dob > today) return BirthdayCheck.IN_FUTURE
        val age = NcaaClockCalculator.ageOn(dob, today)
        return when {
            age < MIN_AGE -> BirthdayCheck.TOO_YOUNG
            !parent && age < PARENT_AGE -> BirthdayCheck.NEEDS_PARENT
            age > MAX_AGE -> BirthdayCheck.TOO_OLD
            age >= 21 && "hs" in level -> BirthdayCheck.OLD_FOR_HIGH_SCHOOL
            age <= 15 && ("college" in now || "college" in level) -> BirthdayCheck.YOUNG_FOR_COLLEGE
            age >= 24 && ("committed" in now || "looking" in now) -> BirthdayCheck.OLD_FOR_COMMITTED
            else -> null
        }
    }

    /**
     * The five-year window: the fall after turning 19, unless a full-time college enrollment came first; then
     * it opened at [enrollment], counted from that academic year.
     */
    fun windowOf(dob: LocalDate, enrollment: LocalDate?): Pair<NcaaClock, Boolean> {
        val byAge = NcaaClockCalculator.clockOf(dob)
        if (enrollment != null && enrollment < byAge.start) {
            val year = if (enrollment.month.number >= 8) enrollment.year else enrollment.year - 1
            return NcaaClock(year = year, start = enrollment, closes = year + 5) to true
        }
        return byAge to false
    }

    fun laneOf(
        dob: LocalDate,
        enrollment: LocalDate?,
        pro: Boolean,
        today: LocalDate = NcaaClockCalculator.today()
    ): LaneInfo {
        val (clock, byEnrollment) = windowOf(dob, enrollment)
        val age = NcaaClockCalculator.ageOn(dob, today)
        val end = LocalDate(clock.closes, 8, 15)
        val running = clock.start <= today
        val closed = today >= end
        val lane = when {
            pro -> LaneKind.PRO
            closed -> LaneKind.CLOSED
            running -> LaneKind.RUNNING
            age < 16 -> LaneKind.EARLY
            else -> LaneKind.APPROACHING
        }
        val yearN = if (running && !closed) min(5, clock.start.daysUntil(today) / 365 + 1) else 0
        return LaneInfo(lane, age, clock, byEnrollment, yearN)
    }

    /** Most athletes finish school in June the year they turn 18 (19 when born September to December). */
    fun gradGuess(dob: LocalDate): Pair<Int, Int> = 6 to dob.year + 18 + if (dob.month.number >= 9) 1 else 0

    /**
     * The summer between school and college preseason (about Aug 10): from graduation, or late July after
     * Junior Olympics ([availability] "jo"); none when they go straight into preseason.
     */
    fun summerGap(gradMonth: Int, gradYear: Int, availability: String): SummerGap? {
        if (availability.isEmpty()) return null
        val from = if (availability == "jo") LocalDate(gradYear, 7, 28) else LocalDate(gradYear, gradMonth, 15)
        val to = LocalDate(if (gradMonth > 8) gradYear + 1 else gradYear, 8, 10)
        if (availability == "preseason" || to <= from) return SummerGap(from, to, 0)
        return SummerGap(from, to, (from.daysUntil(to) / 7.0).roundToInt())
    }

    /** Seasons kept going to college in [college] (fall), never below 0 or above 5. */
    fun seasons(clock: NcaaClock, college: Int): Int = max(0, min(5, NcaaClockCalculator.seasons(clock, college)))
}

/** First full-time college enrollment, when they're in college and gave one. */
val Profile.enrollment: LocalDate?
    get() = if ("college" in now && enrollMonth != null && enrollYear != null) LocalDate(enrollYear, enrollMonth, 1) else null

/** Where this athlete stands with the NCAA window; null without a birthday. */
fun Profile.laneInfo(today: LocalDate = NcaaClockCalculator.today()): LaneInfo? =
    birthday?.let { LaneCalculator.laneOf(it, enrollment, pro = "pro" in now, today = today) }

/** The summer between school and preseason, from the saved finish and availability. */
val Profile.summerGap: SummerGap?
    get() = LaneCalculator.summerGap(gradMonth ?: return null, gradYear ?: return null, availability)

/** The year between school and the NCAA window, if there is one. */
val Profile.freeYear: FreeYear?
    get() = PathwayCalculator.freeYear(birthday ?: return null, gradMonth ?: return null, gradYear ?: return null, "college" in now)

/** An athlete under 18: a parent confirms the account. */
fun Profile.isMinor(today: LocalDate = NcaaClockCalculator.today()): Boolean =
    lane == Lane.ATHLETE && (birthday?.let { NcaaClockCalculator.ageOn(it, today) } ?: 99) < 18
