package com.global_water_polo.play_abroad.core.navigation.utils

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import com.global_water_polo.play_abroad.core.components.globe.GlobeMode
import com.global_water_polo.play_abroad.core.navigation.Screen

/** Where the globe sits for the screen on top of the back stack. */
fun NavDestination?.globeMode(): GlobeMode = when {
    this == null || hasRoute<Screen.IntroScreen>() -> GlobeMode.INTRO
    hasRoute<Screen.ClockScreen>() -> GlobeMode.CLOCK
    hasRoute<Screen.TourScreen>() || hasRoute<Screen.WorldScreen>() -> GlobeMode.REVEAL
    hasRoute<Screen.AthleteHomeScreen>() || hasRoute<Screen.ParentHomeScreen>() ||
        hasRoute<Screen.ApplyReviewScreen>() || hasRoute<Screen.ReviewStatusScreen>() ||
        hasRoute<Screen.CoachHomeScreen>() -> GlobeMode.HIDDEN
    else -> GlobeMode.BADGE
}
