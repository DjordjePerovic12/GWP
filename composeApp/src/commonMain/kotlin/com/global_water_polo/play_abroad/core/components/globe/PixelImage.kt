package com.global_water_polo.play_abroad.core.components.globe

import androidx.compose.ui.graphics.ImageBitmap

/**
 * A width x height ARGB pixel buffer that can be shown as an [ImageBitmap].
 * The globe is rendered on the CPU into [pixels] every frame; [update] hands them to the platform bitmap.
 */
expect class PixelImage(width: Int, height: Int) {
    val width: Int
    val height: Int
    val pixels: IntArray
    fun update(): ImageBitmap
}
