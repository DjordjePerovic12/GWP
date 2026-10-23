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

    // Onboarding graph: every step shares OnboardingViewModel, scoped to this graph.
    @Serializable
    data object OnboardingGraph : Screen

    @Serializable
    data object WhoScreen : Screen

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
    data class WindowScreen(val lane: String) : Screen {
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
    data class PositionScreen(val lane: String) : Screen {
        constructor(lane: Lane) : this(lane.name)
    }

    @Serializable
    data class SaveScreen(val lane: String) : Screen {
        constructor(lane: Lane) : this(lane.name)
    }

    @Serializable
    data object InviteScreen : Screen

    @Serializable
    data class AccountScreen(val lane: String) : Screen {
        constructor(lane: Lane) : this(lane.name)
    }

    @Serializable
    data object PendingScreen : Screen

    // App screens after onboarding
    @Serializable
    data object AthleteHomeScreen : Screen

    @Serializable
    data object ParentHomeScreen : Screen

    @Serializable
    data object ApplyReviewScreen : Screen

    @Serializable
    data object ReviewStatusScreen : Screen
}
