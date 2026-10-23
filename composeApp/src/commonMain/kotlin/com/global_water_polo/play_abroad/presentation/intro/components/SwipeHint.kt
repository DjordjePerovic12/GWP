package com.global_water_polo.play_abroad.presentation.intro.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme
import kotlin.math.PI
import kotlin.math.cos

/** "SWIPE RIGHT ›››" with the three chevrons pulsing to the right one after another. */
@Composable
fun SwipeHint(text: String, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition()
    val t by transition.animateFloat(0f, 1f, infiniteRepeatable(tween(1400, easing = LinearEasing), RepeatMode.Restart))
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
    ) {
        Text(text = text.uppercase(), style = GWPPATheme.typography.archivoExpandedExtraBold23, color = GWPPATheme.colors.inkBlack)
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            repeat(3) { i ->
                Text(
                    text = "›",
                    style = GWPPATheme.typography.archivoExpandedExtraBold23,
                    color = GWPPATheme.colors.chlorineBlue,
                    modifier = Modifier.graphicsLayer {
                        val phase = ((t - i * 0.107f) % 1f + 1f) % 1f
                        val v = 0.5f - 0.5f * cos(2f * PI.toFloat() * phase)
                        alpha = 0.25f + 0.75f * v
                        translationX = 4.dp.toPx() * v
                    }
                )
            }
        }
    }
}
