package com.global_water_polo.play_abroad.presentation.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.TextFieldWithValidation
import com.global_water_polo.play_abroad.core.utils.KeyboardType
import com.global_water_polo.play_abroad.resources.Res
import com.global_water_polo.play_abroad.resources.login_back
import com.global_water_polo.play_abroad.resources.login_button
import com.global_water_polo.play_abroad.resources.login_email
import com.global_water_polo.play_abroad.resources.login_password
import com.global_water_polo.play_abroad.resources.login_success
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

@Composable
fun LoginScreenContent(
    modifier: Modifier = Modifier,
    state: LoginState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onLoginClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxSize()
            .background(GWPPATheme.colors.colorWhite)
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        TextButton(onClick = onBackClick) {
            Text(text = stringResource(Res.string.login_back), style = GWPPATheme.typography.defaultRegular14)
        }
        TextFieldWithValidation(
            value = state.email,
            onValueChange = onEmailChanged,
            label = stringResource(Res.string.login_email),
            errorMessage = state.emailError,
            keyboardOptions = KeyboardType.textKeyboardEmailNext(),
            keyboardActions = KeyboardType.keyboardActions()
        )
        TextFieldWithValidation(
            value = state.password,
            onValueChange = onPasswordChanged,
            label = stringResource(Res.string.login_password),
            errorMessage = state.passwordError,
            keyboardOptions = KeyboardType.passwordKeyboardDone(),
            keyboardActions = KeyboardType.keyboardActions(),
            visualTransformation = PasswordVisualTransformation()
        )
        Button(
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(Res.string.login_button), style = GWPPATheme.typography.defaultRegular14)
        }
        if (state.isSuccess) {
            Text(
                text = stringResource(Res.string.login_success),
                style = GWPPATheme.typography.defaultRegular14,
                color = GWPPATheme.colors.colorBlack
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenContentPreview() {
    GWPPATheme {
        LoginScreenContent(
            state = LoginState(email = "test@example.com", passwordError = "Password cannot be empty"),
            onEmailChanged = {},
            onPasswordChanged = {},
            onLoginClick = {},
            onBackClick = {}
        )
    }
}
