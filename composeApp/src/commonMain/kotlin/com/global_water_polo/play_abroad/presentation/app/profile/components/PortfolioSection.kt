package com.global_water_polo.play_abroad.presentation.app.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.InfoPanel
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.domain.model.ProfileVisibility
import com.global_water_polo.play_abroad.presentation.app.profile.ProfileState
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

private data class PortfolioItem(val key: String, val title: StringResource, val sub: StringResource)

private val ITEMS = listOf(
    PortfolioItem("photo", Res.string.port_photo, Res.string.port_photo_sub),
    PortfolioItem("video", Res.string.port_video, Res.string.port_video_sub),
    PortfolioItem("position", Res.string.port_position, Res.string.port_position_sub),
    PortfolioItem("personal", Res.string.port_personal, Res.string.port_personal_sub),
    PortfolioItem("transcript", Res.string.port_transcript, Res.string.port_transcript_sub),
    PortfolioItem("letters", Res.string.port_letters, Res.string.port_letters_sub)
)

/**
 * Portfolio (the prototype's edit-in-place profile): what coaches see, with "Preview as coach" hiding
 * everything not added yet. Eligibility comes from the NCAA clock; documents always stay private.
 */
@Composable
fun PortfolioSection(
    state: ProfileState,
    onPreviewToggle: (Boolean) -> Unit,
    onItemClick: (String) -> Unit,
    onVisibilityClick: (ProfileVisibility) -> Unit
) {
    val colors = GWPPATheme.colors
    val done = state.portfolioItems.toMap()
    Row(verticalAlignment = Alignment.CenterVertically) {
        Toggle(
            options = listOf(stringResource(Res.string.port_edit), stringResource(Res.string.port_preview)),
            selected = if (state.preview) 1 else 0,
            onSelect = { onPreviewToggle(it == 1) },
            modifier = Modifier.weight(1f)
        )
        if (!state.preview) {
            Text(
                stringResource(Res.string.port_complete, "${state.portfolioPercent}%"),
                style = GWPPATheme.typography.archivoBold13,
                color = colors.chlorineBlue,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
    if (state.preview) SmallText(text = stringResource(Res.string.port_preview_note), modifier = Modifier.padding(top = 10.dp))

    val shown = if (state.preview) ITEMS.filter { done[it.key] == true } else ITEMS
    if (shown.isEmpty()) {
        SmallText(text = stringResource(Res.string.port_empty_preview), modifier = Modifier.padding(top = 14.dp))
    } else {
        InfoPanel(modifier = Modifier.padding(top = 14.dp)) {
            shown.forEachIndexed { i, item ->
                if (i > 0) HorizontalDivider(color = colors.lineGrey)
                val added = done[item.key] == true
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !state.preview) { onItemClick(item.key) }
                        .padding(vertical = 12.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(if (added) colors.chlorineBlue else colors.colorWhite)
                            .border(1.5.dp, if (added) colors.chlorineBlue else colors.mistGrey, CircleShape)
                    ) {
                        if (added) Text("✓", style = GWPPATheme.typography.archivoBold12, color = colors.colorWhite)
                    }
                    Column(Modifier.weight(1f).padding(start = 12.dp)) {
                        Text(stringResource(item.title), style = GWPPATheme.typography.archivoBold16, color = colors.inkBlack)
                        val sub = if (item.key == "position" && state.profile.position.isNotEmpty()) state.profile.position else stringResource(item.sub)
                        Text(sub, style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey)
                    }
                    if (!state.preview && !added) Text(stringResource(Res.string.port_add), style = GWPPATheme.typography.archivoBold13, color = colors.chlorineBlue)
                }
            }
        }
    }

    SectionHeader(title = stringResource(Res.string.port_eligibility), note = stringResource(Res.string.port_estimate))
    val f = state.freeYear
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        Cell(stringResource(Res.string.match_clock_starts), state.clockYear?.let { stringResource(Res.string.match_fall, it) } ?: "-", Modifier.weight(1f))
        Cell(stringResource(Res.string.port_seasons_left), f?.let { stringResource(Res.string.home_of_five, it.straight) } ?: "-", Modifier.weight(1f))
        Cell(
            stringResource(if (f != null && f.free > 0) Res.string.port_free_time else Res.string.inq_class),
            when {
                f != null && f.free > 0 -> if (f.free == 1) stringResource(Res.string.match_a_free_year) else stringResource(Res.string.match_n_free_years, f.free)
                else -> state.profile.gradYear?.toString() ?: "-"
            },
            Modifier.weight(1f)
        )
    }
    SmallText(text = stringResource(Res.string.ncaa_estimate), modifier = Modifier.padding(top = 8.dp))

    if (!state.preview) {
        SectionHeader(title = stringResource(Res.string.port_visibility))
        Toggle(
            options = listOf(
                stringResource(Res.string.home_visibility_all),
                stringResource(Res.string.home_visibility_coaches),
                stringResource(Res.string.home_visibility_private)
            ),
            selected = state.profile.visibility.ordinal,
            onSelect = { onVisibilityClick(ProfileVisibility.entries[it]) }
        )
        SmallText(text = stringResource(Res.string.port_visibility_note), modifier = Modifier.padding(top = 8.dp))
    }
    SmallText(text = stringResource(Res.string.port_proto), center = true, modifier = Modifier.fillMaxWidth().padding(top = 18.dp))
}

@Composable
private fun Cell(label: String, value: String, modifier: Modifier = Modifier) {
    val colors = GWPPATheme.colors
    Column(modifier = modifier.clip(RoundedCornerShape(14.dp)).background(colors.colorWhite).padding(12.dp)) {
        Text(label, style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey)
        Text(value, style = GWPPATheme.typography.archivoExpandedBold16, color = colors.inkBlack)
    }
}

/** A pill segmented control. */
@Composable
private fun Toggle(options: List<String>, selected: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    val colors = GWPPATheme.colors
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(colors.lineGrey.copy(alpha = 0.6f))
            .padding(3.dp)
            .height(androidx.compose.foundation.layout.IntrinsicSize.Min)
    ) {
        options.forEachIndexed { i, label ->
            Text(
                label,
                style = GWPPATheme.typography.archivoBold12,
                color = if (i == selected) colors.colorWhite else colors.inkBlack,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .clip(CircleShape)
                    .background(if (i == selected) colors.inkBlack else colors.colorWhite.copy(alpha = 0f))
                    .clickable { onSelect(i) }
                    .padding(vertical = 9.dp, horizontal = 4.dp)
            )
        }
    }
}
