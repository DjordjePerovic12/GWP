package com.global_water_polo.play_abroad.presentation.app.inbox

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.core.navigation.utils.Navigator
import com.global_water_polo.play_abroad.core.navigation.utils.openTab
import com.global_water_polo.play_abroad.core.utils.LockAction
import com.global_water_polo.play_abroad.domain.repository.ProfileRepository
import com.global_water_polo.play_abroad.domain.repository.lockStep
import com.global_water_polo.play_abroad.presentation.base.BaseViewModel

/**
 * Inbox (v29's inbox28): what needs them (the email link, a parent's confirmation, a sample coach request)
 * and recent activity. The demo buttons stand in for the email link and the parent.
 */
class InboxViewModel(
    private val profileRepository: ProfileRepository,
    private val navigator: Navigator
) : BaseViewModel<InboxState, InboxEvent>() {

    override val initialViewState = InboxState()
    private var hasLoadedInitialData = false

    override fun dismissError() {
        state.update { it.copy(error = null) }
    }

    val inboxState = state
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

    fun onEvent(event: InboxEvent) {
        when (event) {
            is InboxEvent.OnTabClick -> open(event.tab.route)
            is InboxEvent.OnLock -> {
                state.update { it.copy(lockView = profileRepository.lockStep(event.action, it.lockView)) }
                if (event.action == LockAction.DONE) open(Screen.HomeScreen)
            }
            // Sample: nothing is shared with anyone.
            is InboxEvent.OnTranscriptAnswer -> profileRepository.update {
                it.copy(transcriptRequest = if (event.approved) "approved" else "declined")
            }
        }
    }

    private fun open(route: Screen) = viewModelScope.launch { navigator.openTab(route) }
}
