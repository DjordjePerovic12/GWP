package com.global_water_polo.play_abroad.presentation.onboarding.free.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

enum class SegmentKind {
    GRADUATE,
    FREE,
    CLOCK
}

data class TimelineSegment(val kind: SegmentKind, val title: String, val sub: String, val weight: Float)

/** The prototype's `.tline`: graduate (ink), the free year (ball stripes), clock starts (red). */
@Composable
fun FreeTimeline(segments: List<TimelineSegment>, modifier: Modifier = Modifier) {
    val colors = GWPPATheme.colors
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = modifier.fillMaxWidth()) {
        segments.forEach { segment ->
            Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.weight(segment.weight)) {
                when (segment.kind) {
                    SegmentKind.GRADUATE -> Bar(colors.inkBlack)
                    SegmentKind.CLOCK -> Bar(colors.liveRed)
                    SegmentKind.FREE -> StripedBar(colors.ballYellow, colors.chlorineBlue)
                }
                Text(text = segment.title, style = GWPPATheme.typography.archivoBold13, color = colors.inkBlack)
                Text(text = segment.sub, style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey)
            }
        }
    }
}

@Composable
private fun Bar(color: Color) {
    Column(
        Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(CircleShape)
            .background(color)
    ) {}
}

@Composable
private fun StripedBar(stripe: Color, gap: Color) {
    Canvas(
        Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(CircleShape)
    ) {
        drawRect(gap)
        val step = 12.dp.toPx()
        var x = 0f
        while (x < size.width) {
            drawRect(stripe, topLeft = androidx.compose.ui.geometry.Offset(x, 0f), size = androidx.compose.ui.geometry.Size(step * 0.6f, size.height))
            x += step
        }
    }
}
