package com.global_water_polo.play_abroad.presentation.auth.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.global_water_polo.play_abroad.core.utils.observeWithLifecycle
import com.global_water_polo.play_abroad.presentation.base.BaseContentView

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel,
    showSnackBar: (String) -> Unit
) {
    val state by viewModel.loginState.collectAsStateWithLifecycle()

    viewModel.snackBarChannel.observeWithLifecycle { message ->
        showSnackBar(message)
    }

    BaseContentView(
        modifier = modifier,
        viewState = state
    ) {
        LoginScreenContent(
            state = state,
            onEmailChanged = { viewModel.onEvent(LoginEvent.OnEmailChanged(it)) },
            onPasswordChanged = { viewModel.onEvent(LoginEvent.OnPasswordChanged(it)) },
            onLoginClick = { viewModel.onEvent(LoginEvent.OnLoginClick) },
            onBackClick = { viewModel.onEvent(LoginEvent.NavigateBack) }
        )
    }
}
