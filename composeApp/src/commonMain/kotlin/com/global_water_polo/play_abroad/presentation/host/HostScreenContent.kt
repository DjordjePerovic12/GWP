package com.global_water_polo.play_abroad.presentation.host

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
import com.global_water_polo.play_abroad.core.components.BarStart
/** Your club on GWP: partner view with the club's schedule and the athletes placed there. Nothing else. */
@Composable
fun HostScreenContent(
    modifier: Modifier = Modifier,
    state: HostState,
    onEvent: (HostEvent) -> Unit
) {
    val colors = GWPPATheme.colors
    val kicker = GWPPATheme.typography.archivoBold11.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 0.14.em)
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        BarStart(onBackClick = { onEvent(HostEvent.OnBackClick) })
        Text(stringResource(Res.string.host_kicker).uppercase(), style = kicker, color = colors.antiqueGold, modifier = Modifier.padding(top = 16.dp))
        Headline(text = stringResource(Res.string.host_title))
        HelpText(text = stringResource(Res.string.host_help))
        InfoPanel(modifier = Modifier.padding(top = 16.dp)) {
            InfoRow(stringResource(Res.string.host_club), stringResource(Res.string.host_club_value))
            HorizontalDivider(color = colors.lineGrey)
            InfoRow(stringResource(Res.string.host_city), stringResource(Res.string.host_city_value))
            HorizontalDivider(color = colors.lineGrey)
            InfoRow(stringResource(Res.string.host_contact), stringResource(Res.string.host_contact_value))
        }
        Section(stringResource(Res.string.host_week))
        InfoPanel {
            HostState.WEEK.forEachIndexed { i, (time, what) ->
                if (i > 0) HorizontalDivider(color = colors.lineGrey)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                    Text(time, style = GWPPATheme.typography.archivoBold13, color = colors.slateGrey, modifier = Modifier.width(96.dp))
                    Text(what, style = GWPPATheme.typography.archivoRegular15, color = colors.inkBlack)
                }
            }
        }
        PrimaryButton(
            text = stringResource(Res.string.host_upload),
            onClick = { onEvent(HostEvent.OnUploadClick) },
            style = ButtonStyle.GHOST,
            modifier = Modifier.padding(top = 12.dp)
        )
        Section(stringResource(Res.string.host_placed))
        InfoPanel {
            HostState.PLACED.forEachIndexed { i, (who, status) ->
                if (i > 0) HorizontalDivider(color = colors.lineGrey)
                InfoRow(who, status)
            }
        }
        SmallText(text = stringResource(Res.string.host_fine), modifier = Modifier.padding(top = 14.dp, bottom = 24.dp))
    }
}

/** A section title with the "Sample" tag on the right. */
@Composable
private fun Section(title: String) {
    val colors = GWPPATheme.colors
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 10.dp)) {
        Text(title, style = GWPPATheme.typography.archivoExpandedBold16, color = colors.inkBlack, modifier = Modifier.weight(1f))
        Text(stringResource(Res.string.inbox_sample), style = GWPPATheme.typography.archivoBold12, color = colors.mistGrey)
    }
}

@Preview(showBackground = true)
@Composable
private fun HostScreenContentPreview() {
    GWPPATheme {
        HostScreenContent(state = HostState(profile = Profile()), onEvent = {})
    }
}
