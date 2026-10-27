package com.global_water_polo.play_abroad.core.components.globe

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.geometry.Offset
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

    /** Globe+ zoom: 1 = the ball fits under the header; up to [MAX_ZOOM] to see clubs and cities. */
    var zoom by mutableFloatStateOf(1f)
        private set

    /** Globe+ clusters, pins, badges and labels; drawn by layer according to [zoom]. */
    var markers by mutableStateOf<GlobeMarkers?>(null)

    /** Where each tappable marker was last drawn, in root pixels (written by the stage every frame). */
    internal var markerHits: List<Pair<Offset, GlobeHit>> = emptyList()

    /** Called with every turn the user caused (drag or its fling), in degrees. */
    var onUserTurn: ((Float) -> Unit)? = null

    private var velocity = 0f
    private var dragging = false
    private var userSpin = false
    private var kicked = false
    private var idleSpeed = 0f
    private var aim: Float? = null
    private var tiltTarget = BASE_TILT
    private var zoomTarget: Float? = null

    fun setMode(mode: GlobeMode) {
        // A drag never outlives its screen. Its end can be lost when the screen leaves mid-gesture, and a
        // stuck drag freezes the turn (the tour then tilts to each city but never turns to it).
        dragging = false
        // On the intro the globe rests on the device's own time zone until someone spins it.
        idleSpeed = if (mode == GlobeMode.INTRO || mode == GlobeMode.REVEAL || mode == GlobeMode.EXPLORE) 0f else 0.006f
        if (mode != GlobeMode.REVEAL && mode != GlobeMode.EXPLORE) {
            aim = null
            tiltTarget = BASE_TILT
            pins = emptyList()
        }
        if (mode != GlobeMode.EXPLORE) {
            zoom = 1f
            zoomTarget = null
            markers = null
            markerHits = emptyList()
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
    fun aimAt(longitude: Float, latitude: Float? = null, zoom: Float? = null) {
        zoomTarget = zoom?.coerceIn(MIN_ZOOM, MAX_ZOOM)
        // Globe+ zooms in, so the place must land dead centre: solve for the turn and lean that put it
        // there through the ball's roll. The tour keeps the prototype's simpler aim.
        val exact = if (zoom != null && latitude != null) centring(latitude, longitude) else null
        tiltTarget = exact?.second ?: latitude?.let { it * kotlin.math.PI.toFloat() / 180f } ?: BASE_TILT
        val target = exact?.first ?: (-90f - longitude)
        aim = angle + (((target - angle) % 360f + 540f) % 360f - 180f)
        dragging = false
        velocity = 0f
        userSpin = false
    }

    /** Drops the tour's pins and aim (a tour can still tick once while its screen animates out). */
    fun clearTour() {
        pins = emptyList()
        aim = null
        tiltTarget = BASE_TILT
    }

    /**
     * The ball turn (degrees) and lean (radians) that put [latitude], [longitude] in the middle of the ball,
     * inverting GlobeMesh.project: after the turn the point must sit on the roll's vertical axis, then the
     * lean lifts it to the centre.
     */
    private fun centring(latitude: Float, longitude: Float): Pair<Float, Float>? {
        val pi = kotlin.math.PI.toFloat()
        val phi = (longitude + 180f) / 360f * 2f * pi
        val theta = (90f - latitude) * pi / 180f
        val x0 = -kotlin.math.cos(phi) * kotlin.math.sin(theta)
        val y0 = kotlin.math.cos(theta)
        val z0 = kotlin.math.sin(phi) * kotlin.math.sin(theta)
        val r = kotlin.math.sqrt(x0 * x0 + z0 * z0)
        val need = y0 * kotlin.math.tan(GLOBE_ROLL) / r
        if (r < 1e-4f || kotlin.math.abs(need) > 1f) return null
        val delta = kotlin.math.atan2(z0, x0)
        // Of the two turns, keep the one that brings the point to the front.
        val a = listOf(delta + kotlin.math.acos(need), delta - kotlin.math.acos(need)).maxBy { a -> -x0 * kotlin.math.sin(a) + z0 * kotlin.math.cos(a) }
        val x1 = x0 * kotlin.math.cos(a) + z0 * kotlin.math.sin(a)
        val z1 = -x0 * kotlin.math.sin(a) + z0 * kotlin.math.cos(a)
        val y2 = x1 * kotlin.math.sin(GLOBE_ROLL) + y0 * kotlin.math.cos(GLOBE_ROLL)
        val lean = kotlin.math.atan2(y2, z1).coerceIn(-1.25f, 1.25f)
        return a * 180f / pi to lean
    }

    /** Pinch or the zoom buttons: multiplies the zoom right away. */
    fun zoomBy(factor: Float) {
        zoomTarget = null
        zoom = (zoom * factor).coerceIn(MIN_ZOOM, MAX_ZOOM)
    }

    /** Eases to [zoom] (the zoom buttons), keeping the current aim. */
    fun zoomTo(zoom: Float) {
        zoomTarget = zoom.coerceIn(MIN_ZOOM, MAX_ZOOM)
    }

    /** A vertical drag on Globe+ leans the ball, like the prototype's Google Earth globe. */
    fun tiltBy(radians: Float) {
        tilt = (tilt + radians).coerceIn(-1.25f, 1.25f)
        tiltTarget = tilt
    }

    /** The marker nearest [rootPosition] within [slopPx], or null. Club badges win over what lies under them. */
    fun markerAt(rootPosition: Offset, slopPx: Float): GlobeHit? {
        val near = markerHits.filter { (p, _) -> (p - rootPosition).getDistance() <= slopPx }
        return near.filter { it.second is GlobeHit.Group }.minByOrNull { (p, _) -> (p - rootPosition).getDistance() }?.second
            ?: near.minByOrNull { (p, _) -> (p - rootPosition).getDistance() }?.second
    }

    fun dragStart() {
        aim = null
        zoomTarget = null
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
            zoomTarget?.let { z ->
                zoom += (z - zoom) * (1f - kotlin.math.exp(-dt / 380f))
                if (kotlin.math.abs(z - zoom) < 0.003f) {
                    zoom = z
                    zoomTarget = null
                }
            }
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

const val MIN_ZOOM = 0.75f
const val MAX_ZOOM = 6f

val LocalGlobeController = staticCompositionLocalOf { GlobeController() }
