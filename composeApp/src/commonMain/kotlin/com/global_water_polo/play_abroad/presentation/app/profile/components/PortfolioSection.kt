package com.global_water_polo.play_abroad.presentation.app.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.nameWithoutExtension
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.InfoPanel
import com.global_water_polo.play_abroad.core.components.MediaKind
import com.global_water_polo.play_abroad.core.components.PlayIcon
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.core.components.VideoPlayer
import com.global_water_polo.play_abroad.core.components.rememberGalleryPicker
import com.global_water_polo.play_abroad.core.components.rememberMediaPicker
import com.global_water_polo.play_abroad.domain.model.InfoGroup
import com.global_water_polo.play_abroad.domain.model.ProfileVisibility
import com.global_water_polo.play_abroad.domain.model.classOf
import com.global_water_polo.play_abroad.presentation.app.profile.ProfileEvent
import com.global_water_polo.play_abroad.presentation.app.profile.ProfileState
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

private const val PHOTO = "photo"
private const val HIGHLIGHT = "highlight"
private const val TRANSCRIPT = "transcript"
private const val LETTER = "letter"

/**
 * Portfolio (the prototype's profileTab, edit in place): the photo hero, positions, the highlight, eligibility,
 * personal info, documents, letters, visibility and the gallery. "Preview as coach" shows what coaches see:
 * empty sections hide, documents stay locked. Files stay on the device; nothing is uploaded.
 */
@Composable
fun PortfolioSection(state: ProfileState, onEvent: (ProfileEvent) -> Unit) {
    val colors = GWPPATheme.colors
    val profile = state.profile
    val portfolio = profile.portfolio
    val preview = state.preview
    val picker = rememberMediaPicker { key, file ->
        when (key) {
            PHOTO -> onEvent(ProfileEvent.OnPhotoPicked(file))
            HIGHLIGHT -> onEvent(ProfileEvent.OnHighlightPicked(file))
            TRANSCRIPT -> onEvent(ProfileEvent.OnTranscriptPicked(file))
            LETTER -> onEvent(ProfileEvent.OnLetterUploaded(file.nameWithoutExtension, file))
        }
    }
    val addToGallery = rememberGalleryPicker { onEvent(ProfileEvent.OnGalleryAdd(it)) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Toggle(
            options = listOf(stringResource(Res.string.port_edit), stringResource(Res.string.port_preview)),
            selected = if (preview) 1 else 0,
            onSelect = { onEvent(ProfileEvent.OnPreviewToggle(it == 1)) },
            modifier = Modifier.weight(1f)
        )
        if (!preview) {
            Text(
                stringResource(Res.string.port_complete, "${state.portfolioPercent}%"),
                style = GWPPATheme.typography.archivoBold13,
                color = colors.chlorineBlue,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
    if (preview) SmallText(text = stringResource(Res.string.port_preview_note), modifier = Modifier.padding(top = 10.dp))

    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 14.dp)) {
        PortfolioHero(profile = profile, preview = preview, onAddPhoto = { picker.open(PHOTO, MediaKind.PHOTO) })
        PositionTags(profile = profile, preview = preview, onEditClick = { onEvent(ProfileEvent.OnEditInfo(InfoGroup.POSITION)) })
    }

    // Highlight: the clip plays in place; coaches see nothing until there is one.
    val highlight = portfolio.highlight
    if (highlight != null || !preview) {
        SectionHeader(title = stringResource(Res.string.pf_highlight))
        if (highlight != null) {
            Box(Modifier.fillMaxWidth().aspectRatio(16f / 9f).clip(RoundedCornerShape(20.dp)).background(Color.Black)) {
                VideoPlayer(highlight.file, Modifier.fillMaxSize())
                if (!preview) {
                    Text(
                        stringResource(Res.string.pf_replace),
                        style = GWPPATheme.typography.archivoBold12,
                        color = colors.colorWhite,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .clickable { picker.open(HIGHLIGHT, MediaKind.VIDEO) }
                            .padding(horizontal = 11.dp, vertical = 6.dp)
                    )
                }
            }
        } else {
            EmptyCard(
                play = true,
                title = stringResource(Res.string.pf_add_highlight),
                sub = stringResource(Res.string.port_video_sub),
                onClick = { picker.open(HIGHLIGHT, MediaKind.VIDEO) },
                modifier = Modifier.aspectRatio(16f / 9f)
            )
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
                else -> profile.classOf.ifEmpty { "-" }
            },
            Modifier.weight(1f)
        )
    }
    SmallText(text = stringResource(Res.string.ncaa_estimate), modifier = Modifier.padding(top = 8.dp))

    // Personal info: every row opens its editor; coaches only see the rows filled in.
    val info = portfolio.info
    val rows = listOf(
        InfoRowData(Res.string.pf_height, info.height, InfoGroup.BODY),
        InfoRowData(Res.string.pf_wingspan, info.wingspan, InfoGroup.BODY),
        InfoRowData(Res.string.pf_fifty, info.fifty.ifEmpty { null }?.let { "$it s" }.orEmpty(), InfoGroup.BODY),
        InfoRowData(Res.string.pf_club, info.club, InfoGroup.CLUB),
        InfoRowData(Res.string.pf_coach, info.coach, InfoGroup.CLUB),
        InfoRowData(Res.string.pf_hometown, info.hometown, InfoGroup.HOME),
        InfoRowData(Res.string.pf_high_school, info.highSchool, InfoGroup.HOME),
        InfoRowData(Res.string.pf_class_label, profile.classOf, InfoGroup.SCHOOL),
        InfoRowData(Res.string.pf_gpa, info.gpa, InfoGroup.SCHOOL)
    ).filter { !preview || it.value.isNotEmpty() }
    SectionHeader(title = stringResource(Res.string.port_personal))
    if (rows.isEmpty()) {
        SmallText(text = stringResource(Res.string.pf_not_added))
    } else {
        InfoPanel {
            rows.forEachIndexed { i, row ->
                if (i > 0) HorizontalDivider(color = colors.lineGrey)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (preview) Modifier else Modifier.clickable { onEvent(ProfileEvent.OnEditInfo(row.group)) })
                        .padding(vertical = 12.dp)
                ) {
                    Text(stringResource(row.label), style = GWPPATheme.typography.archivoRegular15, color = colors.slateGrey, modifier = Modifier.weight(1f))
                    Text(
                        row.value.ifEmpty { stringResource(Res.string.port_add) },
                        style = GWPPATheme.typography.archivoBold13,
                        color = if (row.value.isEmpty()) colors.chlorineBlue else colors.inkBlack
                    )
                    if (!preview) Text("  ›", style = GWPPATheme.typography.archivoBold16, color = colors.mistGrey)
                }
            }
        }
    }

    // Documents: always private. Coaches see that a transcript exists, not the file.
    val transcript = portfolio.transcript
    if (transcript != null || !preview) {
        SectionHeader(title = stringResource(Res.string.pf_documents), note = stringResource(Res.string.pf_private))
        DocRow(
            title = stringResource(Res.string.port_transcript),
            sub = when {
                transcript == null -> stringResource(Res.string.pf_transcript_empty)
                preview -> stringResource(Res.string.pf_on_request)
                else -> stringResource(Res.string.pf_private_until, transcript.name)
            },
            empty = transcript == null,
            locked = preview,
            actions = if (preview) emptyList() else listOf(
                stringResource(if (transcript == null) Res.string.pf_upload else Res.string.pf_replace) to { picker.open(TRANSCRIPT, MediaKind.DOCUMENT) }
            )
        )
    }

    // Recommendation letters: requested from a coach, or uploaded.
    val letters = portfolio.letters
    if (letters.isNotEmpty() || !preview) {
        SectionHeader(title = stringResource(Res.string.port_letters), note = if (letters.isEmpty()) "" else letters.size.toString())
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            letters.forEach { letter ->
                DocRow(
                    title = letter.from,
                    sub = when {
                        preview -> stringResource(if (letter.file != null) Res.string.pf_on_request_short else Res.string.pf_requested)
                        letter.file != null -> stringResource(Res.string.pf_private_share, letter.file.name)
                        letter.club.isNotEmpty() -> stringResource(Res.string.pf_requested_from, letter.club)
                        else -> stringResource(Res.string.pf_requested_not_sent)
                    },
                    empty = false,
                    locked = preview && letter.file != null,
                    actions = if (preview) emptyList() else listOf(stringResource(Res.string.port_remove) to { onEvent(ProfileEvent.OnLetterRemove(letter.id)) })
                )
            }
            if (!preview) {
                DocRow(
                    title = stringResource(Res.string.pf_add_letter),
                    sub = stringResource(Res.string.pf_add_letter_sub),
                    empty = true,
                    locked = false,
                    icon = "＋",
                    actions = listOf(
                        stringResource(Res.string.pf_request) to { onEvent(ProfileEvent.OnLetterRequestClick) },
                        stringResource(Res.string.pf_upload) to { picker.open(LETTER, MediaKind.DOCUMENT) }
                    )
                )
            }
        }
    }

    if (!preview) {
        SectionHeader(title = stringResource(Res.string.port_visibility))
        Toggle(
            options = listOf(
                stringResource(Res.string.home_visibility_all),
                stringResource(Res.string.home_visibility_coaches),
                stringResource(Res.string.home_visibility_private)
            ),
            selected = profile.visibility.ordinal,
            onSelect = { onEvent(ProfileEvent.OnVisibilityClick(ProfileVisibility.entries[it])) }
        )
        SmallText(text = stringResource(Res.string.port_visibility_note), modifier = Modifier.padding(top = 8.dp))

        GallerySection(
            portfolio = portfolio,
            filter = state.galleryFilter,
            onFilterClick = { onEvent(ProfileEvent.OnGalleryFilter(it)) },
            onAddClick = addToGallery,
            onItemClick = { onEvent(ProfileEvent.OnGalleryItemClick(it)) }
        )
    }
    SmallText(text = stringResource(Res.string.port_proto), center = true, modifier = Modifier.fillMaxWidth().padding(top = 18.dp))
}

private data class InfoRowData(val label: StringResource, val value: String, val group: InfoGroup)

/** A dashed card that asks for something not added yet (`.hl.empty`). */
@Composable
private fun EmptyCard(play: Boolean, title: String, sub: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = GWPPATheme.colors
    val shape = RoundedCornerShape(20.dp)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.colorWhite)
            .border(1.5.dp, colors.lineGrey, shape)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(48.dp).clip(CircleShape).background(colors.chlorineBlue)) {
            if (play) PlayIcon(colors.colorWhite, Modifier.size(20.dp))
        }
        Text(title, style = GWPPATheme.typography.archivoBold16, color = colors.inkBlack)
        Text(sub, style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey, textAlign = TextAlign.Center)
    }
}

/** A document or letter row (`.doc`): an icon, title and line, then its actions; dashed while empty. */
@Composable
private fun DocRow(
    title: String,
    sub: String,
    empty: Boolean,
    locked: Boolean,
    actions: List<Pair<String, () -> Unit>>,
    icon: String = "▤"
) {
    val colors = GWPPATheme.colors
    val shape = RoundedCornerShape(18.dp)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (empty) Color.Transparent else colors.colorWhite)
            .border(1.dp, if (empty) colors.mistGrey.copy(alpha = 0.6f) else colors.lineGrey, shape)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(colors.chlorineBlueSoft)) {
            Text(icon, style = GWPPATheme.typography.archivoBold16, color = colors.chlorineBlue)
        }
        Column(Modifier.weight(1f)) {
            Text(title, style = GWPPATheme.typography.archivoBold16, color = colors.inkBlack, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(sub, style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey)
        }
        if (locked) Text("🔒", style = GWPPATheme.typography.archivoRegular14)
        Column(horizontalAlignment = Alignment.End) {
            actions.forEach { (label, onClick) ->
                Text(
                    label,
                    style = GWPPATheme.typography.archivoBold13,
                    color = colors.chlorineBlue,
                    modifier = Modifier.clip(CircleShape).clickable(onClick = onClick).padding(horizontal = 6.dp, vertical = 4.dp)
                )
            }
        }
    }
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
            .height(IntrinsicSize.Min)
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
