package com.global_water_polo.play_abroad.presentation.host

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

/** The invite-only host club view (v29's h-host): the club's week and the athletes placed there. Demo: from Quick start. */
class HostViewModel(
    private val profileRepository: ProfileRepository,
    private val navigator: Navigator
) : BaseViewModel<HostState, HostEvent>() {

    override val initialViewState = HostState()
    private var hasLoadedInitialData = false

    override fun dismissError() {
        state.update { it.copy(error = null) }
    }

    val hostState = state
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

    fun onEvent(event: HostEvent) {
        when (event) {
            // Prototype: schedule uploads aren't handled yet.
            HostEvent.OnUploadClick -> Unit
            HostEvent.OnBackClick -> viewModelScope.launch { navigator.navigateUp() }
        }
    }
}
