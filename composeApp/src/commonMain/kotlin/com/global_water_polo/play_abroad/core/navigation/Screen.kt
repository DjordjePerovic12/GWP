package com.global_water_polo.play_abroad.core.navigation

import kotlinx.serialization.Serializable
import com.global_water_polo.play_abroad.domain.model.Lane

/**
This interface represents all navigation destinations in the app.
The sealed interface ensures type-safe navigation.
Onboarding steps are shared by several lanes (athlete, parent, coach, club) and take the lane as argument.
The lane travels as its name: enum arguments would need a custom NavType on every platform.
 **/

sealed interface Screen {

    @Serializable
    data object IntroScreen : Screen

    /** The 3D opener: the flight in from space to the GWP ball (custom/3Dmodel_test). */
    @Serializable
    data object OpenerScreen : Screen

    // Onboarding graph: every step shares OnboardingViewModel, scoped to this graph.
    @Serializable
    data object OnboardingGraph : Screen

    @Serializable
    data object WhoScreen : Screen

    /** "What do you coach?": college, high school or club, or club admin (the club lane). */
    @Serializable
    data object CoachTypeScreen : Screen

    // Coach paths (v29): college, high school or club, GWP staff.
    @Serializable
    data object CoachEmailScreen : Screen

    @Serializable
    data object CoachSchoolScreen : Screen

    @Serializable
    data object CoachRecruitScreen : Screen

    @Serializable
    data object CoachTeamScreen : Screen

    @Serializable
    data object CoachNeedsScreen : Screen

    @Serializable
    data object CoachInviteScreen : Screen

    @Serializable
    data object CoachStaffScreen : Screen

    /** "Follow the game": a fan's name and email, then the feed. */
    @Serializable
    data object FanScreen : Screen

    // One-tap questions, all drawn by the shared ask screen. Each has its own route: with one route for all of
    // them, launchSingleTop would swap one question for the next instead of stacking them (no Back, no animation).
    @Serializable
    data class NowScreen(val lane: String) : Screen {
        constructor(lane: Lane) : this(lane.name)
    }

    @Serializable
    data class LevelScreen(val lane: String) : Screen {
        constructor(lane: Lane) : this(lane.name)
    }

    @Serializable
    data class GoalScreen(val lane: String) : Screen {
        constructor(lane: Lane) : this(lane.name)
    }

    @Serializable
    data class ClubScreen(val lane: String) : Screen {
        constructor(lane: Lane) : this(lane.name)
    }

    @Serializable
    data class ProbeScreen(val lane: String) : Screen {
        constructor(lane: Lane) : this(lane.name)
    }

    @Serializable
    data class StopScreen(val lane: String) : Screen {
        constructor(lane: Lane) : this(lane.name)
    }

    @Serializable
    data class GradScreen(val lane: String) : Screen {
        constructor(lane: Lane) : this(lane.name)
    }

    @Serializable
    data class EnrollScreen(val lane: String) : Screen {
        constructor(lane: Lane) : this(lane.name)
    }
    @Serializable
    data class GradSuggestScreen(val lane: String) : Screen {
        constructor(lane: Lane) : this(lane.name)
    }
    @Serializable
    data class AvailScreen(val lane: String) : Screen {
        constructor(lane: Lane) : this(lane.name)
    }
    @Serializable
    data class LearnScreen(val lane: String) : Screen {
        constructor(lane: Lane) : this(lane.name)
    }
    @Serializable
    data class ReadyScreen(val lane: String) : Screen {
        constructor(lane: Lane) : this(lane.name)
    }

    @Serializable
    data class FreeYearScreen(val lane: String) : Screen {
        constructor(lane: Lane) : this(lane.name)
    }

    @Serializable
    data class TourScreen(val lane: String) : Screen {
        constructor(lane: Lane) : this(lane.name)
    }

    @Serializable
    data class LinkScreen(val lane: String) : Screen {
        constructor(lane: Lane) : this(lane.name)
    }

    @Serializable
    data class WorldScreen(val lane: String) : Screen {
        constructor(lane: Lane) : this(lane.name)
    }

    @Serializable
    data class TrialScreen(val lane: String) : Screen {
        constructor(lane: Lane) : this(lane.name)
    }

    @Serializable
    data class MatchScreen(val lane: String) : Screen {
        constructor(lane: Lane) : this(lane.name)
    }

    @Serializable
    data class OffersScreen(val lane: String) : Screen {
        constructor(lane: Lane) : this(lane.name)
    }

    @Serializable
    data object TripPlanScreen : Screen

    // Club lane
    @Serializable
    data object ClubPlaceScreen : Screen

    @Serializable
    data object FacilitiesScreen : Screen

    @Serializable
    data object ClubStaffScreen : Screen

    @Serializable
    data object ClubProfileScreen : Screen

    @Serializable
    data class BirthdayScreen(val lane: String) : Screen {
        constructor(lane: Lane) : this(lane.name)
    }

    @Serializable
    data class ClockScreen(val lane: String) : Screen {
        constructor(lane: Lane) : this(lane.name)
    }

    @Serializable
    data class NameScreen(val lane: String) : Screen {
        constructor(lane: Lane) : this(lane.name)
    }

    @Serializable
    data class EmailScreen(val lane: String) : Screen {
        constructor(lane: Lane) : this(lane.name)
    }

    @Serializable
    data class LookScreen(val lane: String) : Screen {
        constructor(lane: Lane) : this(lane.name)
    }

    @Serializable
    data class TravelScreen(val lane: String) : Screen {
        constructor(lane: Lane) : this(lane.name)
    }

    @Serializable
    data object InviteScreen : Screen

    @Serializable
    data class AccountScreen(val lane: String) : Screen {
        constructor(lane: Lane) : this(lane.name)
    }


    // App screens after onboarding

    @Serializable
    data object ParentHomeScreen : Screen

    @Serializable
    data object CoachHomeScreen : Screen

    /** "GWP is reviewing your account.": high school, club and staff coaches after sign-up. */
    @Serializable
    data object CoachPendingScreen : Screen

    /** The invite-only host club view (demo: from Quick start). */
    @Serializable
    data object HostScreen : Screen

    // Athlete app: one route per tab (v29: Home, Feed, Globe+, Inbox, Profile)
    @Serializable
    data object HomeScreen : Screen

    @Serializable
    data object InboxScreen : Screen

    /** [segment] opens a section ("card", "lab", "member", "account"); [preview] opens the card as coaches see it. */
    @Serializable
    data class ProfileScreen(val segment: String = "", val preview: Boolean = false) : Screen

    @Serializable
    data object FeedScreen : Screen

    /** [city] flies the globe to that city and opens its page; [trip] opens that trip's review instead. */
    @Serializable
    data class GlobeScreen(val city: String = "", val trip: Long = 0L) : Screen

    @Serializable
    data object FlightRadarScreen : Screen

    @Serializable
    data object SettingsScreen : Screen
}
