package com.global_water_polo.play_abroad.presentation.app.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.ButtonStyle
import com.global_water_polo.play_abroad.core.components.InfoPanel
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.presentation.app.profile.components.SectionHeader
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** The Studio tools and who they are for (the prototype's studioHTML). */
private val TOOLS: List<Pair<StringResource, StringResource>> = listOf(
    Res.string.studio_videos to Res.string.studio_videos_who,
    Res.string.studio_club to Res.string.studio_club_who,
    Res.string.studio_availability to Res.string.studio_availability_who,
    Res.string.studio_tours to Res.string.studio_tours_who,
    Res.string.studio_publish to Res.string.studio_publish_who
)

/**
 * Studio (the prototype's studioHTML): the publishing tools verified coaches, clubs and housing partners get,
 * locked for athletes, and a request for access. Prototype: the request is only remembered.
 */
@Composable
fun StudioSection(requested: Boolean, onRequestClick: () -> Unit) {
    val colors = GWPPATheme.colors
    SectionHeader(title = stringResource(Res.string.studio_title), note = stringResource(Res.string.studio_note))
    InfoPanel {
        TOOLS.forEachIndexed { i, (tool, who) ->
            if (i > 0) HorizontalDivider(color = colors.lineGrey)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                Column(Modifier.weight(1f)) {
                    Text(stringResource(tool), style = GWPPATheme.typography.archivoRegular15, color = colors.inkBlack)
                    Text(stringResource(who), style = GWPPATheme.typography.archivoRegular13, color = colors.mistGrey)
                }
                Text("🔒", style = GWPPATheme.typography.archivoRegular14)
            }
        }
    }
    SmallText(text = stringResource(Res.string.studio_fine), modifier = Modifier.padding(top = 8.dp))
    PrimaryButton(
        text = stringResource(if (requested) Res.string.studio_requested else Res.string.studio_request),
        onClick = onRequestClick,
        enabled = !requested,
        style = ButtonStyle.GHOST,
        modifier = Modifier.padding(top = 10.dp)
    )
}
