package com.global_water_polo.play_abroad.presentation.opener

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.global_water_polo.play_abroad.BuildKonfig
import com.global_water_polo.play_abroad.homeOf
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.core.navigation.utils.Navigator
import com.global_water_polo.play_abroad.data.local.DemoProfiles
import com.global_water_polo.play_abroad.domain.model.Lane
import com.global_water_polo.play_abroad.domain.repository.ProfileRepository
import com.global_water_polo.play_abroad.presentation.base.BaseViewModel

/**
 * The 3D opener (the reference's opener/index.html): the flight in from space to the GWP ball, the welcome,
 * then "Begin Your Journey" hands over to the first onboarding question. The motion itself lives in
 * OpenerTimeline; this keeps the tap, the skip and the hand-over.
 */
class OpenerViewModel(
    private val navigator: Navigator,
    private val profileRepository: ProfileRepository
) : BaseViewModel<OpenerState, OpenerEvent>() {

    override val initialViewState = OpenerState()

    override fun dismissError() {
        state.update { it.copy(error = null) }
    }

    val openerState = state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = state.value
        )

    fun onEvent(event: OpenerEvent) {
        when (event) {
            is OpenerEvent.OnBeginClick -> if (state.value.tappedAt == null) state.update { it.copy(tappedAt = event.at) }
            OpenerEvent.OnSkipClick -> state.update { it.copy(skipped = true) }
            OpenerEvent.OnFinished -> finish()
            OpenerEvent.OnQuickStartOpen -> if (BuildKonfig.DEMO_SHORTCUTS) state.update { it.copy(quickStartOpen = true) }
            OpenerEvent.OnQuickStartDismiss -> state.update { it.copy(quickStartOpen = false) }
            is OpenerEvent.OnQuickStart -> quickStart(event.lane)
        }
    }

    private fun finish() {
        if (state.value.finished) return
        state.update { it.copy(finished = true) }
        viewModelScope.launch {
            navigator.navigateTo(Screen.OnboardingGraph) { popUpTo(Screen.OpenerScreen) { inclusive = true } }
        }
    }

    /** Testing shortcut: replaces the saved profile with a sample one and opens that lane's home. */
    private fun quickStart(lane: Lane) {
        val profile = DemoProfiles.of(lane)
        profileRepository.update { profile }
        state.update { it.copy(quickStartOpen = false) }
        viewModelScope.launch {
            navigator.navigateTo(homeOf(profile)) { popUpTo(Screen.OpenerScreen) { inclusive = true } }
        }
    }
}
