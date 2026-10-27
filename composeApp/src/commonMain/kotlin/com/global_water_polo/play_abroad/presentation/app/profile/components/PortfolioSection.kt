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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.em
import androidx.compose.ui.text.font.FontWeight
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

    // `.pf-top`: the Edit / Preview as coach toggle (`.seg2`) and the completion pill (`.pct-pill`).
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        ModeToggle(
            options = listOf(stringResource(Res.string.port_edit), stringResource(Res.string.port_preview)),
            selected = if (preview) 1 else 0,
            onSelect = { onEvent(ProfileEvent.OnPreviewToggle(it == 1)) }
        )
        if (!preview) {
            Text(
                "${state.portfolioPercent}%",
                style = GWPPATheme.typography.archivoBold13.copy(fontWeight = FontWeight.ExtraBold),
                color = colors.inkBlack,
                modifier = Modifier.clip(CircleShape).background(colors.sandBeige).padding(horizontal = 10.dp, vertical = 6.dp)
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
    // `.elig`: one panel split in three by thin lines.
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(18.dp))
            .background(colors.colorWhite)
            .border(1.dp, colors.lineGrey, RoundedCornerShape(18.dp))
    ) {
        Cell(stringResource(Res.string.match_clock_starts), state.clockYear?.let { stringResource(Res.string.match_fall, it) } ?: "-", Modifier.weight(1f))
        Box(Modifier.width(1.dp).fillMaxHeight().background(colors.lineGrey))
        Cell(stringResource(Res.string.port_seasons_left), f?.let { stringResource(Res.string.home_of_five, it.straight) } ?: "-", Modifier.weight(1f))
        Box(Modifier.width(1.dp).fillMaxHeight().background(colors.lineGrey))
        Cell(
            stringResource(if (f != null && f.free > 0) Res.string.port_free_time else Res.string.inq_class),
            when {
                f != null && f.free > 0 -> if (f.free == 1) stringResource(Res.string.port_one_year) else stringResource(Res.string.port_n_years, f.free)
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
        // `.irows`: a white panel, full-width lines between rows; an empty value reads "Add".
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(colors.colorWhite)
                .border(1.dp, colors.lineGrey, RoundedCornerShape(18.dp))
        ) {
            rows.forEachIndexed { i, row ->
                if (i > 0) HorizontalDivider(color = colors.lineGrey)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (preview) Modifier else Modifier.clickable { onEvent(ProfileEvent.OnEditInfo(row.group)) })
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Text(stringResource(row.label), style = GWPPATheme.typography.archivoRegular15, color = colors.slateGrey, modifier = Modifier.weight(1f))
                    Text(
                        row.value.ifEmpty { stringResource(Res.string.port_add) },
                        style = if (row.value.isEmpty()) GWPPATheme.typography.archivoSemiBold15 else GWPPATheme.typography.archivoBold16,
                        color = colors.inkBlack
                    )
                    if (!preview) Text("›", style = GWPPATheme.typography.archivoRegular16, color = colors.mistGrey)
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
            // Two separate rows, each tappable across its width, so requesting and uploading can't be mixed up.
            if (!preview) {
                if (letters.isEmpty()) SmallText(text = stringResource(Res.string.pf_add_letter_sub))
                DocRow(
                    title = stringResource(Res.string.pf_letter_request_row),
                    sub = stringResource(Res.string.pf_letter_request_row_sub),
                    empty = true,
                    locked = false,
                    icon = "@",
                    actions = emptyList(),
                    onClick = { onEvent(ProfileEvent.OnLetterRequestClick) }
                )
                DocRow(
                    title = stringResource(Res.string.pf_letter_upload_row),
                    sub = stringResource(Res.string.pf_letter_upload_row_sub),
                    empty = true,
                    locked = false,
                    icon = "↑",
                    actions = emptyList(),
                    onClick = { picker.open(LETTER, MediaKind.DOCUMENT) }
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

/**
 * A document or letter row (`.doc`): an icon, title and line, then its actions; dashed while empty. With
 * [onClick] the whole row is the button and shows a chevron instead of actions.
 */
@Composable
private fun DocRow(
    title: String,
    sub: String,
    empty: Boolean,
    locked: Boolean,
    actions: List<Pair<String, () -> Unit>>,
    icon: String = "▤",
    onClick: (() -> Unit)? = null
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
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
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
        if (onClick != null) Text("›", style = GWPPATheme.typography.archivoBold16, color = colors.mistGrey)
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
    Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = modifier.padding(horizontal = 12.dp, vertical = 14.dp)) {
        Text(
            label.uppercase(),
            style = GWPPATheme.typography.archivoBold11.copy(letterSpacing = 0.06.em),
            color = colors.mistGrey
        )
        Text(value, style = GWPPATheme.typography.archivoBold17, color = colors.inkBlack)
    }
}

/** `.seg2`: a small grey toggle, the chosen side on white. */
@Composable
private fun ModeToggle(options: List<String>, selected: Int, onSelect: (Int) -> Unit) {
    val colors = GWPPATheme.colors
    Row(modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(colors.inkBlack.copy(alpha = 0.06f)).padding(2.dp)) {
        options.forEachIndexed { i, label ->
            val on = i == selected
            Text(
                label,
                style = GWPPATheme.typography.archivoSemiBold13,
                color = if (on) colors.inkBlack else colors.slateGrey,
                modifier = Modifier
                    .then(if (on) Modifier.shadow(2.dp, RoundedCornerShape(8.dp)) else Modifier)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (on) colors.colorWhite else Color.Transparent)
                    .clickable { onSelect(i) }
                    .padding(horizontal = 12.dp, vertical = 7.dp)
            )
        }
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
        // Every option as tall as the tallest and its label centred both ways ("Players and coaches" wraps).
        options.forEachIndexed { i, label ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(if (i == selected) colors.inkBlack else colors.colorWhite.copy(alpha = 0f))
                    .clickable { onSelect(i) }
                    .padding(vertical = 11.dp, horizontal = 6.dp)
            ) {
                Text(
                    label,
                    style = GWPPATheme.typography.archivoBold12,
                    color = if (i == selected) colors.colorWhite else colors.inkBlack,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
