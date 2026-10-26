package com.global_water_polo.play_abroad.presentation.onboarding.email

import com.global_water_polo.play_abroad.presentation.onboarding.progress
import com.global_water_polo.play_abroad.presentation.onboarding.FlowStep
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.BarStart
import com.global_water_polo.play_abroad.core.components.ClockChip
import com.global_water_polo.play_abroad.core.components.FormField
import com.global_water_polo.play_abroad.core.components.Headline
import com.global_water_polo.play_abroad.core.components.HelpText
import com.global_water_polo.play_abroad.core.components.OnboardingLayout
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.domain.model.Lane
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingState
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

@Composable
fun EmailScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    onEmailChanged: (String) -> Unit,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    val athlete = state.lane == Lane.ATHLETE
    OnboardingLayout(
        modifier = modifier,
        barStart = { BarStart(onBackClick = onBackClick, progress = state.progress(FlowStep.EMAIL)) },
        barEnd = { ClockChip(countdown = state.countdown, clockYear = state.window?.clock?.year) },
        bottom = {
            PrimaryButton(
                text = stringResource(if (state.isTrip) Res.string.trip_email_cta else Res.string.common_continue),
                onClick = onContinueClick,
                enabled = state.isEmailValid
            )
        }
    ) {
        Headline(text = stringResource(if (state.isTrip) Res.string.trip_email_title else Res.string.welcome_title))
        HelpText(
            text = stringResource(
                when {
                    state.isTrip -> Res.string.trip_email_help
                    athlete -> Res.string.welcome_help_athlete
                    else -> Res.string.welcome_help_parent
                }
            )
        )
        FormField(
            label = stringResource(Res.string.email_label),
            value = state.email,
            onValueChange = onEmailChanged,
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done,
            modifier = Modifier.padding(top = 24.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmailScreenContentPreview() {
    GWPPATheme {
        EmailScreenContent(state = OnboardingState(email = "alex@"), onEmailChanged = {}, onBackClick = {}, onContinueClick = {})
    }
}
