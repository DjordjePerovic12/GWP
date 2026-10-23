package com.global_water_polo.play_abroad.core.navigation.destinations

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.koin.compose.viewmodel.koinViewModel
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.presentation.review.apply.ApplyReviewScreen
import com.global_water_polo.play_abroad.presentation.review.apply.ApplyReviewViewModel

fun NavGraphBuilder.applyReviewScreenComposable(
    navController: NavController,
    showSnackBar: (String) -> Unit
) {
    composable<Screen.ApplyReviewScreen> {
        val viewModel = koinViewModel<ApplyReviewViewModel>()
        ApplyReviewScreen(
            viewModel = viewModel,
            showSnackBar = showSnackBar
        )
    }
}
