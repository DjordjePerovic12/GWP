package com.global_water_polo.play_abroad.presentation.onboarding.club

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.BrandBar
import com.global_water_polo.play_abroad.core.components.ButtonStyle
import com.global_water_polo.play_abroad.core.components.Headline
import com.global_water_polo.play_abroad.core.components.HelpText
import com.global_water_polo.play_abroad.core.components.OnboardingLayout
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingState
import com.global_water_polo.play_abroad.presentation.onboarding.ask.components.FACILITIES
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** The club's draft profile, as GWP will review it: place, facilities, staff and what the club is open to. */
@Composable
fun ClubProfileScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    onBackToStartClick: () -> Unit
) {
    val colors = GWPPATheme.colors
    val name = state.organization.trim().ifEmpty { stringResource(Res.string.club_profile_your_club) }
    val spot = countryLocation(stringArrayResource(Res.array.club_countries), state.clubCountry)
    val facilities = FACILITIES.filter { it.key in state.facilities }.map { stringResource(it.title) }
    val openTo = clubIntents(state.looking)
    val shape = RoundedCornerShape(22.dp)
    OnboardingLayout(
        modifier = modifier,
        barStart = { BrandBar() },
        bottom = { PrimaryButton(text = stringResource(Res.string.pending_back_to_start), onClick = onBackToStartClick, style = ButtonStyle.GHOST) }
    ) {
        Headline(text = stringResource(Res.string.club_profile_title, name))
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .padding(top = 18.dp)
                .fillMaxWidth()
                .clip(shape)
                .background(colors.colorWhite)
                .border(1.dp, colors.lineGrey, shape)
                .padding(18.dp)
        ) {
            Row(Modifier.fillMaxWidth()) {
                Text(stringResource(Res.string.club_profile_label).uppercase(), style = GWPPATheme.typography.archivoBold11, color = colors.mistGrey, modifier = Modifier.weight(1f))
                Text(stringResource(Res.string.club_profile_draft).uppercase(), style = GWPPATheme.typography.archivoBold11, color = colors.chlorineBlue)
            }
            Text(name, style = GWPPATheme.typography.archivoExpandedBold21, color = colors.inkBlack, modifier = Modifier.padding(top = 6.dp))
            Text(
                listOf(state.clubCity.trim(), state.clubCountry).filter { it.isNotEmpty() }.joinToString(", "),
                style = GWPPATheme.typography.archivoRegular15,
                color = colors.slateGrey
            )
            spot?.let { Text(formatCoordinates(it.first, it.second), style = GWPPATheme.typography.archivoBold12, color = colors.mistGrey) }
            Section(stringResource(Res.string.club_profile_facilities)) { Tags(facilities) }
            Section(stringResource(Res.string.club_profile_staff)) {
                val coach = state.staffCoach.trim()
                val staffLine = (if (coach.isNotEmpty()) stringResource(Res.string.club_profile_head_coach, coach) else stringResource(Res.string.club_profile_no_coach)) +
                    (if (state.staffSize.isNotEmpty()) stringResource(Res.string.club_profile_staff_size, state.staffSize) else "")
                Text(staffLine, style = GWPPATheme.typography.archivoRegular15, color = colors.inkBlack)
                if (state.languages.isNotEmpty()) {
                    Text(state.languages.joinToString(", "), style = GWPPATheme.typography.archivoRegular13, color = colors.mistGrey)
                }
            }
            Section(stringResource(Res.string.club_profile_open_to)) { Tags(openTo) }
        }
        HelpText(text = stringResource(Res.string.club_profile_help), modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
private fun clubIntents(keys: Set<String>): List<String> = listOfNotNull(
    if ("host" in keys) stringResource(Res.string.look_host) else null,
    if ("trip" in keys) stringResource(Res.string.look_trips_club) else null,
    if ("tournament" in keys) stringResource(Res.string.look_tournament_club) else null,
    if ("partner" in keys) stringResource(Res.string.look_partner) else null,
    if ("develop" in keys) stringResource(Res.string.look_develop) else null
)

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Box(Modifier.padding(top = 10.dp).fillMaxWidth().height(1.dp).background(GWPPATheme.colors.lineGrey))
    Text(title, style = GWPPATheme.typography.archivoSemiBold13, color = GWPPATheme.colors.slateGrey, modifier = Modifier.padding(top = 8.dp))
    content()
}

@Composable
private fun Tags(items: List<String>) {
    val colors = GWPPATheme.colors
    if (items.isEmpty()) {
        Text(stringResource(Res.string.club_profile_not_added), style = GWPPATheme.typography.archivoRegular13, color = colors.mistGrey)
        return
    }
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        items.forEach {
            Text(
                it,
                style = GWPPATheme.typography.archivoSemiBold13,
                color = colors.inkBlack,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(colors.boneWhite)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ClubProfileScreenContentPreview() {
    GWPPATheme {
        ClubProfileScreenContent(
            state = OnboardingState(organization = "VK Primorac", clubCity = "Kotor", clubCountry = "Montenegro", facilities = setOf("outdoor", "gym"), looking = setOf("host")),
            onBackToStartClick = {}
        )
    }
}
