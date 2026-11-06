package com.global_water_polo.play_abroad.core.components.globe

import com.global_water_polo.play_abroad.core.components.LoaderVisibility
import com.global_water_polo.play_abroad.presentation.opener.components.OpenerStage
import com.global_water_polo.play_abroad.presentation.opener.components.OpenerTimeline
import com.global_water_polo.play_abroad.presentation.opener.components.BallSpinMemory
import com.global_water_polo.play_abroad.core.components.model3d.BallModelView
import com.global_water_polo.play_abroad.core.components.model3d.BallFrame
import androidx.compose.runtime.withFrameNanos
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme
import kotlin.math.min

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
    routeMode: GlobeMode,
    ball3d: Boolean = false,
    content: @Composable () -> Unit
) {
    val mode = if (controller.revealOverride) GlobeMode.REVEAL else routeMode
    LaunchedEffect(controller) { controller.run() }
    LaunchedEffect(mode) { controller.setMode(mode) }
    val texture by produceState<ImageBitmap?>(null) {
        value = withContext(Dispatchers.Default) { runCatching { loadGlobeTexture() }.getOrNull() }
    }
    val night by animateFloatAsState(if (mode == GlobeMode.CLOCK || mode == GlobeMode.REVEAL || mode == GlobeMode.EXPLORE) 1f else 0f, tween(500))
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
                mode == GlobeMode.REVEAL || lastMode == GlobeMode.REVEAL || mode == GlobeMode.EXPLORE -> 1200
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
            if (!ball3d) texture?.let { tex ->
                val explore = mode == GlobeMode.EXPLORE
                SpinningGlobe(
                    texture = tex,
                    angle = controller.angle,
                    tilt = controller.tilt,
                    pins = if (mode == GlobeMode.REVEAL || explore) controller.pins else emptyList(),
                    markers = if (explore) controller.markers else null,
                    zoom = controller.zoom,
                    centerY = cy.value.dp,
                    diameter = (d.value * (1f + controller.punch * 0.18f) * (if (explore) controller.zoom else 1f)).dp,
                    onMarkersDrawn = { controller.markerHits = it }
                )
            }
        }
        if (ball3d) {
            // The opener's space, then one 3D ball for the opener and the first question. It stays put across
            // the hand-over: the opener moves it into the badge spot, then it idles on from the same turn.
            OpenerStage.backdrop?.invoke()
            var clock by remember { mutableFloatStateOf(0f) }
            LaunchedEffect(Unit) {
                val t0 = withFrameNanos { it }
                while (true) clock = withFrameNanos { (it - t0) / 1e9f }
            }
            val handOver = remember { object { var spin = BallSpinMemory.spin; var at = 0f } }
            BallModelView(
                frame = {
                    OpenerStage.frame?.invoke()?.also {
                        handOver.spin = it.spin
                        handOver.at = clock
                    } ?: BallFrame(
                        radius = d.value / 2f / h,
                        centerY = cy.value / h,
                        spin = handOver.spin + OpenerTimeline.IDLE_SPEED * (clock - handOver.at),
                        tilt = 0.14f,
                        visible = !LoaderVisibility.showing
                    )
                },
                modifier = Modifier.fillMaxSize()
            )
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

/** Centre y and diameter (dp) of the big intro ball on a [w] x [h] dp stage, so the intro can hit-test taps on it. */
/**
 * Globe+ ball size before zoom (the prototype's gBase), centred at 44% of the screen: zoomed out it sits on
 * the night sky between the filters and the lists; zoomed in it fills the screen behind them. GlobeScreenContent uses it for its drag radius.
 */
fun exploreGlobeDiameter(w: Float, h: Float): Float = min(w * 0.92f, h * 0.5f)

/** Centre y and diameter (dp) of the onboarding badge ball, under a status bar [top] dp tall (the 3D opener lands there). */
fun badgeGlobeCircle(w: Float, h: Float, top: Float): Pair<Float, Float> =
    layoutFor(GlobeMode.BADGE, w, h, top).let { it.cy to it.d }

fun introGlobeCircle(w: Float, h: Float): Pair<Float, Float> =
    layoutFor(GlobeMode.INTRO, w, h, 0f).let { it.cy to it.d }

private fun layoutFor(mode: GlobeMode, w: Float, h: Float, top: Float): GlobeLayout = when (mode) {
    // Lower and a little smaller than the other modes: the title and the two manifesto lines sit above it.
    GlobeMode.INTRO -> GlobeLayout(cy = h * 0.52f, d = min(min(w * 0.8f, h * 0.38f), 520f))
    GlobeMode.CLOCK -> min(min(w * 0.36f, h * 0.17f), 150f).let { GlobeLayout(cy = top + 14f + it / 2f, d = it) }
    // The tour and the world reveal: a big ball rising from the top of the screen, like the prototype's tweenBall.
    GlobeMode.REVEAL -> min(w * 0.9f, h * 0.46f).let { GlobeLayout(cy = top + 24f + it / 2f, d = it) }
    GlobeMode.EXPLORE -> GlobeLayout(cy = h * 0.44f, d = exploreGlobeDiameter(w, h))
    GlobeMode.BADGE, GlobeMode.HIDDEN -> min(min(w * 0.24f, h * 0.12f), 104f).let { GlobeLayout(cy = top + 16f + it / 2f, d = it) }
}

private fun insetFor(mode: GlobeMode, w: Float, h: Float, top: Float): Float = when (mode) {
    GlobeMode.INTRO, GlobeMode.HIDDEN, GlobeMode.REVEAL, GlobeMode.EXPLORE -> top
    GlobeMode.CLOCK -> layoutFor(mode, w, h, top).let { it.cy + it.d / 2f + 6f }
    GlobeMode.BADGE -> layoutFor(mode, w, h, top).let { it.cy + it.d / 2f + 10f }
}

@Composable
private fun SpinningGlobe(
    texture: ImageBitmap,
    angle: Float,
    tilt: Float,
    pins: List<GlobePin>,
    markers: GlobeMarkers?,
    zoom: Float,
    centerY: Dp,
    diameter: Dp,
    onMarkersDrawn: (List<Pair<Offset, GlobeHit>>) -> Unit
) {
    val measurer = rememberTextMeasurer()
    var origin by remember { mutableStateOf(Offset.Zero) }
    val mesh = remember(texture) { GlobeMesh(texture.width, texture.height) }
    val paint = remember(texture) {
        Paint().apply {
            shader = ImageShader(texture, TileMode.Clamp, TileMode.Clamp)
            isAntiAlias = true
            filterQuality = FilterQuality.High
        }
    }
    Canvas(Modifier.fillMaxSize().onGloballyPositioned { origin = it.positionInRoot() }) {
        val r = diameter.toPx() / 2f
        val c = Offset(size.width / 2f, centerY.toPx())
        // The textured ball, drawn by the GPU; a hair larger than the rim so the edge stays round.
        drawIntoCanvas { canvas ->
            canvas.drawVertices(mesh.vertices(angle, c.x, c.y, r * 1.002f, tilt), BlendMode.Modulate, paint)
        }
        // Lighting is fixed to the camera, so it is painted over the ball: key light from the upper
        // left, shade towards the lower right limb, the clear-coat highlight and a cool rim.
        drawCircle(
            brush = Brush.radialGradient(
                0f to Color.Transparent,
                0.45f to Color.Black.copy(alpha = 0.12f),
                0.8f to Color.Black.copy(alpha = 0.42f),
                1f to Color.Black.copy(alpha = 0.7f),
                center = Offset(c.x - r * 0.35f, c.y - r * 0.4f),
                radius = r * 1.75f
            ),
            radius = r,
            center = c
        )
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color.White.copy(alpha = 0.7f), Color.White.copy(alpha = 0.22f), Color.Transparent),
                center = Offset(c.x - r * 0.36f, c.y - r * 0.42f),
                radius = r * 0.36f
            ),
            radius = r,
            center = c
        )
        drawCircle(
            brush = Brush.radialGradient(
                0.82f to Color.Transparent,
                1f to Color(0x33CFE6FF),
                center = c,
                radius = r
            ),
            radius = r,
            center = c
        )
        drawCircle(Color.Black.copy(alpha = 0.08f), radius = r - 0.5f * density, center = c, style = Stroke(width = 1f * density))
        // Globe+: clusters, pins, club badges and labels by zoom; their spots go back for taps.
        if (markers != null) {
            val hits = drawGlobeMarkers(markers, zoom, { lat, lon -> mesh.project(lat, lon, angle, tilt, c.x, c.y, r) }, measurer)
            onMarkersDrawn(hits.map { (p, hit) -> (p + origin) to hit })
            return@Canvas
        }
        // Tour pins: club cities on the ball, the current one bigger with a ring.
        pins.forEach { pin ->
            val p = mesh.project(pin.latitude, pin.longitude, angle, tilt, c.x, c.y, r) ?: return@forEach
            val dot = (if (pin.active) 7f else 4f) * density
            if (pin.active) drawCircle(Color.White.copy(alpha = 0.85f), radius = dot * 2.2f, center = p, style = Stroke(width = 2f * density))
            drawCircle(Color(0xFF0B1117), radius = dot + 1.5f * density, center = p)
            drawCircle(Color(0xFFF5C400), radius = dot, center = p)
        }
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
