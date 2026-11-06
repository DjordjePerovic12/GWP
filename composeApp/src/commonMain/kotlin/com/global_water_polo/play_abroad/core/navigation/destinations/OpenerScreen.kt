package com.global_water_polo.play_abroad.core.navigation.destinations

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.koin.compose.viewmodel.koinViewModel
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.presentation.opener.OpenerScreen
import com.global_water_polo.play_abroad.presentation.opener.OpenerViewModel

fun NavGraphBuilder.openerScreenComposable(
    navController: NavController,
    showSnackBar: (String) -> Unit
) {
    composable<Screen.OpenerScreen> {
        val viewModel = koinViewModel<OpenerViewModel>()
        OpenerScreen(
            viewModel = viewModel,
            showSnackBar = showSnackBar
        )
    }
}
