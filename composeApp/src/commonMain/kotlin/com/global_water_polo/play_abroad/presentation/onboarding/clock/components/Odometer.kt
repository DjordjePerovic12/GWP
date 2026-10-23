package com.global_water_polo.play_abroad.presentation.onboarding.clock.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.em
import kotlin.math.roundToInt

/** Ease of the prototype's reels: fast start, long soft landing. */
val ReelEasing = CubicBezierEasing(0.12f, 0.9f, 0.18f, 1f)

/** Reels are 30 digits tall (0-9 three times); a digit d rests at position 20 + d, the middle cycle. */
fun reelPosition(digit: Int): Float = 20f + digit

/**
 * A row of rolling digits, e.g. "412" or "05:12:33". Each digit is a reel driven by one
 * [Animatable] from [reels] (taken in order, starting at [firstReel]); other characters are
 * dimmed separators.
 */
@Composable
fun OdometerRow(
    text: String,
    reels: List<Animatable<Float, AnimationVector1D>>,
    firstReel: Int,
    style: TextStyle,
    separatorColor: Color,
    modifier: Modifier = Modifier
) {
    val cellStyle = style.copy(
        lineHeight = 1.em,
        lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.Both),
        fontFeatureSettings = "tnum"
    )
    val cell = with(LocalDensity.current) { style.fontSize.toDp() }
    var reel = firstReel
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        text.forEach { ch ->
            if (ch.isDigit()) {
                val animatable = reels.getOrNull(reel++)
                Box(Modifier.height(cell).clipToBounds()) {
                    val cellPx = with(LocalDensity.current) { cell.toPx() }
                    Column(
                        Modifier
                            .wrapContentHeight(align = Alignment.Top, unbounded = true)
                            .offset { IntOffset(0, -((animatable?.value ?: reelPosition(ch.digitToInt())) * cellPx).roundToInt()) }
                    ) {
                        repeat(30) { k ->
                            Box(Modifier.height(cell), contentAlignment = Alignment.Center) {
                                Text(text = (k % 10).toString(), style = cellStyle)
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = ch.toString(),
                    style = cellStyle.copy(color = separatorColor),
                    modifier = Modifier.padding(horizontal = with(LocalDensity.current) { (style.fontSize.value * 0.12f).toDp() })
                )
            }
        }
    }
}
