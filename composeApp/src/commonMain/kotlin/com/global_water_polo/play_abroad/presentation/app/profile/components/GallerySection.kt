package com.global_water_polo.play_abroad.presentation.app.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.layout.layout
import androidx.compose.ui.graphics.Brush
import io.github.vinceglb.filekit.name
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.ButtonStyle
import com.global_water_polo.play_abroad.core.components.ChoiceChip
import com.global_water_polo.play_abroad.core.components.PickedImage
import com.global_water_polo.play_abroad.core.components.PlayIcon
import androidx.compose.foundation.layout.size
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.domain.model.GalleryItem
import com.global_water_polo.play_abroad.domain.model.Portfolio
import com.global_water_polo.play_abroad.presentation.app.profile.GalleryFilter
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** Lets a block run past the page's side padding ([by] on each side), like the prototype's negative margins. */
private fun Modifier.bleed(by: Dp): Modifier = layout { measurable, constraints ->
    val extra = by.roundToPx() * 2
    val placeable = measurable.measure(constraints.copy(minWidth = constraints.maxWidth + extra, maxWidth = constraints.maxWidth + extra))
    layout(constraints.maxWidth, placeable.height) { placeable.place(-extra / 2, 0) }
}

/**
 * The gallery (the prototype's galleryTab, inside Portfolio): add photos and clips several at once, filter
 * them, and tap one to put it on the profile. Items in use carry an "On profile" badge.
 */
@Composable
fun GallerySection(
    portfolio: Portfolio,
    filter: GalleryFilter,
    onFilterClick: (GalleryFilter) -> Unit,
    onAddClick: () -> Unit,
    onItemClick: (Long) -> Unit
) {
    val colors = GWPPATheme.colors
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 10.dp)) {
        Text(stringResource(Res.string.pf_gallery), style = GWPPATheme.typography.archivoExpandedBold21, color = colors.inkBlack, modifier = Modifier.weight(1f))
        Text(
            "＋ " + stringResource(Res.string.pf_gallery_add_short),
            style = GWPPATheme.typography.archivoBold13,
            color = colors.chlorineBlue,
            modifier = Modifier.clip(CircleShape).clickable(onClick = onAddClick).padding(6.dp)
        )
    }
    if (portfolio.gallery.isEmpty()) {
        // `.g-empty`: a ghost 3 x 3 grid edge to edge, faded, with the message and button over it.
        Box(Modifier.bleed(20.dp).padding(top = 4.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                repeat(3) { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        repeat(3) { col ->
                            Box(
                                Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .background(colors.inkBlack.copy(alpha = if ((row * 3 + col) % 2 == 0) 0.08f else 0.05f))
                            )
                        }
                    }
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
                modifier = Modifier
                    .matchParentSize()
                    .background(Brush.verticalGradient(0f to colors.boneWhite.copy(alpha = 0.55f), 1f to colors.boneWhite.copy(alpha = 0.92f)))
                    .padding(horizontal = 40.dp, vertical = 20.dp)
            ) {
                Text(stringResource(Res.string.pf_gallery_empty), style = GWPPATheme.typography.archivoBold16, color = colors.inkBlack)
                Text(stringResource(Res.string.pf_gallery_empty_sub), style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey, textAlign = TextAlign.Center)
                PrimaryButton(text = stringResource(Res.string.pf_gallery_add), onClick = onAddClick, style = ButtonStyle.BLUE, modifier = Modifier.padding(top = 10.dp))
            }
        }
        return
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(GalleryFilter.ALL to Res.string.pf_gallery_all, GalleryFilter.PHOTOS to Res.string.pf_gallery_photos, GalleryFilter.VIDEOS to Res.string.pf_gallery_videos).forEach { (f, label) ->
            ChoiceChip(text = stringResource(label), selected = f == filter, onClick = { onFilterClick(f) })
        }
    }
    val items = portfolio.gallery.filter {
        when (filter) {
            GalleryFilter.ALL -> true
            GalleryFilter.PHOTOS -> !it.video
            GalleryFilter.VIDEOS -> it.video
        }
    }
    if (items.isEmpty()) {
        Text(stringResource(Res.string.pf_gallery_none), style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey, modifier = Modifier.padding(top = 12.dp))
        return
    }
    Column(verticalArrangement = Arrangement.spacedBy(3.dp), modifier = Modifier.padding(top = 12.dp).clip(RoundedCornerShape(14.dp))) {
        items.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                (0 until 3).forEach { i ->
                    val item = row.getOrNull(i)
                    Box(Modifier.weight(1f).aspectRatio(1f)) {
                        if (item != null) Tile(item, onProfile = item.id == portfolio.photoId || item.id == portfolio.highlightId, onClick = { onItemClick(item.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun Tile(item: GalleryItem, onProfile: Boolean, onClick: () -> Unit) {
    val colors = GWPPATheme.colors
    Box(Modifier.fillMaxSize().background(colors.nightNavy).clickable(onClick = onClick)) {
        if (item.video) {
            Column(Modifier.align(Alignment.Center).padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                PlayIcon(colors.colorWhite, Modifier.size(24.dp))
                Text(item.file.name, style = GWPPATheme.typography.archivoRegular13, color = colors.colorWhite.copy(alpha = 0.7f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        } else {
            PickedImage(item.file, Modifier.fillMaxSize())
        }
        if (onProfile) {
            Text(
                stringResource(Res.string.pf_on_profile),
                style = GWPPATheme.typography.archivoBold11,
                color = colors.inkBlack,
                modifier = Modifier.align(Alignment.TopStart).padding(6.dp).clip(RoundedCornerShape(6.dp)).background(colors.ballYellow).padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}
