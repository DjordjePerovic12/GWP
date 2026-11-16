package com.global_water_polo.play_abroad.core.navigation.destinations

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.koin.compose.viewmodel.koinViewModel
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.presentation.app.inbox.InboxScreen
import com.global_water_polo.play_abroad.presentation.app.inbox.InboxViewModel

fun NavGraphBuilder.inboxScreenComposable(
    navController: NavController,
    showSnackBar: (String) -> Unit
) {
    composable<Screen.InboxScreen> {
        val viewModel = koinViewModel<InboxViewModel>()
        InboxScreen(
            viewModel = viewModel,
            showSnackBar = showSnackBar
        )
    }
}
