package com.global_water_polo.play_abroad

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import com.global_water_polo.play_abroad.core.components.LocalStatusBarOverride
import com.global_water_polo.play_abroad.core.components.LocalSound
import com.global_water_polo.play_abroad.core.components.rememberSoundPlayer
import com.global_water_polo.play_abroad.core.components.StatusBarStyle
import com.global_water_polo.play_abroad.core.components.globe.GlobeMode
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import com.global_water_polo.play_abroad.core.components.globe.GlobeController
import com.global_water_polo.play_abroad.core.components.globe.GlobeStage
import com.global_water_polo.play_abroad.core.navigation.BaseAppNavigation
import com.global_water_polo.play_abroad.core.navigation.utils.Navigator
import com.global_water_polo.play_abroad.core.navigation.utils.globeMode
import com.global_water_polo.play_abroad.core.utils.AppEnvironment
import com.global_water_polo.play_abroad.core.utils.CustomModifiers
import com.global_water_polo.play_abroad.core.utils.observeWithLifecycle
import com.global_water_polo.play_abroad.core.utils.rememberAppState
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.domain.model.Lane
import com.global_water_polo.play_abroad.domain.model.Profile
import com.global_water_polo.play_abroad.domain.repository.ProfileRepository
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/**
  Root composable shared by Android (MainActivity) and iOS (MainViewController).
  It owns the theme, the app-wide pool + globe stage, the snack bar host and the navigation graph.
 **/

@Composable
fun App() {
    val navigator = koinInject<Navigator>()
    val mainViewModel = koinViewModel<MainViewModel>()
    val state by mainViewModel.viewStateFlow.collectAsStateWithLifecycle()
    val globe = remember { GlobeController() }
    val profileRepository = koinInject<ProfileRepository>()
    val loaded by profileRepository.loaded.collectAsStateWithLifecycle()

    AppEnvironment(languageTag = state.languageTag) {
        GWPPATheme {
            val appState = rememberAppState()
            val entry by appState.navController.currentBackStackEntryAsState()
            mainViewModel.snackBarChannel.observeWithLifecycle { message ->
                appState.showSnackBar(message)
            }

            val routeMode = entry?.destination.globeMode()
            val statusOverride = remember { mutableStateOf<Boolean?>(null) }
            // White icons while the globe is at night (clock, tour, world, Globe+), unless a screen asks otherwise.
            val night = routeMode == GlobeMode.CLOCK || routeMode == GlobeMode.REVEAL || routeMode == GlobeMode.EXPLORE
            StatusBarStyle(lightIcons = statusOverride.value ?: night)

            val sound = rememberSoundPlayer()
            CompositionLocalProvider(LocalStatusBarOverride provides statusOverride, LocalSound provides sound) {
                GlobeStage(controller = globe, routeMode = routeMode) {
                    // The screens handle insets themselves; this Scaffold only hosts the snack bar, so it must not
                    // consume the system bars (that left screens running under the status bar and home indicator).
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = Color.Transparent,
                        contentWindowInsets = WindowInsets(0),
                        snackbarHost = { CustomModifiers.snackBarHost(appState.scaffoldState) }
                    ) {
                        // Wait for the saved profile so a returning user opens on their home, not the intro.
                        if (!loaded) return@Scaffold
                        val start = remember { homeOf(profileRepository.profile.value) }
                        BaseAppNavigation(
                            navigator = navigator,
                            navController = appState.navController,
                            showSnackBar = { message ->
                                appState.showSnackBar(message)
                            },
                            density = LocalDensity.current,
                            startDestination = start
                        )
                    }
                }
            }
        }
    }
}

/** Where the app opens: the lane's home once onboarding is done, else the intro. */
private fun homeOf(profile: Profile): Screen = if (!profile.onboarded) Screen.IntroScreen else when (profile.lane) {
    Lane.ATHLETE -> Screen.ProfileScreen()
    Lane.PARENT -> Screen.ParentHomeScreen
    Lane.COACH -> Screen.CoachHomeScreen
    Lane.CLUB, null -> Screen.IntroScreen
}
