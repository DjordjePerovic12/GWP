package com.global_water_polo.play_abroad.presentation.onboarding.pending

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.ButtonStyle
import com.global_water_polo.play_abroad.core.components.Headline
import com.global_water_polo.play_abroad.core.components.HelpText
import com.global_water_polo.play_abroad.core.components.OnboardingLayout
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingState
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

@Composable
fun PendingScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    onBackToStartClick: () -> Unit
) {
    val help = buildList {
        if ("recruit" in state.looking) add(stringResource(Res.string.pending_recruit))
        if ("develop" in state.looking) add(stringResource(Res.string.pending_develop))
        if ("trip" in state.looking) add(stringResource(Res.string.pending_trip))
    }.joinToString(" ")
    OnboardingLayout(
        modifier = modifier,
        bottom = { PrimaryButton(text = stringResource(Res.string.pending_back_to_start), onClick = onBackToStartClick, style = ButtonStyle.GHOST) }
    ) {
        Headline(text = stringResource(Res.string.pending_title), large = true)
        if (help.isNotEmpty()) HelpText(text = help)
    }
}

@Preview(showBackground = true)
@Composable
private fun PendingScreenContentPreview() {
    GWPPATheme {
        PendingScreenContent(state = OnboardingState(looking = setOf("recruit", "trip")), onBackToStartClick = {})
    }
}
