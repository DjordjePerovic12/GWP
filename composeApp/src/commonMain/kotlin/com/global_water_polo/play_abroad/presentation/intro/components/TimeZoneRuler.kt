package com.global_water_polo.play_abroad.presentation.intro.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.global_water_polo.play_abroad.core.utils.TimeZoneLabels
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme
import kotlin.math.floor
import kotlin.math.roundToInt

/**
 * Longitude ruler under the globe: a tick every 5°, a UTC label every 15°, the blue marker in the
 * middle on the meridian facing you. Reads the angle while drawing, so spinning never recomposes.
 */
@Composable
fun TimeZoneRuler(
    angle: () -> Float,
    pulse: () -> Float,
    modifier: Modifier = Modifier
) {
    val measurer = rememberTextMeasurer()
    val ink = GWPPATheme.colors.inkBlack
    val blue = GWPPATheme.colors.chlorineBlue
    val labelStyle = GWPPATheme.typography.archivoSemiBold11.copy(color = ink.copy(alpha = 0.6f))
    Canvas(
        modifier
            .fillMaxWidth()
            .height(50.dp)
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .drawWithContent {
                drawContent()
                drawRect(
                    Brush.horizontalGradient(
                        0f to Color.Transparent, 0.22f to Color.Black, 0.78f to Color.Black, 1f to Color.Transparent
                    ),
                    blendMode = BlendMode.DstIn
                )
            }
    ) {
        val longitude = TimeZoneLabels.facing(angle())
        val ppd = 3.4.dp.toPx()
        val cx = size.width / 2f
        val span = cx / ppd + 10f
        var d = floor((longitude - span) / 5f) * 5f
        while (d <= longitude + span) {
            val x = cx + (d - longitude) * ppd
            val major = (d.roundToInt() % 15) == 0
            drawLine(
                color = ink.copy(alpha = if (major) 0.8f else 0.3f),
                start = Offset(x, 6.dp.toPx()),
                end = Offset(x, (if (major) 22 else 13).dp.toPx()),
                strokeWidth = (if (major) 1.5f else 1f).dp.toPx()
            )
            if (major) {
                val layout = measurer.measure(TimeZoneLabels.tzLabel(d), labelStyle)
                drawText(layout, topLeft = Offset(x - layout.size.width / 2f, 30.dp.toPx()))
            }
            d += 5f
        }
        drawLine(blue, Offset(cx, 2.dp.toPx()), Offset(cx, 28.dp.toPx()), strokeWidth = (2f + pulse() * 2f).dp.toPx())
        val tri = Path().apply {
            moveTo(cx - 5.dp.toPx(), 0f); lineTo(cx + 5.dp.toPx(), 0f); lineTo(cx, 6.dp.toPx()); close()
        }
        drawPath(tri, blue)
    }
}
