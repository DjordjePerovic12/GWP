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
import com.global_water_polo.play_abroad.core.components.isVideo
import com.global_water_polo.play_abroad.domain.model.GalleryItem
import com.global_water_polo.play_abroad.domain.model.Letter
import com.global_water_polo.play_abroad.domain.model.Pathway
import com.global_water_polo.play_abroad.domain.model.Portfolio
import kotlin.time.Clock
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
            ProfileEvent.OnTodayClick -> state.update { it.copy(sheet = ProfileSheet.Today) }
            is ProfileEvent.OnWorkoutToggle -> profileRepository.update {
                it.copy(workoutDone = if (event.index in it.workoutDone) it.workoutDone - event.index else it.workoutDone + event.index)
            }
            is ProfileEvent.OnEditInfo -> state.update { it.copy(sheet = ProfileSheet.Editor(event.group)) }
            is ProfileEvent.OnInfoSaved -> {
                updatePortfolio { it.copy(info = event.info) }
                state.update { it.copy(sheet = null) }
            }
            is ProfileEvent.OnPositionsSaved -> {
                profileRepository.update { it.copy(positions = event.positions, leftHanded = event.leftHanded) }
                state.update { it.copy(sheet = null) }
            }
            // Prototype: picked files stay on the device; nothing is uploaded yet. A picked photo or highlight
            // also lands in the gallery, like the prototype.
            is ProfileEvent.OnPhotoPicked -> updatePortfolio {
                val item = GalleryItem(newId(), event.file, video = false)
                it.copy(gallery = listOf(item) + it.gallery, photoId = item.id)
            }
            is ProfileEvent.OnHighlightPicked -> updatePortfolio {
                val item = GalleryItem(newId(), event.file, video = true)
                it.copy(gallery = listOf(item) + it.gallery, highlightId = item.id)
            }
            is ProfileEvent.OnTranscriptPicked -> updatePortfolio { it.copy(transcript = event.file) }
            ProfileEvent.OnLetterRequestClick -> state.update { it.copy(sheet = ProfileSheet.LetterRequest) }
            // Prototype: no email goes to the coach.
            is ProfileEvent.OnLetterRequested -> {
                updatePortfolio { it.copy(letters = it.letters + Letter(newId(), event.from, event.club)) }
                state.update { it.copy(sheet = null) }
            }
            is ProfileEvent.OnLetterUploaded -> updatePortfolio { it.copy(letters = it.letters + Letter(newId(), event.from, file = event.file)) }
            is ProfileEvent.OnLetterRemove -> updatePortfolio { p -> p.copy(letters = p.letters.filterNot { it.id == event.id }) }
            is ProfileEvent.OnGalleryAdd -> updatePortfolio { p ->
                val base = newId()
                p.copy(gallery = event.files.mapIndexed { i, f -> GalleryItem(base + i, f, f.isVideo()) } + p.gallery)
            }
            is ProfileEvent.OnGalleryFilter -> state.update { it.copy(galleryFilter = event.filter) }
            is ProfileEvent.OnGalleryItemClick -> state.update { it.copy(sheet = ProfileSheet.GalleryItem(event.id)) }
            is ProfileEvent.OnUseOnProfile -> {
                updatePortfolio { p ->
                    val item = p.gallery.find { it.id == event.id } ?: return@updatePortfolio p
                    if (item.video) p.copy(highlightId = item.id) else p.copy(photoId = item.id)
                }
                state.update { it.copy(sheet = null) }
            }
            is ProfileEvent.OnGalleryRemove -> {
                updatePortfolio { p ->
                    p.copy(
                        gallery = p.gallery.filterNot { it.id == event.id },
                        photoId = p.photoId.takeIf { it != event.id },
                        highlightId = p.highlightId.takeIf { it != event.id }
                    )
                }
                state.update { it.copy(sheet = null) }
            }
            is ProfileEvent.OnVisibilityClick -> profileRepository.update { it.copy(visibility = event.visibility) }
        }
    }

    private fun updatePortfolio(transform: (Portfolio) -> Portfolio) = profileRepository.update { it.copy(portfolio = transform(it.portfolio)) }

    private var lastId = 0L

    /** A new id for a letter or gallery item: the clock in milliseconds, never repeated. */
    private fun newId(): Long {
        lastId = maxOf(lastId + 1000, Clock.System.now().toEpochMilliseconds() * 1000)
        return lastId
    }

    private fun openTab(route: Screen) = viewModelScope.launch { navigator.openTab(route) }
}
