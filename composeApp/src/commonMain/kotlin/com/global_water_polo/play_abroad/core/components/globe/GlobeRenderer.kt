package com.global_water_polo.play_abroad.core.components.globe

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Vertices
import androidx.compose.ui.graphics.VertexMode
import org.jetbrains.compose.resources.decodeToImageBitmap
import com.global_water_polo.play_abroad.resources.Res
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * The ball texture (equirectangular, 2048 x 1024) with the bump map baked into its brightness,
 * so the panel seams and pebble grain show without a lighting pass.
 */
suspend fun loadGlobeTexture(): ImageBitmap {
    val map = Res.readBytes("files/ball_map.jpg").decodeToImageBitmap()
    val bump = Res.readBytes("files/ball_bump.jpg").decodeToImageBitmap()
    val w = map.width
    val h = map.height
    val colors = IntArray(w * h).also { map.readPixels(it) }
    val bumps = IntArray(bump.width * bump.height).also { bump.readPixels(it) }
    val out = PixelImage(w, h)
    for (y in 0 until h) {
        val by = y * bump.height / h
        for (x in 0 until w) {
            val c = colors[y * w + x]
            val b = (bumps[by * bump.width + x * bump.width / w] shr 16 and 0xFF) / 255f
            val k = 0.8f + 0.28f * b
            val r = min(255f, (c shr 16 and 0xFF) * k).toInt()
            val g = min(255f, (c shr 8 and 0xFF) * k).toInt()
            val bl = min(255f, (c and 0xFF) * k).toInt()
            out.pixels[y * w + x] = (0xFF shl 24) or (r shl 16) or (g shl 8) or bl
        }
    }
    return out.update()
}

/**
 * The water polo globe as a textured sphere mesh, drawn by the GPU (Canvas.drawVertices), like the
 * prototype's three.js scene: tilted by rotation.z = -0.32 and rotation.x = 0.18, spinning around its
 * own axis. The texture is attached to the ball (fixed UVs), only vertex positions change per frame,
 * so a frame costs a few thousand multiplications whatever the ball's size on screen.
 */
/** The ball's resting forward tilt (three.js rotation.x = 0.18). */
const val BASE_TILT = 0.18f

/** The ball's fixed sideways roll (radians), like the prototype's tilt.rotation.z. */
const val GLOBE_ROLL = -0.32f

/** A place marked on the ball (the tour's club cities); [active] is drawn bigger with a ring. */
data class GlobePin(val latitude: Float, val longitude: Float, val active: Boolean = false)

class GlobeMesh(private val textureWidth: Int, private val textureHeight: Int) {

    private val lonSegments = 72
    private val latSegments = 36
    private val columns = lonSegments + 1
    private val count = columns * (latSegments + 1)

    // Point on the unit ball for each vertex (three.js SphereGeometry layout) and its texture position.
    private val px = FloatArray(count)
    private val py = FloatArray(count)
    private val pz = FloatArray(count)
    private val uv: List<Offset>
    private val white = List(count) { Color.White }

    private val tiltX = BASE_TILT
    private val tiltZ = GLOBE_ROLL

    init {
        val coords = ArrayList<Offset>(count)
        for (lat in 0..latSegments) {
            val theta = PI.toFloat() * lat / latSegments
            for (lon in 0..lonSegments) {
                val phi = 2f * PI.toFloat() * lon / lonSegments
                val i = lat * columns + lon
                px[i] = -cos(phi) * sin(theta)
                py[i] = cos(theta)
                pz[i] = sin(phi) * sin(theta)
                coords += Offset(textureWidth * lon.toFloat() / lonSegments, textureHeight * lat.toFloat() / latSegments)
            }
        }
        uv = coords
    }

    /**
     * Screen position of [latitude], [longitude] on the ball, or null when it is on the far side.
     * Same rotations as [vertices]; the texture's left edge is longitude -180.
     */
    fun project(latitude: Float, longitude: Float, angleDegrees: Float, tilt: Float, cx: Float, cy: Float, radius: Float): Offset? {
        val phi = (longitude + 180f) / 360f * 2f * PI.toFloat()
        val theta = (90f - latitude) * PI.toFloat() / 180f
        val x0 = -cos(phi) * sin(theta)
        val y0 = cos(theta)
        val z0 = sin(phi) * sin(theta)
        val a = angleDegrees * PI.toFloat() / 180f
        val x1 = x0 * cos(a) + z0 * sin(a)
        val z1 = -x0 * sin(a) + z0 * cos(a)
        val x2 = x1 * cos(tiltZ) - y0 * sin(tiltZ)
        val y2 = x1 * sin(tiltZ) + y0 * cos(tiltZ)
        val y3 = y2 * cos(tilt) - z1 * sin(tilt)
        val z3 = y2 * sin(tilt) + z1 * cos(tilt)
        return if (z3 < 0.08f) null else Offset(cx + x2 * radius, cy - y3 * radius)
    }

    /** Triangles of the half of the ball facing the viewer, centred at ([cx], [cy]) with [radius] in px. */
    fun vertices(angleDegrees: Float, cx: Float, cy: Float, radius: Float, tilt: Float = tiltX): Vertices {
        val a = angleDegrees * PI.toFloat() / 180f
        val ca = cos(a)
        val sa = sin(a)
        val cz = cos(tiltZ)
        val sz = sin(tiltZ)
        val cxr = cos(tilt)
        val sxr = sin(tilt)
        val positions = ArrayList<Offset>(count)
        val depth = FloatArray(count)
        for (i in 0 until count) {
            // Spin (Ry), then tilt: Rz(-0.32), then Rx(0.18).
            val x1 = px[i] * ca + pz[i] * sa
            val z1 = -px[i] * sa + pz[i] * ca
            val y1 = py[i]
            val x2 = x1 * cz - y1 * sz
            val y2 = x1 * sz + y1 * cz
            val y3 = y2 * cxr - z1 * sxr
            val z3 = y2 * sxr + z1 * cxr
            positions += Offset(cx + x2 * radius, cy - y3 * radius)
            depth[i] = z3
        }
        val indices = ArrayList<Int>(lonSegments * latSegments * 6 / 2 + 64)
        for (lat in 0 until latSegments) {
            for (lon in 0 until lonSegments) {
                val i0 = lat * columns + lon
                val i1 = i0 + 1
                val i2 = i0 + columns
                val i3 = i2 + 1
                // A convex ball: quads facing away are hidden, and facing quads never overlap.
                if (depth[i0] > -0.05f || depth[i1] > -0.05f || depth[i2] > -0.05f || depth[i3] > -0.05f) {
                    indices += i0; indices += i2; indices += i1
                    indices += i1; indices += i2; indices += i3
                }
            }
        }
        return Vertices(
            vertexMode = VertexMode.Triangles,
            positions = positions,
            textureCoordinates = uv,
            colors = white,
            indices = indices
        )
    }
}
