package com.global_water_polo.play_abroad.presentation.onboarding.invite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.BackButton
import com.global_water_polo.play_abroad.core.components.ClockChip
import com.global_water_polo.play_abroad.core.components.FormField
import com.global_water_polo.play_abroad.core.components.Headline
import com.global_water_polo.play_abroad.core.components.HelpText
import com.global_water_polo.play_abroad.core.components.OnboardingLayout
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.TextLink
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingState
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

@Composable
fun InviteScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    onKidNameChanged: (String) -> Unit,
    onKidContactChanged: (String) -> Unit,
    onInviteClick: () -> Unit,
    onDoneClick: () -> Unit,
    onBackClick: () -> Unit
) {
    OnboardingLayout(
        modifier = modifier,
        barStart = { BackButton(onClick = onBackClick) },
        barEnd = { ClockChip(countdown = state.countdown, clockYear = state.window?.clock?.year) },
        bottom = {
            if (state.invited) {
                PrimaryButton(text = stringResource(Res.string.common_continue), onClick = onDoneClick)
            } else {
                PrimaryButton(text = stringResource(Res.string.invite_send), onClick = onInviteClick, enabled = state.canInvite)
                TextLink(text = stringResource(Res.string.invite_later), onClick = onDoneClick, modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    ) {
        Headline(text = stringResource(Res.string.invite_title))
        HelpText(text = stringResource(Res.string.invite_help))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 24.dp)) {
            FormField(label = stringResource(Res.string.invite_kid_name), value = state.kidName, onValueChange = onKidNameChanged, capitalization = KeyboardCapitalization.Words)
            FormField(label = stringResource(Res.string.invite_kid_contact), value = state.kidContact, onValueChange = onKidContactChanged)
        }
        if (state.invited) {
            Text(
                text = stringResource(Res.string.invite_ready, state.kidName.trim()),
                style = GWPPATheme.typography.archivoBold13,
                color = GWPPATheme.colors.chlorineBlue,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InviteScreenContentPreview() {
    GWPPATheme {
        InviteScreenContent(state = OnboardingState(kidName = "Alex"), onKidNameChanged = {}, onKidContactChanged = {}, onInviteClick = {}, onDoneClick = {}, onBackClick = {})
    }
}
