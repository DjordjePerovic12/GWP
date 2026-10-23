package com.global_water_polo.play_abroad.presentation.home.athlete

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.global_water_polo.play_abroad.core.utils.observeWithLifecycle
import com.global_water_polo.play_abroad.presentation.base.BaseContentView

@Composable
fun AthleteHomeScreen(
    modifier: Modifier = Modifier,
    viewModel: AthleteHomeViewModel,
    showSnackBar: (String) -> Unit
) {
    val state by viewModel.athleteHomeState.collectAsStateWithLifecycle()

    viewModel.snackBarChannel.observeWithLifecycle { message ->
        showSnackBar(message)
    }

    BaseContentView(
        modifier = modifier,
        viewState = state
    ) {
        AthleteHomeScreenContent(
            state = state,
            onVisibilityClick = { viewModel.onEvent(AthleteHomeEvent.OnVisibilityClick(it)) },
            onTodoClick = { viewModel.onEvent(AthleteHomeEvent.OnTodoClick(it)) },
            onReviewClick = { viewModel.onEvent(AthleteHomeEvent.OnReviewClick) },
            onBookSessionClick = { viewModel.onEvent(AthleteHomeEvent.OnBookSessionClick) },
            onPlanAbroadClick = { viewModel.onEvent(AthleteHomeEvent.OnPlanAbroadClick) }
        )
    }
}
