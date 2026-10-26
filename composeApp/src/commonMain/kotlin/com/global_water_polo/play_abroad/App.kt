package com.global_water_polo.play_abroad

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import com.global_water_polo.play_abroad.core.components.LocalStatusBarOverride
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

            CompositionLocalProvider(LocalStatusBarOverride provides statusOverride) {
                GlobeStage(controller = globe, routeMode = routeMode) {
                    // The screens handle insets themselves; this Scaffold only hosts the snack bar, so it must not
                    // consume the system bars (that left screens running under the status bar and home indicator).
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = Color.Transparent,
                        contentWindowInsets = WindowInsets(0),
                        snackbarHost = { CustomModifiers.snackBarHost(appState.scaffoldState) }
                    ) {
                        BaseAppNavigation(
                            navigator = navigator,
                            navController = appState.navController,
                            showSnackBar = { message ->
                                appState.showSnackBar(message)
                            },
                            density = LocalDensity.current
                        )
                    }
                }
            }
        }
    }
}
