package com.global_water_polo.play_abroad.presentation.opener.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.global_water_polo.play_abroad.core.components.model3d.BALL_FRONT_SPIN
import com.global_water_polo.play_abroad.core.components.model3d.BALL_FRONT_TILT
import com.global_water_polo.play_abroad.core.components.model3d.BallFrame
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sin

/**
 * The 3D opener's choreography (the reference's opener/index.html), in seconds:
 *
 * - 0 to 3.4: the camera flies in from deep space to the ball, most of the way early, a long soft arrival,
 *   while the ball turns about 400 degrees and slows to land front-on (GWP logo) at 3.9.
 * - 3.39 "Welcome to Global Water Polo" above, 3.79 the tagline below, 4.19 "Begin Your Journey".
 * - After the tap ([u] seconds later): the ball spins up and back down, then rises and shrinks into the
 *   onboarding badge from 1.75 to 3.1 while ivory spreads out from behind it from 2.0 to 2.9.
 *
 * Sizes are in dp on a [width] x [height] screen; [badgeCy] / [badgeD] is where the ball lands on the first
 * onboarding screen.
 */
class OpenerTimeline(
    private val width: Float,
    private val height: Float,
    private val badgeCy: Float,
    private val badgeD: Float
) {
    /** The hero ball: 60 % of the screen width across, the welcome, tagline and button grouped around it. */
    val holdRadius = width * 0.30f
    private val groupHeight = WEL_H + GAP_W + holdRadius * 2 + GAP_1 + TXT_H + GAP_2 + BTN_H
    val holdCy = (height - groupHeight) / 2f + WEL_H + GAP_W + holdRadius + 14f
    val welcomeTop = holdCy - holdRadius - GAP_W - WEL_H
    val taglineTop = holdCy + holdRadius + GAP_1
    val buttonTop = taglineTop + TXT_H + GAP_2

    /** Ball centre and radius (dp) at [t] seconds, or [u] seconds after the tap. */
    fun ballCy(t: Float, u: Float?): Float {
        val held = lerp(height * 0.5f, holdCy, seg(t, 0.1f, 2.4f, ::io2))
        return if (u == null) held else lerp(held, badgeCy, travel(u))
    }

    fun ballRadius(t: Float, u: Float?): Float {
        val e = approachEase((t / APPROACH).coerceIn(0f, 1f))
        val held = logLerp(holdRadius * 0.012f, holdRadius, e)
        return if (u == null) held else logLerp(held, badgeD / 2f, travel(u))
    }

    fun spin(t: Float, u: Float?): Float = BALL_FRONT_SPIN + spinAuto(t) + (u?.let(::spinPost) ?: 0f)

    /** The lean: a little more from far away, settling on the logo square-on. */
    fun tilt(t: Float): Float = lerp(0.42f, BALL_FRONT_TILT, seg(t, 0f, 3.4f, ::io2))

    fun frame(t: Float, u: Float?) = BallFrame(
        radius = ballRadius(t, u) / height,
        centerY = ballCy(t, u) / height,
        spin = spin(t, u),
        tilt = tilt(t)
    )

    private fun travel(u: Float) = seg(u, 1.75f, POST_END, ::io2)

    // Texts and space, all 0..1.
    fun welcome(t: Float, u: Float?) = seg(t, 3.39f, 3.99f, ::o2) * out(u)
    fun tagline(t: Float, u: Float?) = seg(t, 3.79f, 4.39f, ::o2) * out(u)
    fun button(t: Float, u: Float?) = seg(t, 4.19f, 4.79f, ::o2) * out(u)
    fun skip(t: Float, u: Float?) = if (u != null) 0f else 1f - seg(t, 4.19f, 4.79f, ::lin)
    fun ivory(u: Float?) = if (u == null) 0f else seg(u, 2.0f, 2.9f, ::io2)
    fun stars(t: Float, u: Float?) = lerp(0.8f, 0.38f, seg(t, 1.6f, 3.0f, ::lin)) * (1f - ivory(u))
    fun deepSpace(t: Float) = 1f - seg(t, 1.3f, 2.3f, ::lin)
    fun halo(t: Float, u: Float?) = seg(t, 2.2f, 3.2f, ::lin) * (1f - ivory(u))
    fun petrol(t: Float) = seg(t, 1.8f, 3.0f, ::lin)
    fun done(u: Float?) = u != null && u >= POST_END
    private fun out(u: Float?) = if (u == null) 1f else 1f - seg(u, 0f, 0.3f, ::o2)

    // One continuous decelerating turn landing front-on at 3.9 s, then a drift of a few degrees.
    private fun spinAuto(t: Float): Float =
        -SPIN_A * (1f - o2((t / SPIN_T1).coerceIn(0f, 1f))) +
            0.045f * sin(2f * PI.toFloat() * (t - SPIN_T1) / 7f) * seg(t, SPIN_T1, SPIN_T1 + 1.3f, ::io2)

    // After the tap: angular speed rises, holds, slows to a turn, then to an idle (one turn per 30 s).
    private fun omega(u: Float): Float = when {
        u < 0.08f -> 0f
        u < 0.6f -> W_MAX * i2((u - 0.08f) / 0.52f)
        u < 0.85f -> W_MAX
        u < 1.25f -> lerp(W_MAX, W_ROT, io2((u - 0.85f) / 0.4f))
        u < 1.75f -> W_ROT
        u < POST_END -> lerp(W_ROT, W_IDLE, io2((u - 1.75f) / (POST_END - 1.75f)))
        else -> W_IDLE
    }

    private val spinTable: FloatArray = FloatArray((4f / SPIN_DT).toInt() + 1).also { tab ->
        var a = 0f
        for (i in 1 until tab.size) {
            a += omega((i - 0.5f) * SPIN_DT) * SPIN_DT
            tab[i] = a
        }
    }

    private fun spinPost(u: Float): Float {
        if (u <= 0f) return 0f
        val i = u / SPIN_DT
        val k = i.toInt()
        return if (k < spinTable.size - 1) lerp(spinTable[k], spinTable[k + 1], i - k)
        else spinTable.last() + W_IDLE * (u - (spinTable.size - 1) * SPIN_DT)
    }

    companion object {
        const val APPROACH = 3.4f
        const val AUTO_END = 4.8f
        const val POST_END = 3.1f
        private const val SPIN_A = 7.0f
        private const val SPIN_T1 = 3.9f
        private const val SPIN_DT = 1f / 240f
        private const val W_MAX = 6.5f
        private const val W_ROT = 1.6f
        private val W_IDLE = 2f * PI.toFloat() / 30f
        private const val WEL_H = 58f
        private const val GAP_W = 32f
        private const val GAP_1 = 32f
        const val TXT_H = 62f
        private const val GAP_2 = 36f
        const val BTN_H = 58f

        /** Idle turn speed of the ball once it sits on onboarding (radians per second). */
        val IDLE_SPEED = W_IDLE
    }
}

/** Where the ball's turn ended, so the first onboarding screen's ball carries on from the same face. */
object BallSpinMemory {
    var spin = BALL_FRONT_SPIN
}

/**
 * What the opener puts on the app's stage (GlobeStage) while it runs: its space backdrop and where the ball
 * is each frame. The stage keeps one 3D ball for the opener and the first question, so the hand-over moves
 * the same, already loaded ball instead of swapping views (which blinked on Android while the new one loaded).
 */
object OpenerStage {
    var backdrop: (@Composable () -> Unit)? by mutableStateOf(null)
    var frame: (() -> BallFrame)? by mutableStateOf(null)
}

private fun lerp(a: Float, b: Float, t: Float) = a + (b - a) * t
private fun logLerp(a: Float, b: Float, t: Float) = exp(lerp(ln(a), ln(b), t))
private fun lin(x: Float) = x
private fun o2(x: Float) = 1f - (1f - x) * (1f - x)
private fun i2(x: Float) = x * x
private fun io2(x: Float) = if (x < 0.5f) 2f * x * x else 1f - (-2f * x + 2f).pow(2) / 2f
private fun approachEase(s: Float) = (1f - (1f - s).pow(3)) * (if (s < 0.12f) io2(s / 0.12f) else 1f)
private fun seg(t: Float, a: Float, b: Float, ease: (Float) -> Float) = ease(((t - a) / (b - a)).coerceIn(0f, 1f))
