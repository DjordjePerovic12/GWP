package com.global_water_polo.play_abroad.core.navigation.destinations

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.koin.compose.viewmodel.koinViewModel
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.presentation.app.settings.SettingsScreen
import com.global_water_polo.play_abroad.presentation.app.settings.SettingsViewModel

fun NavGraphBuilder.settingsScreenComposable(
    navController: NavController,
    showSnackBar: (String) -> Unit
) {
    composable<Screen.SettingsScreen> {
        val viewModel = koinViewModel<SettingsViewModel>()
        SettingsScreen(
            viewModel = viewModel,
            showSnackBar = showSnackBar
        )
    }
}
