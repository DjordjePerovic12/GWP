package com.global_water_polo.play_abroad.presentation.app.feed.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.drawableNamed
import com.global_water_polo.play_abroad.presentation.app.feed.Story
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/**
 * One story in For you (the prototype's `.rl3`): the photo fills the page edge to edge, darkened at the top
 * and bottom; gold category, the title in big capitals, length, a see-through button that leads somewhere in
 * the app, and the like / save rail on the right.
 */
@Composable
fun ReelCard(
    story: Story,
    locked: Boolean,
    liked: Boolean,
    saved: Boolean,
    onLikeClick: () -> Unit,
    onSaveClick: () -> Unit,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GWPPATheme.colors
    val ivory = colors.colorWhite
    Box(modifier = modifier.fillMaxSize().background(colors.nightNavy)) {
        drawableNamed(story.image)?.let {
            Image(painterResource(it), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.matchParentSize())
        }
        // `.rl-sh`: dark under the tabs, clear in the middle, nearly opaque behind the text.
        Box(
            Modifier.matchParentSize().background(
                Brush.verticalGradient(
                    0f to colors.nightNavy.copy(alpha = 0.6f),
                    0.2f to Color.Transparent,
                    0.48f to Color.Transparent,
                    1f to colors.nightNavy.copy(alpha = 0.92f)
                )
            )
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.align(Alignment.BottomStart).padding(start = 20.dp, end = 80.dp, bottom = 26.dp)
        ) {
            Text(
                story.category.uppercase(),
                style = GWPPATheme.typography.archivoBold11.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 0.16.em),
                color = colors.eliteGold
            )
            Text(
                story.title.uppercase(),
                style = GWPPATheme.typography.archivoExpandedExtraBold30.copy(lineHeight = 0.93.em, letterSpacing = (-0.01).em),
                color = ivory
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(story.meta, style = GWPPATheme.typography.archivoRegular13, color = ivory.copy(alpha = 0.72f))
                if (story.elite) {
                    Text(
                        stringResource(if (locked) Res.string.feed_elite else Res.string.feed_elite_open),
                        style = GWPPATheme.typography.archivoBold11.copy(fontWeight = FontWeight.ExtraBold, fontSize = 10.sp, letterSpacing = 0.06.em),
                        color = colors.eliteGold,
                        modifier = Modifier.border(1.dp, colors.eliteGold, CircleShape).padding(horizontal = 8.dp, vertical = 1.dp)
                    )
                }
            }
            val action = if (locked) stringResource(Res.string.feed_watch_elite) else story.actionLabel
            if (action != null) {
                Text(
                    action,
                    style = GWPPATheme.typography.archivoBold13,
                    color = ivory,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clip(CircleShape)
                        .background(ivory.copy(alpha = 0.08f))
                        .border(1.dp, ivory.copy(alpha = 0.45f), CircleShape)
                        .clickable(onClick = onActionClick)
                        .padding(horizontal = 15.dp, vertical = 9.dp)
                )
            }
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 12.dp, bottom = 30.dp)
        ) {
            RailButton(on = liked, label = stringResource(Res.string.feed_like), onClick = onLikeClick) { color, filled -> heart(color, filled) }
            RailButton(on = saved, label = stringResource(Res.string.feed_save), onClick = onSaveClick) { color, filled -> bookmark(color, filled) }
        }
    }
}

/** `.rl-rail button`: a dark see-through circle with the icon; gold and filled when on. */
@Composable
private fun RailButton(on: Boolean, label: String, onClick: () -> Unit, icon: androidx.compose.ui.graphics.drawscope.DrawScope.(Color, Boolean) -> Unit) {
    val colors = GWPPATheme.colors
    val tint = if (on) colors.eliteGold else colors.colorWhite
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(colors.nightNavy.copy(alpha = 0.38f))
            .clickable(onClickLabel = label, onClick = onClick)
    ) {
        Canvas(Modifier.size(23.dp)) { icon(tint, on) }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.heart(color: Color, filled: Boolean) {
    val w = size.width
    val h = size.height
    val path = Path().apply {
        moveTo(w * 0.5f, h * 0.88f)
        cubicTo(w * 0.1f, h * 0.6f, w * 0.02f, h * 0.38f, w * 0.12f, h * 0.22f)
        cubicTo(w * 0.24f, h * 0.04f, w * 0.44f, h * 0.1f, w * 0.5f, h * 0.26f)
        cubicTo(w * 0.56f, h * 0.1f, w * 0.76f, h * 0.04f, w * 0.88f, h * 0.22f)
        cubicTo(w * 0.98f, h * 0.38f, w * 0.9f, h * 0.6f, w * 0.5f, h * 0.88f)
        close()
    }
    drawPath(path, color, style = if (filled) Fill else Stroke(width = 1.9.dp.toPx(), join = StrokeJoin.Round))
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.bookmark(color: Color, filled: Boolean) {
    val w = size.width
    val h = size.height
    val path = Path().apply {
        moveTo(w * 0.24f, h * 0.1f)
        lineTo(w * 0.76f, h * 0.1f)
        lineTo(w * 0.76f, h * 0.9f)
        lineTo(w * 0.5f, h * 0.7f)
        lineTo(w * 0.24f, h * 0.9f)
        close()
    }
    drawPath(path, color, style = if (filled) Fill else Stroke(width = 1.9.dp.toPx(), join = StrokeJoin.Round))
}

/** The bookmark on its own, for saved course tiles (`.lb-sv`). */
@Composable
fun BookmarkIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) { bookmark(color, filled = true) }
}
