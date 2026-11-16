package com.global_water_polo.play_abroad.presentation.onboarding.coachemail

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
import com.global_water_polo.play_abroad.core.components.ChipGroup
import com.global_water_polo.play_abroad.core.components.FormField
import com.global_water_polo.play_abroad.core.components.Headline
import com.global_water_polo.play_abroad.core.components.HelpText
import com.global_water_polo.play_abroad.core.components.OnboardingLayout
import com.global_water_polo.play_abroad.core.components.PickCard
import com.global_water_polo.play_abroad.core.components.PickMarker
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.core.utils.EmailRules
import com.global_water_polo.play_abroad.domain.model.Lane
import com.global_water_polo.play_abroad.presentation.onboarding.FlowStep
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingEvent
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingState
import com.global_water_polo.play_abroad.presentation.onboarding.progress
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** College, step 1 (v29's c-email): name and an official university email. */
@Composable
fun CoachEmailScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    onEvent: (OnboardingEvent) -> Unit
) {
    OnboardingLayout(
        modifier = modifier,
        barStart = { BarStart(onBackClick = { onEvent(OnboardingEvent.NavigateBack) }, progress = state.progress(FlowStep.COACH_EMAIL)) },
        bottom = {
            PrimaryButton(
                text = stringResource(Res.string.common_continue),
                onClick = { onEvent(OnboardingEvent.OnCoachContinueClick(FlowStep.COACH_EMAIL)) },
                enabled = state.coachStepDone(FlowStep.COACH_EMAIL)
            )
        }
    ) {
        Headline(text = stringResource(Res.string.c_email_title))
        HelpText(text = stringResource(Res.string.c_email_help))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 24.dp)) {
            FormField(label = stringResource(Res.string.c_full_name), value = state.firstName, onValueChange = { onEvent(OnboardingEvent.OnFirstNameChanged(it)) }, capitalization = KeyboardCapitalization.Words)
            FormField(
                label = stringResource(Res.string.c_email_label),
                value = state.email,
                onValueChange = { onEvent(OnboardingEvent.OnEmailChanged(it)) },
                placeholder = "name@university.edu",
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            )
            if (EmailRules.showUniversityHint(state.email)) {
                SmallText(text = stringResource(Res.string.c_email_error))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CoachEmailScreenContentPreview() {
    GWPPATheme {
        CoachEmailScreenContent(state = OnboardingState(lane = Lane.COACH, firstName = "Jordan Lee", email = "jordan@gmail.com"), onEvent = {})
    }
}
