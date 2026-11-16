package com.global_water_polo.play_abroad.core.navigation.destinations

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.core.navigation.utils.onboardingViewModel
import com.global_water_polo.play_abroad.presentation.onboarding.coachschool.CoachSchoolScreen

fun NavGraphBuilder.coachSchoolScreenComposable(
    navController: NavController,
    showSnackBar: (String) -> Unit
) {
    composable<Screen.CoachSchoolScreen> { backStackEntry ->
        CoachSchoolScreen(
            viewModel = onboardingViewModel(navController, backStackEntry),
            showSnackBar = showSnackBar
        )
    }
}
