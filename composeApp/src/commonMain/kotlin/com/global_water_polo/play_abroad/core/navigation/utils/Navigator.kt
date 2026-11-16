package com.global_water_polo.play_abroad.core.navigation.utils

import androidx.navigation.NavOptionsBuilder
import kotlinx.coroutines.flow.Flow
import com.global_water_polo.play_abroad.core.navigation.Screen

/**
  Interface defining navigation operations for the app.

  This interface provides a contract for handling navigation events through a Flow-based approach,
  making it easier to manage navigation in a clean and testable way.

  Properties:
  - navigationFlow: A Flow that emits navigation events (NavType)

  Methods:
  - navigateTo: Navigate to a specific screen with optional navigation options
  - navigateUp: Navigate up (back) in the navigation stack
 **/

interface Navigator {
    val navigationFlow: Flow<NavType>
    suspend fun navigateTo(
        destination: Screen,
        navOptions: NavOptionsBuilder.() -> Unit = {}
    )

    suspend fun navigateUp()
}

/**
 * Switches the athlete app's tab: Home is the root of the tabs, so every other tab sits right on top of it
 * and Back from any tab lands on Home.
 */
suspend fun Navigator.openTab(route: Screen) = navigateTo(route) {
    popUpTo<Screen.HomeScreen> { inclusive = route is Screen.HomeScreen }
}
