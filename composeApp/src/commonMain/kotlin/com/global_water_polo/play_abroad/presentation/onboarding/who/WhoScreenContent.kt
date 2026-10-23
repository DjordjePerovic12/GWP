package com.global_water_polo.play_abroad.presentation.onboarding.who

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.Headline
import com.global_water_polo.play_abroad.core.components.HelpText
import com.global_water_polo.play_abroad.core.components.OnboardingLayout
import com.global_water_polo.play_abroad.core.components.PickCard
import com.global_water_polo.play_abroad.core.components.TextLink
import com.global_water_polo.play_abroad.domain.model.Lane
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingState
import com.global_water_polo.play_abroad.resources.Res
import com.global_water_polo.play_abroad.resources.who_athlete
import com.global_water_polo.play_abroad.resources.who_athlete_sub
import com.global_water_polo.play_abroad.resources.who_back_to_globe
import com.global_water_polo.play_abroad.resources.who_club
import com.global_water_polo.play_abroad.resources.who_club_sub
import com.global_water_polo.play_abroad.resources.who_coach
import com.global_water_polo.play_abroad.resources.who_coach_sub
import com.global_water_polo.play_abroad.resources.who_help
import com.global_water_polo.play_abroad.resources.who_parent
import com.global_water_polo.play_abroad.resources.who_parent_sub
import com.global_water_polo.play_abroad.resources.who_title
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

@Composable
fun WhoScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    onRoleClick: (Lane) -> Unit,
    onBackToGlobeClick: () -> Unit
) {
    OnboardingLayout(
        modifier = modifier,
        showBar = false,
        bottom = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                PickCard(title = stringResource(Res.string.who_athlete), subtitle = stringResource(Res.string.who_athlete_sub), onClick = { onRoleClick(Lane.ATHLETE) })
                PickCard(title = stringResource(Res.string.who_parent), subtitle = stringResource(Res.string.who_parent_sub), onClick = { onRoleClick(Lane.PARENT) })
                PickCard(title = stringResource(Res.string.who_coach), subtitle = stringResource(Res.string.who_coach_sub), onClick = { onRoleClick(Lane.COACH) })
                PickCard(title = stringResource(Res.string.who_club), subtitle = stringResource(Res.string.who_club_sub), onClick = { onRoleClick(Lane.CLUB) })
            }
            TextLink(
                text = stringResource(Res.string.who_back_to_globe),
                onClick = onBackToGlobeClick,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 12.dp)
            )
        }
    ) {
        Headline(text = stringResource(Res.string.who_title), large = true)
        HelpText(text = stringResource(Res.string.who_help))
    }
}

@Preview(showBackground = true)
@Composable
private fun WhoScreenContentPreview() {
    GWPPATheme {
        WhoScreenContent(state = OnboardingState(), onRoleClick = {}, onBackToGlobeClick = {})
    }
}
