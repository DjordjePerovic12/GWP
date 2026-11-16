package com.global_water_polo.play_abroad.core.navigation.destinations

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.core.navigation.utils.onboardingViewModel
import com.global_water_polo.play_abroad.presentation.onboarding.coachrecruit.CoachRecruitScreen

fun NavGraphBuilder.coachRecruitScreenComposable(
    navController: NavController,
    showSnackBar: (String) -> Unit
) {
    composable<Screen.CoachRecruitScreen> { backStackEntry ->
        CoachRecruitScreen(
            viewModel = onboardingViewModel(navController, backStackEntry),
            showSnackBar = showSnackBar
        )
    }
}
