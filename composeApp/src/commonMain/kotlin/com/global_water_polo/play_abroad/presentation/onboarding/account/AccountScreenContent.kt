package com.global_water_polo.play_abroad.presentation.onboarding.account

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
import com.global_water_polo.play_abroad.core.components.BackButton
import com.global_water_polo.play_abroad.core.components.FormField
import com.global_water_polo.play_abroad.core.components.Headline
import com.global_water_polo.play_abroad.core.components.OnboardingLayout
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.domain.model.Lane
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingState
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

@Composable
fun AccountScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    onNameChanged: (String) -> Unit,
    onOrganizationChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onBackClick: () -> Unit,
    onCreateClick: () -> Unit
) {
    val club = state.lane == Lane.CLUB
    OnboardingLayout(
        modifier = modifier,
        barStart = { BackButton(onClick = onBackClick) },
        bottom = { PrimaryButton(text = stringResource(Res.string.account_create), onClick = onCreateClick, enabled = state.canCreateAccount) }
    ) {
        Headline(text = stringResource(if (club) Res.string.account_title_club else Res.string.account_title_coach))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 24.dp)) {
            FormField(label = stringResource(Res.string.account_full_name), value = state.firstName, onValueChange = onNameChanged, capitalization = KeyboardCapitalization.Words)
            FormField(
                label = stringResource(if (club) Res.string.account_club else Res.string.account_school_or_club),
                value = state.organization,
                onValueChange = onOrganizationChanged,
                capitalization = KeyboardCapitalization.Words
            )
            FormField(
                label = stringResource(Res.string.email_label),
                value = state.email,
                onValueChange = onEmailChanged,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AccountScreenContentPreview() {
    GWPPATheme {
        AccountScreenContent(state = OnboardingState(lane = Lane.COACH), onNameChanged = {}, onOrganizationChanged = {}, onEmailChanged = {}, onBackClick = {}, onCreateClick = {})
    }
}
