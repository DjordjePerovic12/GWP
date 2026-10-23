package com.global_water_polo.play_abroad.core.components.globe

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

actual class PixelImage actual constructor(actual val width: Int, actual val height: Int) {
    actual val pixels: IntArray = IntArray(width * height)
    private val bitmap: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    private val image: ImageBitmap = bitmap.asImageBitmap()

    actual fun update(): ImageBitmap {
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return image
    }
}
