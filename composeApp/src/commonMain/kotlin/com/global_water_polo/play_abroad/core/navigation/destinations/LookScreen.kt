package com.global_water_polo.play_abroad.core.navigation.destinations

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.core.navigation.utils.onboardingViewModel
import com.global_water_polo.play_abroad.presentation.onboarding.look.LookScreen

fun NavGraphBuilder.lookScreenComposable(
    navController: NavController,
    showSnackBar: (String) -> Unit
) {
    composable<Screen.LookScreen> { backStackEntry ->
        LookScreen(
            viewModel = onboardingViewModel(navController, backStackEntry),
            showSnackBar = showSnackBar
        )
    }
}
