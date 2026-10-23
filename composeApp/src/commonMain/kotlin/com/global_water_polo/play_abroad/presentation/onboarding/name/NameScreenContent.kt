package com.global_water_polo.play_abroad.presentation.onboarding.name

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
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
import com.global_water_polo.play_abroad.resources.Res
import com.global_water_polo.play_abroad.resources.common_continue
import com.global_water_polo.play_abroad.resources.name_first
import com.global_water_polo.play_abroad.resources.name_help_athlete
import com.global_water_polo.play_abroad.resources.name_help_parent
import com.global_water_polo.play_abroad.resources.name_last
import com.global_water_polo.play_abroad.resources.name_title_athlete
import com.global_water_polo.play_abroad.resources.name_title_parent
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

@Composable
fun NameScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    onFirstNameChanged: (String) -> Unit,
    onLastNameChanged: (String) -> Unit,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    val athlete = state.lane == Lane.ATHLETE
    OnboardingLayout(
        modifier = modifier,
        barStart = { BarStart(onBackClick = onBackClick, step = 2, of = 3) },
        barEnd = { ClockChip(countdown = state.countdown, clockYear = state.window?.clock?.year) },
        bottom = { PrimaryButton(text = stringResource(Res.string.common_continue), onClick = onContinueClick, enabled = state.isNameValid) }
    ) {
        Headline(text = stringResource(if (athlete) Res.string.name_title_athlete else Res.string.name_title_parent))
        HelpText(text = stringResource(if (athlete) Res.string.name_help_athlete else Res.string.name_help_parent))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 24.dp)) {
            FormField(label = stringResource(Res.string.name_first), value = state.firstName, onValueChange = onFirstNameChanged, capitalization = KeyboardCapitalization.Words)
            FormField(label = stringResource(Res.string.name_last), value = state.lastName, onValueChange = onLastNameChanged, capitalization = KeyboardCapitalization.Words)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NameScreenContentPreview() {
    GWPPATheme {
        NameScreenContent(state = OnboardingState(firstName = "Alex"), onFirstNameChanged = {}, onLastNameChanged = {}, onBackClick = {}, onContinueClick = {})
    }
}
