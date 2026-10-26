package com.global_water_polo.play_abroad.core.navigation.destinations

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.koin.compose.viewmodel.koinViewModel
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.presentation.home.coach.CoachHomeScreen
import com.global_water_polo.play_abroad.presentation.home.coach.CoachHomeViewModel

fun NavGraphBuilder.coachHomeScreenComposable(
    navController: NavController,
    showSnackBar: (String) -> Unit
) {
    composable<Screen.CoachHomeScreen> {
        val viewModel = koinViewModel<CoachHomeViewModel>()
        CoachHomeScreen(
            viewModel = viewModel,
            showSnackBar = showSnackBar
        )
    }
}
