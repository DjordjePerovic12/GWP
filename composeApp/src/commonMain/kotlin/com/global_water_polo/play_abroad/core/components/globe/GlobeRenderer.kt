package com.global_water_polo.play_abroad.core.components.globe

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toPixelMap
import org.jetbrains.compose.resources.decodeToImageBitmap
import com.global_water_polo.play_abroad.resources.Res
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/** The ball texture (equirectangular map with the bump map baked into its brightness). */
class GlobeTexture(val width: Int, val height: Int, val argb: IntArray)

/** Loads `files/ball_map.jpg` + `files/ball_bump.jpg` once, at half resolution (1024 x 512). */
suspend fun loadGlobeTexture(): GlobeTexture {
    val map = Res.readBytes("files/ball_map.jpg").decodeToImageBitmap().toPixelMap()
    val bump = Res.readBytes("files/ball_bump.jpg").decodeToImageBitmap().toPixelMap()
    val w = map.width / 2
    val h = map.height / 2
    val out = IntArray(w * h)
    for (y in 0 until h) {
        for (x in 0 until w) {
            val c = map[x * 2, y * 2]
            val b = bump[x * bump.width / w, y * bump.height / h].red
            // Seams and pebble grain from the bump map darken the colour slightly.
            val k = 0.8f + 0.28f * b
            val r = min(255f, c.red * 255f * k).toInt()
            val g = min(255f, c.green * 255f * k).toInt()
            val bl = min(255f, c.blue * 255f * k).toInt()
            out[y * w + x] = (0xFF shl 24) or (r shl 16) or (g shl 8) or bl
        }
    }
    return GlobeTexture(w, h, out)
}

/**
 * Renders the water polo globe as the prototype's three.js scene does: a sphere tilted by
 * rotation.z = -0.32 and rotation.x = 0.18, spinning around its own axis, lit by a key light
 * from the upper left, a cool rim light and a clear-coat highlight.
 *
 * Everything that does not depend on the spin angle is computed once per pixel, so a frame is
 * one texture lookup and a multiply per pixel.
 */
class GlobeRenderer(private val texture: GlobeTexture, val size: Int) {

    // Two buffers: one is on screen while the next frame is drawn into the other (frames render off the main thread).
    private val images = arrayOf(PixelImage(size, size), PixelImage(size, size))
    private var next = 0
    private val count = size * size
    private val alpha = IntArray(count)
    private val column = FloatArray(count)
    private val rowOffset = IntArray(count)
    private val shade = FloatArray(count)
    private val specular = FloatArray(count)

    init {
        val radius = size / 2f
        val key = normalize(-3f, 4f, 5f)
        val rim = normalize(4f, 1.5f, -3f)
        val half = normalize(key[0], key[1], key[2] + 1f)
        val tiltX = -0.18f
        val tiltZ = 0.32f
        for (py in 0 until size) {
            for (px in 0 until size) {
                val i = py * size + px
                val dx = px + 0.5f - radius
                val dy = py + 0.5f - radius
                val dist = sqrt(dx * dx + dy * dy)
                val coverage = (radius - dist + 0.5f).coerceIn(0f, 1f)
                alpha[i] = (coverage * 255f).toInt()
                if (alpha[i] == 0) continue
                // View-space normal (y up, z towards the viewer).
                val k = min(1f, dist / radius) / max(dist, 1e-4f)
                val nx = dx * k
                val ny = -dy * k
                val nz = sqrt(max(0f, 1f - nx * nx - ny * ny))
                // Undo the tilt: Rx(-0.18), then Rz(0.32), to get the point on the ball itself.
                val y1 = ny * cos(tiltX) - nz * sin(tiltX)
                val z1 = ny * sin(tiltX) + nz * cos(tiltX)
                val qx = nx * cos(tiltZ) - y1 * sin(tiltZ)
                val qy = nx * sin(tiltZ) + y1 * cos(tiltZ)
                val phi = atan2(z1, -qx)
                val u = ((phi / (2 * PI).toFloat()) % 1f + 1f) % 1f
                column[i] = u * texture.width
                val theta = acos(qy.coerceIn(-1f, 1f))
                val row = ((theta / PI.toFloat()) * (texture.height - 1)).toInt().coerceIn(0, texture.height - 1)
                rowOffset[i] = row * texture.width
                // Lighting: hemisphere ambient + key + rim, clear-coat specular and a soft fresnel edge.
                val diffuse = max(0f, nx * key[0] + ny * key[1] + nz * key[2])
                val rimLight = max(0f, nx * rim[0] + ny * rim[1] + nz * rim[2])
                val ambient = 0.46f + 0.12f * ny
                shade[i] = ambient + 0.62f * diffuse + 0.3f * rimLight
                val spec = max(0f, nx * half[0] + ny * half[1] + nz * half[2]).pow(48)
                val fresnel = (1f - nz).pow(3) * 0.18f
                specular[i] = (spec * 0.55f + fresnel) * 255f
            }
        }
    }

    /** Draws the ball turned by [angleDegrees] (drag right = positive). */
    fun render(angleDegrees: Float): ImageBitmap {
        val image = images[next]
        next = 1 - next
        val pixels = image.pixels
        val tex = texture.argb
        val width = texture.width
        val widthF = width.toFloat()
        val shift = (angleDegrees / 360f % 1f) * widthF
        for (i in 0 until count) {
            val a = alpha[i]
            if (a == 0) {
                pixels[i] = 0
                continue
            }
            var col = column[i] - shift
            col -= (col / widthF).toInt() * widthF
            if (col < 0f) col += widthF
            val texel = tex[rowOffset[i] + min(width - 1, col.toInt())]
            val s = shade[i]
            val sp = specular[i]
            val r = min(255f, ((texel shr 16) and 0xFF) * s + sp).toInt()
            val g = min(255f, ((texel shr 8) and 0xFF) * s + sp).toInt()
            val b = min(255f, (texel and 0xFF) * s + sp).toInt()
            pixels[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
        }
        return image.update()
    }

    private fun normalize(x: Float, y: Float, z: Float): FloatArray {
        val l = sqrt(x * x + y * y + z * z)
        return floatArrayOf(x / l, y / l, z / l)
    }
}
