package com.global_water_polo.play_abroad.core.navigation.destinations

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.koin.compose.viewmodel.koinViewModel
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.presentation.intro.IntroScreen
import com.global_water_polo.play_abroad.presentation.intro.IntroViewModel

fun NavGraphBuilder.introScreenComposable(
    navController: NavController,
    showSnackBar: (String) -> Unit
) {
    composable<Screen.IntroScreen> {
        val viewModel = koinViewModel<IntroViewModel>()
        IntroScreen(
            viewModel = viewModel,
            showSnackBar = showSnackBar
        )
    }
}
