package com.global_water_polo.play_abroad.presentation.onboarding.coachneeds

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

/** High school or club, step 2 (v29's c-needs): what they want from GWP. A trip, tournament or clinic adds travel dates. */
@Composable
fun CoachNeedsScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    onEvent: (OnboardingEvent) -> Unit
) {
    OnboardingLayout(
        modifier = modifier,
        barStart = { BarStart(onBackClick = { onEvent(OnboardingEvent.NavigateBack) }, progress = state.progress(FlowStep.COACH_NEEDS)) },
        bottom = {
            PrimaryButton(
                text = stringResource(Res.string.common_continue),
                onClick = { onEvent(OnboardingEvent.OnCoachContinueClick(FlowStep.COACH_NEEDS)) },
                enabled = state.coachStepDone(FlowStep.COACH_NEEDS)
            )
        }
    ) {
        Headline(text = stringResource(Res.string.c_needs_title))
        HelpText(text = stringResource(Res.string.c_needs_help))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 20.dp)) {
            listOf(
                Triple("trip", Res.string.c_need_trip, Res.string.c_need_trip_sub),
                Triple("tournament", Res.string.c_need_tournament, Res.string.c_need_tournament_sub),
                Triple("clinic", Res.string.c_need_clinic, Res.string.c_need_clinic_sub),
                Triple("develop", Res.string.c_need_develop, Res.string.c_need_develop_sub),
                Triple("skills", Res.string.c_need_skills, Res.string.c_need_skills_sub)
            ).forEach { (key, title, sub) ->
                PickCard(
                    title = stringResource(title),
                    subtitle = stringResource(sub),
                    marker = PickMarker.CHECK,
                    selected = key in state.needs,
                    compact = true,
                    onClick = { onEvent(OnboardingEvent.OnNeedClick(key)) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CoachNeedsScreenContentPreview() {
    GWPPATheme {
        CoachNeedsScreenContent(state = OnboardingState(lane = Lane.COACH, needs = setOf("trip")), onEvent = {})
    }
}
