package com.global_water_polo.play_abroad.core.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.utils.clockText
import com.global_water_polo.play_abroad.domain.model.ClockCountdown
import com.global_water_polo.play_abroad.resources.Res
import com.global_water_polo.play_abroad.resources.clock_chip_running
import com.global_water_polo.play_abroad.resources.clock_chip_starts_in
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme
import com.global_water_polo.play_abroad.ui.theme.archivoExpandedBold

/** Dark pill with a pulsing red dot: the NCAA clock, carried on every step after the birthday. */
@Composable
fun ClockChip(
    countdown: ClockCountdown?,
    clockYear: Int?,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    if (countdown == null || clockYear == null) return
    val colors = GWPPATheme.colors
    val pulse = rememberInfiniteTransition()
    val ring by pulse.animateFloat(0f, 1f, infiniteRepeatable(tween(1400), RepeatMode.Restart))
    val startsIn = stringResource(Res.string.clock_chip_starts_in)
    val running = stringResource(Res.string.clock_chip_running, clockYear)
    val bold = archivoExpandedBold
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .background(colors.inkBlack, CircleShape)
            .padding(start = 12.dp, end = 14.dp, top = 8.dp, bottom = 8.dp)
    ) {
        Canvas(Modifier.size(8.dp)) {
            drawCircle(colors.liveRed.copy(alpha = 0.6f * (1f - ring)), radius = size.minDimension / 2f + 8.dp.toPx() * ring)
            drawCircle(colors.liveRed)
        }
        Text(
            text = if (countdown.running) {
                buildAnnotatedString { append(running) }
            } else {
                buildAnnotatedString {
                    append("$startsIn ")
                    withStyle(SpanStyle(fontFamily = bold)) { append(clockText(countdown)) }
                }
            },
            style = GWPPATheme.typography.archivoSemiBold12,
            color = colors.colorWhite,
            maxLines = 1
        )
    }
}
