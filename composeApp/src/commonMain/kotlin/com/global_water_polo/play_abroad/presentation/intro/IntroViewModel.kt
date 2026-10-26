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
import kotlin.math.min

/**
 * "Wind it up": turning the ball to the right winds a spring through the time zones, getting heavier as
 * it loads. Twelve zones in, the spring lets go and the ball runs through the other twelve on its own
 * (IntroScreen drives that run). Turning left slips and takes nothing back. With all 24 zones spun the
 * ball waits for a tap; skipping enters straight away.
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
            IntroEvent.OnBallClick -> if (state.value.spunAll) finish()
        }
    }

    private fun spin(delta: Float) {
        val s = state.value
        if (s.finished || s.spunAll || delta <= 0f) return
        // The spring tightens as it loads by hand; once released it runs freely.
        val load = min(1f, s.progressDegrees / IntroState.BY_HAND)
        val turn = if (s.running) delta else delta * (1f - 0.32f * load)
        val progress = min(IntroState.FULL_TURN, s.progressDegrees + turn)
        state.update {
            it.copy(
                progressDegrees = progress,
                zonesCrossed = min(IntroState.ZONES, floor(progress / 15f).toInt()),
                running = it.running || progress >= IntroState.BY_HAND,
                spunAll = progress >= IntroState.FULL_TURN
            )
        }
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
