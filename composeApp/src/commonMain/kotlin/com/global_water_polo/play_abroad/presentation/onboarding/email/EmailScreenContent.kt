package com.global_water_polo.play_abroad.presentation.onboarding.email

import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.core.components.ChoiceChip
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Arrangement
import com.global_water_polo.play_abroad.presentation.onboarding.progress
import com.global_water_polo.play_abroad.presentation.onboarding.FlowStep
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.BarStart
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
fun EmailScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    onEmailChanged: (String) -> Unit,
    onParentEmailChanged: (String) -> Unit = {},
    onHeardClick: (String) -> Unit = {},
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    val athlete = state.lane == Lane.ATHLETE
    OnboardingLayout(
        modifier = modifier,
        barStart = { BarStart(onBackClick = onBackClick, progress = state.progress(FlowStep.EMAIL)) },
        bottom = {
            PrimaryButton(
                text = stringResource(
                    when {
                        state.isTrip -> Res.string.trip_email_cta
                        athlete -> Res.string.email28_send
                        else -> Res.string.email28_send_parent
                    }
                ),
                onClick = onContinueClick,
                enabled = if (state.isTrip) state.isEmailValid else state.canSendPlan
            )
        }
    ) {
        if (state.isTrip) {
            Headline(text = stringResource(Res.string.trip_email_title))
            HelpText(text = stringResource(Res.string.trip_email_help))
        } else {
            // v29's a-email: one email, a parent's for under 18s, one tap on how they found us.
            Headline(text = stringResource(if (athlete) Res.string.email28_title else Res.string.email28_title_parent))
            HelpText(text = stringResource(Res.string.email28_help))
        }
        FormField(
            label = stringResource(if (state.isTrip) Res.string.email_label else Res.string.email28_your),
            value = state.email,
            onValueChange = onEmailChanged,
            keyboardType = KeyboardType.Email,
            imeAction = if (state.isMinor) ImeAction.Next else ImeAction.Done,
            modifier = Modifier.padding(top = 24.dp)
        )
        if (state.isMinor) {
            FormField(
                label = stringResource(Res.string.email28_parent),
                value = state.parentEmail,
                onValueChange = onParentEmailChanged,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done,
                modifier = Modifier.padding(top = 10.dp)
            )
            SmallText(text = stringResource(Res.string.email28_parent_note), modifier = Modifier.padding(top = 6.dp))
        }
        if (!state.isTrip) {
            Text(
                stringResource(Res.string.email28_heard),
                style = GWPPATheme.typography.archivoBold16,
                color = GWPPATheme.colors.inkBlack,
                modifier = Modifier.padding(top = 24.dp, bottom = 10.dp)
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                HEARD.forEach { source ->
                    ChoiceChip(text = source, selected = state.heardFrom == source, onClick = { onHeardClick(source) })
                }
            }
        }
    }
}

/** v29's HEARD: one tap on how they found GWP. */
private val HEARD = listOf("Instagram", "TikTok", "Coach or club", "Teammate", "Parent", "Google", "Tournament", "Other")

@Preview(showBackground = true)
@Composable
private fun EmailScreenContentPreview() {
    GWPPATheme {
        EmailScreenContent(state = OnboardingState(email = "alex@"), onEmailChanged = {}, onBackClick = {}, onContinueClick = {})
    }
}
