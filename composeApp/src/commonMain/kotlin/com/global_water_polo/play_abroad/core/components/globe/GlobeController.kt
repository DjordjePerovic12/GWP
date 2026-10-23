package com.global_water_polo.play_abroad.core.components.globe

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.withFrameMillis
import kotlin.math.pow

/** Where the globe sits on the current screen. */
enum class GlobeMode {
    INTRO,
    BADGE,
    CLOCK,
    HIDDEN
}

/**
 * Spin physics of the app-wide globe, ported from the prototype: drag to turn, fling with
 * friction, a slow idle spin, a hard spin-up ("kick") when the intro is finished or the clock
 * takes over. Screens feed drags in and listen to user-made turns through [onUserTurn].
 */
@Stable
class GlobeController {
    var angle by mutableFloatStateOf(-50f)
        private set
    var punch by mutableFloatStateOf(0f)
        private set
    var burst by mutableIntStateOf(0)
        private set
    var hapticsOn by mutableStateOf(true)

    /** Called with every turn the user caused (drag or its fling), in degrees. */
    var onUserTurn: ((Float) -> Unit)? = null

    private var velocity = 0f
    private var dragging = false
    private var userSpin = false
    private var kicked = false
    private var idleSpeed = 0.004f

    fun setMode(mode: GlobeMode) {
        idleSpeed = if (mode == GlobeMode.INTRO) 0.004f else 0.006f
        if (mode == GlobeMode.INTRO) {
            kicked = false
            userSpin = false
        }
    }

    fun dragStart() {
        dragging = true
        userSpin = true
        velocity = 0f
    }

    fun drag(deltaDegrees: Float) {
        angle += deltaDegrees
        onUserTurn?.invoke(deltaDegrees)
    }

    /** [velocityDegreesPerMs] from the finger's last movement; clamped like the prototype. */
    fun dragEnd(velocityDegreesPerMs: Float) {
        dragging = false
        velocity = velocityDegreesPerMs.coerceIn(-2.4f, 2.4f)
    }

    /** A strong spin that slows down quickly, with the ball "punching" bigger for a moment. */
    fun kick(velocityDegreesPerMs: Float, withBurst: Boolean) {
        userSpin = false
        kicked = true
        velocity = velocityDegreesPerMs
        punch = 1f
        if (withBurst) burst++
    }

    suspend fun run() {
        var last = withFrameMillis { it }
        while (true) {
            val now = withFrameMillis { it }
            val dt = (now - last).coerceIn(0L, 50L).toFloat()
            last = now
            if (!dragging) {
                if (kotlin.math.abs(velocity) > 0.0015f) {
                    val d = velocity * dt
                    angle += d
                    if (userSpin) onUserTurn?.invoke(d)
                    velocity *= (if (kicked) 0.35f else 0.06f).pow(dt / 1000f)
                } else {
                    velocity = 0f
                    kicked = false
                    if (!userSpin) angle += idleSpeed * dt
                }
            }
            punch *= 0.02f.pow(dt / 1000f)
        }
    }
}

val LocalGlobeController = staticCompositionLocalOf { GlobeController() }
