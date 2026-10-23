package com.global_water_polo.play_abroad.presentation.review.apply

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.global_water_polo.play_abroad.core.utils.observeWithLifecycle
import com.global_water_polo.play_abroad.presentation.base.BaseContentView

@Composable
fun ApplyReviewScreen(
    modifier: Modifier = Modifier,
    viewModel: ApplyReviewViewModel,
    showSnackBar: (String) -> Unit
) {
    val state by viewModel.applyReviewState.collectAsStateWithLifecycle()

    viewModel.snackBarChannel.observeWithLifecycle { message ->
        showSnackBar(message)
    }

    BaseContentView(
        modifier = modifier,
        viewState = state,
        applySystemBarsPadding = false
    ) {
        ApplyReviewScreenContent(
            state = state,
            onBackClick = { viewModel.onEvent(ApplyReviewEvent.NavigateBack) },
            onClipClick = { viewModel.onEvent(ApplyReviewEvent.OnClipClick(it)) },
            onSubmitClick = { viewModel.onEvent(ApplyReviewEvent.OnSubmitClick) }
        )
    }
}
