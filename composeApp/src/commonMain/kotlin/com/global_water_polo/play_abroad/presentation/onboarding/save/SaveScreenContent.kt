package com.global_water_polo.play_abroad.presentation.onboarding.save

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.BackButton
import com.global_water_polo.play_abroad.core.components.ButtonStyle
import com.global_water_polo.play_abroad.core.components.ChoiceChip
import com.global_water_polo.play_abroad.core.components.ClockChip
import com.global_water_polo.play_abroad.core.components.FormField
import com.global_water_polo.play_abroad.core.components.Headline
import com.global_water_polo.play_abroad.core.components.HelpText
import com.global_water_polo.play_abroad.core.components.OnboardingLayout
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.domain.model.Lane
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingState
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SaveScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    onPasswordChanged: (String) -> Unit,
    onRelationClick: (String) -> Unit,
    onHeardFromClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    val parent = state.lane == Lane.PARENT
    OnboardingLayout(
        modifier = modifier,
        barStart = { BackButton(onClick = onBackClick) },
        barEnd = { ClockChip(countdown = state.countdown, clockYear = state.window?.clock?.year) },
        bottom = {
            PrimaryButton(
                text = stringResource(if (parent) Res.string.save_cta_parent else Res.string.save_cta_athlete),
                onClick = onSaveClick,
                enabled = state.canSave,
                style = ButtonStyle.BALL
            )
        }
    ) {
        Headline(text = stringResource(if (parent) Res.string.save_title_parent else Res.string.save_title_athlete))
        HelpText(text = stringResource(if (parent) Res.string.save_help_parent else Res.string.save_help_athlete))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 24.dp)) {
            FormField(label = stringResource(Res.string.email_label), value = state.email, onValueChange = {}, readOnly = true)
            FormField(
                label = stringResource(Res.string.save_password),
                value = state.password,
                onValueChange = onPasswordChanged,
                placeholder = stringResource(Res.string.save_password_hint),
                password = true
            )
            if (parent) {
                ChipGroup(
                    label = stringResource(Res.string.save_relation),
                    options = stringArrayResource(Res.array.relations),
                    selected = state.relation,
                    onClick = onRelationClick
                )
            }
            ChipGroup(
                label = stringResource(Res.string.save_heard),
                options = stringArrayResource(Res.array.heard_from),
                selected = state.heardFrom,
                onClick = onHeardFromClick
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChipGroup(label: String, options: List<String>, selected: String, onClick: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = label, style = GWPPATheme.typography.archivoSemiBold13, color = GWPPATheme.colors.slateGrey)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { option -> ChoiceChip(text = option, selected = selected == option, onClick = { onClick(option) }) }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SaveScreenContentPreview() {
    GWPPATheme {
        SaveScreenContent(
            state = OnboardingState(lane = Lane.PARENT, email = "sam@example.com"),
            onPasswordChanged = {},
            onRelationClick = {},
            onHeardFromClick = {},
            onBackClick = {},
            onSaveClick = {}
        )
    }
}
