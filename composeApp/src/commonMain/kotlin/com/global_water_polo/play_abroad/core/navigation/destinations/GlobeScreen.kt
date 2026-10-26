package com.global_water_polo.play_abroad.core.navigation.destinations

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.koin.compose.viewmodel.koinViewModel
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.presentation.app.globe.GlobeScreen
import com.global_water_polo.play_abroad.presentation.app.globe.GlobeViewModel

fun NavGraphBuilder.globeScreenComposable(
    navController: NavController,
    showSnackBar: (String) -> Unit
) {
    composable<Screen.GlobeScreen> {
        val viewModel = koinViewModel<GlobeViewModel>()
        GlobeScreen(
            viewModel = viewModel,
            showSnackBar = showSnackBar
        )
    }
}
