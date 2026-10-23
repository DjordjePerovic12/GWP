package com.global_water_polo.play_abroad.core.navigation.destinations

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.koin.compose.viewmodel.koinViewModel
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.presentation.review.status.ReviewStatusScreen
import com.global_water_polo.play_abroad.presentation.review.status.ReviewStatusViewModel

fun NavGraphBuilder.reviewStatusScreenComposable(
    navController: NavController,
    showSnackBar: (String) -> Unit
) {
    composable<Screen.ReviewStatusScreen> {
        val viewModel = koinViewModel<ReviewStatusViewModel>()
        ReviewStatusScreen(
            viewModel = viewModel,
            showSnackBar = showSnackBar
        )
    }
}
