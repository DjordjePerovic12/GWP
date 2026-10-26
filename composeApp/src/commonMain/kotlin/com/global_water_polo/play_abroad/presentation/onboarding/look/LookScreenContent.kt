package com.global_water_polo.play_abroad.presentation.onboarding.look

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.BarStart
import com.global_water_polo.play_abroad.presentation.onboarding.FlowStep
import com.global_water_polo.play_abroad.presentation.onboarding.progress
import com.global_water_polo.play_abroad.core.components.ClockChip
import com.global_water_polo.play_abroad.core.components.Headline
import com.global_water_polo.play_abroad.core.components.HelpText
import com.global_water_polo.play_abroad.core.components.OnboardingLayout
import com.global_water_polo.play_abroad.core.components.PickCard
import com.global_water_polo.play_abroad.core.components.PickMarker
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.domain.model.Lane
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingState
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

private data class LookOption(val key: String, val title: StringResource, val sub: StringResource)

private fun optionsFor(lane: Lane): List<LookOption> = when (lane) {
    Lane.ATHLETE -> listOf(
        LookOption("recruit", Res.string.look_recruit_athlete, Res.string.look_recruit_athlete_sub),
        LookOption("abroad", Res.string.look_abroad, Res.string.look_abroad_athlete_sub)
    )
    Lane.PARENT -> listOf(
        LookOption("recruit", Res.string.look_recruit_parent, Res.string.look_recruit_parent_sub),
        LookOption("abroad", Res.string.look_abroad, Res.string.look_abroad_parent_sub),
        LookOption("trip", Res.string.look_trip, Res.string.look_trip_parent_sub)
    )
    Lane.COACH -> listOf(
        LookOption("recruit", Res.string.look_recruit_coach, Res.string.look_recruit_coach_sub),
        LookOption("develop", Res.string.look_develop, Res.string.look_develop_coach_sub),
        LookOption("trip", Res.string.look_trip, Res.string.look_trip_coach_sub),
        LookOption("tournament", Res.string.look_tournament_coach, Res.string.look_tournament_coach_sub)
    )
    Lane.CLUB -> listOf(
        LookOption("host", Res.string.look_host, Res.string.look_host_sub),
        LookOption("trip", Res.string.look_trips_club, Res.string.look_trips_club_sub),
        LookOption("tournament", Res.string.look_tournament_club, Res.string.look_tournament_club_sub),
        LookOption("partner", Res.string.look_partner, Res.string.look_partner_sub),
        LookOption("develop", Res.string.look_develop, Res.string.look_develop_club_new_sub)
    )
}

@Composable
fun LookScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    onToggle: (String) -> Unit,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    val lane = state.lane
    OnboardingLayout(
        modifier = modifier,
        barStart = { BarStart(onBackClick = onBackClick, progress = state.progress(FlowStep.LOOK)) },
        barEnd = { if (lane == Lane.ATHLETE || lane == Lane.PARENT) ClockChip(countdown = state.countdown, clockYear = state.window?.clock?.year) },
        bottom = { PrimaryButton(text = stringResource(Res.string.common_continue), onClick = onContinueClick, enabled = state.looking.isNotEmpty()) }
    ) {
        Headline(
            text = stringResource(
                when (lane) {
                    Lane.ATHLETE -> Res.string.look_title_athlete
                    Lane.PARENT -> Res.string.look_title_parent
                    Lane.COACH -> Res.string.look_title_coach
                    Lane.CLUB -> Res.string.look_title_club
                }
            )
        )
        HelpText(
            text = stringResource(
                when (lane) {
                    Lane.ATHLETE -> Res.string.look_help_athlete
                    Lane.COACH -> Res.string.look_help_coach
                    Lane.PARENT, Lane.CLUB -> Res.string.look_help_all
                }
            )
        )
        Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 24.dp)) {
            optionsFor(lane).forEach { option ->
                PickCard(
                    title = stringResource(option.title),
                    subtitle = stringResource(option.sub),
                    marker = PickMarker.CHECK,
                    selected = option.key in state.looking,
                    onClick = { onToggle(option.key) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LookScreenContentPreview() {
    GWPPATheme {
        LookScreenContent(state = OnboardingState(lane = Lane.PARENT, looking = setOf("abroad")), onToggle = {}, onBackClick = {}, onContinueClick = {})
    }
}
