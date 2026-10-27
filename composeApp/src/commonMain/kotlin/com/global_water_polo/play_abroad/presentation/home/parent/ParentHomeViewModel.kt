package com.global_water_polo.play_abroad.presentation.home.parent

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.core.navigation.utils.Navigator
import com.global_water_polo.play_abroad.core.utils.NcaaClockCalculator
import com.global_water_polo.play_abroad.core.components.membership.MembershipSheet
import com.global_water_polo.play_abroad.domain.repository.ProfileRepository
import com.global_water_polo.play_abroad.domain.repository.cancelPlan
import com.global_water_polo.play_abroad.domain.repository.startPlan
import com.global_water_polo.play_abroad.domain.repository.switchToNext
import com.global_water_polo.play_abroad.presentation.base.BaseViewModel

/** Parent home: the athlete's progress, Play Abroad, a call with GWP, and inviting the athlete. */
class ParentHomeViewModel(
    private val profileRepository: ProfileRepository,
    private val navigator: Navigator
) : BaseViewModel<ParentHomeState, ParentHomeEvent>() {

    override val initialViewState = ParentHomeState()
    private var hasLoadedInitialData = false

    override fun dismissError() {
        state.update { it.copy(error = null) }
    }

    val parentHomeState = state
        .onStart {
            if (!hasLoadedInitialData) {
                hasLoadedInitialData = true
                viewModelScope.launch {
                    profileRepository.profile.collect { p ->
                        val window = p.birthday?.let { NcaaClockCalculator.window(it) }
                        state.update {
                            it.copy(
                                profile = p,
                                firstName = p.firstName,
                                kidName = p.kidName,
                                invited = p.invited,
                                tripStartOffset = if ("trip" in p.now) p.travelStartOffset else null,
                                tripMonths = p.travelMonths,
                                window = window,
                                countdown = window?.let { w -> NcaaClockCalculator.countdown(w.clock) }
                            )
                        }
                    }
                }
                viewModelScope.launch {
                    while (isActive) {
                        state.value.window?.let { w -> state.update { it.copy(countdown = NcaaClockCalculator.countdown(w.clock)) } }
                        delay(1_000L)
                    }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = state.value
        )

    fun onEvent(event: ParentHomeEvent) {
        when (event) {
            ParentHomeEvent.OnInviteClick -> viewModelScope.launch { navigator.navigateTo(Screen.InviteScreen) }
            // Like the prototype: planning a year abroad needs a membership; the trial comes first, then the sheet.
            ParentHomeEvent.OnCompareClick -> state.update {
                if (it.membership.plan == null) it.copy(membershipSheet = MembershipSheet.PAYWALL, abroadAfterTrial = true)
                else it.copy(abroadOpen = true)
            }
            // Prototype: a call or a proposal request is only remembered; GWP would reply by email.
            ParentHomeEvent.OnBookCallClick -> profileRepository.update { it.copy(callRequested = true) }
            ParentHomeEvent.OnAbroadRequestClick -> profileRepository.update { it.copy(abroadRequested = true) }
            ParentHomeEvent.OnSheetDismiss -> state.update { it.copy(abroadOpen = false) }
            is ParentHomeEvent.OnMembershipSheet -> state.update {
                it.copy(membershipSheet = event.sheet, abroadAfterTrial = it.abroadAfterTrial && event.sheet != null)
            }
            is ParentHomeEvent.OnStartPlan -> {
                profileRepository.startPlan(event.plan, event.yearly)
                state.update { it.copy(abroadOpen = it.abroadAfterTrial, abroadAfterTrial = false) }
            }
            ParentHomeEvent.OnSwitchToNext -> profileRepository.switchToNext()
            ParentHomeEvent.OnCancelPlan -> profileRepository.cancelPlan()
        }
    }
}
