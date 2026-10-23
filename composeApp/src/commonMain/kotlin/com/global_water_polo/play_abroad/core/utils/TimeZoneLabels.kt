package com.global_water_polo.play_abroad.core.utils

import kotlin.math.abs
import kotlin.math.roundToInt

/** Longitude helpers for the intro dial: the meridian facing the viewer and its UTC offset. */
object TimeZoneLabels {
    /** Wraps any angle to -180..180. */
    fun norm(degrees: Float): Float = (((degrees + 180f) % 360f) + 360f) % 360f - 180f

    /** Longitude facing the viewer for a globe turned by [angle] (prototype: -90 - angle). */
    fun facing(angle: Float): Float = -90f - angle

    fun tzLabel(longitude: Float): String {
        val offset = (norm(longitude) / 15f).roundToInt()
        return when {
            offset == 0 -> "UTC"
            offset > 0 -> "UTC+$offset"
            else -> "UTC-${abs(offset)}"
        }
    }

    fun lonLabel(longitude: Float): String {
        val n = norm(longitude).roundToInt()
        return when {
            n == 0 -> "0°"
            abs(n) == 180 -> "180°"
            else -> "${abs(n)}°" + if (n < 0) "W" else "E"
        }
    }
}
