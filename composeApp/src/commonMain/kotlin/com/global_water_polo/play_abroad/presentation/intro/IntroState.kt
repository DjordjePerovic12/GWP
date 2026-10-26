package com.global_water_polo.play_abroad.presentation.intro

import com.global_water_polo.play_abroad.presentation.base.BaseError
import com.global_water_polo.play_abroad.presentation.base.BaseViewState

data class IntroState(
    override val isLoading: Boolean = false,
    override val error: BaseError? = null,
    /** How far the spring is wound, 0..360 degrees (15 per time zone). */
    val progressDegrees: Float = 0f,
    val zonesCrossed: Int = 0,
    /** Twelve zones in, the spring lets go and the ball runs through the rest on its own. */
    val running: Boolean = false,
    /** All 24 zones spun: the ball waits for a tap to enter. */
    val spunAll: Boolean = false,
    val finished: Boolean = false
) : BaseViewState(isLoading, error) {
    val progress: Float get() = (progressDegrees / FULL_TURN).coerceIn(0f, 1f)

    companion object {
        const val FULL_TURN = 360f
        const val BY_HAND = 180f
        const val ZONES = 24
    }
}
