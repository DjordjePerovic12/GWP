package com.global_water_polo.play_abroad.presentation.opener.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import kotlin.math.sin
import kotlin.random.Random

private val DEEP = Color(0xFF03101A)
private val PETROL = Color(0xFF0C2533)
private val IVORY = Color(0xFFF2EDE3)

private class Star(val x: Float, val y: Float, val r: Float, val a: Float, val phase: Float)

/**
 * Everything behind the 3D ball on the opener: deep space turning petrol as the ball forms, a field of
 * faint twinkling stars, a far Sun in the corner that leaves with deep space, the ball's soft blue-green
 * atmosphere ([halo]), and after the tap ivory spreading out from behind the ball. [ballCy] and
 * [ballRadius] are in px; every other value is 0..1 from [OpenerTimeline].
 */
@Composable
fun SpaceBackdrop(
    time: () -> Float,
    petrol: () -> Float,
    stars: () -> Float,
    deepSpace: () -> Float,
    halo: () -> Float,
    ivory: () -> Float,
    ballCy: () -> Float,
    ballRadius: () -> Float,
    modifier: Modifier = Modifier
) {
    val field = remember {
        val rnd = Random(7)
        List(170) { Star(rnd.nextFloat(), rnd.nextFloat(), 0.4f + rnd.nextFloat() * rnd.nextFloat() * 1.6f, 0.35f + rnd.nextFloat() * 0.65f, rnd.nextFloat() * 6.28f) }
    }
    Canvas(modifier) {
        val t = time()
        drawRect(lerp(DEEP, PETROL, petrol()))
        val cx = size.width / 2f
        val cy = ballCy()
        val r = ballRadius()

        // A far Sun up in the corner, gone as the ball takes the stage.
        val sun = deepSpace()
        if (sun > 0f) {
            val at = Offset(size.width * 0.86f, size.height * 0.12f)
            drawCircle(Brush.radialGradient(listOf(Color(0xFFFFE7B0).copy(alpha = 0.55f * sun), Color(0x00FFE7B0)), at, size.width * 0.28f), size.width * 0.28f, at)
            drawCircle(Color(0xFFFFF4D8).copy(alpha = 0.9f * sun), 3.2f * density, at)
        }

        val starAlpha = stars()
        if (starAlpha > 0f) {
            field.forEach { s ->
                val twinkle = 0.75f + 0.25f * sin(t * 1.7f + s.phase)
                drawCircle(Color.White.copy(alpha = (s.a * starAlpha * twinkle).coerceIn(0f, 1f)), s.r * density, Offset(s.x * size.width, s.y * size.height))
            }
        }

        // The ball's atmosphere: a soft glow about twice its size.
        val glow = halo()
        if (glow > 0f && r > 0f) {
            val gr = r * 2.1f
            drawCircle(
                Brush.radialGradient(
                    0f to Color(0xFF568CA0).copy(alpha = 0.30f * glow),
                    0.55f to Color(0xFF285468).copy(alpha = 0.12f * glow),
                    1f to Color.Transparent,
                    center = Offset(cx, cy),
                    radius = gr
                ),
                gr,
                Offset(cx, cy)
            )
        }

        // After the tap: ivory grows out from behind the ball until it covers the screen.
        val k = ivory()
        if (k > 0f) {
            val edge = r * 0.9f + k * 1100f * density
            val soft = (40f + 160f * (1f - k)) * density
            val reach = edge + soft
            drawCircle(
                Brush.radialGradient(
                    0f to IVORY.copy(alpha = (k * 3f).coerceAtMost(1f)),
                    (edge / reach) to IVORY.copy(alpha = (k * 3f).coerceAtMost(1f)),
                    1f to IVORY.copy(alpha = 0f),
                    center = Offset(cx, cy),
                    radius = reach
                ),
                reach,
                Offset(cx, cy)
            )
        }
    }
}
