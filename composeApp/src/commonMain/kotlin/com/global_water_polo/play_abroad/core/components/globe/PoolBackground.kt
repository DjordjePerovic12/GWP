package com.global_water_polo.play_abroad.core.components.globe

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import kotlin.math.sin

/**
 * The prototype's "white-tiled pool in sunlight": deck-to-water gradient, tile grout and moving
 * caustic lines. [night] (0..1) fades it to the deep pool the clock takeover uses.
 */
@Composable
fun PoolBackground(
    night: Float,
    modifier: Modifier = Modifier
) {
    var time by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        val start = withFrameMillis { it }
        while (true) {
            time = (withFrameMillis { it } - start) / 1000f * 0.5f
        }
    }
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val deck = lerp(Color(0xFFF3F3EF), Color(0xFF05122A), night)
        val water = lerp(Color(0xFFDDECF0), Color(0xFF062448), night)
        drawRect(Brush.verticalGradient(listOf(deck, lerp(deck, water, 0.55f), water)))

        // Tile grout: square tiles, 9 per screen height, wobbling a little with the water.
        val tile = h / 9f
        val wobble = sin(time * 1.3f) * tile * 0.012f
        val grout = lerp(Color(0x14281C0A), Color(0x0A9CC8FF), night)
        var x = (w / 2f) % tile + wobble
        while (x < w) {
            drawLine(grout, Offset(x, 0f), Offset(x + wobble, h), strokeWidth = 1.2f * density)
            x += tile
        }
        var y = wobble
        while (y < h) {
            drawLine(grout, Offset(0f, y), Offset(w, y + wobble), strokeWidth = 1.2f * density)
            y += tile
        }

        // Caustics: bright wavy filaments drifting across the floor.
        val caustic = lerp(Color.White, Color(0xFF7FC4FF), night)
        for (k in 0 until 6) {
            val path = Path()
            val baseY = h * (0.12f + k * 0.13f)
            val phase = time * (0.8f + k * 0.11f) + k * 1.7f
            var px = -20f
            path.moveTo(px, baseY)
            while (px <= w + 20f) {
                val py = baseY + sin(px / w * 7f + phase) * tile * 0.35f + sin(px / w * 13f - phase * 1.3f) * tile * 0.12f
                path.lineTo(px, py)
                px += 22f
            }
            val strength = if (night > 0.5f) 0.10f else 0.22f
            drawPath(path, caustic.copy(alpha = strength * 0.35f), style = Stroke(width = 9f * density))
            drawPath(path, caustic.copy(alpha = strength), style = Stroke(width = 1.6f * density))
        }

        // Vignette.
        drawRect(
            Brush.radialGradient(
                listOf(Color.Transparent, Color.Black.copy(alpha = 0.08f + night * 0.35f)),
                center = Offset(w / 2f, h / 2f),
                radius = maxOf(w, h) * 0.75f
            )
        )
    }
}
