package com.global_water_polo.play_abroad.presentation.onboarding

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.number
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.core.navigation.utils.Navigator
import com.global_water_polo.play_abroad.core.utils.NcaaClockCalculator
import com.global_water_polo.play_abroad.domain.model.Lane
import com.global_water_polo.play_abroad.domain.repository.ProfileRepository
import com.global_water_polo.play_abroad.presentation.base.BaseViewModel

/**
 * One ViewModel for the whole onboarding graph (who's here, birthday, clock, name, email, window,
 * intent, travel, position, save, invite, coach/club account, pending). It is scoped to
 * Screen.OnboardingGraph, so every step screen reads and writes the same state, like the prototype.
 */
class OnboardingViewModel(
    private val navigator: Navigator,
    private val profileRepository: ProfileRepository
) : BaseViewModel<OnboardingState, OnboardingEvent>() {

    override val initialViewState = seededState()
    private var advanceJob: Job? = null

    /** True when a step is opened again from the app (e.g. parent home -> invite), not during first onboarding. */
    private val resumedFromApp = profileRepository.profile.value.lane != null

    override fun dismissError() {
        state.update { it.copy(error = null) }
    }

    val onboardingState = state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = state.value
        )

    init {
        // The NCAA clock ticks in the device's time zone; refreshed four times a second like the prototype.
        viewModelScope.launch {
            while (isActive) {
                state.value.window?.let { window ->
                    val countdown = NcaaClockCalculator.countdown(window.clock)
                    if (countdown != state.value.countdown) state.update { it.copy(countdown = countdown) }
                }
                delay(250L)
            }
        }
    }

    fun onEvent(event: OnboardingEvent) {
        when (event) {
            OnboardingEvent.NavigateBack -> viewModelScope.launch { navigator.navigateUp() }
            OnboardingEvent.OnBackToGlobeClick -> viewModelScope.launch { navigator.navigateUp() }
            OnboardingEvent.OnBackToStartClick -> go(Screen.WhoScreen) {
                popUpTo(Screen.WhoScreen) { inclusive = true }
            }
            is OnboardingEvent.OnRoleClick -> {
                state.update { it.copy(lane = event.lane, looking = emptySet()) }
                go(
                    when (event.lane) {
                        Lane.ATHLETE, Lane.PARENT -> Screen.BirthdayScreen(event.lane)
                        Lane.COACH, Lane.CLUB -> Screen.LookScreen(event.lane)
                    }
                )
            }

            is OnboardingEvent.OnBirthdayChanged -> onBirthdayChanged(event)
            OnboardingEvent.OnBirthdayContinueClick -> if (state.value.window != null) go(Screen.ClockScreen(state.value.lane))
            OnboardingEvent.OnClockContinueClick -> go(Screen.NameScreen(state.value.lane))

            is OnboardingEvent.OnFirstNameChanged -> state.update { it.copy(firstName = event.value) }
            is OnboardingEvent.OnLastNameChanged -> state.update { it.copy(lastName = event.value) }
            OnboardingEvent.OnNameContinueClick -> if (state.value.isNameValid) go(Screen.EmailScreen(state.value.lane))
            is OnboardingEvent.OnEmailChanged -> state.update { it.copy(email = event.value) }
            OnboardingEvent.OnEmailContinueClick -> if (state.value.isEmailValid) go(Screen.WindowScreen(state.value.lane))

            OnboardingEvent.OnShareClick -> state.update { it.copy(shareCardReady = true) }
            OnboardingEvent.OnWindowContinueClick -> go(Screen.LookScreen(state.value.lane))

            is OnboardingEvent.OnLookToggle -> state.update {
                it.copy(looking = if (event.key in it.looking) it.looking - event.key else it.looking + event.key)
            }
            OnboardingEvent.OnLookContinueClick -> if (state.value.looking.isNotEmpty()) go(routeAfterLook())
            is OnboardingEvent.OnTravelStartClick -> state.update { it.copy(travelStartOffset = event.offset) }
            is OnboardingEvent.OnTravelLengthChanged -> state.update { it.copy(travelMonths = event.months) }
            OnboardingEvent.OnTravelContinueClick -> if (state.value.canTravelContinue) go(routeAfterTravel())
            is OnboardingEvent.OnPositionClick -> state.update { it.copy(position = event.position) }
            OnboardingEvent.OnLeftHandedToggle -> state.update { it.copy(leftHanded = !it.leftHanded) }
            OnboardingEvent.OnPositionContinueClick -> if (state.value.position.isNotEmpty()) go(Screen.SaveScreen(state.value.lane))

            is OnboardingEvent.OnPasswordChanged -> state.update { it.copy(password = event.value) }
            is OnboardingEvent.OnHeardFromClick -> state.update { it.copy(heardFrom = event.value) }
            is OnboardingEvent.OnRelationClick -> state.update { it.copy(relation = event.value) }
            OnboardingEvent.OnSaveClick -> if (state.value.canSave) {
                saveProfile()
                if (state.value.lane == Lane.PARENT) go(Screen.InviteScreen) else goHome(Screen.AthleteHomeScreen)
            }

            is OnboardingEvent.OnKidNameChanged -> state.update { it.copy(kidName = event.value) }
            is OnboardingEvent.OnKidContactChanged -> state.update { it.copy(kidContact = event.value) }
            OnboardingEvent.OnInviteClick -> if (state.value.canInvite) state.update { it.copy(invited = true) }
            OnboardingEvent.OnInviteDoneClick -> {
                saveProfile()
                if (resumedFromApp) viewModelScope.launch { navigator.navigateUp() } else goHome(Screen.ParentHomeScreen)
            }

            is OnboardingEvent.OnOrganizationChanged -> state.update { it.copy(organization = event.value) }
            OnboardingEvent.OnCreateAccountClick -> if (state.value.canCreateAccount) {
                saveProfile()
                go(if ("trip" in state.value.looking) Screen.TravelScreen(state.value.lane) else Screen.PendingScreen)
            }
        }
    }

    private fun onBirthdayChanged(event: OnboardingEvent.OnBirthdayChanged) {
        val complete = event.month != null && event.day != null && event.year != null
        val problem = if (complete) NcaaClockCalculator.problem(event.year!!, event.month!!, event.day!!) else null
        val window = if (complete && problem == null) {
            NcaaClockCalculator.window(NcaaClockCalculator.dateOrNull(event.year!!, event.month!!, event.day!!)!!)
        } else null
        state.update {
            it.copy(
                birthMonth = event.month,
                birthDay = event.day,
                birthYear = event.year,
                birthdayProblem = problem,
                window = window,
                countdown = window?.let { w -> NcaaClockCalculator.countdown(w.clock) }
            )
        }
        // All three wheels set and valid: the clock takes over on its own once the wheels settle.
        advanceJob?.cancel()
        if (window != null && event.fromUser) {
            advanceJob = viewModelScope.launch {
                delay(850L)
                go(Screen.ClockScreen(state.value.lane))
            }
        }
    }

    private fun routeAfterLook(): Screen {
        val s = state.value
        val abroadOrTrip = "abroad" in s.looking || "trip" in s.looking
        return when (s.lane) {
            Lane.ATHLETE, Lane.PARENT -> if (abroadOrTrip) Screen.TravelScreen(s.lane) else Screen.PositionScreen(s.lane)
            Lane.COACH, Lane.CLUB -> Screen.AccountScreen(s.lane)
        }
    }

    private fun routeAfterTravel(): Screen = when (state.value.lane) {
        Lane.ATHLETE, Lane.PARENT -> Screen.PositionScreen(state.value.lane)
        Lane.COACH, Lane.CLUB -> Screen.PendingScreen
    }

    /** Starts from what was already saved, so a step opened again from the app keeps earlier answers. */
    private fun seededState(): OnboardingState {
        val p = profileRepository.profile.value
        val birthday = p.birthday
        val window = birthday?.let { NcaaClockCalculator.window(it) }
        return OnboardingState(
            lane = p.lane ?: Lane.ATHLETE,
            birthMonth = birthday?.month?.number,
            birthDay = birthday?.day,
            birthYear = birthday?.year,
            window = window,
            countdown = window?.let { NcaaClockCalculator.countdown(it.clock) },
            firstName = p.firstName,
            lastName = p.lastName,
            email = p.email,
            heardFrom = p.heardFrom,
            relation = p.relation,
            kidName = p.kidName,
            kidContact = p.kidContact,
            invited = p.invited,
            organization = p.organization,
            looking = p.looking,
            travelStartOffset = p.travelStartOffset,
            travelMonths = p.travelMonths,
            position = p.position,
            leftHanded = p.leftHanded
        )
    }

    private fun saveProfile() {
        val s = state.value
        profileRepository.update {
            it.copy(
                lane = s.lane,
                birthday = s.birthday,
                firstName = s.firstName.trim(),
                lastName = s.lastName.trim(),
                email = s.email.trim(),
                heardFrom = s.heardFrom,
                relation = s.relation,
                kidName = s.kidName.trim(),
                kidContact = s.kidContact.trim(),
                invited = s.invited,
                organization = s.organization.trim(),
                looking = s.looking,
                travelStartOffset = s.travelStartOffset,
                travelMonths = s.travelMonths,
                position = s.position,
                leftHanded = s.leftHanded
            )
        }
    }

    private fun go(screen: Screen, options: androidx.navigation.NavOptionsBuilder.() -> Unit = {}) {
        advanceJob?.cancel()
        viewModelScope.launch { navigator.navigateTo(screen, options) }
    }

    /** Leaves onboarding for the app: the onboarding steps are removed from the back stack. */
    private fun goHome(screen: Screen) = go(screen) {
        popUpTo(Screen.IntroScreen) { inclusive = true }
    }
}
