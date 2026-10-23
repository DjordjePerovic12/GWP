package com.global_water_polo.play_abroad.presentation.home.athlete

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.core.navigation.utils.Navigator
import com.global_water_polo.play_abroad.core.utils.NcaaClockCalculator
import com.global_water_polo.play_abroad.domain.model.Profile
import com.global_water_polo.play_abroad.domain.repository.ProfileRepository
import com.global_water_polo.play_abroad.presentation.base.BaseViewModel

/** Athlete profile home: how coaches see them, visibility, what to finish, the feed and premium moments. */
class AthleteHomeViewModel(
    private val profileRepository: ProfileRepository,
    private val navigator: Navigator
) : BaseViewModel<AthleteHomeState, AthleteHomeEvent>() {

    override val initialViewState = AthleteHomeState()
    private var hasLoadedInitialData = false

    override fun dismissError() {
        state.update { it.copy(error = null) }
    }

    val athleteHomeState = state
        .onStart {
            if (!hasLoadedInitialData) {
                hasLoadedInitialData = true
                observeProfile()
                tickClock()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = state.value
        )

    fun onEvent(event: AthleteHomeEvent) {
        when (event) {
            is AthleteHomeEvent.OnVisibilityClick -> profileRepository.update { it.copy(visibility = event.visibility) }
            is AthleteHomeEvent.OnTodoClick -> profileRepository.update {
                it.copy(finished = if (event.key in it.finished) it.finished - event.key else it.finished + event.key)
            }
            AthleteHomeEvent.OnReviewClick -> viewModelScope.launch {
                navigator.navigateTo(if (state.value.reviewSubmitted) Screen.ReviewStatusScreen else Screen.ApplyReviewScreen)
            }
            //TODO: Sessions with pros and the year-abroad planner are not designed yet.
            AthleteHomeEvent.OnBookSessionClick, AthleteHomeEvent.OnPlanAbroadClick -> Unit
        }
    }

    private fun observeProfile() {
        viewModelScope.launch {
            profileRepository.profile.collect { profile -> state.update { fromProfile(it, profile) } }
        }
    }

    private fun tickClock() {
        viewModelScope.launch {
            while (isActive) {
                state.value.window?.let { w -> state.update { it.copy(countdown = NcaaClockCalculator.countdown(w.clock)) } }
                delay(1_000L)
            }
        }
    }

    private fun fromProfile(current: AthleteHomeState, p: Profile): AthleteHomeState {
        // Opened without onboarding (fresh install deep link, previews): the prototype's sample athlete.
        val birthday = p.birthday ?: LocalDate(2008, 10, 3)
        val window = NcaaClockCalculator.window(birthday)
        return current.copy(
            firstName = p.firstName.ifBlank { "Alex" },
            lastName = p.lastName.ifBlank { "Rivera" },
            position = p.position,
            leftHanded = p.leftHanded,
            window = window,
            countdown = NcaaClockCalculator.countdown(window.clock),
            visibility = p.visibility,
            finished = p.finished,
            reviewSubmitted = p.reviewSubmitted,
            travelStartOffset = p.travelStartOffset,
            travelMonths = p.travelMonths
        )
    }
}
