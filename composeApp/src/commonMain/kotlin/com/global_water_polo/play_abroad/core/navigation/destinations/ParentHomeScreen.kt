package com.global_water_polo.play_abroad.core.navigation.destinations

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.koin.compose.viewmodel.koinViewModel
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.presentation.home.parent.ParentHomeScreen
import com.global_water_polo.play_abroad.presentation.home.parent.ParentHomeViewModel

fun NavGraphBuilder.parentHomeScreenComposable(
    navController: NavController,
    showSnackBar: (String) -> Unit
) {
    composable<Screen.ParentHomeScreen> {
        val viewModel = koinViewModel<ParentHomeViewModel>()
        ParentHomeScreen(
            viewModel = viewModel,
            showSnackBar = showSnackBar
        )
    }
}
