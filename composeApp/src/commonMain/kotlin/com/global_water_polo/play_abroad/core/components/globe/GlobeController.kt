package com.global_water_polo.play_abroad.core.components.globe

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.withFrameMillis
import kotlinx.datetime.TimeZone
import kotlinx.datetime.offsetAt
import kotlin.math.pow
import kotlin.time.Clock

/** Where the globe sits on the current screen. */
enum class GlobeMode {
    INTRO,
    BADGE,
    CLOCK,
    REVEAL,
    EXPLORE,
    HIDDEN
}

/**
 * Spin physics of the app-wide globe, ported from the prototype: drag to turn, fling with
 * friction, a slow idle spin, a hard spin-up ("kick") when the intro is finished or the clock
 * takes over. Screens feed drags in and listen to user-made turns through [onUserTurn].
 */
@Stable
class GlobeController {
    var angle by mutableFloatStateOf(homeAngle())
        private set
    var punch by mutableFloatStateOf(0f)
        private set
    var burst by mutableIntStateOf(0)
        private set
    var hapticsOn by mutableStateOf(true)

    /** Forward tilt in radians; the tour leans it so the city's latitude faces the viewer. */
    var tilt by mutableFloatStateOf(BASE_TILT)
        private set

    /** A screen can raise the big night globe in place (the club's "on the map" moment) without its own route. */
    var revealOverride by mutableStateOf(false)

    /** Places marked on the ball while the tour runs. */
    var pins by mutableStateOf<List<GlobePin>>(emptyList())

    /** Called with every turn the user caused (drag or its fling), in degrees. */
    var onUserTurn: ((Float) -> Unit)? = null

    private var velocity = 0f
    private var dragging = false
    private var userSpin = false
    private var kicked = false
    private var idleSpeed = 0f
    private var aim: Float? = null
    private var tiltTarget = BASE_TILT

    fun setMode(mode: GlobeMode) {
        // On the intro the globe rests on the device's own time zone until someone spins it.
        idleSpeed = if (mode == GlobeMode.INTRO || mode == GlobeMode.REVEAL || mode == GlobeMode.EXPLORE) 0f else 0.006f
        if (mode != GlobeMode.REVEAL && mode != GlobeMode.EXPLORE) {
            aim = null
            tiltTarget = BASE_TILT
            pins = emptyList()
        }
        if (mode == GlobeMode.INTRO) {
            kicked = false
            userSpin = false
        }
    }

    /**
     * Turns the ball the short way round until [longitude] faces the viewer, and leans it so [latitude]
     * does too (the globe tour).
     */
    fun aimAt(longitude: Float, latitude: Float? = null) {
        tiltTarget = latitude?.let { it * kotlin.math.PI.toFloat() / 180f } ?: BASE_TILT
        val target = -90f - longitude
        aim = angle + (((target - angle) % 360f + 540f) % 360f - 180f)
        velocity = 0f
        userSpin = false
    }

    /** Drops the tour's pins and aim (a tour can still tick once while its screen animates out). */
    fun clearTour() {
        pins = emptyList()
        aim = null
        tiltTarget = BASE_TILT
    }

    fun dragStart() {
        aim = null
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
        velocity = velocityDegreesPerMs.coerceIn(-3.6f, 3.6f)
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
            tilt += (tiltTarget - tilt) * (1f - kotlin.math.exp(-dt / 380f))
            val target = aim
            if (!dragging && target != null) {
                angle += (target - angle) * (1f - kotlin.math.exp(-dt / 380f))
            } else if (!dragging) {
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

/**
 * Angle that turns the device's time zone to face the viewer: the facing meridian is -90 - angle,
 * and a UTC offset of h hours sits at longitude 15 * h.
 */
fun homeAngle(): Float {
    val offsetHours = TimeZone.currentSystemDefault().offsetAt(Clock.System.now()).totalSeconds / 3600f
    return -90f - offsetHours * 15f
}

val LocalGlobeController = staticCompositionLocalOf { GlobeController() }
