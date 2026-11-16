package com.global_water_polo.play_abroad.presentation.onboarding.enroll

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.global_water_polo.play_abroad.core.utils.observeWithLifecycle
import com.global_water_polo.play_abroad.presentation.base.BaseContentView
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingEvent
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingViewModel

@Composable
fun EnrollScreen(
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
        EnrollScreenContent(
            state = state,
            onMonthClick = { viewModel.onEvent(OnboardingEvent.OnEnrollMonthClick(it)) },
            onYearClick = { viewModel.onEvent(OnboardingEvent.OnEnrollYearClick(it)) },
            onBackClick = { viewModel.onEvent(OnboardingEvent.NavigateBack) },
            onContinueClick = { viewModel.onEvent(OnboardingEvent.OnEnrollContinueClick) }
        )
    }
}
