package com.global_water_polo.play_abroad.presentation.onboarding.coachinvite

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

/** GWP staff, step 1 (v29's c-invite): the invitation code. Prototype: any code of 4 or more characters works. */
@Composable
fun CoachInviteScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    onEvent: (OnboardingEvent) -> Unit
) {
    OnboardingLayout(
        modifier = modifier,
        barStart = { BarStart(onBackClick = { onEvent(OnboardingEvent.NavigateBack) }, progress = state.progress(FlowStep.COACH_INVITE)) },
        bottom = {
            PrimaryButton(
                text = stringResource(Res.string.common_continue),
                onClick = { onEvent(OnboardingEvent.OnCoachContinueClick(FlowStep.COACH_INVITE)) },
                enabled = state.coachStepDone(FlowStep.COACH_INVITE)
            )
        }
    ) {
        Headline(text = stringResource(Res.string.c_invite_title))
        HelpText(text = stringResource(Res.string.c_invite_help))
        FormField(
            label = stringResource(Res.string.c_invite_code),
            value = state.inviteCode,
            onValueChange = { onEvent(OnboardingEvent.OnInviteCodeChanged(it)) },
            capitalization = KeyboardCapitalization.Characters,
            imeAction = ImeAction.Done,
            modifier = Modifier.padding(top = 24.dp)
        )
        SmallText(text = stringResource(Res.string.c_invite_fine), modifier = Modifier.padding(top = 12.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun CoachInviteScreenContentPreview() {
    GWPPATheme {
        CoachInviteScreenContent(state = OnboardingState(lane = Lane.COACH, inviteCode = "GWP24"), onEvent = {})
    }
}
