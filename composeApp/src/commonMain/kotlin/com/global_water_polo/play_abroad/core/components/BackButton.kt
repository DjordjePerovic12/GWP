package com.global_water_polo.play_abroad.core.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    dark: Boolean = false
) {
    val colors = GWPPATheme.colors
    val tint = if (dark) colors.colorWhite else colors.inkBlack
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(if (dark) Color.White.copy(alpha = 0.08f) else colors.colorWhite.copy(alpha = 0.8f))
            .border(1.5.dp, if (dark) Color.White.copy(alpha = 0.22f) else colors.lineGrey, CircleShape)
            .clickable(onClick = onClick)
    ) {
        Chevron(color = tint, pointsLeft = true, modifier = Modifier.size(16.dp))
    }
}

/** The prototype's chevron icon (also used, pointing right, on pick cards). */
@Composable
fun Chevron(color: Color, pointsLeft: Boolean, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val u = size.width / 16f
        val path = Path().apply {
            if (pointsLeft) {
                moveTo(10f * u, 2f * u); lineTo(4f * u, 8f * u); lineTo(10f * u, 14f * u)
            } else {
                moveTo(6f * u, 2f * u); lineTo(12f * u, 8f * u); lineTo(6f * u, 14f * u)
            }
        }
        drawPath(path, color, style = Stroke(width = 2f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

/** Check mark for multi-pick cards and to-do rows. */
@Composable
fun CheckMark(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val u = size.width / 20f
        val path = Path().apply { moveTo(5f * u, 10.5f * u); lineTo(8.2f * u, 13.7f * u); lineTo(15f * u, 7f * u) }
        drawPath(path, color, style = Stroke(width = 2.2f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}
