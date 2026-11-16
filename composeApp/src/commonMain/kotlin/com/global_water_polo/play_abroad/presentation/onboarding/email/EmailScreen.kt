package com.global_water_polo.play_abroad.presentation.onboarding.email

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.global_water_polo.play_abroad.core.utils.observeWithLifecycle
import com.global_water_polo.play_abroad.presentation.base.BaseContentView
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingEvent
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingViewModel

@Composable
fun EmailScreen(
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
        EmailScreenContent(
            state = state,
            onEmailChanged = { viewModel.onEvent(OnboardingEvent.OnEmailChanged(it)) },
            onParentEmailChanged = { viewModel.onEvent(OnboardingEvent.OnParentEmailChanged(it)) },
            onHeardClick = { viewModel.onEvent(OnboardingEvent.OnHeardFromClick(it)) },
            onBackClick = { viewModel.onEvent(OnboardingEvent.NavigateBack) },
            onContinueClick = { viewModel.onEvent(OnboardingEvent.OnEmailContinueClick) }
        )
    }
}
