package com.global_water_polo.play_abroad.presentation.app.travel.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.global_water_polo.play_abroad.domain.model.WaterPoloMap
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.min

/**
 * The prototype's `.radar`: a flat world grid with every water polo city as a dot, an arc from the home
 * airport to the destination and a plane flying it. The view zooms to fit the route.
 */
@Composable
fun RouteMap(
    from: Pair<Float, Float>,
    to: Pair<Float, Float>,
    fromLabel: String,
    toLabel: String,
    caption: String,
    title: String,
    sub: String,
    modifier: Modifier = Modifier
) {
    val colors = GWPPATheme.colors
    val t by rememberInfiniteTransition().animateFloat(0f, 1f, infiniteRepeatable(tween(6000, easing = LinearEasing), RepeatMode.Restart))
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.6f)
            .clip(RoundedCornerShape(22.dp))
            .background(colors.nightNavy)
    ) {
        Canvas(Modifier.fillMaxSize()) {
            // World on a 360 x 180 plane, cropped around the route.
            fun xy(lat: Float, lon: Float) = Offset(lon + 180f, 90f - lat)
            val a = xy(from.first, from.second)
            val b = xy(to.first, to.second)
            val spanX = max(110f, abs(b.x - a.x) + 90f)
            val spanY = spanX / 1.6f
            val cx = (a.x + b.x) / 2f
            val cy = min(180f - spanY / 2f, max(spanY / 2f, (a.y + b.y) / 2f - spanY * 0.08f))
            val k = size.width / spanX
            fun px(p: Offset) = Offset((p.x - (cx - spanX / 2f)) * k, (p.y - (cy - spanY / 2f)) * k)
            for (lon in -180..180 step 30) {
                val x = px(Offset(lon + 180f, 0f)).x
                drawLine(Color.White.copy(alpha = 0.06f), Offset(x, 0f), Offset(x, size.height), 1f)
            }
            for (lat in -90..90 step 30) {
                val y = px(Offset(0f, 90f - lat)).y
                drawLine(Color.White.copy(alpha = 0.06f), Offset(0f, y), Offset(size.width, y), 1f)
            }
            WaterPoloMap.cities.forEach { c -> drawCircle(Color.White.copy(alpha = 0.35f), radius = 2.2f * density, center = px(xy(c.latitude, c.longitude))) }
            val p1 = px(a)
            val p2 = px(b)
            val lift = abs(p2.x - p1.x) * 0.28f
            val ctrl = Offset((p1.x + p2.x) / 2f, min(p1.y, p2.y) - lift)
            val arc = Path().apply {
                moveTo(p1.x, p1.y)
                quadraticTo(ctrl.x, ctrl.y, p2.x, p2.y)
            }
            drawPath(arc, colors.ballYellow, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
            drawCircle(colors.colorWhite, radius = 4.dp.toPx(), center = p1)
            drawCircle(colors.ballYellow, radius = 4.dp.toPx(), center = p2)
            drawCircle(colors.ballYellow.copy(alpha = 0.4f * (1f - t)), radius = (4f + 14f * t).dp.toPx(), center = p2, style = Stroke(2.dp.toPx()))
            // The plane flies the arc, nose along the path.
            val measure = PathMeasure().apply { setPath(arc, false) }
            val at = measure.getPosition(measure.length * t)
            val tangent = measure.getTangent(measure.length * t)
            val angle = atan2(tangent.y, tangent.x) * 180f / PI.toFloat()
            rotate(angle, pivot = at) {
                val s = 6.dp.toPx()
                val plane = Path().apply {
                    moveTo(at.x + s, at.y)
                    lineTo(at.x - s, at.y - s * 0.55f)
                    lineTo(at.x - s * 0.55f, at.y)
                    lineTo(at.x - s, at.y + s * 0.55f)
                    close()
                }
                drawPath(plane, colors.colorWhite)
            }
        }
        Column(Modifier.align(Alignment.BottomStart).padding(14.dp)) {
            Text(caption.uppercase(), style = GWPPATheme.typography.archivoBold11, color = colors.eliteGold)
            Text("$fromLabel → $toLabel", style = GWPPATheme.typography.archivoExpandedBold21, color = colors.colorWhite)
            Text("$title  $sub", style = GWPPATheme.typography.archivoRegular13, color = colors.colorWhite.copy(alpha = 0.75f))
        }
    }
}
