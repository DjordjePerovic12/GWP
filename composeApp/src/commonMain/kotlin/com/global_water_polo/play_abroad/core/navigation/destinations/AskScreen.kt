package com.global_water_polo.play_abroad.core.navigation.destinations

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.core.navigation.utils.onboardingViewModel
import com.global_water_polo.play_abroad.presentation.onboarding.AskStep
import com.global_water_polo.play_abroad.presentation.onboarding.ask.AskScreen

/** The six one-tap question routes, each drawn by the shared AskScreen. */
fun NavGraphBuilder.askScreenComposable(
    navController: NavController,
    showSnackBar: (String) -> Unit
) {
    composable<Screen.NowScreen> { backStackEntry ->
        AskScreen(step = AskStep.NOW, viewModel = onboardingViewModel(navController, backStackEntry), showSnackBar = showSnackBar)
    }
    composable<Screen.LevelScreen> { backStackEntry ->
        AskScreen(step = AskStep.LEVEL, viewModel = onboardingViewModel(navController, backStackEntry), showSnackBar = showSnackBar)
    }
    composable<Screen.GoalScreen> { backStackEntry ->
        AskScreen(step = AskStep.GOAL, viewModel = onboardingViewModel(navController, backStackEntry), showSnackBar = showSnackBar)
    }
    composable<Screen.ClubScreen> { backStackEntry ->
        AskScreen(step = AskStep.CLUB, viewModel = onboardingViewModel(navController, backStackEntry), showSnackBar = showSnackBar)
    }
    composable<Screen.ProbeScreen> { backStackEntry ->
        AskScreen(step = AskStep.PROBE, viewModel = onboardingViewModel(navController, backStackEntry), showSnackBar = showSnackBar)
    }
    composable<Screen.StopScreen> { backStackEntry ->
        AskScreen(step = AskStep.STOP, viewModel = onboardingViewModel(navController, backStackEntry), showSnackBar = showSnackBar)
    }
    composable<Screen.FacilitiesScreen> { backStackEntry ->
        AskScreen(step = AskStep.FACILITIES, viewModel = onboardingViewModel(navController, backStackEntry), showSnackBar = showSnackBar)
    }
}
