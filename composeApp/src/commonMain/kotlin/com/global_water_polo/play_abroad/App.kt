package com.global_water_polo.play_abroad

import com.global_water_polo.play_abroad.core.components.LoaderAnimation
import androidx.compose.runtime.LaunchedEffect
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
import androidx.navigation.NavDestination.Companion.hasRoute
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
import com.global_water_polo.play_abroad.ui.theme.LocalGWPPAColors
import com.global_water_polo.play_abroad.ui.theme.appColors
import com.global_water_polo.play_abroad.ui.theme.gwppaColors

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
    // Parse the full-screen loader's animation now, so it is ready the first time something loads.
    LaunchedEffect(Unit) { LoaderAnimation.get() }

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
            // The app's tabs use the prototype's app palette (navy and gold on warm paper); onboarding keeps its own.
            val inApp = routeMode == GlobeMode.HIDDEN || routeMode == GlobeMode.EXPLORE
            CompositionLocalProvider(
                LocalStatusBarOverride provides statusOverride,
                LocalSound provides sound,
                LocalGWPPAColors provides if (inApp) appColors else gwppaColors
            ) {
                // One 3D ball on the stage for the opener and the first onboarding question (custom/3Dmodel_test).
                val destination = entry?.destination
                val ball3d = destination?.hasRoute<Screen.OpenerScreen>() == true || destination?.hasRoute<Screen.WhoScreen>() == true
                GlobeStage(controller = globe, routeMode = routeMode, ball3d = ball3d) {
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
internal fun homeOf(profile: Profile): Screen = if (!profile.onboarded) Screen.OpenerScreen else when (profile.lane) {
    Lane.ATHLETE -> Screen.ProfileScreen()
    Lane.PARENT -> Screen.ParentHomeScreen
    Lane.COACH -> Screen.CoachHomeScreen
    Lane.FAN -> Screen.FeedScreen
    Lane.CLUB, null -> Screen.OpenerScreen
}
