package com.global_water_polo.play_abroad.presentation.intro

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.global_water_polo.play_abroad.core.components.globe.LocalGlobeController
import com.global_water_polo.play_abroad.core.utils.observeWithLifecycle
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.max

@Composable
fun IntroScreen(
    modifier: Modifier = Modifier,
    viewModel: IntroViewModel,
    showSnackBar: (String) -> Unit
) {
    val state by viewModel.introState.collectAsStateWithLifecycle()
    val globe = LocalGlobeController.current
    val haptics = LocalHapticFeedback.current

    viewModel.snackBarChannel.observeWithLifecycle { message ->
        showSnackBar(message)
    }

    LaunchedEffect(Unit) { viewModel.onEvent(IntroEvent.OnAppear) }

    // Every turn to the right winds the spring; each 15° boundary clicks like a ratchet tooth.
    DisposableEffect(globe) {
        var lastZone = floor(globe.angle / 15f).toInt()
        globe.onUserTurn = { delta ->
            viewModel.onEvent(IntroEvent.OnSpin(delta))
            val zone = floor(globe.angle / 15f).toInt()
            if (zone != lastZone) {
                lastZone = zone
                if (globe.hapticsOn) haptics.performHapticFeedback(HapticFeedbackType.SegmentTick)
            }
        }
        onDispose { globe.onUserTurn = null }
    }

    // The spring lets go: the ball runs through the remaining zones on its own, slowing but never stopping.
    LaunchedEffect(state.running) {
        if (!state.running || state.spunAll) return@LaunchedEffect
        if (globe.hapticsOn) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        var speed = 520f
        var last = withFrameMillis { it }
        while (!viewModel.introState.value.spunAll) {
            val now = withFrameMillis { it }
            val dt = (now - last).coerceIn(0L, 50L) / 1000f
            last = now
            speed = max(260f, speed * exp(-0.7f * dt))
            globe.drag(speed * dt)
        }
    }

    LaunchedEffect(state.spunAll) {
        if (state.spunAll && !state.finished && globe.hapticsOn) haptics.performHapticFeedback(HapticFeedbackType.Confirm)
    }

    LaunchedEffect(state.finished) {
        if (state.finished) {
            globe.kick(velocityDegreesPerMs = 1.6f, withBurst = true)
            if (globe.hapticsOn) haptics.performHapticFeedback(HapticFeedbackType.Confirm)
        }
    }

    IntroScreenContent(
        modifier = modifier,
        state = state,
        angle = { globe.angle },
        hapticsOn = globe.hapticsOn,
        onHapticsClick = { globe.hapticsOn = !globe.hapticsOn },
        onDragStart = { globe.dragStart() },
        onDrag = { degrees -> globe.drag(degrees) },
        onDragEnd = { velocity -> globe.dragEnd(velocity) },
        onSkipClick = { viewModel.onEvent(IntroEvent.OnSkipClick) },
        onBallClick = { viewModel.onEvent(IntroEvent.OnBallClick) }
    )
}
