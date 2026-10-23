package com.global_water_polo.play_abroad.presentation.example

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.global_water_polo.play_abroad.core.utils.observeWithLifecycle
import com.global_water_polo.play_abroad.presentation.base.BaseContentView

@Composable
fun ExampleScreen(
    modifier: Modifier = Modifier,
    viewModel: ExampleViewModel,
    showSnackBar: (String) -> Unit
) {
    val state by viewModel.exampleState.collectAsStateWithLifecycle()

    viewModel.snackBarChannel.observeWithLifecycle { message ->
        showSnackBar(message)
    }

    BaseContentView(
        modifier = modifier,
        viewState = state
    ) {
        ExampleScreenContent(
            state = state,
            onDataResponseClick = { viewModel.onEvent(ExampleEvent.OnDataResponseClick) },
            onEmptyResponseClick = { viewModel.onEvent(ExampleEvent.OnEmptyResponseClick) },
            onHTMLResponseClick = { viewModel.onEvent(ExampleEvent.OnHTMLResponseClick) },
            onCustomMessageResponseClick = { viewModel.onEvent(ExampleEvent.OnCustomMessageResponseClick) },
            onLoginDemoClick = { viewModel.onEvent(ExampleEvent.OnLoginDemoClick) }
        )
    }
}
