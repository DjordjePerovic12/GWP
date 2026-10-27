package com.global_water_polo.play_abroad.presentation.intro

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.global_water_polo.play_abroad.BuildKonfig
import com.global_water_polo.play_abroad.homeOf
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.data.local.DemoProfiles
import com.global_water_polo.play_abroad.domain.model.Lane
import com.global_water_polo.play_abroad.domain.repository.ProfileRepository
import com.global_water_polo.play_abroad.core.navigation.utils.Navigator
import com.global_water_polo.play_abroad.presentation.base.BaseViewModel
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

/**
 * Spin the world through all 24 time zones (one full turn to the right), then tap the ball to enter.
 * Turning left takes progress back until the turn is complete; skipping enters straight away.
 */
class IntroViewModel(
    private val navigator: Navigator,
    private val profileRepository: ProfileRepository
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
            IntroEvent.OnLogoLongPress -> if (BuildKonfig.DEMO_SHORTCUTS) state.update { it.copy(quickStartOpen = true) }
            IntroEvent.OnQuickStartDismiss -> state.update { it.copy(quickStartOpen = false) }
            is IntroEvent.OnQuickStart -> quickStart(event.lane)
        }
    }

    private fun spin(delta: Float) {
        if (state.value.finished || state.value.spunAll) return
        val progress = max(0f, state.value.progressDegrees + delta)
        state.update {
            it.copy(
                progressDegrees = progress,
                zonesCrossed = min(IntroState.ZONES, floor(progress / 15f).toInt())
            )
        }
        if (progress >= IntroState.FULL_TURN) {
            state.update { it.copy(progressDegrees = IntroState.FULL_TURN, zonesCrossed = IntroState.ZONES, spunAll = true) }
        }
    }

    /** Testing shortcut: replaces the saved profile with a sample one and opens that lane's home. */
    private fun quickStart(lane: Lane) {
        val profile = DemoProfiles.of(lane)
        profileRepository.update { profile }
        state.update { it.copy(quickStartOpen = false) }
        viewModelScope.launch {
            navigator.navigateTo(homeOf(profile)) { popUpTo(Screen.IntroScreen) { inclusive = true } }
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
