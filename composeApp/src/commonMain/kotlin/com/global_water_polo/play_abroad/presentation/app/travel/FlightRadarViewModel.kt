package com.global_water_polo.play_abroad.presentation.app.travel

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
import com.global_water_polo.play_abroad.presentation.base.BaseViewModel

/** Travel tab (Flight Radar): the route from the home airport, the trips asked about on Globe+, and a checklist. */
class FlightRadarViewModel(
    private val profileRepository: ProfileRepository,
    private val navigator: Navigator
) : BaseViewModel<FlightRadarState, FlightRadarEvent>() {

    override val initialViewState = FlightRadarState()
    private var hasLoadedInitialData = false

    override fun dismissError() {
        state.update { it.copy(error = null) }
    }

    val flightRadarState = state
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

    fun onEvent(event: FlightRadarEvent) {
        when (event) {
            is FlightRadarEvent.OnTabClick -> viewModelScope.launch { navigator.openTab(event.tab.route) }
            is FlightRadarEvent.OnHomeAirportClick -> profileRepository.update { it.copy(homeAirport = event.code) }
            // Prototype: a flight request is only remembered; GWP's travel team would propose flights.
            is FlightRadarEvent.OnRequestFlightsClick -> profileRepository.update {
                it.copy(flightRequests = if (event.cityId in it.flightRequests) it.flightRequests - event.cityId else it.flightRequests + event.cityId)
            }
            is FlightRadarEvent.OnCheckClick -> profileRepository.update {
                it.copy(travelChecks = if (event.key in it.travelChecks) it.travelChecks - event.key else it.travelChecks + event.key)
            }
            FlightRadarEvent.OnOpenGlobeClick -> viewModelScope.launch { navigator.openTab(Screen.GlobeScreen()) }
        }
    }
}
