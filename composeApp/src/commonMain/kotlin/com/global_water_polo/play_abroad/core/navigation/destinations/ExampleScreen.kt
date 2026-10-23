package com.global_water_polo.play_abroad.core.navigation.destinations

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.koin.compose.viewmodel.koinViewModel
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.presentation.example.ExampleScreen
import com.global_water_polo.play_abroad.presentation.example.ExampleViewModel

/**
    This and any other file inside destinations package
    defines composables that represent the give screen for all the destination screens in the app
 **/

fun NavGraphBuilder.exampleScreenComposable(
    navController: NavController,
    showSnackBar: (String) -> Unit
) {
    composable<Screen.ExampleScreen> {
        val viewModel = koinViewModel<ExampleViewModel>()
        ExampleScreen(
            viewModel = viewModel,
            showSnackBar = showSnackBar
        )
    }
}
