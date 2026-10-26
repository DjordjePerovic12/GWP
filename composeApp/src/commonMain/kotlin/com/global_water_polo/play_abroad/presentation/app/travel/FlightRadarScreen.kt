package com.global_water_polo.play_abroad.presentation.app.travel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.global_water_polo.play_abroad.core.utils.observeWithLifecycle
import com.global_water_polo.play_abroad.presentation.base.BaseContentView

@Composable
fun FlightRadarScreen(
    modifier: Modifier = Modifier,
    viewModel: FlightRadarViewModel,
    showSnackBar: (String) -> Unit
) {
    val state by viewModel.flightRadarState.collectAsStateWithLifecycle()

    viewModel.snackBarChannel.observeWithLifecycle { message ->
        showSnackBar(message)
    }

    BaseContentView(
        modifier = modifier,
        viewState = state,
        applySystemBarsPadding = false
    ) {
        FlightRadarScreenContent(state = state, onEvent = viewModel::onEvent)
    }
}
