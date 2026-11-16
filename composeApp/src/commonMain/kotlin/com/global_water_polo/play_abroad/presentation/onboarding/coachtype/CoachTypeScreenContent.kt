package com.global_water_polo.play_abroad.presentation.onboarding.coachtype

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
import com.global_water_polo.play_abroad.core.components.Headline
import com.global_water_polo.play_abroad.core.components.HelpText
import com.global_water_polo.play_abroad.core.components.OnboardingLayout
import com.global_water_polo.play_abroad.core.components.PickCard
import com.global_water_polo.play_abroad.domain.model.Lane
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingState
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** The prototype's coach tiers: the key [OnboardingEvent.OnCoachTierClick] gets, the title and the line under it. */
private val TIERS: List<Triple<String, StringResource, StringResource>> = listOf(
    Triple("college", Res.string.coach_tier_college, Res.string.coach_tier_college_sub),
    Triple("team", Res.string.coach_tier_team, Res.string.coach_tier_team_sub),
    Triple("admin", Res.string.coach_tier_admin, Res.string.coach_tier_admin_sub)
)

/**
 * What do you coach? (v29's c-type): college coach, high school or club coach, or club admin. Club admins go to
 * the club lane.
 */
@Composable
fun CoachTypeScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    onTierClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    OnboardingLayout(
        modifier = modifier,
        barStart = { BarStart(onBackClick = onBackClick) }
    ) {
        Headline(text = stringResource(Res.string.coach_type_title))
        HelpText(text = stringResource(Res.string.coach_type_help))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 24.dp)) {
            TIERS.forEach { (key, title, sub) ->
                PickCard(
                    title = stringResource(title),
                    subtitle = stringResource(sub),
                    selected = state.coachTier == key,
                    onClick = { onTierClick(key) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CoachTypeScreenContentPreview() {
    GWPPATheme {
        CoachTypeScreenContent(state = OnboardingState(lane = Lane.COACH), onTierClick = {}, onBackClick = {})
    }
}
