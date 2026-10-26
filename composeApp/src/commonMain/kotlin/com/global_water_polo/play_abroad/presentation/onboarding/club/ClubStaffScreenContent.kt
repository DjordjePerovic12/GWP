package com.global_water_polo.play_abroad.presentation.onboarding.club

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.BarStart
import com.global_water_polo.play_abroad.core.components.ChoiceChip
import com.global_water_polo.play_abroad.core.components.FormField
import com.global_water_polo.play_abroad.core.components.Headline
import com.global_water_polo.play_abroad.core.components.HelpText
import com.global_water_polo.play_abroad.core.components.OnboardingLayout
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.presentation.onboarding.FlowStep
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingState
import com.global_water_polo.play_abroad.presentation.onboarding.progress
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** Coaching staff sizes, as the prototype offers them. */
val STAFF_SIZES = listOf("1", "2–3", "4–6", "7+")

/** Your staff: head coach, staff size and the languages spoken, for visiting athletes and teams. */
@Composable
fun ClubStaffScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    onCoachChanged: (String) -> Unit,
    onSizeClick: (String) -> Unit,
    onLanguageClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    OnboardingLayout(
        modifier = modifier,
        barStart = { BarStart(onBackClick = onBackClick, progress = state.progress(FlowStep.STAFF)) },
        bottom = { PrimaryButton(text = stringResource(Res.string.common_continue), onClick = onContinueClick, enabled = state.canStaffContinue) }
    ) {
        Headline(text = stringResource(Res.string.staff_title))
        HelpText(text = stringResource(Res.string.staff_help))
        FormField(
            label = stringResource(Res.string.staff_coach),
            value = state.staffCoach,
            onValueChange = onCoachChanged,
            capitalization = KeyboardCapitalization.Words,
            modifier = Modifier.padding(top = 24.dp)
        )
        ChipGroup(
            label = stringResource(Res.string.staff_size),
            options = STAFF_SIZES,
            isSelected = { it == state.staffSize },
            onClick = onSizeClick
        )
        ChipGroup(
            label = stringResource(Res.string.staff_langs),
            options = stringArrayResource(Res.array.club_languages),
            isSelected = { it in state.languages },
            onClick = onLanguageClick
        )
    }
}

@Composable
private fun ChipGroup(label: String, options: List<String>, isSelected: (String) -> Boolean, onClick: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 20.dp)) {
        Text(text = label, style = GWPPATheme.typography.archivoSemiBold13, color = GWPPATheme.colors.slateGrey)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { option -> ChoiceChip(text = option, selected = isSelected(option), onClick = { onClick(option) }) }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ClubStaffScreenContentPreview() {
    GWPPATheme {
        ClubStaffScreenContent(
            state = OnboardingState(staffCoach = "Dejan Savić", staffSize = "2–3", languages = setOf("English")),
            onCoachChanged = {},
            onSizeClick = {},
            onLanguageClick = {},
            onBackClick = {},
            onContinueClick = {}
        )
    }
}
