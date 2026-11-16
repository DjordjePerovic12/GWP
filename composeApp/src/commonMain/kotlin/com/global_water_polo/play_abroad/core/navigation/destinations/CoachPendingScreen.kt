package com.global_water_polo.play_abroad.core.navigation.destinations

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.koin.compose.viewmodel.koinViewModel
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.presentation.home.coachpending.CoachPendingScreen
import com.global_water_polo.play_abroad.presentation.home.coachpending.CoachPendingViewModel

fun NavGraphBuilder.coachPendingScreenComposable(
    navController: NavController,
    showSnackBar: (String) -> Unit
) {
    composable<Screen.CoachPendingScreen> {
        val viewModel = koinViewModel<CoachPendingViewModel>()
        CoachPendingScreen(
            viewModel = viewModel,
            showSnackBar = showSnackBar
        )
    }
}
