package com.global_water_polo.play_abroad.presentation.onboarding.fan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.BarStart
import com.global_water_polo.play_abroad.core.components.FormField
import com.global_water_polo.play_abroad.core.components.Headline
import com.global_water_polo.play_abroad.core.components.HelpText
import com.global_water_polo.play_abroad.core.components.OnboardingLayout
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.domain.model.Lane
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingState
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** Follow the game (v29's f-start): a fan's name and email, then straight to the feed. Free. */
@Composable
fun FanScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    onNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onBackClick: () -> Unit,
    onStartClick: () -> Unit
) {
    OnboardingLayout(
        modifier = modifier,
        barStart = { BarStart(onBackClick = onBackClick) },
        bottom = {
            PrimaryButton(
                text = stringResource(Res.string.fan_start),
                onClick = onStartClick,
                enabled = state.firstName.isNotBlank() && state.isEmailValid
            )
        }
    ) {
        Headline(text = stringResource(Res.string.fan_title))
        HelpText(text = stringResource(Res.string.fan_help))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 24.dp)) {
            FormField(label = stringResource(Res.string.fan_name), value = state.firstName, onValueChange = onNameChanged, capitalization = KeyboardCapitalization.Words)
            FormField(
                label = stringResource(Res.string.fan_email),
                value = state.email,
                onValueChange = onEmailChanged,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FanScreenContentPreview() {
    GWPPATheme {
        FanScreenContent(state = OnboardingState(lane = Lane.FAN), onNameChanged = {}, onEmailChanged = {}, onBackClick = {}, onStartClick = {})
    }
}
