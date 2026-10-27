package com.global_water_polo.play_abroad.presentation.intro

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import com.global_water_polo.play_abroad.core.components.LocalSound
import com.global_water_polo.play_abroad.core.components.Sfx
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import com.global_water_polo.play_abroad.core.components.globe.LocalGlobeController
import com.global_water_polo.play_abroad.core.utils.observeWithLifecycle
import kotlin.math.floor

@Composable
fun IntroScreen(
    modifier: Modifier = Modifier,
    viewModel: IntroViewModel,
    showSnackBar: (String) -> Unit
) {
    val state by viewModel.introState.collectAsStateWithLifecycle()
    val globe = LocalGlobeController.current
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val pulse = remember { Animatable(0f) }
    val sound = LocalSound.current

    viewModel.snackBarChannel.observeWithLifecycle { message ->
        showSnackBar(message)
    }

    LaunchedEffect(Unit) { viewModel.onEvent(IntroEvent.OnAppear) }

    // Every user turn counts towards the 24 zones; each 15° boundary ticks like a dial.
    DisposableEffect(globe) {
        var lastZone = floor(globe.angle / 15f).toInt()
        globe.onUserTurn = { delta ->
            viewModel.onEvent(IntroEvent.OnSpin(delta))
            val zone = floor(globe.angle / 15f).toInt()
            if (zone != lastZone) {
                lastZone = zone
                if (globe.hapticsOn) {
                    haptics.performHapticFeedback(HapticFeedbackType.SegmentTick)
                    sound.play(Sfx.RATCHET)
                }
                scope.launch {
                    pulse.snapTo(1f)
                    pulse.animateTo(0f, tween(600))
                }
            }
        }
        onDispose { globe.onUserTurn = null }
    }

    LaunchedEffect(state.spunAll) {
        if (state.spunAll && !state.finished && globe.hapticsOn) haptics.performHapticFeedback(HapticFeedbackType.Confirm)
    }

    LaunchedEffect(state.finished) {
        if (state.finished) {
            globe.kick(velocityDegreesPerMs = 1.6f, withBurst = true)
            if (globe.hapticsOn) {
                haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                sound.play(Sfx.BOOM)
            }
        }
    }

    IntroScreenContent(
        modifier = modifier,
        state = state,
        angle = { globe.angle },
        pulse = { pulse.value },
        hapticsOn = globe.hapticsOn,
        onHapticsClick = { globe.hapticsOn = !globe.hapticsOn },
        onDragStart = { globe.dragStart() },
        onDrag = { degrees -> globe.drag(degrees) },
        onDragEnd = { velocity -> globe.dragEnd(velocity) },
        onSkipClick = { viewModel.onEvent(IntroEvent.OnSkipClick) },
        onBallClick = { viewModel.onEvent(IntroEvent.OnBallClick) },
        onLogoLongPress = { viewModel.onEvent(IntroEvent.OnLogoLongPress) },
        onQuickStart = { viewModel.onEvent(IntroEvent.OnQuickStart(it)) },
        onQuickStartDismiss = { viewModel.onEvent(IntroEvent.OnQuickStartDismiss) }
    )
}
