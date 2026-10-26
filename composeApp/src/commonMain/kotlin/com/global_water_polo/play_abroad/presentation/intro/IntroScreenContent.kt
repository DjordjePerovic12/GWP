package com.global_water_polo.play_abroad.presentation.intro

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.TextLink
import com.global_water_polo.play_abroad.core.components.globe.introGlobeCircle
import com.global_water_polo.play_abroad.core.utils.TimeZoneLabels
import com.global_water_polo.play_abroad.presentation.intro.components.SwipeHint
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/** The manifesto, one line every few zones, then the four ways in (the prototype's data-z reveal points). */
private val MANIFESTO: List<Pair<StringResource, Int>> = listOf(
    Res.string.intro_line_1 to 2,
    Res.string.intro_line_2 to 6,
    Res.string.intro_line_3 to 10,
    Res.string.intro_line_4 to 14
)
private val ROLES: List<Pair<StringResource, Int>> = listOf(
    Res.string.intro_role_athletes to 17,
    Res.string.intro_role_parents to 19,
    Res.string.intro_role_coaches to 21,
    Res.string.intro_role_clubs to 23
)
private val rise = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1f)

@Composable
fun IntroScreenContent(
    modifier: Modifier = Modifier,
    state: IntroState,
    angle: () -> Float,
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
    val longitude by remember { derivedStateOf { TimeZoneLabels.lonLabel(TimeZoneLabels.facing(angle())) } }
    val zone by remember { derivedStateOf { TimeZoneLabels.tzLabel(TimeZoneLabels.facing(angle())) } }
    // A swipe that starts on the ball must never count as a tap on it; while the spring runs, hands off.
    val gesture = remember { object { var dragged = false } }
    // The swipe detector stays on even then, so a swipe is never mistaken for a tap.
    val handsOff by rememberUpdatedState(state.running || state.spunAll)

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
                        if (!handsOff) onDragStart()
                    },
                    onDragEnd = { if (!handsOff) onDragEnd(tracker.calculateVelocity().x * degreesPerPx / 1000f) },
                    onDragCancel = { if (!handsOff) onDragEnd(0f) },
                    onHorizontalDrag = { change, dx ->
                        tracker.addPosition(change.uptimeMillis, change.position)
                        if (!handsOff) onDrag(dx * degreesPerPx)
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
        Bezel(zones = state.zonesCrossed, width = maxWidth.value, height = maxHeight.value, alpha = 1f - exit)
        if (state.spunAll && !state.finished) TapRing(width = maxWidth.value, height = maxHeight.value)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(start = 22.dp, end = 22.dp, top = 12.dp, bottom = 18.dp)
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
                    Text(text = stringResource(Res.string.brand_long), style = typography.archivoExpandedBold12, color = colors.inkBlack)
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
            Column(modifier = Modifier.padding(top = 18.dp)) {
                MANIFESTO.forEach { (line, at) -> ManifestoLine(text = stringResource(line), visible = state.zonesCrossed >= at) }
            }
            Spacer(Modifier.weight(1f))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                Box(Modifier.fillMaxWidth().height(1.dp).background(colors.inkBlack.copy(alpha = 0.18f)))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ROLES.forEach { (role, at) -> RoleWord(text = stringResource(role), lit = state.zonesCrossed >= at) }
                }
                Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxWidth().padding(top = 6.dp)) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontFamily = typography.archivoExpandedBold16.fontFamily, color = colors.inkBlack)) { append(longitude) }
                            append("  ")
                            append(zone)
                        },
                        style = typography.archivoSemiBold13,
                        color = colors.slateGrey,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = colors.inkBlack, fontFamily = typography.archivoExpandedBold16.fontFamily)) {
                                append(state.zonesCrossed.toString().padStart(2, '0'))
                            }
                            append(stringResource(Res.string.intro_zones_of, IntroState.ZONES))
                        },
                        style = typography.archivoSemiBold13,
                        color = colors.slateGrey
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    SwipeHint(
                        text = stringResource(if (state.spunAll) Res.string.intro_tap_ball else Res.string.intro_wind_it_up),
                        modifier = Modifier
                            .weight(1f)
                            .graphicsLayer { alpha = if (state.running && !state.spunAll) 0.35f else 1f }
                    )
                    if (!state.spunAll) TextLink(text = stringResource(Res.string.intro_skip), onClick = onSkipClick)
                }
            }
        }
    }
}

/** One manifesto line rising out of its own mask, like the prototype's `.wnd-l`. */
@Composable
private fun ManifestoLine(text: String, visible: Boolean) {
    Box(Modifier.clip(androidx.compose.ui.graphics.RectangleShape)) {
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(tween(380, easing = rise)) { it } + fadeIn(tween(120))
        ) {
            Text(
                text = text.uppercase(),
                style = GWPPATheme.typography.archivoExpandedExtraBold32.copy(lineHeight = 1.0.em, letterSpacing = (-0.02).em),
                color = GWPPATheme.colors.inkBlack
            )
        }
        // Keeps the line's height while it is still hidden, so nothing jumps.
        if (!visible) {
            Text(
                text = text.uppercase(),
                style = GWPPATheme.typography.archivoExpandedExtraBold32.copy(lineHeight = 1.0.em),
                color = GWPPATheme.colors.inkBlack.copy(alpha = 0f)
            )
        }
    }
}

@Composable
private fun RoleWord(text: String, lit: Boolean) {
    val colors = GWPPATheme.colors
    val color by animateColorAsState(if (lit) colors.eliteGold else colors.inkBlack.copy(alpha = 0.16f), tween(200))
    Text(text = text.uppercase(), style = GWPPATheme.typography.archivoExpandedBold12, color = color)
}

/** 24 ticks around the ball, one per time zone; wound zones light up gold (the prototype's `.bez`). */
@Composable
private fun Bezel(zones: Int, width: Float, height: Float, alpha: Float) {
    val colors = GWPPATheme.colors
    Canvas(Modifier.fillMaxSize().graphicsLayer { this.alpha = alpha }) {
        val (cy, d) = introGlobeCircle(width, height)
        val outer = (d / 2f + 16f).dp.toPx()
        val c = Offset(size.width / 2f, cy.dp.toPx())
        for (i in 0 until 24) {
            val a = i / 24f * 2f * PI.toFloat() - PI.toFloat() / 2f
            val r1 = outer * (if (i % 6 == 0) 43f else 45.5f) / 50f
            val r2 = outer * 49f / 50f
            val on = i < zones
            drawLine(
                color = if (on) colors.eliteGold else colors.inkBlack.copy(alpha = 0.22f),
                start = Offset(c.x + cos(a) * r1, c.y + sin(a) * r1),
                end = Offset(c.x + cos(a) * r2, c.y + sin(a) * r2),
                strokeWidth = (if (on) 2.4f else 1.6f).dp.toPx(),
                cap = StrokeCap.Round
            )
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
            state = IntroState(progressDegrees = 160f, zonesCrossed = 10),
            angle = { -50f },
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
