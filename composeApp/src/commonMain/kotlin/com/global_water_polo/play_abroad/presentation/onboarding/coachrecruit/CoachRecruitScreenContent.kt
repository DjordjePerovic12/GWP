package com.global_water_polo.play_abroad.presentation.onboarding.coachrecruit

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
import com.global_water_polo.play_abroad.core.utils.NcaaClockCalculator
import kotlinx.datetime.number

/** College, step 3 (v29's c-recruit): positions, incoming classes and international athletes; sets up the board. */
@Composable
fun CoachRecruitScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    onEvent: (OnboardingEvent) -> Unit
) {
    OnboardingLayout(
        modifier = modifier,
        barStart = { BarStart(onBackClick = { onEvent(OnboardingEvent.NavigateBack) }, progress = state.progress(FlowStep.COACH_RECRUIT)) },
        bottom = {
            PrimaryButton(
                text = stringResource(Res.string.c_create_account),
                onClick = { onEvent(OnboardingEvent.OnCoachContinueClick(FlowStep.COACH_RECRUIT)) },
                enabled = state.coachStepDone(FlowStep.COACH_RECRUIT)
            )
        }
    ) {
        Headline(text = stringResource(Res.string.c_recruit_title))
        HelpText(text = stringResource(Res.string.c_recruit_help))
        ChipGroup(
            label = stringResource(Res.string.c_positions),
            options = listOf(
                "Goalkeeper" to stringResource(Res.string.c_pos_gk),
                "Center" to stringResource(Res.string.c_pos_center),
                "Center defender" to stringResource(Res.string.c_pos_cd),
                "Driver" to stringResource(Res.string.c_pos_driver),
                "Utility" to stringResource(Res.string.c_pos_utility)
            ),
            isSelected = { it in state.recruitPositions },
            onClick = { onEvent(OnboardingEvent.OnRecruitPositionClick(it)) }
        )
        // Incoming classes: this fall (next fall from September), then three more.
        val today = NcaaClockCalculator.today()
        val first = today.year + if (today.month.number >= 9) 1 else 0
        ChipGroup(
            label = stringResource(Res.string.c_classes),
            options = (first..first + 3).map { it.toString() to stringResource(Res.string.timing_fall, it) },
            isSelected = { it.toInt() in state.recruitClasses },
            onClick = { onEvent(OnboardingEvent.OnRecruitClassClick(it.toInt())) }
        )
        ChipGroup(
            label = stringResource(Res.string.c_intl),
            options = listOf("yes" to stringResource(Res.string.c_intl_yes), "no" to stringResource(Res.string.c_intl_no)),
            isSelected = { it == state.intl },
            onClick = { onEvent(OnboardingEvent.OnIntlClick(it)) }
        )
        SmallText(text = stringResource(Res.string.c_recruit_fine), modifier = Modifier.padding(top = 18.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun CoachRecruitScreenContentPreview() {
    GWPPATheme {
        CoachRecruitScreenContent(state = OnboardingState(lane = Lane.COACH, recruitPositions = setOf("Driver")), onEvent = {})
    }
}
