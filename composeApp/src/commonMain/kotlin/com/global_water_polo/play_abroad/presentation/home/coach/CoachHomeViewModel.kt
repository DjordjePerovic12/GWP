package com.global_water_polo.play_abroad.presentation.home.coach

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

/** Coach home: the scouting board, with sample profiles while GWP reviews the coach account. */
class CoachHomeViewModel(
    private val profileRepository: ProfileRepository,
    private val navigator: Navigator
) : BaseViewModel<CoachHomeState, CoachHomeEvent>() {

    override val initialViewState = CoachHomeState()
    private var hasLoadedInitialData = false

    override fun dismissError() {
        state.update { it.copy(error = null) }
    }

    val coachHomeState = state
        .onStart {
            if (!hasLoadedInitialData) {
                hasLoadedInitialData = true
                viewModelScope.launch {
                    profileRepository.profile.collect { p ->
                        state.update { it.copy(firstName = p.firstName, looking = p.looking) }
                    }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = state.value
        )

    fun onEvent(event: CoachHomeEvent) {
        when (event) {
            is CoachHomeEvent.OnFilterClick -> state.update { it.copy(filter = event.filter) }
            // Prototype: a request only changes the button; nothing is sent.
            is CoachHomeEvent.OnRequestClick -> state.update { it.copy(requested = it.requested + event.id) }
            CoachHomeEvent.OnBackToStartClick -> viewModelScope.launch {
                navigator.navigateTo(Screen.OnboardingGraph) {
                    popUpTo(Screen.CoachHomeScreen) { inclusive = true }
                }
            }
        }
    }
}
