package com.global_water_polo.play_abroad.core.navigation.destinations

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.koin.compose.viewmodel.koinViewModel
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.presentation.home.athlete.AthleteHomeScreen
import com.global_water_polo.play_abroad.presentation.home.athlete.AthleteHomeViewModel

fun NavGraphBuilder.athleteHomeScreenComposable(
    navController: NavController,
    showSnackBar: (String) -> Unit
) {
    composable<Screen.AthleteHomeScreen> {
        val viewModel = koinViewModel<AthleteHomeViewModel>()
        AthleteHomeScreen(
            viewModel = viewModel,
            showSnackBar = showSnackBar
        )
    }
}
