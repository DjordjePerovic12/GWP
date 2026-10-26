package com.global_water_polo.play_abroad.presentation.onboarding

import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptionsBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.number
import kotlinx.datetime.plus
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.core.navigation.utils.Navigator
import com.global_water_polo.play_abroad.core.utils.NcaaClockCalculator
import com.global_water_polo.play_abroad.core.utils.PathwayCalculator
import com.global_water_polo.play_abroad.domain.model.Lane
import com.global_water_polo.play_abroad.domain.repository.ProfileRepository
import com.global_water_polo.play_abroad.presentation.base.BaseViewModel

/**
 * One ViewModel for the whole onboarding graph. It is scoped to Screen.OnboardingGraph, so every step
 * screen reads and writes the same state, like the prototype. The athlete and parent step order lives
 * in OnboardingFlow.kt; coaches and clubs keep their short lane (intent, account, travel, pending).
 */
class OnboardingViewModel(
    private val navigator: Navigator,
    private val profileRepository: ProfileRepository
) : BaseViewModel<OnboardingState, OnboardingEvent>() {

    override val initialViewState = seededState()

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
            // Back to the spinning globe; also works when the entry screen was opened from a home screen.
            OnboardingEvent.OnBackToGlobeClick -> go(Screen.IntroScreen) {
                popUpTo(Screen.IntroScreen) { inclusive = true }
            }
            // Starting over clears every answer, so one person's details never carry into another role.
            OnboardingEvent.OnBackToStartClick -> {
                state.update { OnboardingState() }
                go(Screen.WhoScreen) {
                    popUpTo(Screen.WhoScreen) { inclusive = true }
                }
            }
            is OnboardingEvent.OnRoleClick -> {
                // A different role starts clean; picking the same role again keeps what was typed.
                state.update { if (it.lane == event.lane) it.copy(looking = emptySet()) else OnboardingState(lane = event.lane) }
                go(state.value.flow().first().route(event.lane))
            }
            // Skip and the tour's own timer can both fire; the one-time steps move on only once.
            is OnboardingEvent.OnStepContinueClick -> if (event.step !in ONCE || event.step !in state.value.seen) next(event.step)

            is OnboardingEvent.OnAskPick -> onAskPick(event.step, event.key)
            is OnboardingEvent.OnAskContinueClick -> if (state.value.picks(event.step).isNotEmpty()) next(event.step.flowStep)

            is OnboardingEvent.OnBirthdayChanged -> onBirthdayChanged(event)
            OnboardingEvent.OnBirthdayContinueClick -> if (state.value.window != null) next(FlowStep.BIRTHDAY)
            OnboardingEvent.OnClockContinueClick -> next(FlowStep.CLOCK)

            is OnboardingEvent.OnFirstNameChanged -> state.update { it.copy(firstName = event.value) }
            is OnboardingEvent.OnLastNameChanged -> state.update { it.copy(lastName = event.value) }
            OnboardingEvent.OnNameContinueClick -> if (state.value.isNameValid) next(FlowStep.NAME)

            is OnboardingEvent.OnGradChanged -> state.update {
                it.copy(gradMonth = event.month, gradYear = event.year).withSuggestedStart()
            }
            OnboardingEvent.OnGradContinueClick -> if (state.value.isGradSet) next(FlowStep.GRAD)

            is OnboardingEvent.OnEmailChanged -> state.update { it.copy(email = event.value) }
            OnboardingEvent.OnEmailContinueClick -> if (state.value.isEmailValid) next(FlowStep.EMAIL)

            is OnboardingEvent.OnHeardFromClick -> state.update { it.copy(heardFrom = event.value) }
            is OnboardingEvent.OnRelationClick -> state.update { it.copy(relation = event.value) }
            OnboardingEvent.OnResendLinkClick -> state.update { it.copy(linkResent = true) }

            is OnboardingEvent.OnTrialPick -> state.update { it.copy(trialPick = event.plan) }
            OnboardingEvent.OnTrialStartClick -> {
                state.update { it.copy(plan = it.trialPick) }
                next(FlowStep.TRIAL)
            }
            OnboardingEvent.OnTrialLaterClick -> next(FlowStep.TRIAL)

            is OnboardingEvent.OnProgramPick -> state.update { it.copy(program = event.key) }
            OnboardingEvent.OnMatchApplyClick -> {
                // Prototype: applying for a locked program starts the Elite trial; nothing is charged.
                state.update {
                    val program = it.pathwayProgram
                    it.copy(
                        program = program.key,
                        applied = true,
                        plan = if (program.needsElite) OnboardingState.PLAN_ELITE else it.plan ?: OnboardingState.PLAN_NEXT
                    )
                }
                next(FlowStep.MATCH)
            }
            OnboardingEvent.OnMatchLaterClick -> next(FlowStep.MATCH)

            is OnboardingEvent.OnLookToggle -> state.update {
                it.copy(looking = if (event.key in it.looking) it.looking - event.key else it.looking + event.key)
            }
            OnboardingEvent.OnLookContinueClick -> if (state.value.looking.isNotEmpty()) next(FlowStep.LOOK)
            // The length slider shows 3 months until it is touched; picking a start keeps that as the length.
            is OnboardingEvent.OnTravelStartClick -> state.update {
                it.copy(travelStartOffset = event.offset, travelMonths = if (it.travelMonths > 0) it.travelMonths else 3)
            }
            is OnboardingEvent.OnTravelLengthChanged -> state.update { it.copy(travelMonths = event.months) }
            OnboardingEvent.OnTravelContinueClick -> if (state.value.canTravelContinue) next(FlowStep.TRAVEL)

            OnboardingEvent.OnSeeAllProgramsClick -> go(Screen.OffersScreen(state.value.lane))
            is OnboardingEvent.OnOfferPick -> state.update { it.copy(program = if (it.program == event.key) "" else event.key) }
            OnboardingEvent.OnOffersPrimaryClick -> onOffersPrimary()
            OnboardingEvent.OnOffersLaterClick -> nextFromOffers()
            // Prototype: starting the Elite trial unlocks every program; nothing is charged.
            OnboardingEvent.OnEliteStartClick -> state.update { it.copy(plan = OnboardingState.PLAN_ELITE) }

            OnboardingEvent.OnTripPlanContinueClick -> next(FlowStep.TRIP_PLAN)

            is OnboardingEvent.OnClubCityChanged -> state.update { it.copy(clubCity = event.value) }
            is OnboardingEvent.OnClubCountryClick -> state.update { it.copy(clubCountry = toggleSingle(it.clubCountry, event.value)) }
            OnboardingEvent.OnPlaceClubClick -> if (state.value.canPlaceClub) state.update { it.copy(clubPlaced = true) }
            OnboardingEvent.OnClubPlacedBackClick -> state.update { it.copy(clubPlaced = false) }
            OnboardingEvent.OnClubPlacedContinueClick -> {
                state.update { it.copy(clubPlaced = false) }
                next(FlowStep.PLACE)
            }
            is OnboardingEvent.OnStaffCoachChanged -> state.update { it.copy(staffCoach = event.value) }
            is OnboardingEvent.OnStaffSizeClick -> state.update { it.copy(staffSize = toggleSingle(it.staffSize, event.value)) }
            is OnboardingEvent.OnLanguageToggle -> state.update {
                it.copy(languages = if (event.value in it.languages) it.languages - event.value else it.languages + event.value)
            }
            OnboardingEvent.OnStaffContinueClick -> if (state.value.canStaffContinue) next(FlowStep.STAFF)

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
                next(FlowStep.ACCOUNT)
            }
        }
    }

    private fun onAskPick(step: AskStep, key: String) = state.update { s ->
        when (step) {
            AskStep.NOW -> {
                val now = toggleMulti(s.now, key)
                // A different starting point asks a different probe; switching to or from a team trip resets the dates.
                val tripChanged = ("trip" in now) != ("trip" in s.now)
                s.copy(
                    now = now,
                    probe = "",
                    travelStartOffset = if (tripChanged) null else s.travelStartOffset,
                    travelMonths = if (tripChanged) 0 else s.travelMonths
                )
            }
            AskStep.LEVEL -> s.copy(level = toggleMulti(s.level, key))
            AskStep.GOAL -> s.copy(goal = toggleSingle(s.goal, key))
            AskStep.CLUB -> s.copy(club = toggleSingle(s.club, key)).withSuggestedStart()
            AskStep.PROBE -> s.copy(probe = toggleSingle(s.probe, key))
            AskStep.STOP -> s.copy(stops = toggleMulti(s.stops, key))
            AskStep.FACILITIES -> s.copy(facilities = if (key in s.facilities) s.facilities - key else s.facilities + key)
        }
    }

    /** "Nothing", "Not sure" and "Team trip" stand alone; any other pick clears them. */
    private fun toggleMulti(current: Set<String>, key: String): Set<String> =
        if (key in EXCLUSIVE) {
            if (key in current) emptySet() else setOf(key)
        } else {
            val rest = current - EXCLUSIVE
            if (key in rest) rest - key else rest + key
        }

    private fun toggleSingle(current: String, key: String): String = if (current == key) "" else key

    /** Graduation plus club season give the earliest start, pre-filled for the pathway. */
    private fun OnboardingState.withSuggestedStart(): OnboardingState {
        val month = gradMonth ?: return this
        val year = gradYear ?: return this
        if (club.isEmpty()) return this
        return copy(travelStartOffset = PathwayCalculator.suggestStart(month, year, club))
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
    }

    /**
     * Moves to the step after [from]. The globe tour and the world reveal play once: they leave the
     * back stack when done and are skipped when the user comes forward again.
     */
    private fun next(from: FlowStep) {
        val s = state.value
        val steps = s.flow()
        val upcoming = steps.drop(steps.indexOf(from) + 1).firstOrNull { it !in ONCE || it !in s.seen }
        if (from in ONCE) state.update { it.copy(seen = it.seen + from) }
        if (upcoming == null) {
            finish()
            return
        }
        val route = upcoming.route(s.lane)
        if (from in ONCE) {
            go(route) { popUpTo(from.route(s.lane)) { inclusive = true } }
        } else {
            go(route)
        }
    }

    private fun finish() {
        saveProfile()
        when (state.value.lane) {
            Lane.ATHLETE -> goHome(Screen.ProfileScreen())
            Lane.PARENT -> go(Screen.InviteScreen)
            Lane.COACH -> goHome(Screen.CoachHomeScreen)
            // The club profile is the club lane's last step; it ends with "Back to start".
            Lane.CLUB -> Unit
        }
    }

    /**
     * All offers' main button, as in the prototype: no plan yet starts the GWP Next trial; a program picked
     * applies for it; otherwise move on (Elite members) or stay (Next members get the Elite sheet on screen).
     */
    private fun onOffersPrimary() {
        val s = state.value
        when {
            s.plan == null -> state.update { it.copy(plan = OnboardingState.PLAN_NEXT) }
            s.offerPick != null -> {
                state.update { it.copy(applied = true) }
                nextFromOffers()
            }
            s.isElite -> nextFromOffers()
        }
    }

    /** All offers is a flow step for full-window athletes, and opens from "See all programs" on the pathway. */
    private fun nextFromOffers() = next(if (FlowStep.OFFERS in state.value.flow()) FlowStep.OFFERS else FlowStep.MATCH)

    /** Starts from what was already saved, so a step opened again from the app keeps earlier answers. */
    private fun seededState(): OnboardingState {
        val p = profileRepository.profile.value
        val birthday = p.birthday
        val window = birthday?.let { NcaaClockCalculator.window(it) }
        return OnboardingState(
            lane = p.lane ?: Lane.ATHLETE,
            now = p.now,
            level = p.level,
            birthMonth = birthday?.month?.number,
            birthDay = birthday?.day,
            birthYear = birthday?.year,
            window = window,
            countdown = window?.let { NcaaClockCalculator.countdown(it.clock) },
            firstName = p.firstName,
            lastName = p.lastName,
            gradMonth = p.gradMonth,
            gradYear = p.gradYear,
            goal = p.goal,
            club = p.club,
            stops = p.stops,
            email = p.email,
            heardFrom = p.heardFrom,
            relation = p.relation,
            plan = p.plan,
            program = p.appliedProgram,
            kidName = p.kidName,
            kidContact = p.kidContact,
            invited = p.invited,
            organization = p.organization,
            looking = p.looking,
            travelStartOffset = p.travelStartOffset,
            travelMonths = p.travelMonths
        )
    }

    private fun saveProfile() {
        val s = state.value
        profileRepository.update {
            it.copy(
                lane = s.lane,
                now = s.now,
                level = s.level,
                birthday = s.birthday,
                firstName = s.firstName.trim(),
                lastName = s.lastName.trim(),
                gradMonth = s.gradMonth,
                gradYear = s.gradYear,
                goal = s.goal,
                club = s.club,
                stops = s.stops,
                email = s.email.trim(),
                heardFrom = s.heardFrom,
                relation = s.relation,
                plan = s.plan,
                trialEnds = if (s.plan == null) null else it.trialEnds ?: NcaaClockCalculator.today().plus(DatePeriod(days = 7)),
                appliedProgram = if (s.applied) s.program else it.appliedProgram,
                kidName = s.kidName.trim(),
                kidContact = s.kidContact.trim(),
                invited = s.invited,
                organization = s.organization.trim(),
                looking = s.looking,
                travelStartOffset = s.travelStartOffset,
                travelMonths = if (s.travelMonths > 0) s.travelMonths else s.pathwayProgram.months
            )
        }
    }

    private fun go(screen: Screen, options: NavOptionsBuilder.() -> Unit = {}) {
        viewModelScope.launch { navigator.navigateTo(screen, options) }
    }

    /** Leaves onboarding for the app: the onboarding steps are removed from the back stack. */
    private fun goHome(screen: Screen) = go(screen) {
        popUpTo(Screen.IntroScreen) { inclusive = true }
    }

    private companion object {
        val EXCLUSIVE = setOf("nothing", "unsure", "trip")
        val ONCE = setOf(FlowStep.TOUR, FlowStep.WORLD)
    }
}
