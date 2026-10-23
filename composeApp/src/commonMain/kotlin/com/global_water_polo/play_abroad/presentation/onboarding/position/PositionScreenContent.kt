package com.global_water_polo.play_abroad.presentation.onboarding.position

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.BackButton
import com.global_water_polo.play_abroad.core.components.ButtonStyle
import com.global_water_polo.play_abroad.core.components.ClockChip
import com.global_water_polo.play_abroad.core.components.Headline
import com.global_water_polo.play_abroad.core.components.HelpText
import com.global_water_polo.play_abroad.core.components.OnboardingLayout
import com.global_water_polo.play_abroad.core.components.PickCard
import com.global_water_polo.play_abroad.core.components.PickMarker
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.domain.model.Lane
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingState
import com.global_water_polo.play_abroad.presentation.onboarding.position.components.LeftHandedToggle
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** Position names are stored as typed here (prototype values); subtitles only where the name needs one. */
val POSITIONS = listOf(
    "Goalkeeper" to null,
    "Center" to Res.string.position_center_sub,
    "Center defender" to null,
    "Driver" to Res.string.position_driver_sub,
    "Utility" to Res.string.position_utility_sub
)

@Composable
fun PositionScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    onPositionClick: (String) -> Unit,
    onLeftHandedClick: () -> Unit,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    val athlete = state.lane == Lane.ATHLETE
    OnboardingLayout(
        modifier = modifier,
        barStart = { BackButton(onClick = onBackClick) },
        barEnd = { ClockChip(countdown = state.countdown, clockYear = state.window?.clock?.year) },
        bottom = { PrimaryButton(text = stringResource(Res.string.common_continue), onClick = onContinueClick, enabled = state.position.isNotEmpty(), style = ButtonStyle.BALL) }
    ) {
        Headline(text = stringResource(if (athlete) Res.string.position_title_athlete else Res.string.position_title_parent))
        HelpText(text = stringResource(if (athlete) Res.string.position_help_athlete else Res.string.position_help_parent))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 18.dp)) {
            POSITIONS.forEach { (name, sub) ->
                PickCard(
                    title = name,
                    subtitle = sub?.let { stringResource(it) },
                    marker = PickMarker.CHECK,
                    selected = state.position == name,
                    compact = true,
                    onClick = { onPositionClick(name) }
                )
            }
        }
        LeftHandedToggle(
            title = stringResource(Res.string.position_left_handed),
            subtitle = stringResource(if (athlete) Res.string.position_left_handed_sub_athlete else Res.string.position_left_handed_sub_parent),
            checked = state.leftHanded,
            onClick = onLeftHandedClick,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PositionScreenContentPreview() {
    GWPPATheme {
        PositionScreenContent(state = OnboardingState(position = "Center", leftHanded = true), onPositionClick = {}, onLeftHandedClick = {}, onBackClick = {}, onContinueClick = {})
    }
}
