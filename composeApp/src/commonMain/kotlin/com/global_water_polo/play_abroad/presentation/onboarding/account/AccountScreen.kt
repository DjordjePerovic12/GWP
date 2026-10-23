package com.global_water_polo.play_abroad.presentation.onboarding.account

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.global_water_polo.play_abroad.core.utils.observeWithLifecycle
import com.global_water_polo.play_abroad.presentation.base.BaseContentView
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingEvent
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingViewModel

@Composable
fun AccountScreen(
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
        AccountScreenContent(
            state = state,
            onNameChanged = { viewModel.onEvent(OnboardingEvent.OnFirstNameChanged(it)) },
            onOrganizationChanged = { viewModel.onEvent(OnboardingEvent.OnOrganizationChanged(it)) },
            onEmailChanged = { viewModel.onEvent(OnboardingEvent.OnEmailChanged(it)) },
            onBackClick = { viewModel.onEvent(OnboardingEvent.NavigateBack) },
            onCreateClick = { viewModel.onEvent(OnboardingEvent.OnCreateAccountClick) }
        )
    }
}
