package com.global_water_polo.play_abroad.core.navigation.destinations

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.koin.compose.viewmodel.koinViewModel
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.presentation.app.feed.FeedScreen
import com.global_water_polo.play_abroad.presentation.app.feed.FeedViewModel

fun NavGraphBuilder.feedScreenComposable(
    navController: NavController,
    showSnackBar: (String) -> Unit
) {
    composable<Screen.FeedScreen> {
        val viewModel = koinViewModel<FeedViewModel>()
        FeedScreen(
            viewModel = viewModel,
            showSnackBar = showSnackBar
        )
    }
}
