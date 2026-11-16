package com.global_water_polo.play_abroad.presentation.onboarding

import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.core.utils.LaneKind
import com.global_water_polo.play_abroad.domain.model.Lane

/** Every onboarding step of every lane, in the prototype's final order (BASEFLOW, FLOW.c, FLOW.k, FLOW.trip). */
enum class FlowStep {
    NOW,
    LEVEL,
    BIRTHDAY,
    CLOCK,
    NAME,
    GRAD,
    FREE,
    GOAL,
    CLUB,
    PROBE,
    STOP,
    TOUR,
    EMAIL,
    LINK,
    WORLD,
    TRIAL,
    MATCH,
    OFFERS,
    TRAVEL,
    TRIP_PLAN,
    LOOK,
    ACCOUNT,
    PLACE,
    FACILITIES,
    STAFF,
    CLUB_PROFILE,
    COACH_TYPE,
    FAN,
    ENROLL,
    GRAD_SUGGEST,
    AVAIL,
    LEARN,
    READY
}

/** The one-tap question steps, all drawn by the shared ask screen. [multi] = pick any, otherwise pick one. */
enum class AskStep(val multi: Boolean) {
    NOW(multi = false),
    LEVEL(multi = true),
    GOAL(multi = false),
    CLUB(multi = false),
    PROBE(multi = false),
    STOP(multi = true),
    FACILITIES(multi = true);

    val flowStep: FlowStep get() = FlowStep.valueOf(name)
}

/** Which follow-up question the probe step asks; it depends on "Where are you now" (athletes) or is fixed (parents). */
enum class ProbeKind {
    PARENT,
    COMMITTED,
    LOOKING,
    BEYOND
}

/**
 * v29's order for athletes and parents: ask everything first, then one big reveal. Steps that don't apply to
 * the athlete's lane drop out (no clock when going pro or the window closed, no cards once it runs).
 */
private fun OnboardingState.laneFlow(): List<FlowStep> {
    val lane = laneInfo?.lane
    val college = inCollege
    return buildList {
        add(FlowStep.NOW)
        if (college) add(FlowStep.ENROLL)
        add(FlowStep.LEVEL)
        add(FlowStep.NAME)
        add(FlowStep.BIRTHDAY)
        if (lane != LaneKind.CLOSED) {
            add(if (college) FlowStep.GRAD else FlowStep.GRAD_SUGGEST)
            if (!college && gradCustom) add(FlowStep.GRAD)
        }
        if (lane != LaneKind.PRO && lane != LaneKind.CLOSED) add(FlowStep.AVAIL)
        if (lane != LaneKind.RUNNING && lane != LaneKind.PRO && lane != LaneKind.CLOSED && !college) add(FlowStep.LEARN)
        if (lane != LaneKind.PRO && lane != LaneKind.CLOSED) add(FlowStep.CLOCK)
        add(FlowStep.TOUR)
        add(FlowStep.READY)
        add(FlowStep.EMAIL)
    }
}

// A parent planning a team trip skips the athlete's clock: who, dates, email, the inquiry, done.
private val TRIP_FLOW = listOf(FlowStep.NOW, FlowStep.NAME, FlowStep.TRAVEL, FlowStep.EMAIL, FlowStep.TRIP_PLAN, FlowStep.LINK)

private val CLUB_FLOW = listOf(FlowStep.LOOK, FlowStep.PLACE, FlowStep.FACILITIES, FlowStep.STAFF, FlowStep.ACCOUNT, FlowStep.CLUB_PROFILE)

fun OnboardingState.flow(): List<FlowStep> = when {
    lane == Lane.CLUB -> CLUB_FLOW
    lane == Lane.FAN -> listOf(FlowStep.FAN)
    // Coaches planning a trip or a tournament pick dates before their scouting board.
    lane == Lane.COACH -> listOfNotNull(
        FlowStep.COACH_TYPE,
        FlowStep.LOOK,
        FlowStep.ACCOUNT,
        if ("trip" in looking || "tournament" in looking) FlowStep.TRAVEL else null
    )
    isTrip -> TRIP_FLOW
    else -> laneFlow()
}

/** Share of the flow done on [step], for the progress bar. */
fun OnboardingState.progress(step: FlowStep): Float {
    val steps = flow()
    val index = steps.indexOf(step)
    return if (index < 0) 0f else (index + 1f) / steps.size
}

fun FlowStep.route(lane: Lane): Screen = when (this) {
    FlowStep.NOW -> Screen.NowScreen(lane)
    FlowStep.LEVEL -> Screen.LevelScreen(lane)
    FlowStep.GOAL -> Screen.GoalScreen(lane)
    FlowStep.CLUB -> Screen.ClubScreen(lane)
    FlowStep.PROBE -> Screen.ProbeScreen(lane)
    FlowStep.STOP -> Screen.StopScreen(lane)
    FlowStep.BIRTHDAY -> Screen.BirthdayScreen(lane)
    FlowStep.CLOCK -> Screen.ClockScreen(lane)
    FlowStep.NAME -> Screen.NameScreen(lane)
    FlowStep.GRAD -> Screen.GradScreen(lane)
    FlowStep.FREE -> Screen.FreeYearScreen(lane)
    FlowStep.TOUR -> Screen.TourScreen(lane)
    FlowStep.EMAIL -> Screen.EmailScreen(lane)
    FlowStep.LINK -> Screen.LinkScreen(lane)
    FlowStep.WORLD -> Screen.WorldScreen(lane)
    FlowStep.TRIAL -> Screen.TrialScreen(lane)
    FlowStep.MATCH -> Screen.MatchScreen(lane)
    FlowStep.OFFERS -> Screen.OffersScreen(lane)
    FlowStep.TRAVEL -> Screen.TravelScreen(lane)
    FlowStep.TRIP_PLAN -> Screen.TripPlanScreen
    FlowStep.LOOK -> Screen.LookScreen(lane)
    FlowStep.ACCOUNT -> Screen.AccountScreen(lane)
    FlowStep.PLACE -> Screen.ClubPlaceScreen
    FlowStep.FACILITIES -> Screen.FacilitiesScreen
    FlowStep.STAFF -> Screen.ClubStaffScreen
    FlowStep.CLUB_PROFILE -> Screen.ClubProfileScreen
    FlowStep.COACH_TYPE -> Screen.CoachTypeScreen
    FlowStep.FAN -> Screen.FanScreen
    FlowStep.ENROLL -> Screen.EnrollScreen(lane)
    FlowStep.GRAD_SUGGEST -> Screen.GradSuggestScreen(lane)
    FlowStep.AVAIL -> Screen.AvailScreen(lane)
    FlowStep.LEARN -> Screen.LearnScreen(lane)
    FlowStep.READY -> Screen.ReadyScreen(lane)
}
