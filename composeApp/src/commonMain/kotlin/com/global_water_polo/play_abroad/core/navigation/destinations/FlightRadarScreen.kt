package com.global_water_polo.play_abroad.core.navigation.destinations

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.koin.compose.viewmodel.koinViewModel
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.presentation.app.travel.FlightRadarScreen
import com.global_water_polo.play_abroad.presentation.app.travel.FlightRadarViewModel

fun NavGraphBuilder.flightRadarScreenComposable(
    navController: NavController,
    showSnackBar: (String) -> Unit
) {
    composable<Screen.FlightRadarScreen> {
        val viewModel = koinViewModel<FlightRadarViewModel>()
        FlightRadarScreen(
            viewModel = viewModel,
            showSnackBar = showSnackBar
        )
    }
}
