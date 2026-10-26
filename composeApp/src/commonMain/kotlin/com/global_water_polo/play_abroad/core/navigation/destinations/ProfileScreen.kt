package com.global_water_polo.play_abroad.core.navigation.destinations

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.koin.compose.viewmodel.koinViewModel
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.presentation.app.profile.ProfileScreen
import com.global_water_polo.play_abroad.presentation.app.profile.ProfileViewModel

fun NavGraphBuilder.profileScreenComposable(
    navController: NavController,
    showSnackBar: (String) -> Unit
) {
    composable<Screen.ProfileScreen> {
        val viewModel = koinViewModel<ProfileViewModel>()
        ProfileScreen(
            viewModel = viewModel,
            showSnackBar = showSnackBar
        )
    }
}
