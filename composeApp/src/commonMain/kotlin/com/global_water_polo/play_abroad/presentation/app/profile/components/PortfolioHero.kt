package com.global_water_polo.play_abroad.presentation.app.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.PickedImage
import com.global_water_polo.play_abroad.domain.model.Profile
import com.global_water_polo.play_abroad.domain.model.classOf
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/**
 * The portfolio's photo hero (the prototype's `.ph-hero`): the profile photo with name, positions, LH and
 * class of over it. Without a photo, editing shows "Add your photo"; coaches see the initials instead.
 */
@Composable
fun PortfolioHero(profile: Profile, preview: Boolean, onAddPhoto: () -> Unit) {
    val colors = GWPPATheme.colors
    val photo = profile.portfolio.photo
    val name = listOf(profile.firstName, profile.lastName).filter { it.isNotBlank() }.joinToString(" ").ifEmpty { "Alex" }
    val initials = (profile.firstName.take(1) + profile.lastName.take(1)).uppercase().ifEmpty { "A" }
    val shape = RoundedCornerShape(26.dp)
    Box(
        Modifier
            .fillMaxWidth()
            .aspectRatio(4f / 5f)
            .clip(shape)
            .background(if (photo != null || preview) colors.nightNavy else colors.colorWhite)
            .then(if (photo == null && !preview) Modifier.border(1.5.dp, colors.lineGrey, shape).clickable(onClick = onAddPhoto) else Modifier)
    ) {
        when {
            photo != null -> PickedImage(photo.file, Modifier.fillMaxSize())
            preview -> Text(
                initials,
                style = GWPPATheme.typography.archivoExpandedExtraBold104,
                color = colors.colorWhite.copy(alpha = 0.16f),
                modifier = Modifier.align(Alignment.Center).padding(bottom = 80.dp)
            )
            else -> Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.align(Alignment.Center).padding(bottom = 90.dp, start = 24.dp, end = 24.dp)
            ) {
                Text("＋", style = GWPPATheme.typography.archivoExpandedBold32, color = colors.chlorineBlue)
                Text(stringResource(Res.string.pf_add_photo), style = GWPPATheme.typography.archivoBold16, color = colors.inkBlack)
                Text(stringResource(Res.string.port_photo_sub), style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey, textAlign = TextAlign.Center)
            }
        }
        Box(Modifier.matchParentSize().background(Brush.verticalGradient(0.5f to Color.Transparent, 1f to colors.nightNavy.copy(alpha = if (photo != null || preview) 0.85f else 0.75f))))
        if (photo != null && !preview) {
            Text(
                stringResource(Res.string.pf_change),
                style = GWPPATheme.typography.archivoBold12,
                color = colors.colorWhite,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.45f))
                    .clickable(onClick = onAddPhoto)
                    .padding(horizontal = 12.dp, vertical = 7.dp)
            )
        }
        Column(Modifier.align(Alignment.BottomStart).padding(18.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(name.uppercase(), style = GWPPATheme.typography.archivoExpandedExtraBold32, color = colors.colorWhite)
            val meta = listOfNotNull(
                profile.positions.joinToString(" / ").ifEmpty { if (preview) null else stringResource(Res.string.pf_no_position) },
                if (profile.leftHanded) "LH" else null,
                profile.classOf.ifEmpty { null }?.let { stringResource(Res.string.pf_class_of, it) }
            )
            Text(meta.joinToString("  ·  "), style = GWPPATheme.typography.archivoSemiBold14, color = colors.colorWhite.copy(alpha = 0.85f))
        }
    }
}

/** The position tags under the hero (`.pf-chips`): the main one filled, then the rest, LH, and the edit tag. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PositionTags(profile: Profile, preview: Boolean, onEditClick: () -> Unit) {
    val colors = GWPPATheme.colors
    if (preview && profile.positions.isEmpty() && !profile.leftHanded) return
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        profile.positions.forEachIndexed { i, position -> Tag(position, filled = i == 0) }
        if (profile.leftHanded) Tag(stringResource(Res.string.pf_left_handed), filled = false)
        if (!preview) {
            Text(
                stringResource(if (profile.positions.isEmpty()) Res.string.pf_add_position else Res.string.pf_edit_position),
                style = GWPPATheme.typography.archivoBold13,
                color = colors.chlorineBlue,
                modifier = Modifier
                    .clip(CircleShape)
                    .border(1.dp, colors.chlorineBlue.copy(alpha = 0.5f), CircleShape)
                    .clickable(onClick = onEditClick)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun Tag(text: String, filled: Boolean) {
    val colors = GWPPATheme.colors
    Text(
        text,
        style = GWPPATheme.typography.archivoBold13,
        color = if (filled) colors.colorWhite else colors.inkBlack,
        modifier = Modifier
            .clip(CircleShape)
            .background(if (filled) colors.inkBlack else colors.colorWhite)
            .border(1.dp, if (filled) colors.inkBlack else colors.lineGrey, CircleShape)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}
