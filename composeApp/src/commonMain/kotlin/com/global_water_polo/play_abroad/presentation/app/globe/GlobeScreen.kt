package com.global_water_polo.play_abroad.presentation.app.globe

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.pluralStringResource
import com.global_water_polo.play_abroad.core.components.globe.GlobeBadge
import com.global_water_polo.play_abroad.core.components.globe.GlobeCity
import com.global_water_polo.play_abroad.core.components.globe.GlobeCluster
import com.global_water_polo.play_abroad.core.components.globe.GlobeHit
import com.global_water_polo.play_abroad.core.components.globe.GlobeLabel
import com.global_water_polo.play_abroad.core.components.globe.GlobeMarkers
import com.global_water_polo.play_abroad.domain.model.City
import com.global_water_polo.play_abroad.resources.Res
import com.global_water_polo.play_abroad.resources.globe_clubs_count
import kotlin.math.hypot
import kotlin.math.max
import com.global_water_polo.play_abroad.core.components.globe.LocalGlobeController
import com.global_water_polo.play_abroad.core.utils.observeWithLifecycle
import com.global_water_polo.play_abroad.domain.model.WaterPoloMap
import com.global_water_polo.play_abroad.presentation.app.globe.components.CitySheet
import com.global_water_polo.play_abroad.presentation.app.globe.components.ClubSheet
import com.global_water_polo.play_abroad.presentation.app.globe.components.ApartmentTour
import com.global_water_polo.play_abroad.presentation.app.globe.components.DatePickerSheet
import com.global_water_polo.play_abroad.presentation.app.globe.components.ExperienceSheet
import com.global_water_polo.play_abroad.presentation.app.globe.components.TripReviewSheet
import com.global_water_polo.play_abroad.presentation.app.globe.components.TripsSheet
import com.global_water_polo.play_abroad.core.utils.NcaaClockCalculator
import com.global_water_polo.play_abroad.presentation.base.BaseContentView

/** Where each region puts the globe: latitude, longitude and zoom (the prototype's region fly-to). */
private val REGION_VIEW = mapOf("eu" to Triple(45f, 16f, 3f), "am" to Triple(33f, -95f, 1.7f), "world" to Triple(20f, 60f, 0.95f))

@Composable
fun GlobeScreen(
    modifier: Modifier = Modifier,
    viewModel: GlobeViewModel,
    showSnackBar: (String) -> Unit
) {
    val state by viewModel.globeState.collectAsStateWithLifecycle()
    val globe = LocalGlobeController.current
    val slop = with(LocalDensity.current) { 28.dp.toPx() }

    viewModel.snackBarChannel.observeWithLifecycle { message ->
        showSnackBar(message)
    }

    // Clusters, pins, club badges and labels for every city and club; the picked city stands out.
    val markers = globeMarkers(activeCityId = state.focus?.id)
    LaunchedEffect(markers) { globe.markers = markers }

    // Each change flies the globe on its own: the region, a country picked in the filter, a city or club,
    // a club group's pop-up. Whichever changed last wins.
    LaunchedEffect(state.region) {
        REGION_VIEW[state.region]?.let { (lat, lon, zoom) -> globe.aimAt(lon, lat, zoom) }
    }
    LaunchedEffect(state.country) {
        val inCountry = WaterPoloMap.cities.filter { it.country == state.country }
        if (inCountry.isNotEmpty()) {
            val zoom = if (inCountry.size > 4) 1.8f else 3.4f
            globe.aimAt(inCountry.map { it.longitude }.average().toFloat(), inCountry.map { it.latitude }.average().toFloat(), zoom)
        }
    }
    LaunchedEffect(state.focus) {
        state.focus?.let { globe.aimAt(it.longitude, it.latitude, max(globe.zoom, 2.8f)) }
    }
    LaunchedEffect(state.popup) {
        state.popup?.let { WaterPoloMap.clubGroups.getOrNull(it) }?.let { globe.aimAt(it.longitude, it.latitude, max(globe.zoom, 3.4f)) }
    }
    DisposableEffect(globe) {
        onDispose { globe.clearTour() }
    }

    BaseContentView(
        modifier = modifier,
        viewState = state,
        applySystemBarsPadding = false
    ) {
        GlobeScreenContent(
            state = state,
            onEvent = viewModel::onEvent,
            zoom = globe.zoom,
            onDragStart = { globe.dragStart() },
            onDrag = { degrees, tilt ->
                globe.drag(degrees)
                globe.tiltBy(tilt)
            },
            onDragEnd = { globe.dragEnd(it) },
            onPinch = { globe.zoomBy(it) },
            onTap = { position ->
                when (val hit = globe.markerAt(position, slop)) {
                    is GlobeHit.Group -> viewModel.onEvent(GlobeEvent.OnGroupClick(hit.index))
                    is GlobeHit.City -> viewModel.onEvent(GlobeEvent.OnCityClick(hit.cityId))
                    // A country circle from far away: fly in on it, like the prototype's zoom into a region.
                    is GlobeHit.Country -> WaterPoloMap.cities.filter { it.country == hit.country }.takeIf { it.isNotEmpty() }?.let { list ->
                        globe.aimAt(list.map { it.longitude }.average().toFloat(), list.map { it.latitude }.average().toFloat(), 3.4f)
                    }
                    null -> Unit
                }
            },
            onZoomIn = { globe.zoomTo(globe.zoom * 1.45f) },
            onZoomOut = { globe.zoomTo(globe.zoom / 1.45f) }
        )
    }

    // Each page is its own sheet (key), so going city -> club -> city swaps the sheet instead of
    // reusing the one already open.
    val sheet = state.sheet
    if (sheet != null) key(sheet) {
        GlobePage(sheet = sheet, state = state, onEvent = viewModel::onEvent)
    }
}

@Composable
private fun GlobePage(sheet: GlobeSheet, state: GlobeState, onEvent: (GlobeEvent) -> Unit) {
    val dismiss = { onEvent(GlobeEvent.OnSheetDismiss) }
    when (sheet) {
        is GlobeSheet.CityPage -> WaterPoloMap.city(sheet.cityId)?.let { city ->
            CitySheet(
                city = city,
                trip = state.tripIn(city.id),
                onClubClick = { onEvent(GlobeEvent.OnClubClick(it)) },
                onBuildClick = { onEvent(GlobeEvent.OnBuildClick(city.id)) },
                onDismiss = dismiss
            )
        }
        is GlobeSheet.ClubPage -> WaterPoloMap.club(sheet.clubId)?.let { club ->
            ClubSheet(
                club = club,
                trip = state.tripIn(club.cityId),
                onCityClick = { onEvent(GlobeEvent.OnCityClick(club.cityId)) },
                onBuildClick = { onEvent(GlobeEvent.OnBuildClick(club.cityId, club.id)) },
                onDismiss = dismiss
            )
        }
        GlobeSheet.Build -> state.draft?.let { draft ->
            ExperienceSheet(
                trip = draft,
                onChange = { onEvent(GlobeEvent.OnDraftChanged(it)) },
                onDatesClick = { onEvent(GlobeEvent.OnDatesClick) },
                onTourClick = { onEvent(GlobeEvent.OnTourClick) },
                onReviewClick = { onEvent(GlobeEvent.OnReviewClick) },
                onDismiss = dismiss
            )
        }
        GlobeSheet.Dates -> state.draft?.let { draft ->
            DatePickerSheet(
                cityName = WaterPoloMap.city(draft.cityId)?.name.orEmpty(),
                arrive = draft.arrive,
                leave = draft.leave,
                today = NcaaClockCalculator.today(),
                onApply = { arrive, leave -> onEvent(GlobeEvent.OnDatesApplied(arrive, leave)) },
                onDismiss = { onEvent(GlobeEvent.OnBackToBuild) }
            )
        }
        GlobeSheet.Tour -> state.draft?.let { draft ->
            ApartmentTour(
                cityName = WaterPoloMap.city(draft.cityId)?.name.orEmpty(),
                onRequestClick = { onEvent(GlobeEvent.OnApartmentRequestClick) },
                onClose = { onEvent(GlobeEvent.OnBackToBuild) }
            )
        }
        is GlobeSheet.Review -> state.trip(sheet.tripId)?.let { trip ->
            TripReviewSheet(
                trip = trip,
                onEditClick = { onEvent(GlobeEvent.OnEditTripClick(trip.id)) },
                onNotesChange = { onEvent(GlobeEvent.OnNotesChanged(trip.id, it)) },
                onSendClick = { onEvent(GlobeEvent.OnSendInquiryClick(trip.id)) },
                onDismiss = dismiss
            )
        }
        GlobeSheet.Trips -> TripsSheet(
            trips = state.profile.trips,
            onOpenClick = { onEvent(GlobeEvent.OnTripOpenClick(it)) },
            onRemoveClick = { onEvent(GlobeEvent.OnTripRemoveClick(it)) },
            onDismiss = dismiss
        )
    }
}

/** Globe+ markers from the club map (the prototype's clusters, CLUB_GROUPS badges and city labels). */
@Composable
private fun globeMarkers(activeCityId: String?): GlobeMarkers {
    val notes = WaterPoloMap.cities.associate { city ->
        val n = WaterPoloMap.clubsIn(city.id).size
        city.id to if (n == 0) "" else pluralStringResource(Res.plurals.globe_clubs_count, n, n)
    }
    return remember(activeCityId, notes) {
        val clusters = WaterPoloMap.cities.groupBy { it.country }.map { (country, list) ->
            GlobeCluster(
                country = country,
                latitude = list.map { it.latitude }.average().toFloat(),
                longitude = list.map { it.longitude }.average().toFloat(),
                count = list.sumOf { max(1, WaterPoloMap.clubsIn(it.id).size) }
            )
        }
        // One label per spot: cities closer than a degree (Piraeus and Athens) would print on top of each other.
        val labelled = ArrayList<City>()
        WaterPoloMap.cities.forEach { c -> if (labelled.none { hypot(it.latitude - c.latitude, it.longitude - c.longitude) < 0.9f }) labelled += c }
        GlobeMarkers(
            clusters = clusters,
            cities = WaterPoloMap.cities.map { GlobeCity(it.id, it.latitude, it.longitude, active = it.id == activeCityId) },
            badges = WaterPoloMap.clubGroups.mapIndexed { i, g ->
                GlobeBadge(i, g.latitude, g.longitude, WaterPoloMap.monogram(g.clubs.first().name), g.clubs.size)
            },
            labels = labelled.map { GlobeLabel(it.id, it.latitude, it.longitude, it.name, notes[it.id].orEmpty()) }
        )
    }
}
