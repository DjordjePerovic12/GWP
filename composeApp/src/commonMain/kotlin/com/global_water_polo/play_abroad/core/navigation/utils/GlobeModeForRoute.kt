package com.global_water_polo.play_abroad.core.navigation.utils

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import com.global_water_polo.play_abroad.core.components.globe.GlobeMode
import com.global_water_polo.play_abroad.core.navigation.Screen

/** Where the globe sits for the screen on top of the back stack. */
fun NavDestination?.globeMode(): GlobeMode = when {
    // The 3D opener draws its own space and ball; the 2D stage stays out of sight.
    this?.hasRoute<Screen.OpenerScreen>() == true -> GlobeMode.HIDDEN
    // Before the first screen shows (the saved profile loading): no 2D ball or pool, the app opens on the opener.
    this == null -> GlobeMode.HIDDEN
    hasRoute<Screen.IntroScreen>() -> GlobeMode.INTRO
    hasRoute<Screen.ClockScreen>() -> GlobeMode.CLOCK
    hasRoute<Screen.TourScreen>() || hasRoute<Screen.WorldScreen>() -> GlobeMode.REVEAL
    hasRoute<Screen.GlobeScreen>() -> GlobeMode.EXPLORE
    hasRoute<Screen.ParentHomeScreen>() || hasRoute<Screen.ProfileScreen>() || hasRoute<Screen.FeedScreen>() ||
        hasRoute<Screen.FlightRadarScreen>() || hasRoute<Screen.SettingsScreen>() ||
        hasRoute<Screen.CoachHomeScreen>() -> GlobeMode.HIDDEN
    else -> GlobeMode.BADGE
}
