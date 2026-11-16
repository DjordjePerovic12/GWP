package com.global_water_polo.play_abroad.core.navigation.destinations

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.koin.compose.viewmodel.koinViewModel
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.presentation.host.HostScreen
import com.global_water_polo.play_abroad.presentation.host.HostViewModel

fun NavGraphBuilder.hostScreenComposable(
    navController: NavController,
    showSnackBar: (String) -> Unit
) {
    composable<Screen.HostScreen> {
        val viewModel = koinViewModel<HostViewModel>()
        HostScreen(
            viewModel = viewModel,
            showSnackBar = showSnackBar
        )
    }
}
