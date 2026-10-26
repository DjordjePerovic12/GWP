package com.global_water_polo.play_abroad.presentation.app.settings

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.core.navigation.utils.Navigator
import com.global_water_polo.play_abroad.core.navigation.utils.openTab
import com.global_water_polo.play_abroad.domain.repository.ProfileRepository
import com.global_water_polo.play_abroad.domain.repository.cancelPlan
import com.global_water_polo.play_abroad.domain.repository.startPlan
import com.global_water_polo.play_abroad.domain.repository.switchToNext
import com.global_water_polo.play_abroad.presentation.base.BaseViewModel

/** Settings tab: profile shortcuts, membership, notifications, home airport and sign out. */
class SettingsViewModel(
    private val profileRepository: ProfileRepository,
    private val navigator: Navigator
) : BaseViewModel<SettingsState, SettingsEvent>() {

    override val initialViewState = SettingsState()
    private var hasLoadedInitialData = false

    override fun dismissError() {
        state.update { it.copy(error = null) }
    }

    val settingsState = state
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

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.OnTabClick -> open(event.tab.route)
            SettingsEvent.OnEditProfileClick -> open(Screen.ProfileScreen(segment = "port"))
            SettingsEvent.OnPreviewClick -> open(Screen.ProfileScreen(segment = "port", preview = true))
            is SettingsEvent.OnMembershipSheet -> state.update { it.copy(membershipSheet = event.sheet) }
            is SettingsEvent.OnStartPlan -> profileRepository.startPlan(event.plan, event.yearly)
            SettingsEvent.OnSwitchToNext -> profileRepository.switchToNext()
            SettingsEvent.OnCancelPlan -> profileRepository.cancelPlan()
            SettingsEvent.OnNotificationsClick -> profileRepository.update { it.copy(notifications = !it.notifications) }
            SettingsEvent.OnHomeAirportClick -> open(Screen.FlightRadarScreen)
            // Prototype: signing out forgets everything (it was only kept in memory) and starts again at the globe.
            SettingsEvent.OnSignOutClick -> {
                profileRepository.reset()
                viewModelScope.launch {
                    navigator.navigateTo(Screen.IntroScreen) {
                        popUpTo<Screen.ProfileScreen> { inclusive = true }
                    }
                }
            }
        }
    }

    private fun open(route: Screen) = viewModelScope.launch { navigator.openTab(route) }
}
