package com.global_water_polo.play_abroad.presentation.onboarding.coachschool

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

/** College, step 2 (v29's c-school): university, division, team and role. */
@Composable
fun CoachSchoolScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    onEvent: (OnboardingEvent) -> Unit
) {
    OnboardingLayout(
        modifier = modifier,
        barStart = { BarStart(onBackClick = { onEvent(OnboardingEvent.NavigateBack) }, progress = state.progress(FlowStep.COACH_SCHOOL)) },
        bottom = {
            PrimaryButton(
                text = stringResource(Res.string.common_continue),
                onClick = { onEvent(OnboardingEvent.OnCoachContinueClick(FlowStep.COACH_SCHOOL)) },
                enabled = state.coachStepDone(FlowStep.COACH_SCHOOL)
            )
        }
    ) {
        Headline(text = stringResource(Res.string.c_school_title))
        FormField(
            label = stringResource(Res.string.c_university),
            value = state.school,
            onValueChange = { onEvent(OnboardingEvent.OnSchoolChanged(it)) },
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Done,
            modifier = Modifier.padding(top = 20.dp)
        )
        ChipGroup(
            label = stringResource(Res.string.c_division),
            options = listOf(
                "d1" to stringResource(Res.string.c_div_d1),
                "d2" to stringResource(Res.string.c_div_d2),
                "d3" to stringResource(Res.string.c_div_d3),
                "naia" to stringResource(Res.string.c_div_naia),
                "cwpa" to stringResource(Res.string.c_div_cwpa),
                "intl" to stringResource(Res.string.c_div_intl)
            ),
            isSelected = { it == state.division },
            onClick = { onEvent(OnboardingEvent.OnDivisionClick(it)) }
        )
        ChipGroup(
            label = stringResource(Res.string.c_team),
            options = listOf("men" to stringResource(Res.string.c_team_men), "women" to stringResource(Res.string.c_team_women), "both" to stringResource(Res.string.c_team_both)),
            isSelected = { it == state.coachTeam },
            onClick = { onEvent(OnboardingEvent.OnCoachTeamClick(it)) }
        )
        ChipGroup(
            label = stringResource(Res.string.c_role),
            options = listOf("head" to stringResource(Res.string.c_role_head), "assistant" to stringResource(Res.string.c_role_assistant), "ops" to stringResource(Res.string.c_role_ops)),
            isSelected = { it == state.coachRole },
            onClick = { onEvent(OnboardingEvent.OnCoachRoleClick(it)) }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CoachSchoolScreenContentPreview() {
    GWPPATheme {
        CoachSchoolScreenContent(state = OnboardingState(lane = Lane.COACH, school = "Stanford", division = "d1"), onEvent = {})
    }
}
