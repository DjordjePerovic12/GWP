package com.global_water_polo.play_abroad.presentation.home.parent

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.global_water_polo.play_abroad.core.utils.observeWithLifecycle
import com.global_water_polo.play_abroad.presentation.base.BaseContentView

@Composable
fun ParentHomeScreen(
    modifier: Modifier = Modifier,
    viewModel: ParentHomeViewModel,
    showSnackBar: (String) -> Unit
) {
    val state by viewModel.parentHomeState.collectAsStateWithLifecycle()

    viewModel.snackBarChannel.observeWithLifecycle { message ->
        showSnackBar(message)
    }

    BaseContentView(
        modifier = modifier,
        viewState = state
    ) {
        ParentHomeScreenContent(
            state = state,
            onCompareClick = { viewModel.onEvent(ParentHomeEvent.OnCompareClick) },
            onBookCallClick = { viewModel.onEvent(ParentHomeEvent.OnBookCallClick) },
            onInviteClick = { viewModel.onEvent(ParentHomeEvent.OnInviteClick) }
        )
    }
}
