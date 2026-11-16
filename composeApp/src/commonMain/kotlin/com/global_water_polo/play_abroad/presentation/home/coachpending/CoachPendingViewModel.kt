package com.global_water_polo.play_abroad.presentation.home.coachpending

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.core.navigation.utils.Navigator
import com.global_water_polo.play_abroad.domain.repository.ProfileRepository
import com.global_water_polo.play_abroad.presentation.base.BaseViewModel

/** Under review (v29's pending29): high school, club and GWP staff coaches wait for GWP to check the account. */
class CoachPendingViewModel(
    private val profileRepository: ProfileRepository,
    private val navigator: Navigator
) : BaseViewModel<CoachPendingState, CoachPendingEvent>() {

    override val initialViewState = CoachPendingState()
    private var hasLoadedInitialData = false

    override fun dismissError() {
        state.update { it.copy(error = null) }
    }

    val coachPendingState = state
        .onStart {
            if (!hasLoadedInitialData) {
                hasLoadedInitialData = true
                viewModelScope.launch {
                    profileRepository.profile.collect { p -> state.update { it.copy(profile = p) } }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = state.value
        )

    fun onEvent(event: CoachPendingEvent) {
        when (event) {
            // Back to the role picker, as v29's "Back to start".
            CoachPendingEvent.OnBackToStartClick -> viewModelScope.launch {
                navigator.navigateTo(Screen.OnboardingGraph) {
                    popUpTo(Screen.CoachPendingScreen) { inclusive = true }
                }
            }
        }
    }
}
