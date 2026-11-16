package com.global_water_polo.play_abroad.presentation.home.coachpending

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.BrandBar
import com.global_water_polo.play_abroad.core.components.ButtonStyle
import com.global_water_polo.play_abroad.core.components.Headline
import com.global_water_polo.play_abroad.core.components.HelpText
import com.global_water_polo.play_abroad.core.components.InfoPanel
import com.global_water_polo.play_abroad.core.components.InfoRow
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.domain.model.Profile
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** "GWP is reviewing your account." and what opens once it's approved (team coaches or GWP staff). */
@Composable
fun CoachPendingScreenContent(
    modifier: Modifier = Modifier,
    state: CoachPendingState,
    onEvent: (CoachPendingEvent) -> Unit
) {
    val colors = GWPPATheme.colors
    val items = if (state.staff) {
        listOf(Res.string.pend_staff_1, Res.string.pend_staff_2, Res.string.pend_staff_3)
    } else {
        listOfNotNull(Res.string.pend_team_1, Res.string.pend_team_2, Res.string.pend_team_3, if (state.travels) Res.string.pend_team_trip else null)
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        BrandBar(modifier = Modifier.padding(bottom = 14.dp))
        Headline(text = stringResource(Res.string.pend_title))
        HelpText(text = stringResource(if (state.staff) Res.string.pend_help_staff else Res.string.pend_help_team))
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .padding(top = 18.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(colors.colorWhite)
                .border(1.dp, colors.lineGrey, RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            items.forEach { item ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("✓", style = GWPPATheme.typography.archivoBold16, color = colors.antiqueGold)
                    Text(stringResource(item), style = GWPPATheme.typography.archivoRegular16, color = colors.inkBlack)
                }
            }
        }
        if (state.travels && !state.staff) SmallText(text = stringResource(Res.string.pend_trip_note), modifier = Modifier.padding(top = 12.dp))
        SmallText(text = stringResource(Res.string.pend_fine), modifier = Modifier.padding(top = 12.dp))
        PrimaryButton(
            text = stringResource(Res.string.pending_back_to_start),
            onClick = { onEvent(CoachPendingEvent.OnBackToStartClick) },
            style = ButtonStyle.GHOST,
            modifier = Modifier.padding(top = 24.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CoachPendingScreenContentPreview() {
    GWPPATheme {
        CoachPendingScreenContent(state = CoachPendingState(profile = Profile(coachTier = "team", coachNeeds = setOf("trip", "develop"))), onEvent = {})
    }
}
