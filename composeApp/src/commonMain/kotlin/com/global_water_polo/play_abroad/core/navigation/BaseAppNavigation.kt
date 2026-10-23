package com.global_water_polo.play_abroad.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import co.touchlab.kermit.Logger
import com.global_water_polo.play_abroad.core.navigation.destinations.exampleScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.loginScreenComposable
import com.global_water_polo.play_abroad.core.navigation.utils.NavType
import com.global_water_polo.play_abroad.core.navigation.utils.Navigator
import com.global_water_polo.play_abroad.core.navigation.utils.ObserveAsEvents

//TODO: Rename after the app name, set start destination to app start destination
/**
BaseAppNavigation is the root navigation composable that defines the navigation graph
for the whole app. It sets up all available screen destinations and their
respective navigation paths.

@param navController The NavHostController that manages app navigation
Key Components:
 * 1. ObserveAsEvents: Acts as a "navigation event listener"
 * 2. NavHost: Defines the navigation structure and screen compositions

 showSnackBar is the function responsible for showing a snack bar on our screens
 it should be passed down to all screen across the app
 **/

@Composable
fun BaseAppNavigation(
    navController: NavHostController,
    navigator: Navigator,
    showSnackBar: (String) -> Unit
) {

    ObserveAsEvents(flow = navigator.navigationFlow) { navType ->
        Logger.d { "TYPE $navType" }
        when (navType) {
            is NavType.NavigateToRoute -> {
                navController.navigate(navType.destination) {
                    launchSingleTop = true
                    restoreState = true
                    navType.navOptions(this)
                }
            }

            NavType.NavigateUp -> {
                if (navController.previousBackStackEntry != null) {
                    navController.navigateUp()
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.ExampleScreen
    ) {
        exampleScreenComposable(navController = navController, showSnackBar = showSnackBar)
        loginScreenComposable(navController = navController, showSnackBar = showSnackBar)
    }
}
