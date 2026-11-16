package com.global_water_polo.play_abroad.presentation.app.home

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import com.global_water_polo.play_abroad.core.components.membership.MembershipSheet
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.core.navigation.utils.Navigator
import com.global_water_polo.play_abroad.core.navigation.utils.openTab
import com.global_water_polo.play_abroad.core.utils.AccountGate
import com.global_water_polo.play_abroad.core.utils.LaneKind
import com.global_water_polo.play_abroad.core.utils.LockAction
import com.global_water_polo.play_abroad.core.utils.NcaaClockCalculator
import com.global_water_polo.play_abroad.core.utils.PathwayCalculator
import com.global_water_polo.play_abroad.core.utils.freeYear
import com.global_water_polo.play_abroad.core.utils.laneInfo
import com.global_water_polo.play_abroad.domain.model.Pathway
import com.global_water_polo.play_abroad.domain.model.Profile
import com.global_water_polo.play_abroad.domain.repository.ProfileRepository
import com.global_water_polo.play_abroad.domain.repository.cancelPlan
import com.global_water_polo.play_abroad.domain.repository.lockStep
import com.global_water_polo.play_abroad.domain.repository.startPlan
import com.global_water_polo.play_abroad.domain.repository.switchToNext
import com.global_water_polo.play_abroad.presentation.base.BaseViewModel

/**
 * Home (v29's home28): the greeting and NCAA chip, "Your next chapter" with the plan built from the
 * onboarding answers, programs and clinics. The plan sheet can adjust those answers and rebuild the plan.
 */
class HomeViewModel(
    private val profileRepository: ProfileRepository,
    private val navigator: Navigator
) : BaseViewModel<HomeState, HomeEvent>() {

    override val initialViewState = HomeState()
    private var hasLoadedInitialData = false

    override fun dismissError() {
        state.update { it.copy(error = null) }
    }

    val homeState = state
        .onStart {
            if (!hasLoadedInitialData) {
                hasLoadedInitialData = true
                viewModelScope.launch {
                    profileRepository.profile.collect { p -> state.update { it.copy(profile = p) } }
                }
                // Profiles from before v29 have answers but no plan: build it once from what they said.
                val p = profileRepository.profile.value
                if (p.pathway == null && p.gradYear != null) profileRepository.update { it.copy(pathway = planOf(it)) }
                // Approaching the window: the chip counts down live.
                viewModelScope.launch {
                    while (isActive) {
                        val lane = state.value.lane
                        state.update {
                            it.copy(countdown = if (lane?.lane == LaneKind.APPROACHING) NcaaClockCalculator.countdown(lane.clock) else null)
                        }
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

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.OnTabClick -> open(event.tab.route)
            is HomeEvent.OnLock -> {
                state.update { it.copy(lockView = profileRepository.lockStep(event.action, it.lockView)) }
                if (event.action == LockAction.DONE) state.update { it.copy(sheet = null) }
            }
            HomeEvent.OnSheetDismiss -> state.update { it.copy(sheet = null) }

            HomeEvent.OnPlanClick -> state.update { it.copy(sheet = HomeSheet.Plan()) }
            HomeEvent.OnAdjustClick -> state.update {
                val p = it.profile
                it.copy(
                    sheet = HomeSheet.Plan(adjusting = true),
                    draft = PlanDraft(now = p.now.firstOrNull().orEmpty(), gradYear = p.gradYear, availability = p.availability, firstStep = p.firstStep)
                )
            }
            is HomeEvent.OnDraftNow -> state.update { it.copy(draft = it.draft.copy(now = event.key)) }
            is HomeEvent.OnDraftGrad -> state.update { it.copy(draft = it.draft.copy(gradYear = event.year)) }
            is HomeEvent.OnDraftAvailability -> state.update { it.copy(draft = it.draft.copy(availability = event.key)) }
            is HomeEvent.OnDraftFirstStep -> state.update { it.copy(draft = it.draft.copy(firstStep = event.key)) }
            HomeEvent.OnRebuildClick -> {
                val d = state.value.draft
                profileRepository.update {
                    val changed = it.copy(
                        now = if (d.now.isEmpty()) it.now else setOf(d.now),
                        gradMonth = if (d.gradYear != null) it.gradMonth ?: 6 else it.gradMonth,
                        gradYear = d.gradYear ?: it.gradYear,
                        availability = d.availability,
                        firstStep = d.firstStep
                    )
                    changed.copy(pathway = planOf(changed))
                }
                state.update { it.copy(sheet = HomeSheet.Plan()) }
            }

            HomeEvent.OnGlobeClick -> open(Screen.GlobeScreen())
            is HomeEvent.OnProgramClick -> state.update { it.copy(sheet = HomeSheet.ProgramDetail(event.program)) }
            is HomeEvent.OnProgramCtaClick -> {
                val s = state.value
                val pathway = s.pathway
                when {
                    event.program.key in s.profile.inquiries -> Unit
                    // A locked program asks for a plan first: the paywall with no plan, the Elite offer on Next.
                    pathway.locked(event.program) -> state.update {
                        it.copy(sheet = null, membershipSheet = if (pathway.membership.plan == null) MembershipSheet.PAYWALL else MembershipSheet.ELITE)
                    }
                    lockedAccount() -> Unit
                    else -> state.update { it.copy(sheet = HomeSheet.Inquiry(event.program)) }
                }
            }
            is HomeEvent.OnSendInquiry -> if (!lockedAccount()) {
                // Prototype: the inquiry is only remembered here; nothing is sent.
                profileRepository.update { it.copy(inquiries = it.inquiries + event.program.key) }
                state.update { it.copy(sheet = HomeSheet.Inquiry(event.program, sent = true)) }
            }
            is HomeEvent.OnCityClick -> open(Screen.GlobeScreen(city = event.cityId))
            is HomeEvent.OnClinicClick -> if (!lockedAccount()) profileRepository.update {
                it.copy(clinicRequests = if (event.index in it.clinicRequests) it.clinicRequests - event.index else it.clinicRequests + event.index)
            }

            is HomeEvent.OnMembershipSheet -> if (event.sheet == null || !lockedAccount()) state.update { it.copy(membershipSheet = event.sheet) }
            is HomeEvent.OnStartPlan -> profileRepository.startPlan(event.plan, event.yearly)
            HomeEvent.OnSwitchToNext -> profileRepository.switchToNext()
            HomeEvent.OnCancelPlan -> profileRepository.cancelPlan()
        }
    }

    /** While the account is locked, an action opens the lock sheet instead (v29's click guard). */
    private fun lockedAccount(): Boolean {
        if (!AccountGate.locked(state.value.profile)) return false
        state.update { it.copy(sheet = null, lockView = it.lockView.copy(sheet = true)) }
        return true
    }

    private fun open(route: Screen) = viewModelScope.launch { navigator.openTab(route) }

    private fun planOf(p: Profile): Pathway = PathwayCalculator.planOf(p)
}
