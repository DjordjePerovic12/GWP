package com.global_water_polo.play_abroad.presentation.app.feed.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.drawableNamed
import com.global_water_polo.play_abroad.presentation.app.feed.Story
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/**
 * One story in For you (the prototype's `.rl3`): a tall photo, category, title and length, a button that
 * leads somewhere in the app, and the like / save rail on the right.
 */
@Composable
fun ReelCard(
    story: Story,
    locked: Boolean,
    liked: Boolean,
    saved: Boolean,
    showNote: Boolean,
    height: Dp,
    onLikeClick: () -> Unit,
    onSaveClick: () -> Unit,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GWPPATheme.colors
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(24.dp))
            .background(colors.nightNavy)
    ) {
        drawableNamed(story.image)?.let {
            Image(painterResource(it), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.matchParentSize())
        }
        Box(Modifier.matchParentSize().background(Brush.verticalGradient(0.45f to Color.Transparent, 1f to colors.nightNavy.copy(alpha = 0.95f))))
        if (showNote) {
            Text(
                stringResource(Res.string.feed_sample_note),
                style = GWPPATheme.typography.archivoRegular13,
                color = colors.colorWhite,
                modifier = Modifier.align(Alignment.TopStart).padding(14.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.45f)).padding(horizontal = 10.dp, vertical = 5.dp)
            )
        }
        if (story.meta.contains(':')) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.align(Alignment.Center).size(64.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.25f))
            ) {
                Text("▶", style = GWPPATheme.typography.archivoExpandedBold27, color = colors.colorWhite)
            }
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.align(Alignment.BottomStart).padding(start = 18.dp, end = 72.dp, bottom = 20.dp)
        ) {
            Text(story.category.uppercase(), style = GWPPATheme.typography.archivoBold12, color = colors.ballYellow)
            Text(story.title, style = GWPPATheme.typography.archivoExpandedBold21, color = colors.colorWhite)
            val tag = when {
                !story.elite -> ""
                locked -> "  ·  " + stringResource(Res.string.feed_elite)
                else -> "  ·  " + stringResource(Res.string.feed_elite_open)
            }
            Text(story.meta + tag, style = GWPPATheme.typography.archivoRegular14, color = colors.colorWhite.copy(alpha = 0.8f))
            val action = if (locked) stringResource(Res.string.feed_watch_elite) else story.actionLabel
            if (action != null) {
                Text(
                    action,
                    style = GWPPATheme.typography.archivoBold13,
                    color = colors.inkBlack,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clip(CircleShape)
                        .background(if (locked) colors.eliteGold else colors.colorWhite)
                        .clickable(onClick = onActionClick)
                        .padding(horizontal = 14.dp, vertical = 9.dp)
                )
            }
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 14.dp, bottom = 22.dp)
        ) {
            RailButton(symbol = if (liked) "♥" else "♡", on = liked, label = stringResource(Res.string.feed_like), onClick = onLikeClick)
            RailButton(symbol = if (saved) "★" else "☆", on = saved, label = stringResource(Res.string.feed_save), onClick = onSaveClick)
        }
    }
}

@Composable
private fun RailButton(symbol: String, on: Boolean, label: String, onClick: () -> Unit) {
    val colors = GWPPATheme.colors
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(44.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.35f)).clickable(onClick = onClick)
        ) {
            Text(symbol, style = GWPPATheme.typography.archivoExpandedBold21, color = if (on) colors.ballYellow else colors.colorWhite)
        }
        Text(label, style = GWPPATheme.typography.archivoSemiBold11, color = colors.colorWhite.copy(alpha = 0.8f))
    }
}
