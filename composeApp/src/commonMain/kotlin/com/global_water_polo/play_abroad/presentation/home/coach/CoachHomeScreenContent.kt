package com.global_water_polo.play_abroad.presentation.home.coach

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.BrandBar
import com.global_water_polo.play_abroad.core.components.ButtonStyle
import com.global_water_polo.play_abroad.core.components.ChoiceChip
import com.global_water_polo.play_abroad.core.components.Headline
import com.global_water_polo.play_abroad.core.components.HelpText
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** The coach's scouting board: filters, sample athletes who share their profiles, and a request per athlete. */
@Composable
fun CoachHomeScreenContent(
    modifier: Modifier = Modifier,
    state: CoachHomeState,
    onFilterClick: (ScoutFilter) -> Unit,
    onRequestClick: (String) -> Unit,
    onBackToStartClick: () -> Unit
) {
    val colors = GWPPATheme.colors
    val first = state.firstName.trim().substringBefore(" ")
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        BrandBar(modifier = Modifier.padding(bottom = 14.dp))
        Headline(text = if (first.isNotEmpty()) stringResource(Res.string.scout_title_named, first) else stringResource(Res.string.scout_title))
        HelpText(text = stringResource(Res.string.scout_help))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            ScoutFilter.entries.forEach { filter ->
                ChoiceChip(text = filterLabel(filter), selected = state.filter == filter, onClick = { onFilterClick(filter) })
            }
        }
        Text(
            text = stringResource(Res.string.scout_samples).uppercase(),
            style = GWPPATheme.typography.archivoBold11,
            color = colors.mistGrey,
            modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            state.visibleProfiles.forEach { profile ->
                ScoutRow(profile = profile, requested = profile.id in state.requested, onRequestClick = { onRequestClick(profile.id) })
            }
        }
        SmallText(text = stringResource(Res.string.scout_note), modifier = Modifier.padding(top = 12.dp))
        if (state.showTripNote) SmallText(text = stringResource(Res.string.scout_trip_note), modifier = Modifier.padding(top = 8.dp))
        PrimaryButton(
            text = stringResource(Res.string.pending_back_to_start),
            onClick = onBackToStartClick,
            style = ButtonStyle.GHOST,
            modifier = Modifier.padding(top = 24.dp)
        )
    }
}

@Composable
private fun filterLabel(filter: ScoutFilter): String = stringResource(
    when (filter) {
        ScoutFilter.ALL -> Res.string.scout_all
        ScoutFilter.DRIVERS -> Res.string.scout_drivers
        ScoutFilter.CENTERS -> Res.string.scout_centers
        ScoutFilter.GOALKEEPERS -> Res.string.scout_goalkeepers
        ScoutFilter.LEFT_HANDED -> Res.string.scout_left_handed
    }
)

@Composable
private fun ScoutRow(profile: ScoutProfile, requested: Boolean, onRequestClick: () -> Unit) {
    val colors = GWPPATheme.colors
    val shape = RoundedCornerShape(18.dp)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.colorWhite)
            .border(1.dp, colors.lineGrey, shape)
            .padding(14.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(42.dp).clip(CircleShape).background(colors.nightNavy)
        ) {
            Text(profile.id, style = GWPPATheme.typography.archivoExpandedBold12, color = colors.colorWhite)
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(profile.position, style = GWPPATheme.typography.archivoBold16, color = colors.inkBlack)
                if (profile.leftHanded) {
                    Text(
                        "LH",
                        style = GWPPATheme.typography.archivoBold11,
                        color = colors.inkBlack,
                        modifier = Modifier.clip(CircleShape).background(colors.ballYellow).padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Text(
                stringResource(Res.string.scout_line, profile.classOf, profile.height, profile.free50),
                style = GWPPATheme.typography.archivoRegular13,
                color = colors.slateGrey
            )
            Text(profile.program, style = GWPPATheme.typography.archivoSemiBold12, color = colors.chlorineBlue)
        }
        ChoiceChip(
            text = stringResource(if (requested) Res.string.scout_requested else Res.string.scout_request),
            selected = requested,
            onClick = onRequestClick
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CoachHomeScreenContentPreview() {
    GWPPATheme {
        CoachHomeScreenContent(state = CoachHomeState(firstName = "Sam Lee", requested = setOf("AK")), onFilterClick = {}, onRequestClick = {}, onBackToStartClick = {})
    }
}
