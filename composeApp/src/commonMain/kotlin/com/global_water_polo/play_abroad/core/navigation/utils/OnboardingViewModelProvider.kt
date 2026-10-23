package com.global_water_polo.play_abroad.core.navigation.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import org.koin.compose.viewmodel.koinViewModel
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingViewModel

/**
 * Every onboarding step shares one OnboardingViewModel, scoped to the Screen.OnboardingGraph back stack
 * entry (the KMP version of scoping list/detail ViewModels to Screen.ROOT).
 */
@Composable
fun onboardingViewModel(navController: NavController, entry: NavBackStackEntry): OnboardingViewModel {
    val parentEntry = remember(entry) { navController.getBackStackEntry(Screen.OnboardingGraph) }
    return koinViewModel(viewModelStoreOwner = parentEntry)
}
