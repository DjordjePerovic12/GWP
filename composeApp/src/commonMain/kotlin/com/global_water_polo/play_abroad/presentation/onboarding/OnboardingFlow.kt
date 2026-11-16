package com.global_water_polo.play_abroad.presentation.onboarding

import com.global_water_polo.play_abroad.core.navigation.Screen
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
    FAN
}

/** The one-tap question steps, all drawn by the shared ask screen. [multi] = pick any, otherwise pick one. */
enum class AskStep(val multi: Boolean) {
    NOW(multi = true),
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

private val FULL_FLOW = listOf(
    FlowStep.NOW, FlowStep.LEVEL, FlowStep.BIRTHDAY, FlowStep.CLOCK, FlowStep.NAME, FlowStep.GRAD, FlowStep.FREE,
    FlowStep.GOAL, FlowStep.CLUB, FlowStep.PROBE, FlowStep.STOP, FlowStep.TOUR, FlowStep.EMAIL, FlowStep.LINK,
    FlowStep.WORLD, FlowStep.TRIAL, FlowStep.MATCH
)

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
    // With the full five-year window left, every program fits: All offers replaces the single pathway.
    else -> FULL_FLOW
        .filter { it != FlowStep.PROBE || probeKind != null }
        .map { if (it == FlowStep.MATCH && fullWindow) FlowStep.OFFERS else it }
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
}
