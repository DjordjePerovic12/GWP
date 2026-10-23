package com.global_water_polo.play_abroad.core.navigation.destinations

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.koin.compose.viewmodel.koinViewModel
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.presentation.auth.login.LoginScreen
import com.global_water_polo.play_abroad.presentation.auth.login.LoginViewModel

fun NavGraphBuilder.loginScreenComposable(
    navController: NavController,
    showSnackBar: (String) -> Unit
) {
    composable<Screen.LoginScreen> {
        val viewModel = koinViewModel<LoginViewModel>()
        LoginScreen(
            viewModel = viewModel,
            showSnackBar = showSnackBar
        )
    }
}
