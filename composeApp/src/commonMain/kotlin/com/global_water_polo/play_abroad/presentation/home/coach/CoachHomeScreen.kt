package com.global_water_polo.play_abroad.presentation.home.coach

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.global_water_polo.play_abroad.core.utils.observeWithLifecycle
import com.global_water_polo.play_abroad.presentation.base.BaseContentView

@Composable
fun CoachHomeScreen(
    modifier: Modifier = Modifier,
    viewModel: CoachHomeViewModel,
    showSnackBar: (String) -> Unit
) {
    val state by viewModel.coachHomeState.collectAsStateWithLifecycle()

    viewModel.snackBarChannel.observeWithLifecycle { message ->
        showSnackBar(message)
    }

    BaseContentView(
        modifier = modifier,
        viewState = state
    ) {
        CoachHomeScreenContent(
            state = state,
            onFilterClick = { viewModel.onEvent(CoachHomeEvent.OnFilterClick(it)) },
            onRequestClick = { viewModel.onEvent(CoachHomeEvent.OnRequestClick(it)) },
            onBackToStartClick = { viewModel.onEvent(CoachHomeEvent.OnBackToStartClick) }
        )
    }
}
