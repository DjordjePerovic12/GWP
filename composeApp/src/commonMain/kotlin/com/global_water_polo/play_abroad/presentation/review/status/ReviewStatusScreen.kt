package com.global_water_polo.play_abroad.presentation.review.status

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.global_water_polo.play_abroad.core.utils.observeWithLifecycle
import com.global_water_polo.play_abroad.presentation.base.BaseContentView

@Composable
fun ReviewStatusScreen(
    modifier: Modifier = Modifier,
    viewModel: ReviewStatusViewModel,
    showSnackBar: (String) -> Unit
) {
    val state by viewModel.reviewStatusState.collectAsStateWithLifecycle()

    viewModel.snackBarChannel.observeWithLifecycle { message ->
        showSnackBar(message)
    }

    BaseContentView(
        modifier = modifier,
        viewState = state,
        applySystemBarsPadding = false
    ) {
        ReviewStatusScreenContent(
            state = state,
            onBackToProfileClick = { viewModel.onEvent(ReviewStatusEvent.OnBackToProfileClick) }
        )
    }
}
