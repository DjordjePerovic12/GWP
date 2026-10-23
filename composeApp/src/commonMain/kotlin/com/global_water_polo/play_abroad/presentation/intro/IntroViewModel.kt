package com.global_water_polo.play_abroad.presentation.intro

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.core.navigation.utils.Navigator
import com.global_water_polo.play_abroad.presentation.base.BaseViewModel
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

/**
 * Spin the world through all 24 time zones (one full turn to the right) to enter.
 * Turning left takes progress back; skipping counts as a full turn.
 */
class IntroViewModel(
    private val navigator: Navigator
) : BaseViewModel<IntroState, IntroEvent>() {

    override val initialViewState = IntroState()

    override fun dismissError() {
        state.update { it.copy(error = null) }
    }

    val introState = state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = state.value
        )

    fun onEvent(event: IntroEvent) {
        when (event) {
            IntroEvent.OnAppear -> state.update { IntroState() }
            is IntroEvent.OnSpin -> spin(event.deltaDegrees)
            IntroEvent.OnSkipClick -> finish()
        }
    }

    private fun spin(delta: Float) {
        if (state.value.finished) return
        val progress = max(0f, state.value.progressDegrees + delta)
        state.update {
            it.copy(
                progressDegrees = progress,
                zonesCrossed = min(IntroState.ZONES, floor(progress / 15f).toInt())
            )
        }
        if (progress >= IntroState.FULL_TURN) finish()
    }

    private fun finish() {
        if (state.value.finished) return
        state.update { it.copy(progressDegrees = IntroState.FULL_TURN, zonesCrossed = IntroState.ZONES, finished = true) }
        viewModelScope.launch {
            // Let the burst and the ball's shrink play before the first question rises.
            delay(650L)
            navigator.navigateTo(Screen.OnboardingGraph)
        }
    }
}
