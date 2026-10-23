package com.global_water_polo.play_abroad.core.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme
import kotlin.math.roundToInt

/**
 * Plain slider: a thin track, the filled part in chlorine blue and a round dot as the head.
 * The dot follows the finger smoothly and reports whole values from [range]; it settles on the
 * nearest value when let go.
 */
@Composable
fun DotSlider(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GWPPATheme.colors
    val span = (range.last - range.first).coerceAtLeast(1)
    var dragFraction by remember { mutableStateOf<Float?>(null) }
    val currentValue by rememberUpdatedState(value)
    val change by rememberUpdatedState(onValueChange)
    val fraction = dragFraction ?: ((value - range.first).toFloat() / span)
    val dot = 11.dp
    fun valueAt(f: Float) = range.first + (f.coerceIn(0f, 1f) * span).roundToInt()

    Canvas(
        modifier
            .fillMaxWidth()
            .height(32.dp)
            .pointerInput(range) {
                detectTapGestures { offset ->
                    val inset = dot.toPx()
                    change(valueAt((offset.x - inset) / (size.width - 2 * inset)))
                }
            }
            .pointerInput(range) {
                detectHorizontalDragGestures(
                    onDragStart = { offset ->
                        val inset = dot.toPx()
                        dragFraction = ((offset.x - inset) / (size.width - 2 * inset)).coerceIn(0f, 1f)
                    },
                    onDragEnd = { dragFraction = null },
                    onDragCancel = { dragFraction = null },
                    onHorizontalDrag = { pointer, dx ->
                        pointer.consume()
                        val inset = dot.toPx()
                        val f = ((dragFraction ?: 0f) + dx / (size.width - 2 * inset)).coerceIn(0f, 1f)
                        dragFraction = f
                        val v = valueAt(f)
                        if (v != currentValue) change(v)
                    }
                )
            }
    ) {
        val inset = dot.toPx()
        val y = size.height / 2f
        val x = inset + (size.width - 2 * inset) * fraction
        drawLine(colors.lineGrey, Offset(inset, y), Offset(size.width - inset, y), strokeWidth = 4.dp.toPx(), cap = StrokeCap.Round)
        drawLine(colors.chlorineBlue, Offset(inset, y), Offset(x, y), strokeWidth = 4.dp.toPx(), cap = StrokeCap.Round)
        drawCircle(colors.chlorineBlue, radius = inset, center = Offset(x, y))
        drawCircle(colors.colorWhite, radius = inset * 0.36f, center = Offset(x, y))
    }
}
