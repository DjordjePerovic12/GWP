package com.global_water_polo.play_abroad.presentation.onboarding.coachstaff

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

/** GWP staff, step 2 (v29's c-staff): name, club, email and what they'll do with GWP. */
@Composable
fun CoachStaffScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    onEvent: (OnboardingEvent) -> Unit
) {
    OnboardingLayout(
        modifier = modifier,
        barStart = { BarStart(onBackClick = { onEvent(OnboardingEvent.NavigateBack) }, progress = state.progress(FlowStep.COACH_STAFF)) },
        bottom = {
            PrimaryButton(
                text = stringResource(Res.string.c_create_account),
                onClick = { onEvent(OnboardingEvent.OnCoachContinueClick(FlowStep.COACH_STAFF)) },
                enabled = state.coachStepDone(FlowStep.COACH_STAFF)
            )
        }
    ) {
        Headline(text = stringResource(Res.string.c_staff_title))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 20.dp)) {
            FormField(label = stringResource(Res.string.c_full_name), value = state.firstName, onValueChange = { onEvent(OnboardingEvent.OnFirstNameChanged(it)) }, capitalization = KeyboardCapitalization.Words)
            FormField(
                label = stringResource(Res.string.c_your_club),
                value = state.coachClub,
                onValueChange = { onEvent(OnboardingEvent.OnCoachClubChanged(it)) },
                capitalization = KeyboardCapitalization.Words
            )
            FormField(label = stringResource(Res.string.fan_email), value = state.email, onValueChange = { onEvent(OnboardingEvent.OnEmailChanged(it)) }, keyboardType = KeyboardType.Email, imeAction = ImeAction.Done)
        }
        ChipGroup(
            label = stringResource(Res.string.c_staff_do),
            options = listOf(
                "camps" to stringResource(Res.string.c_staff_camps),
                "host" to stringResource(Res.string.c_staff_host),
                "remote" to stringResource(Res.string.c_staff_remote)
            ),
            isSelected = { it in state.staffRoles },
            onClick = { onEvent(OnboardingEvent.OnStaffRoleClick(it)) }
        )
        SmallText(text = stringResource(Res.string.c_staff_fine), modifier = Modifier.padding(top = 18.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun CoachStaffScreenContentPreview() {
    GWPPATheme {
        CoachStaffScreenContent(state = OnboardingState(lane = Lane.COACH, firstName = "Jordan Lee"), onEvent = {})
    }
}
