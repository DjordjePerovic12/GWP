package com.global_water_polo.play_abroad.presentation.onboarding.clock

import com.global_water_polo.play_abroad.core.components.LocalSound
import com.global_water_polo.play_abroad.core.components.Sfx
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.global_water_polo.play_abroad.core.components.globe.LocalGlobeController
import com.global_water_polo.play_abroad.core.utils.observeWithLifecycle
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingEvent
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingViewModel

/**
 * The dramatic moment: the pool goes dark, the ball spins up, every digit of the countdown rolls
 * and locks in, then it ticks every second. Counts down to Aug 15 of the clock year at midnight
 * in the device's own time zone.
 */
@Composable
fun ClockScreen(
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel,
    showSnackBar: (String) -> Unit
) {
    val state by viewModel.onboardingState.collectAsStateWithLifecycle()
    val globe = LocalGlobeController.current
    val haptics = LocalHapticFeedback.current
    val sound = LocalSound.current

    viewModel.snackBarChannel.observeWithLifecycle { message ->
        showSnackBar(message)
    }

    LaunchedEffect(Unit) { globe.kick(velocityDegreesPerMs = 2.4f, withBurst = false) }

    ClockScreenContent(
        modifier = modifier,
        state = state,
        hapticsOn = globe.hapticsOn,
        onHapticsClick = { globe.hapticsOn = !globe.hapticsOn },
        onDigitLocked = {
            if (globe.hapticsOn) {
                haptics.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                sound.play(Sfx.RATCHET)
            }
        },
        onSettled = {
            if (globe.hapticsOn) {
                haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                sound.play(Sfx.BOOM)
            }
        },
        onSecond = {
            if (globe.hapticsOn) {
                haptics.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                sound.play(Sfx.TICK)
            }
        },
        onBackClick = { viewModel.onEvent(OnboardingEvent.NavigateBack) },
        onContinueClick = { viewModel.onEvent(OnboardingEvent.OnClockContinueClick) }
    )
}
