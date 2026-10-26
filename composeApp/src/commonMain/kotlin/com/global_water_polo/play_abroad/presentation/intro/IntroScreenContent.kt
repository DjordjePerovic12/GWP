package com.global_water_polo.play_abroad.presentation.intro

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.AnnotatedString
import com.global_water_polo.play_abroad.core.components.globe.introGlobeCircle
import com.global_water_polo.play_abroad.resources.intro_all_zones
import com.global_water_polo.play_abroad.resources.intro_tap_ball
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.TextLink
import com.global_water_polo.play_abroad.core.utils.TimeZoneLabels
import com.global_water_polo.play_abroad.presentation.intro.components.SwipeHint
import com.global_water_polo.play_abroad.presentation.intro.components.TimeZoneRuler
import com.global_water_polo.play_abroad.resources.Res
import com.global_water_polo.play_abroad.resources.brand_short
import com.global_water_polo.play_abroad.resources.gwp_logo
import com.global_water_polo.play_abroad.resources.haptics_off
import com.global_water_polo.play_abroad.resources.haptics_on
import com.global_water_polo.play_abroad.resources.intro_global
import com.global_water_polo.play_abroad.resources.intro_skip
import com.global_water_polo.play_abroad.resources.intro_swipe_right
import com.global_water_polo.play_abroad.resources.intro_water_polo
import com.global_water_polo.play_abroad.resources.intro_zones_suffix
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme
import kotlin.math.min

@Composable
fun IntroScreenContent(
    modifier: Modifier = Modifier,
    state: IntroState,
    angle: () -> Float,
    pulse: () -> Float,
    hapticsOn: Boolean,
    onHapticsClick: () -> Unit,
    onDragStart: () -> Unit,
    onDrag: (Float) -> Unit,
    onDragEnd: (Float) -> Unit,
    onSkipClick: () -> Unit,
    onBallClick: () -> Unit
) {
    val colors = GWPPATheme.colors
    val typography = GWPPATheme.typography
    val exit by animateFloatAsState(if (state.finished) 1f else 0f, tween(500))
    // A swipe that starts on the ball must never count as a tap on it.
    val gesture = remember { object { var dragged = false } }
    val longitude by remember { derivedStateOf { TimeZoneLabels.lonLabel(TimeZoneLabels.facing(angle())) } }
    val zone by remember { derivedStateOf { TimeZoneLabels.tzLabel(TimeZoneLabels.facing(angle())) } }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                // 220° of turn per screen width (capped at 700dp), fling with the finger's speed.
                val degreesPerPx = 220f / min(size.width / density, 700f) / density
                val tracker = VelocityTracker()
                detectHorizontalDragGestures(
                    onDragStart = {
                        gesture.dragged = true
                        tracker.resetTracking()
                        onDragStart()
                    },
                    onDragEnd = { onDragEnd(tracker.calculateVelocity().x * degreesPerPx / 1000f) },
                    onDragCancel = { onDragEnd(0f) },
                    onHorizontalDrag = { change, dx ->
                        tracker.addPosition(change.uptimeMillis, change.position)
                        onDrag(dx * degreesPerPx)
                    }
                )
            }
            .pointerInput(Unit) {
                // Once every zone is spun, the ball itself is the way in.
                detectTapGestures(onPress = { gesture.dragged = false }) { tap ->
                    if (gesture.dragged) return@detectTapGestures
                    val (cy, d) = introGlobeCircle(size.width / density, size.height / density)
                    val dx = tap.x / density - size.width / density / 2f
                    val dy = tap.y / density - cy
                    if (dx * dx + dy * dy <= (d / 2f) * (d / 2f)) onBallClick()
                }
            }
    ) {
        if (state.spunAll && !state.finished) TapRing(width = maxWidth.value, height = maxHeight.value)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 18.dp)
                .graphicsLayer {
                    alpha = 1f - exit
                    scaleX = 1f + 0.04f * exit
                    scaleY = 1f + 0.04f * exit
                }
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Image(painter = painterResource(Res.drawable.gwp_logo), contentDescription = null, modifier = Modifier.size(26.dp))
                    Text(text = stringResource(Res.string.brand_short), style = typography.archivoExpandedBold12, color = colors.inkBlack)
                }
                Text(
                    text = stringResource(if (hapticsOn) Res.string.haptics_on else Res.string.haptics_off),
                    style = typography.archivoSemiBold12,
                    color = if (hapticsOn) colors.inkBlack else colors.mistGrey,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(colors.colorWhite.copy(alpha = 0.7f))
                        .border(1.dp, colors.lineGrey, CircleShape)
                        .clickable(onClick = onHapticsClick)
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                )
            }
            Text(
                text = stringResource(Res.string.intro_global).uppercase(),
                style = typography.archivoExpandedExtraBold50,
                color = colors.inkBlack,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 22.dp)
            )
            Text(
                text = stringResource(Res.string.intro_water_polo).uppercase(),
                style = typography.archivoExpandedSemiBold21,
                color = colors.slateGrey,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 10.dp)
            )
            Spacer(Modifier.weight(1f))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = longitude, style = typography.archivoExpandedBold16, color = colors.inkBlack)
                    Text(text = zone, style = typography.archivoSemiBold13, color = colors.slateGrey)
                }
                TimeZoneRuler(angle = angle, pulse = pulse)
                Box(
                    Modifier
                        .width(min(240f, this@BoxWithConstraints.maxWidth.value * 0.7f).dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(colors.inkBlack.copy(alpha = 0.1f))
                ) {
                    Box(
                        Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(state.progress)
                            .background(colors.chlorineBlue)
                    )
                }
                SwipeHint(
                    text = stringResource(if (state.spunAll) Res.string.intro_tap_ball else Res.string.intro_swipe_right),
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = if (state.spunAll) AnnotatedString(stringResource(Res.string.intro_all_zones, IntroState.ZONES)) else buildAnnotatedString {
                        withStyle(SpanStyle(color = colors.inkBlack, fontFamily = typography.archivoBold13.fontFamily)) {
                            append(state.zonesCrossed.toString())
                        }
                        append(stringResource(Res.string.intro_zones_suffix, IntroState.ZONES))
                    },
                    style = typography.archivoRegular14,
                    color = colors.slateGrey,
                    textAlign = TextAlign.Center
                )
                TextLink(text = stringResource(Res.string.intro_skip), onClick = onSkipClick)
            }
        }
    }
}

/** A ring breathing out of the ball while it waits to be tapped. */
@Composable
private fun TapRing(width: Float, height: Float) {
    val color = GWPPATheme.colors.chlorineBlue
    val ring by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1400, easing = LinearEasing), RepeatMode.Restart)
    )
    Canvas(Modifier.fillMaxSize()) {
        val (cy, d) = introGlobeCircle(width, height)
        val r = d.dp.toPx() / 2f
        drawCircle(
            color = color.copy(alpha = 0.55f * (1f - ring)),
            radius = r * (1.02f + 0.16f * ring),
            center = Offset(size.width / 2f, cy.dp.toPx()),
            style = Stroke(width = 3.dp.toPx())
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun IntroScreenContentPreview() {
    GWPPATheme {
        IntroScreenContent(
            state = IntroState(progressDegrees = 120f, zonesCrossed = 8),
            angle = { -50f },
            pulse = { 0f },
            hapticsOn = true,
            onHapticsClick = {},
            onDragStart = {},
            onDrag = {},
            onDragEnd = {},
            onSkipClick = {},
            onBallClick = {}
        )
    }
}
