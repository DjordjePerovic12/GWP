package com.global_water_polo.play_abroad.presentation.home.coachpending

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.global_water_polo.play_abroad.core.utils.observeWithLifecycle
import com.global_water_polo.play_abroad.presentation.base.BaseContentView

@Composable
fun CoachPendingScreen(
    modifier: Modifier = Modifier,
    viewModel: CoachPendingViewModel,
    showSnackBar: (String) -> Unit
) {
    val state by viewModel.coachPendingState.collectAsStateWithLifecycle()

    viewModel.snackBarChannel.observeWithLifecycle { message ->
        showSnackBar(message)
    }

    BaseContentView(
        modifier = modifier,
        viewState = state
    ) {
        CoachPendingScreenContent(state = state, onEvent = viewModel::onEvent)
    }
}
