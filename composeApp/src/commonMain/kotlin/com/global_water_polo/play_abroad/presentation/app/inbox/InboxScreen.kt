package com.global_water_polo.play_abroad.presentation.app.inbox

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.global_water_polo.play_abroad.core.components.AccountLockLayer
import com.global_water_polo.play_abroad.core.utils.observeWithLifecycle
import com.global_water_polo.play_abroad.presentation.base.BaseContentView

@Composable
fun InboxScreen(
    modifier: Modifier = Modifier,
    viewModel: InboxViewModel,
    showSnackBar: (String) -> Unit
) {
    val state by viewModel.inboxState.collectAsStateWithLifecycle()

    viewModel.snackBarChannel.observeWithLifecycle { message ->
        showSnackBar(message)
    }

    BaseContentView(
        modifier = modifier,
        viewState = state,
        applySystemBarsPadding = false
    ) {
        InboxScreenContent(
            state = state,
            onEvent = viewModel::onEvent
        )
    }

    AccountLockLayer(profile = state.profile, view = state.lockView, onAction = { viewModel.onEvent(InboxEvent.OnLock(it)) })
}
