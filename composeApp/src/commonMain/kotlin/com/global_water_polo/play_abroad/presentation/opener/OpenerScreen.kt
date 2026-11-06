package com.global_water_polo.play_abroad.presentation.opener

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.global_water_polo.play_abroad.core.utils.observeWithLifecycle
import com.global_water_polo.play_abroad.presentation.base.BaseContentView

@Composable
fun OpenerScreen(
    modifier: Modifier = Modifier,
    viewModel: OpenerViewModel,
    showSnackBar: (String) -> Unit
) {
    val state by viewModel.openerState.collectAsStateWithLifecycle()

    viewModel.snackBarChannel.observeWithLifecycle { message ->
        showSnackBar(message)
    }

    BaseContentView(
        modifier = modifier,
        viewState = state,
        applySystemBarsPadding = false
    ) {
        OpenerScreenContent(state = state, onEvent = viewModel::onEvent)
    }
}
