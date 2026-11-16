package com.global_water_polo.play_abroad.core.navigation

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Density
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navigation
import co.touchlab.kermit.Logger
import com.global_water_polo.play_abroad.core.navigation.destinations.accountScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.askScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.gradScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.freeYearScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.tourScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.linkScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.worldScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.trialScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.matchScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.offersScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.tripPlanScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.clubPlaceScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.clubStaffScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.clubProfileScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.homeScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.coachEmailScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.coachSchoolScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.coachRecruitScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.coachTeamScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.coachNeedsScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.coachInviteScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.coachStaffScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.coachPendingScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.hostScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.inboxScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.coachHomeScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.profileScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.feedScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.globeScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.flightRadarScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.settingsScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.birthdayScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.clockScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.emailScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.introScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.coachTypeScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.fanScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.enrollScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.gradSuggestScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.availScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.learnScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.readyScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.openerScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.inviteScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.lookScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.nameScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.parentHomeScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.travelScreenComposable
import com.global_water_polo.play_abroad.core.navigation.destinations.whoScreenComposable
import com.global_water_polo.play_abroad.core.navigation.utils.NavType
import com.global_water_polo.play_abroad.core.navigation.utils.Navigator
import com.global_water_polo.play_abroad.core.navigation.utils.ObserveAsEvents

private val enterEasing = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1f)

/**
GWPPA's navigation graph: the spin intro, the onboarding graph (one shared OnboardingViewModel for every
step of every lane), then the app screens. Screens rise in like the prototype's `.screen.enter`.

 showSnackBar is the function responsible for showing a snack bar on our screens
 it should be passed down to all screen across the app
 **/

@Composable
fun BaseAppNavigation(
    navController: NavHostController,
    navigator: Navigator,
    showSnackBar: (String) -> Unit,
    density: Density,
    startDestination: Screen = Screen.OpenerScreen
) {

    ObserveAsEvents(flow = navigator.navigationFlow) { navType ->
        Logger.d { "TYPE $navType" }
        when (navType) {
            is NavType.NavigateToRoute -> {
                navController.navigate(navType.destination) {
                    launchSingleTop = true
                    restoreState = true
                    navType.navOptions(this)
                }
            }

            NavType.NavigateUp -> {
                if (navController.previousBackStackEntry != null) {
                    navController.navigateUp()
                }
            }
        }
    }

    val rise = with(density) { 16.dp.roundToPx() }
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { fadeIn(tween(460, easing = enterEasing)) + slideInVertically(tween(460, easing = enterEasing)) { rise } },
        exitTransition = { fadeOut(tween(200)) },
        popEnterTransition = { fadeIn(tween(460, easing = enterEasing)) + slideInVertically(tween(460, easing = enterEasing)) { rise } },
        popExitTransition = { fadeOut(tween(200)) }
    ) {
        openerScreenComposable(navController = navController, showSnackBar = showSnackBar)
        introScreenComposable(navController = navController, showSnackBar = showSnackBar)
        navigation<Screen.OnboardingGraph>(startDestination = Screen.WhoScreen) {
            whoScreenComposable(navController = navController, showSnackBar = showSnackBar)
            coachTypeScreenComposable(navController = navController, showSnackBar = showSnackBar)
            fanScreenComposable(navController = navController, showSnackBar = showSnackBar)
            coachEmailScreenComposable(navController = navController, showSnackBar = showSnackBar)
            coachSchoolScreenComposable(navController = navController, showSnackBar = showSnackBar)
            coachRecruitScreenComposable(navController = navController, showSnackBar = showSnackBar)
            coachTeamScreenComposable(navController = navController, showSnackBar = showSnackBar)
            coachNeedsScreenComposable(navController = navController, showSnackBar = showSnackBar)
            coachInviteScreenComposable(navController = navController, showSnackBar = showSnackBar)
            coachStaffScreenComposable(navController = navController, showSnackBar = showSnackBar)
            enrollScreenComposable(navController = navController, showSnackBar = showSnackBar)
            gradSuggestScreenComposable(navController = navController, showSnackBar = showSnackBar)
            availScreenComposable(navController = navController, showSnackBar = showSnackBar)
            learnScreenComposable(navController = navController, showSnackBar = showSnackBar)
            readyScreenComposable(navController = navController, showSnackBar = showSnackBar)
            askScreenComposable(navController = navController, showSnackBar = showSnackBar)
            gradScreenComposable(navController = navController, showSnackBar = showSnackBar)
            freeYearScreenComposable(navController = navController, showSnackBar = showSnackBar)
            tourScreenComposable(navController = navController, showSnackBar = showSnackBar)
            linkScreenComposable(navController = navController, showSnackBar = showSnackBar)
            worldScreenComposable(navController = navController, showSnackBar = showSnackBar)
            trialScreenComposable(navController = navController, showSnackBar = showSnackBar)
            matchScreenComposable(navController = navController, showSnackBar = showSnackBar)
            birthdayScreenComposable(navController = navController, showSnackBar = showSnackBar)
            clockScreenComposable(navController = navController, showSnackBar = showSnackBar)
            nameScreenComposable(navController = navController, showSnackBar = showSnackBar)
            emailScreenComposable(navController = navController, showSnackBar = showSnackBar)
            lookScreenComposable(navController = navController, showSnackBar = showSnackBar)
            travelScreenComposable(navController = navController, showSnackBar = showSnackBar)
            inviteScreenComposable(navController = navController, showSnackBar = showSnackBar)
            accountScreenComposable(navController = navController, showSnackBar = showSnackBar)
            offersScreenComposable(navController = navController, showSnackBar = showSnackBar)
            tripPlanScreenComposable(navController = navController, showSnackBar = showSnackBar)
            clubPlaceScreenComposable(navController = navController, showSnackBar = showSnackBar)
            clubStaffScreenComposable(navController = navController, showSnackBar = showSnackBar)
            clubProfileScreenComposable(navController = navController, showSnackBar = showSnackBar)
        }
        parentHomeScreenComposable(navController = navController, showSnackBar = showSnackBar)
        coachHomeScreenComposable(navController = navController, showSnackBar = showSnackBar)
        coachPendingScreenComposable(navController = navController, showSnackBar = showSnackBar)
        hostScreenComposable(navController = navController, showSnackBar = showSnackBar)
        homeScreenComposable(navController = navController, showSnackBar = showSnackBar)
        inboxScreenComposable(navController = navController, showSnackBar = showSnackBar)
        profileScreenComposable(navController = navController, showSnackBar = showSnackBar)
        feedScreenComposable(navController = navController, showSnackBar = showSnackBar)
        globeScreenComposable(navController = navController, showSnackBar = showSnackBar)
        flightRadarScreenComposable(navController = navController, showSnackBar = showSnackBar)
        settingsScreenComposable(navController = navController, showSnackBar = showSnackBar)
    }
}
