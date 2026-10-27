package com.global_water_polo.play_abroad.presentation.onboarding.tour

import com.global_water_polo.play_abroad.core.components.LocalSound
import com.global_water_polo.play_abroad.core.components.Sfx
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import com.global_water_polo.play_abroad.core.components.globe.LocalGlobeController
import com.global_water_polo.play_abroad.core.utils.observeWithLifecycle
import com.global_water_polo.play_abroad.presentation.base.BaseContentView
import com.global_water_polo.play_abroad.presentation.onboarding.FlowStep
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingEvent
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingViewModel
import com.global_water_polo.play_abroad.presentation.onboarding.tour.components.TOUR_CITIES
import com.global_water_polo.play_abroad.presentation.onboarding.tour.components.TOUR_HOME_LATITUDE
import com.global_water_polo.play_abroad.presentation.onboarding.tour.components.TOUR_HOME_LONGITUDE
import com.global_water_polo.play_abroad.core.components.globe.GlobePin

/** Total length of the tour, as in the prototype. */
const val TOUR_MILLIS = 20_000

/**
 * While GWP "builds the pathway": the globe flies through the club cities for 20 seconds, then says the
 * profile is ready and moves on by itself. Skip ends it at once.
 */
@Composable
fun TourScreen(
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel,
    showSnackBar: (String) -> Unit
) {
    val state by viewModel.onboardingState.collectAsStateWithLifecycle()
    val globe = LocalGlobeController.current
    val haptics = LocalHapticFeedback.current
    val sound = LocalSound.current
    // -1: "The world" before the first city; TOUR_CITIES.size: "Your profile is ready."
    var stop by remember { mutableIntStateOf(-1) }

    viewModel.snackBarChannel.observeWithLifecycle { message ->
        showSnackBar(message)
    }

    DisposableEffect(globe) {
        onDispose { globe.clearTour() }
    }

    LaunchedEffect(Unit) {
        val step = TOUR_MILLIS / (TOUR_CITIES.size + 1)
        globe.pins = TOUR_CITIES.map { GlobePin(it.latitude, it.longitude) }
        globe.aimAt(TOUR_HOME_LONGITUDE, TOUR_HOME_LATITUDE)
        delay(step * 6L / 10L)
        TOUR_CITIES.forEachIndexed { index, city ->
            stop = index
            globe.aimAt(city.longitude, city.latitude)
            globe.pins = TOUR_CITIES.mapIndexed { i, c -> GlobePin(c.latitude, c.longitude, active = i == index) }
            if (globe.hapticsOn) {
                haptics.performHapticFeedback(HapticFeedbackType.SegmentTick)
                sound.play(Sfx.TICK)
            }
            delay(step.toLong())
        }
        stop = TOUR_CITIES.size
        globe.pins = TOUR_CITIES.map { GlobePin(it.latitude, it.longitude) }
        globe.aimAt(TOUR_HOME_LONGITUDE, TOUR_HOME_LATITUDE)
        if (globe.hapticsOn) haptics.performHapticFeedback(HapticFeedbackType.Confirm)
        delay(1_100L)
        viewModel.onEvent(OnboardingEvent.OnStepContinueClick(FlowStep.TOUR))
    }

    BaseContentView(
        modifier = modifier,
        viewState = state,
        applySystemBarsPadding = false
    ) {
        TourScreenContent(
            state = state,
            stop = stop,
            onSkipClick = { viewModel.onEvent(OnboardingEvent.OnStepContinueClick(FlowStep.TOUR)) }
        )
    }
}
