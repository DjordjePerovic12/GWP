package com.global_water_polo.play_abroad.core.components.globe

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo

actual class PixelImage actual constructor(actual val width: Int, actual val height: Int) {
    actual val pixels: IntArray = IntArray(width * height)
    private val bytes = ByteArray(width * height * 4)
    private val info = ImageInfo(width, height, ColorType.BGRA_8888, ColorAlphaType.UNPREMUL)

    actual fun update(): ImageBitmap {
        var b = 0
        for (argb in pixels) {
            bytes[b] = argb.toByte()
            bytes[b + 1] = (argb shr 8).toByte()
            bytes[b + 2] = (argb shr 16).toByte()
            bytes[b + 3] = (argb ushr 24).toByte()
            b += 4
        }
        return Image.makeRaster(info, bytes, width * 4).toComposeImageBitmap()
    }
}
