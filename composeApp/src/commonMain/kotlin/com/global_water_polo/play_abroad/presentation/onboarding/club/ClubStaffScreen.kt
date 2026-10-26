package com.global_water_polo.play_abroad.presentation.onboarding.club

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.global_water_polo.play_abroad.core.utils.observeWithLifecycle
import com.global_water_polo.play_abroad.presentation.base.BaseContentView
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingEvent
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingViewModel

@Composable
fun ClubStaffScreen(
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel,
    showSnackBar: (String) -> Unit
) {
    val state by viewModel.onboardingState.collectAsStateWithLifecycle()

    viewModel.snackBarChannel.observeWithLifecycle { message ->
        showSnackBar(message)
    }

    BaseContentView(
        modifier = modifier,
        viewState = state,
        applySystemBarsPadding = false
    ) {
        ClubStaffScreenContent(
            state = state,
            onCoachChanged = { viewModel.onEvent(OnboardingEvent.OnStaffCoachChanged(it)) },
            onSizeClick = { viewModel.onEvent(OnboardingEvent.OnStaffSizeClick(it)) },
            onLanguageClick = { viewModel.onEvent(OnboardingEvent.OnLanguageToggle(it)) },
            onBackClick = { viewModel.onEvent(OnboardingEvent.NavigateBack) },
            onContinueClick = { viewModel.onEvent(OnboardingEvent.OnStaffContinueClick) }
        )
    }
}
