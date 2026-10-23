package com.global_water_polo.play_abroad.core.navigation.utils

import androidx.navigation.NavOptionsBuilder
import com.global_water_polo.play_abroad.core.navigation.Screen

/**
  Sealed class representing different types of navigation actions.

  This class helps organize and type-safe handling of different navigation scenarios:

  Types:
  1. NavigateUp: Simple navigation up/back action
  2. NavigateToRoute: Navigation to a specific screen with optional configuration
     - destination: The target Screen to navigate to
     - navOptions: Optional configuration for how the navigation should behave
 **/

sealed class NavType {
    data object NavigateUp : NavType()

    data class NavigateToRoute(
        val destination: Screen,
        val navOptions: NavOptionsBuilder.() -> Unit = {}
    ) : NavType()
}
