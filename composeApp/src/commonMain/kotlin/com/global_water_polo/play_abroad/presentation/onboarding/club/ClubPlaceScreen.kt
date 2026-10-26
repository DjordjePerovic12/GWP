package com.global_water_polo.play_abroad.presentation.onboarding.club

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringArrayResource
import com.global_water_polo.play_abroad.core.components.globe.GlobePin
import com.global_water_polo.play_abroad.core.components.globe.LocalGlobeController
import com.global_water_polo.play_abroad.core.utils.observeWithLifecycle
import com.global_water_polo.play_abroad.presentation.base.BaseContentView
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingEvent
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingViewModel
import com.global_water_polo.play_abroad.resources.Res
import com.global_water_polo.play_abroad.resources.club_countries

@Composable
fun ClubPlaceScreen(
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel,
    showSnackBar: (String) -> Unit
) {
    val state by viewModel.onboardingState.collectAsStateWithLifecycle()
    val globe = LocalGlobeController.current
    val haptics = LocalHapticFeedback.current
    val countries = stringArrayResource(Res.array.club_countries)

    viewModel.snackBarChannel.observeWithLifecycle { message ->
        showSnackBar(message)
    }

    // "Place my club on the globe": the big night globe rises, flies to the country and pins the club.
    LaunchedEffect(state.clubPlaced) {
        val spot = countryLocation(countries, state.clubCountry)
        globe.revealOverride = state.clubPlaced && spot != null
        if (state.clubPlaced && spot != null) {
            globe.pins = listOf(GlobePin(spot.first, spot.second, active = true))
            globe.aimAt(spot.second, spot.first)
            if (globe.hapticsOn) haptics.performHapticFeedback(HapticFeedbackType.Confirm)
        }
    }
    DisposableEffect(globe) {
        onDispose { globe.revealOverride = false }
    }

    BaseContentView(
        modifier = modifier,
        viewState = state,
        applySystemBarsPadding = false
    ) {
        ClubPlaceScreenContent(
            state = state,
            onNameChanged = { viewModel.onEvent(OnboardingEvent.OnOrganizationChanged(it)) },
            onCityChanged = { viewModel.onEvent(OnboardingEvent.OnClubCityChanged(it)) },
            onCountryClick = { viewModel.onEvent(OnboardingEvent.OnClubCountryClick(it)) },
            onPlaceClick = { viewModel.onEvent(OnboardingEvent.OnPlaceClubClick) },
            onPlacedContinueClick = { viewModel.onEvent(OnboardingEvent.OnClubPlacedContinueClick) },
            onBackClick = { viewModel.onEvent(OnboardingEvent.NavigateBack) }
        )
    }
}
