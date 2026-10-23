package com.global_water_polo.play_abroad.core.components.globe

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.runtime.withFrameMillis
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme
import kotlin.math.min
import kotlin.math.roundToInt

/** Space a screen leaves above its content so the globe badge (or clock ball) never covers it. */
val LocalGlobeTopInset = staticCompositionLocalOf { 0.dp }

private val easeOut = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1f)

/**
 * App-wide stage behind every screen: the pool, the globe and the burst rings. The globe moves
 * between modes the way the prototype does: big in the middle on the intro, a small badge on top
 * of onboarding steps, a bigger spinning ball over the night-time clock, gone on the app screens.
 */
@Composable
fun GlobeStage(
    controller: GlobeController,
    mode: GlobeMode,
    content: @Composable () -> Unit
) {
    LaunchedEffect(controller) { controller.run() }
    LaunchedEffect(mode) { controller.setMode(mode) }
    val texture by produceState<GlobeTexture?>(null) {
        value = withContext(Dispatchers.Default) { runCatching { loadGlobeTexture() }.getOrNull() }
    }
    val night by animateFloatAsState(if (mode == GlobeMode.CLOCK) 1f else 0f, tween(500))
    val stageAlpha by animateFloatAsState(if (mode == GlobeMode.HIDDEN) 0f else 1f, tween(500))
    val statusTop = with(LocalDensity.current) { WindowInsets.statusBars.getTop(this).toDp() }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val w = maxWidth.value
        val h = maxHeight.value
        val target = layoutFor(mode, w, h, statusTop.value)
        val cy = remember { Animatable(target.cy) }
        val d = remember { Animatable(target.d) }
        var lastMode by remember { mutableStateOf(mode) }
        LaunchedEffect(target) {
            val duration = when {
                mode == GlobeMode.CLOCK -> 1000
                lastMode == GlobeMode.CLOCK -> 800
                lastMode == GlobeMode.INTRO -> 1100
                else -> 900
            }
            lastMode = mode
            launch { cy.animateTo(target.cy, tween(duration, easing = easeOut)) }
            launch { d.animateTo(target.d, tween(duration, easing = easeOut)) }
        }

        val bone = GWPPATheme.colors.boneWhite
        Canvas(Modifier.fillMaxSize()) { drawRect(bone) }
        Box(Modifier.fillMaxSize().alpha(stageAlpha)) {
            PoolBackground(night = night, modifier = Modifier.fillMaxSize())
            if (mode == GlobeMode.INTRO) ContactShadow(cy.value, d.value)
            BurstRings(controller.burst, cy.value)
            texture?.let { tex ->
                SpinningGlobe(
                    texture = tex,
                    angle = controller.angle,
                    centerY = cy.value.dp,
                    diameter = (d.value * (1f + controller.punch * 0.18f)).dp
                )
            }
        }
        CompositionLocalProvider(
            LocalGlobeController provides controller,
            LocalGlobeTopInset provides insetFor(mode, w, h, statusTop.value).dp
        ) {
            content()
        }
    }
}

private data class GlobeLayout(val cy: Float, val d: Float)

private fun layoutFor(mode: GlobeMode, w: Float, h: Float, top: Float): GlobeLayout = when (mode) {
    GlobeMode.INTRO -> GlobeLayout(cy = h * 0.485f, d = min(min(w * 0.84f, h * 0.45f), 560f))
    GlobeMode.CLOCK -> min(min(w * 0.36f, h * 0.17f), 150f).let { GlobeLayout(cy = top + 14f + it / 2f, d = it) }
    GlobeMode.BADGE, GlobeMode.HIDDEN -> min(min(w * 0.24f, h * 0.12f), 104f).let { GlobeLayout(cy = top + 16f + it / 2f, d = it) }
}

private fun insetFor(mode: GlobeMode, w: Float, h: Float, top: Float): Float = when (mode) {
    GlobeMode.INTRO, GlobeMode.HIDDEN -> top
    GlobeMode.CLOCK -> layoutFor(mode, w, h, top).let { it.cy + it.d / 2f + 6f }
    GlobeMode.BADGE -> layoutFor(mode, w, h, top).let { it.cy + it.d / 2f + 10f }
}

@Composable
private fun SpinningGlobe(
    texture: GlobeTexture,
    angle: Float,
    centerY: Dp,
    diameter: Dp
) {
    val density = LocalDensity.current
    // Two fixed render sizes (the big intro ball, the small badge/clock ball), each built once,
    // capped so a frame stays a few milliseconds of work.
    val big = with(density) { min(360.dp.roundToPx(), 400) }
    val small = with(density) { min(150.dp.roundToPx(), 300) }
    val useBig = with(density) { diameter.toPx() > small * 1.15f }
    val renderer = remember(texture, useBig) { GlobeRenderer(texture, if (useBig) big else small) }
    val currentAngle by rememberUpdatedState(angle)
    var frame by remember(renderer) { mutableStateOf<ImageBitmap?>(null) }
    // Pixels are computed on a background thread, one frame at a time; the UI thread only draws the result.
    LaunchedEffect(renderer) {
        var drawn = Float.NaN
        while (true) {
            withFrameMillis { }
            val a = currentAngle
            if (a != drawn) {
                frame = withContext(Dispatchers.Default) { renderer.render(a) }
                drawn = a
            }
        }
    }
    Canvas(Modifier.fillMaxSize()) {
        val image = frame ?: return@Canvas
        val dPx = diameter.toPx()
        drawImage(
            image = image,
            srcOffset = IntOffset.Zero,
            srcSize = IntSize(renderer.size, renderer.size),
            dstOffset = IntOffset(((size.width - dPx) / 2f).roundToInt(), (centerY.toPx() - dPx / 2f).roundToInt()),
            dstSize = IntSize(dPx.roundToInt(), dPx.roundToInt()),
            filterQuality = FilterQuality.High
        )
    }
}

@Composable
private fun ContactShadow(cy: Float, d: Float) {
    Canvas(Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f + 0.04f * d * density, (cy + d * 0.56f).dp.toPx())
        val rx = d * 0.46f * density
        val ry = d * 0.07f * density
        drawOval(
            brush = Brush.radialGradient(
                listOf(Color(0x520B1E32), Color(0x140B1E32), Color.Transparent),
                center = center,
                radius = rx
            ),
            topLeft = Offset(center.x - rx, center.y - ry),
            size = Size(rx * 2f, ry * 2f)
        )
    }
}

/** Two rings that shoot out of the ball when the intro is done. */
@Composable
private fun BurstRings(trigger: Int, cy: Float) {
    if (trigger == 0) return
    val ink = GWPPATheme.colors.inkBlack
    val blue = GWPPATheme.colors.chlorineBlue
    val first = remember(trigger) { Animatable(0f) }
    val second = remember(trigger) { Animatable(0f) }
    LaunchedEffect(trigger) {
        launch { first.animateTo(1f, tween(900, easing = CubicBezierEasing(0.15f, 0.7f, 0.3f, 1f))) }
        launch {
            delay(120)
            second.animateTo(1f, tween(900, easing = CubicBezierEasing(0.15f, 0.7f, 0.3f, 1f)))
        }
    }
    Canvas(Modifier.fillMaxSize()) {
        val c = Offset(size.width / 2f, cy.dp.toPx())
        listOf(first.value to ink, second.value to blue).forEach { (t, color) ->
            if (t in 0.001f..0.999f) {
                drawCircle(
                    color = color.copy(alpha = 0.7f * (1f - t)),
                    radius = 5.dp.toPx() * (1f + 89f * t),
                    center = c,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }
    }
}
