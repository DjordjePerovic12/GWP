package com.global_water_polo.play_abroad.presentation.app.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.AppSheet
import com.global_water_polo.play_abroad.core.components.ButtonStyle
import com.global_water_polo.play_abroad.core.components.PickedImage
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.TextLink
import com.global_water_polo.play_abroad.core.components.VideoPlayer
import com.global_water_polo.play_abroad.domain.model.GalleryItem
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/**
 * One gallery item (the prototype's galleryItem): the photo or the clip full size, "Use as profile photo"
 * or "Use as highlight video", and "Remove from gallery".
 */
@Composable
fun GalleryItemSheet(
    item: GalleryItem,
    inUse: Boolean,
    onUseClick: () -> Unit,
    onRemoveClick: () -> Unit,
    onDismiss: () -> Unit
) {
    AppSheet(onDismiss = onDismiss) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(if (item.video) 16f / 9f else 1f)
                .clip(RoundedCornerShape(16.dp))
                .background(GWPPATheme.colors.nightNavy)
        ) {
            if (item.video) VideoPlayer(item.file, Modifier.fillMaxSize())
            else PickedImage(item.file, Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
        }
        PrimaryButton(
            text = stringResource(
                when {
                    item.video && inUse -> Res.string.pf_is_highlight
                    item.video -> Res.string.pf_use_highlight
                    inUse -> Res.string.pf_is_photo
                    else -> Res.string.pf_use_photo
                }
            ),
            onClick = onUseClick,
            enabled = !inUse,
            style = ButtonStyle.BALL
        )
        TextLink(text = stringResource(Res.string.pf_gallery_remove), onClick = onRemoveClick, modifier = Modifier.fillMaxWidth())
    }
}
