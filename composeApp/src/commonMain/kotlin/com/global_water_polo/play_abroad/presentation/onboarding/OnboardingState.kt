package com.global_water_polo.play_abroad.presentation.onboarding

import kotlinx.datetime.LocalDate
import com.global_water_polo.play_abroad.core.utils.FreeYear
import com.global_water_polo.play_abroad.core.utils.PathwayCalculator
import com.global_water_polo.play_abroad.core.utils.BirthdayCheck
import com.global_water_polo.play_abroad.core.utils.LaneCalculator
import com.global_water_polo.play_abroad.core.utils.LaneInfo
import com.global_water_polo.play_abroad.core.utils.SummerGap
import com.global_water_polo.play_abroad.domain.model.ClockCountdown
import com.global_water_polo.play_abroad.domain.model.Lane
import com.global_water_polo.play_abroad.domain.model.NcaaWindow
import com.global_water_polo.play_abroad.domain.model.Program
import com.global_water_polo.play_abroad.presentation.base.BaseError
import com.global_water_polo.play_abroad.presentation.base.BaseViewState

/** Everything typed or picked across the onboarding steps of one lane. Shared by every step screen. */
data class OnboardingState(
    override val isLoading: Boolean = false,
    override val error: BaseError? = null,
    val lane: Lane = Lane.ATHLETE,
    /** What a coach coaches: "college" or "team" (club admins move to the club lane). */
    val coachTier: String = "",
    val now: Set<String> = emptySet(),
    val level: Set<String> = emptySet(),
    val birthMonth: Int? = null,
    val birthDay: Int? = null,
    val birthYear: Int? = null,
    val birthdayProblem: BirthdayCheck? = null,
    /** First full-time college enrollment (only asked when already in college); it can open the window early. */
    val enrollMonth: Int? = null,
    val enrollYear: Int? = null,
    val window: NcaaWindow? = null,
    val countdown: ClockCountdown? = null,
    val firstName: String = "",
    val lastName: String = "",
    val gradMonth: Int? = null,
    val gradYear: Int? = null,
    /** "Different date" on the suggested finish, or "already finished": the month and year wheels. */
    val gradCustom: Boolean = false,
    val gradDone: Boolean = false,
    /** When are you free: "grad" (right after school), "jo" (after Junior Olympics) or "preseason". */
    val availability: String = "",
    /** The first step picked under the summer gap: summer, fall, winter, spring or year. */
    val firstStep: String = "",
    /** Which of the three cards before the clock is showing, 0 to 2. */
    val learnIndex: Int = 0,
    /** Under 18: a parent's email to confirm the account. */
    val parentEmail: String = "",
    val goal: String = "",
    val club: String = "",
    val probe: String = "",
    val stops: Set<String> = emptySet(),
    val email: String = "",
    val linkResent: Boolean = false,
    val heardFrom: String = "",
    val relation: String = "",
    val trialPick: String = PLAN_ELITE,
    val plan: String? = null,
    val program: String = "",
    val applied: Boolean = false,
    val seen: Set<FlowStep> = emptySet(),
    val kidName: String = "",
    val kidContact: String = "",
    val invited: Boolean = false,
    val organization: String = "",
    val clubCity: String = "",
    val clubCountry: String = "",
    val clubPlaced: Boolean = false,
    val facilities: Set<String> = emptySet(),
    val staffCoach: String = "",
    val staffSize: String = "",
    val languages: Set<String> = emptySet(),
    val looking: Set<String> = emptySet(),
    val travelStartOffset: Int? = null,
    val travelMonths: Int = 0
) : BaseViewState(isLoading, error) {

    val birthday: LocalDate?
        get() = if (birthYear != null && birthMonth != null && birthDay != null && birthdayProblem == null) {
            LocalDate(birthYear, birthMonth, birthDay)
        } else null

    val isTrip: Boolean get() = lane == Lane.PARENT && "trip" in now
    val isParent: Boolean get() = lane == Lane.PARENT

    val enrollment: LocalDate?
        get() = if (inCollege && enrollMonth != null && enrollYear != null) LocalDate(enrollYear, enrollMonth, 1) else null

    /** Where the athlete stands with the NCAA window; null until the birthday is in. */
    val laneInfo: LaneInfo? get() = birthday?.let { LaneCalculator.laneOf(it, enrollment, pro = "pro" in now) }

    val summerGap: SummerGap?
        get() = LaneCalculator.summerGap(gradMonth ?: return null, gradYear ?: return null, availability)

    /** An athlete under 18: a parent confirms the account. */
    val isMinor: Boolean get() = lane == Lane.ATHLETE && (laneInfo?.age ?: 99) < 18
    val isParentEmailValid: Boolean get() = EMAIL.matches(parentEmail.trim())
    val canSendPlan: Boolean get() = isEmailValid && (!isMinor || isParentEmailValid)
    val isEnrollSet: Boolean get() = enrollMonth != null && enrollYear != null
    val inCollege: Boolean get() = "college" in now

    val freeYear: FreeYear?
        get() {
            val dob = birthday ?: return null
            val month = gradMonth ?: return null
            val year = gradYear ?: return null
            return PathwayCalculator.freeYear(dob, month, year, inCollege)
        }

    val probeKind: ProbeKind?
        get() = when {
            lane == Lane.PARENT -> ProbeKind.PARENT
            "committed" in now || "ready" in now -> ProbeKind.COMMITTED
            "looking" in now -> ProbeKind.LOOKING
            "beyond" in now -> ProbeKind.BEYOND
            else -> null
        }

    /** The program on the pathway screen: the one picked, or GWP's recommendation. */
    val pathwayProgram: Program
        get() = Program.of(program) ?: PathwayCalculator.recommend(freeYear, goal, now, travelStartOffset)

    val isElite: Boolean get() = plan == PLAN_ELITE

    /** All five NCAA seasons still in hand when the athlete leaves school (the prototype's fullWindow). */
    val fullWindow: Boolean get() = freeYear?.let { !it.running && it.straight >= 5 } == true

    /** The program picked on All offers, if the plan allows applying for it. */
    val offerPick: Program?
        get() = Program.of(program)?.takeIf { plan != null && (!it.needsElite || isElite) }

    val canPlaceClub: Boolean get() = organization.isNotBlank() && clubCountry.isNotEmpty()
    val canStaffContinue: Boolean get() = staffCoach.isNotBlank()

    fun picks(step: AskStep): Set<String> = when (step) {
        AskStep.NOW -> now
        AskStep.LEVEL -> level
        AskStep.GOAL -> setOf(goal) - ""
        AskStep.CLUB -> setOf(club) - ""
        AskStep.PROBE -> setOf(probe) - ""
        AskStep.STOP -> stops
        AskStep.FACILITIES -> facilities
    }

    val isNameValid: Boolean get() = firstName.isNotBlank() && lastName.isNotBlank()
    val isEmailValid: Boolean get() = EMAIL.matches(email.trim())
    val canInvite: Boolean get() = kidName.isNotBlank() && (inviteByEmail || kidContact.count { it.isDigit() } >= 7)
    val inviteByEmail: Boolean get() = EMAIL.matches(kidContact.trim())
    val canCreateAccount: Boolean get() = firstName.isNotBlank() && organization.isNotBlank() && isEmailValid
    val canTravelContinue: Boolean get() = travelStartOffset != null && travelMonths > 0
    val isGradSet: Boolean get() = gradMonth != null && gradYear != null

    companion object {
        const val PLAN_ELITE = "elite"
        const val PLAN_NEXT = "next"
        private val EMAIL = Regex(".+@.+\\..+")
    }
}
