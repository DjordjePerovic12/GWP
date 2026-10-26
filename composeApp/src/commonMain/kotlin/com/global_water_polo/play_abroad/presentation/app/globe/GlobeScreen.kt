package com.global_water_polo.play_abroad.presentation.app.globe

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.global_water_polo.play_abroad.core.components.globe.GlobePin
import com.global_water_polo.play_abroad.core.components.globe.LocalGlobeController
import com.global_water_polo.play_abroad.core.utils.observeWithLifecycle
import com.global_water_polo.play_abroad.domain.model.WaterPoloMap
import com.global_water_polo.play_abroad.presentation.app.globe.components.CitySheet
import com.global_water_polo.play_abroad.presentation.app.globe.components.ClubSheet
import com.global_water_polo.play_abroad.presentation.base.BaseContentView

/** Region centres the globe turns to when no city is picked: Europe, the Americas, the world. */
private val REGION_VIEW = mapOf("eu" to (44f to 15f), "am" to (30f to -95f), "world" to (20f to 30f))

@Composable
fun GlobeScreen(
    modifier: Modifier = Modifier,
    viewModel: GlobeViewModel,
    showSnackBar: (String) -> Unit
) {
    val state by viewModel.globeState.collectAsStateWithLifecycle()
    val globe = LocalGlobeController.current

    viewModel.snackBarChannel.observeWithLifecycle { message ->
        showSnackBar(message)
    }

    // The listed cities are pinned on the ball; the picked one is bigger and faces the viewer.
    LaunchedEffect(state.cities, state.focus) {
        globe.pins = state.cities.map { GlobePin(it.latitude, it.longitude, active = it.id == state.focus?.id) }
    }
    LaunchedEffect(state.focus, state.region) {
        val focus = state.focus
        if (focus != null && focus.region == state.region) {
            globe.aimAt(focus.longitude, focus.latitude)
        } else {
            REGION_VIEW[state.region]?.let { (lat, lon) -> globe.aimAt(lon, lat) }
        }
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
            onDragStart = { globe.dragStart() },
            onDrag = { globe.drag(it) },
            onDragEnd = { globe.dragEnd(it) }
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
    when (sheet) {
        is GlobeSheet.CityPage -> WaterPoloMap.city(sheet.cityId)?.let { city ->
            CitySheet(
                city = city,
                asked = city.id in state.profile.trips,
                onClubClick = { onEvent(GlobeEvent.OnClubClick(it)) },
                onAskClick = { onEvent(GlobeEvent.OnAskClick(city.id)) },
                onDismiss = { onEvent(GlobeEvent.OnSheetDismiss) }
            )
        }
        is GlobeSheet.ClubPage -> WaterPoloMap.club(sheet.clubId)?.let { club ->
            ClubSheet(
                club = club,
                asked = club.cityId in state.profile.trips,
                onCityClick = { onEvent(GlobeEvent.OnCityClick(club.cityId)) },
                onAskClick = { onEvent(GlobeEvent.OnAskClick(club.cityId)) },
                onDismiss = { onEvent(GlobeEvent.OnSheetDismiss) }
            )
        }
    }
}
