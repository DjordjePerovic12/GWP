package com.global_water_polo.play_abroad.presentation.app.globe

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.core.navigation.utils.Navigator
import com.global_water_polo.play_abroad.core.navigation.utils.openTab
import com.global_water_polo.play_abroad.domain.model.WaterPoloMap
import com.global_water_polo.play_abroad.domain.repository.ProfileRepository
import com.global_water_polo.play_abroad.presentation.base.BaseViewModel

/**
 * Globe+: every club city on one globe. Search or pick a region, open a city and its clubs, and ask GWP
 * about a stay there (the trip then shows up on Travel). Booking a full experience comes later.
 */
class GlobeViewModel(
    savedStateHandle: SavedStateHandle,
    private val profileRepository: ProfileRepository,
    private val navigator: Navigator
) : BaseViewModel<GlobeState, GlobeEvent>() {

    private val startCity = savedStateHandle.toRoute<Screen.GlobeScreen>().city

    override val initialViewState = WaterPoloMap.city(startCity)?.let {
        GlobeState(region = it.region, sheet = GlobeSheet.CityPage(it.id), focus = it)
    } ?: GlobeState()
    private var hasLoadedInitialData = false

    override fun dismissError() {
        state.update { it.copy(error = null) }
    }

    val globeState = state
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

    fun onEvent(event: GlobeEvent) {
        when (event) {
            is GlobeEvent.OnTabClick -> viewModelScope.launch { navigator.openTab(event.tab.route) }
            is GlobeEvent.OnQueryChanged -> state.update { it.copy(query = event.query) }
            is GlobeEvent.OnRegionClick -> state.update { it.copy(region = event.region, query = "") }
            is GlobeEvent.OnCityClick -> state.update {
                it.copy(sheet = GlobeSheet.CityPage(event.cityId), focus = WaterPoloMap.city(event.cityId) ?: it.focus)
            }
            is GlobeEvent.OnClubClick -> state.update {
                val city = WaterPoloMap.club(event.clubId)?.let { club -> WaterPoloMap.city(club.cityId) }
                it.copy(sheet = GlobeSheet.ClubPage(event.clubId), focus = city ?: it.focus)
            }
            // Prototype: asking GWP only adds the city to the athlete's trips; nothing is sent.
            is GlobeEvent.OnAskClick -> profileRepository.update {
                it.copy(trips = if (event.cityId in it.trips) it.trips - event.cityId else it.trips + event.cityId)
            }
            GlobeEvent.OnSheetDismiss -> state.update { it.copy(sheet = null) }
        }
    }
}
