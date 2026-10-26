package com.global_water_polo.play_abroad.presentation.app.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.global_water_polo.play_abroad.core.components.AppTab
import com.global_water_polo.play_abroad.core.components.membership.MembershipSheet
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.core.navigation.utils.Navigator
import com.global_water_polo.play_abroad.core.navigation.utils.openTab
import com.global_water_polo.play_abroad.core.utils.PathwayCalculator
import com.global_water_polo.play_abroad.domain.model.Pathway
import com.global_water_polo.play_abroad.domain.repository.ProfileRepository
import com.global_water_polo.play_abroad.domain.repository.cancelPlan
import com.global_water_polo.play_abroad.domain.repository.startPlan
import com.global_water_polo.play_abroad.domain.repository.switchToNext
import com.global_water_polo.play_abroad.presentation.base.BaseViewModel

/**
 * Profile tab: who the athlete is and their membership, then Pathway (programs, clinics, "build my
 * pathway"), GWP LAB (the eight filmed tests) and Portfolio (what coaches see). Everything is kept in
 * ProfileRepository so the other tabs see the same plan and answers.
 */
class ProfileViewModel(
    savedStateHandle: SavedStateHandle,
    private val profileRepository: ProfileRepository,
    private val navigator: Navigator
) : BaseViewModel<ProfileState, ProfileEvent>() {

    private val route = savedStateHandle.toRoute<Screen.ProfileScreen>()
    override val initialViewState = ProfileState(segment = ProfileSegment.of(route.segment), preview = route.preview)
    private var hasLoadedInitialData = false

    override fun dismissError() {
        state.update { it.copy(error = null) }
    }

    val profileState = state
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

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.OnTabClick -> openTab(event.tab.route)
            is ProfileEvent.OnSegmentClick -> state.update { it.copy(segment = event.segment) }
            is ProfileEvent.OnMembershipSheet -> state.update { it.copy(membershipSheet = event.sheet) }
            is ProfileEvent.OnStartPlan -> profileRepository.startPlan(event.plan, event.yearly)
            ProfileEvent.OnSwitchToNext -> profileRepository.switchToNext()
            ProfileEvent.OnCancelPlan -> profileRepository.cancelPlan()
            ProfileEvent.OnSheetDismiss -> state.update { it.copy(sheet = null) }

            ProfileEvent.OnBuildPathwayClick -> state.update { it.copy(sheet = ProfileSheet.BuildPathway) }
            is ProfileEvent.OnBuildPathway -> {
                val s = state.value
                val steps = PathwayCalculator.buildPathway(
                    education = event.education,
                    timing = event.timing,
                    gradMonth = s.profile.gradMonth,
                    gradYear = s.profile.gradYear,
                    club = s.profile.club,
                    freeYear = s.freeYear
                )
                profileRepository.update {
                    it.copy(
                        pathway = Pathway(event.goal, event.level, event.education, event.timing, event.destinations, steps),
                        goal = event.goal,
                        level = if (event.level.isEmpty()) it.level else setOf(event.level)
                    )
                }
                state.update { it.copy(sheet = null, segment = ProfileSegment.PATH) }
            }
            ProfileEvent.OnExploreGlobeClick -> openTab(Screen.GlobeScreen())
            is ProfileEvent.OnProgramClick -> state.update { it.copy(sheet = ProfileSheet.ProgramDetail(event.program)) }
            is ProfileEvent.OnProgramCtaClick -> {
                val s = state.value
                when {
                    event.program.key in s.profile.inquiries -> Unit
                    // A locked program asks for a plan first: the paywall with no plan, the Elite offer on Next.
                    s.locked(event.program) -> state.update {
                        it.copy(sheet = null, membershipSheet = if (s.membership.plan == null) MembershipSheet.PAYWALL else MembershipSheet.ELITE)
                    }
                    else -> state.update { it.copy(sheet = ProfileSheet.Inquiry(event.program)) }
                }
            }
            is ProfileEvent.OnSendInquiry -> {
                // Prototype: the inquiry is only remembered here; nothing is sent.
                profileRepository.update { it.copy(inquiries = it.inquiries + event.program.key) }
                state.update { it.copy(sheet = ProfileSheet.Inquiry(event.program, sent = true)) }
            }
            is ProfileEvent.OnCityClick -> openTab(Screen.GlobeScreen(city = event.cityId))
            is ProfileEvent.OnClinicClick -> profileRepository.update {
                it.copy(clinicRequests = if (event.index in it.clinicRequests) it.clinicRequests - event.index else it.clinicRequests + event.index)
            }

            is ProfileEvent.OnLabTestClick -> state.update { it.copy(sheet = ProfileSheet.LabTest(event.index)) }
            is ProfileEvent.OnLabAnglePicked -> profileRepository.update {
                if (event.index in it.labSent) return@update it
                val angles = it.labAngles[event.index].orEmpty()
                it.copy(labAngles = it.labAngles + (event.index to angles + (event.angle to event.file)))
            }
            is ProfileEvent.OnLabAngleRemove -> profileRepository.update {
                if (event.index in it.labSent) return@update it
                it.copy(labAngles = it.labAngles + (event.index to it.labAngles[event.index].orEmpty() - event.angle))
            }
            is ProfileEvent.OnLabSendClick -> {
                val s = state.value
                // Coach reviews are part of GWP Elite.
                if (!s.membership.elite) {
                    state.update { it.copy(sheet = null, membershipSheet = if (s.membership.plan == null) MembershipSheet.PAYWALL else MembershipSheet.ELITE) }
                } else if (s.labState(event.index) == "ready") {
                    profileRepository.update { it.copy(labSent = it.labSent + event.index) }
                }
            }
            ProfileEvent.OnLabExampleClick -> state.update { it.copy(sheet = ProfileSheet.LabExample) }

            is ProfileEvent.OnPreviewToggle -> state.update { it.copy(preview = event.preview) }
            // Only the position row is tapped to mark it; the rest open the picker.
            is ProfileEvent.OnPortfolioItemClick -> profileRepository.update { it.copy(positionAdded = !it.positionAdded) }
            // Prototype: the picked file stays on the device; nothing is uploaded yet.
            is ProfileEvent.OnPortfolioFilePicked -> profileRepository.update { it.copy(uploads = it.uploads + (event.key to event.file)) }
            is ProfileEvent.OnPortfolioFileRemove -> profileRepository.update { it.copy(uploads = it.uploads - event.key) }
            is ProfileEvent.OnVisibilityClick -> profileRepository.update { it.copy(visibility = event.visibility) }
        }
    }

    private fun openTab(route: Screen) = viewModelScope.launch { navigator.openTab(route) }
}
