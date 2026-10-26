package com.global_water_polo.play_abroad.presentation.onboarding.trial

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.BarStart
import com.global_water_polo.play_abroad.core.components.ButtonStyle
import com.global_water_polo.play_abroad.core.components.ClockChip
import com.global_water_polo.play_abroad.core.components.Headline
import com.global_water_polo.play_abroad.core.components.HelpText
import com.global_water_polo.play_abroad.core.components.OnboardingLayout
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.core.components.TextLink
import com.global_water_polo.play_abroad.domain.model.Lane
import com.global_water_polo.play_abroad.presentation.onboarding.FlowStep
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingState
import com.global_water_polo.play_abroad.presentation.onboarding.progress
import com.global_water_polo.play_abroad.presentation.onboarding.trial.components.TierCard
import com.global_water_polo.play_abroad.presentation.onboarding.trial.components.TierCompareTable
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** 7 days free: GWP Elite (recommended) and GWP Next side by side, then the comparison. Nothing is charged. */
@Composable
fun TrialScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    onPick: (String) -> Unit,
    onStartClick: () -> Unit,
    onLaterClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val colors = GWPPATheme.colors
    val parent = state.lane == Lane.PARENT
    val elite = state.trialPick == OnboardingState.PLAN_ELITE
    // Coming back after starting a trial: say it is on instead of selling it again.
    if (state.plan != null) {
        OnboardingLayout(
            modifier = modifier,
            barStart = { BarStart(onBackClick = onBackClick, progress = state.progress(FlowStep.TRIAL)) },
            bottom = { PrimaryButton(text = stringResource(Res.string.common_continue), onClick = onLaterClick, style = ButtonStyle.BALL) }
        ) {
            Headline(text = stringResource(if (state.isElite) Res.string.trial_on_elite else Res.string.trial_on_next))
            HelpText(text = stringResource(Res.string.trial_on_help))
        }
        return
    }
    OnboardingLayout(
        modifier = modifier,
        barStart = { BarStart(onBackClick = onBackClick, progress = state.progress(FlowStep.TRIAL)) },
        barEnd = { if (state.window != null) ClockChip(countdown = state.countdown, clockYear = state.window.clock.year) },
        bottom = {
            PrimaryButton(
                text = stringResource(if (elite) Res.string.trial_start_elite else Res.string.trial_start_next) + "\n" +
                    stringResource(if (elite) Res.string.trial_elite_then else Res.string.trial_next_then),
                onClick = onStartClick
            )
            TextLink(
                text = stringResource(Res.string.trial_not_now),
                onClick = onLaterClick,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 6.dp)
            )
        }
    ) {
        Text(text = stringResource(Res.string.trial_kicker).uppercase(), style = GWPPATheme.typography.archivoBold12, color = colors.eliteGold)
        Text(
            text = stringResource(if (parent) Res.string.trial_title_parent else Res.string.trial_title_athlete).uppercase(),
            style = GWPPATheme.typography.archivoExpandedExtraBold32,
            color = colors.inkBlack,
            modifier = Modifier.padding(top = 6.dp)
        )
        HelpText(text = stringResource(if (parent) Res.string.trial_sub_parent else Res.string.trial_sub_athlete))
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .padding(top = 18.dp)
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
        ) {
            TierCard(elite = true, selected = elite, onClick = { onPick(OnboardingState.PLAN_ELITE) }, modifier = Modifier.weight(1f))
            TierCard(elite = false, selected = !elite, onClick = { onPick(OnboardingState.PLAN_NEXT) }, modifier = Modifier.weight(1f))
        }
        TierCompareTable(modifier = Modifier.padding(top = 16.dp))
        SmallText(text = stringResource(Res.string.trial_fine), modifier = Modifier.padding(top = 10.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun TrialScreenContentPreview() {
    GWPPATheme {
        TrialScreenContent(state = OnboardingState(), onPick = {}, onStartClick = {}, onLaterClick = {}, onBackClick = {})
    }
}
