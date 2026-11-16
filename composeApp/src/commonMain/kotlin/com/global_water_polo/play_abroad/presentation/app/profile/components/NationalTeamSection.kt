package com.global_water_polo.play_abroad.presentation.app.profile.components

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
import com.global_water_polo.play_abroad.core.components.ChoiceChip
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** Profile › Card, "National team" (v29's nt28): teams and international events played, as chips. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NationalTeamSection(
    teams: Set<String>,
    events: Set<String>,
    onTeamClick: (String) -> Unit,
    onEventClick: (String) -> Unit
) {
    val colors = GWPPATheme.colors
    SectionHeader(title = stringResource(Res.string.nt_title), note = stringResource(Res.string.nt_optional))
    Column {
        Text(stringResource(Res.string.nt_teams), style = GWPPATheme.typography.archivoBold13, color = colors.slateGrey, modifier = Modifier.padding(bottom = 8.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            stringArrayResource(Res.array.nt_team_options).forEach { team ->
                ChoiceChip(text = team, selected = team in teams, onClick = { onTeamClick(team) })
            }
        }
        Text(stringResource(Res.string.nt_events), style = GWPPATheme.typography.archivoBold13, color = colors.slateGrey, modifier = Modifier.padding(top = 14.dp, bottom = 8.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            stringArrayResource(Res.array.nt_event_options).forEach { event ->
                ChoiceChip(text = event, selected = event in events, onClick = { onEventClick(event) })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NationalTeamSectionPreview() {
    GWPPATheme { Column { NationalTeamSection(teams = setOf("U18"), events = emptySet(), onTeamClick = {}, onEventClick = {}) } }
}
