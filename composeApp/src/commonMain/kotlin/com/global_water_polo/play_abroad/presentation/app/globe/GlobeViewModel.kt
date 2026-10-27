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
import com.global_water_polo.play_abroad.core.utils.NcaaClockCalculator
import com.global_water_polo.play_abroad.domain.model.Trip
import com.global_water_polo.play_abroad.domain.model.WaterPoloMap
import kotlin.time.Clock
import com.global_water_polo.play_abroad.domain.repository.ProfileRepository
import com.global_water_polo.play_abroad.presentation.base.BaseViewModel

/**
 * Globe+: every club city on one globe. Search or pick a region, open a city and its clubs, then build an
 * experience there (club, dates, home, extras), review it and send GWP an availability inquiry. Sent
 * trips are tracked on Travel. Prototype: nothing is sent or charged.
 */
class GlobeViewModel(
    savedStateHandle: SavedStateHandle,
    private val profileRepository: ProfileRepository,
    private val navigator: Navigator
) : BaseViewModel<GlobeState, GlobeEvent>() {

    private val route = savedStateHandle.toRoute<Screen.GlobeScreen>()

    override val initialViewState = run {
        // Travel opens a draft's review straight away; otherwise a city page when one is given.
        val trip = profileRepository.profile.value.trips.find { it.id == route.trip }
        val city = WaterPoloMap.city(trip?.cityId ?: route.city)
        when {
            trip != null && city != null -> GlobeState(region = city.region, sheet = GlobeSheet.Review(trip.id), focus = city)
            city != null -> GlobeState(region = city.region, sheet = GlobeSheet.CityPage(city.id), focus = city)
            else -> GlobeState()
        }
    }
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
            // The prototype's g-prog: the programs live on the Pathway.
            GlobeEvent.OnProgramsClick -> viewModelScope.launch { navigator.openTab(Screen.ProfileScreen(segment = "path")) }
            is GlobeEvent.OnQueryChanged -> state.update { it.copy(query = event.query) }
            is GlobeEvent.OnRegionClick -> state.update { it.copy(region = event.region, query = "") }
            is GlobeEvent.OnCityClick -> state.update {
                it.copy(sheet = GlobeSheet.CityPage(event.cityId), focus = WaterPoloMap.city(event.cityId) ?: it.focus, popup = null)
            }
            is GlobeEvent.OnClubClick -> state.update {
                val city = WaterPoloMap.club(event.clubId)?.let { club -> WaterPoloMap.city(club.cityId) }
                it.copy(sheet = GlobeSheet.ClubPage(event.clubId), focus = city ?: it.focus, popup = null)
            }
            GlobeEvent.OnSheetDismiss -> state.update { it.copy(sheet = null) }
            is GlobeEvent.OnCountryClick -> state.update { it.copy(country = event.country, popup = null) }
            is GlobeEvent.OnSexClick -> state.update { it.copy(sex = if (it.sex == event.sex) "" else event.sex) }
            is GlobeEvent.OnGroupClick -> state.update { it.copy(popup = event.index) }
            GlobeEvent.OnPopupDismiss -> state.update { it.copy(popup = null) }

            is GlobeEvent.OnBuildClick -> state.update {
                // Like the prototype's startBuild: an unsent trip to the city is edited, not duplicated.
                val existing = it.profile.trips.find { trip -> trip.cityId == event.cityId && !trip.sent }
                val draft = existing ?: Trip.draft(
                    id = Clock.System.now().toEpochMilliseconds(),
                    cityId = event.cityId,
                    clubId = event.clubId,
                    today = NcaaClockCalculator.today(),
                    startOffset = it.profile.travelStartOffset ?: 1
                )
                it.copy(draft = if (event.clubId.isNotEmpty()) draft.copy(clubId = event.clubId) else draft, sheet = GlobeSheet.Build)
            }
            is GlobeEvent.OnDraftChanged -> state.update { it.copy(draft = event.draft) }
            GlobeEvent.OnDatesClick -> state.update { it.copy(sheet = GlobeSheet.Dates) }
            is GlobeEvent.OnDatesApplied -> state.update {
                it.copy(draft = it.draft?.copy(arrive = event.arrive, leave = event.leave), sheet = GlobeSheet.Build)
            }
            GlobeEvent.OnTourClick -> state.update { it.copy(sheet = GlobeSheet.Tour) }
            GlobeEvent.OnApartmentRequestClick -> state.update { it.copy(draft = it.draft?.copy(apartmentRequested = true), sheet = GlobeSheet.Build) }
            GlobeEvent.OnBackToBuild -> state.update { it.copy(sheet = GlobeSheet.Build) }
            GlobeEvent.OnReviewClick -> {
                val draft = state.value.draft ?: return
                profileRepository.update { p ->
                    p.copy(trips = if (p.trips.any { it.id == draft.id }) p.trips.map { if (it.id == draft.id) draft else it } else p.trips + draft)
                }
                state.update { it.copy(sheet = GlobeSheet.Review(draft.id)) }
            }

            is GlobeEvent.OnEditTripClick -> state.update {
                val trip = it.trip(event.tripId)
                if (trip == null || trip.sent) it.copy(sheet = null) else it.copy(draft = trip, sheet = GlobeSheet.Build)
            }
            is GlobeEvent.OnNotesChanged -> updateTrip(event.tripId) { it.copy(notes = event.notes) }
            // Prototype: sending only marks the trip as an inquiry; GWP would verify club and housing first.
            is GlobeEvent.OnSendInquiryClick -> {
                updateTrip(event.tripId) { it.copy(sent = true) }
                state.update { it.copy(sheet = null, draft = null) }
                viewModelScope.launch { navigator.openTab(Screen.FlightRadarScreen) }
            }
            GlobeEvent.OnTripsClick -> state.update { it.copy(sheet = GlobeSheet.Trips) }
            is GlobeEvent.OnTripOpenClick -> {
                val trip = state.value.trip(event.tripId) ?: return
                if (trip.sent) {
                    state.update { it.copy(sheet = null) }
                    viewModelScope.launch { navigator.openTab(Screen.FlightRadarScreen) }
                } else {
                    state.update { it.copy(sheet = GlobeSheet.Review(trip.id)) }
                }
            }
            is GlobeEvent.OnTripRemoveClick -> profileRepository.update { p -> p.copy(trips = p.trips.filterNot { it.id == event.tripId }) }
        }
    }

    private fun updateTrip(id: Long, transform: (Trip) -> Trip) = profileRepository.update { p ->
        p.copy(trips = p.trips.map { if (it.id == id) transform(it) else it })
    }
}
