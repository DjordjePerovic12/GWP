package com.global_water_polo.play_abroad.presentation.intro

import com.global_water_polo.play_abroad.presentation.base.BaseError
import com.global_water_polo.play_abroad.presentation.base.BaseViewState

data class IntroState(
    override val isLoading: Boolean = false,
    override val error: BaseError? = null,
    val progressDegrees: Float = 0f,
    val zonesCrossed: Int = 0,
    /** All 24 zones spun: the ball waits for a tap to enter. */
    val spunAll: Boolean = false,
    val finished: Boolean = false,
    /** The Quick start sheet (testing shortcut, see DemoProfiles). */
    val quickStartOpen: Boolean = false
) : BaseViewState(isLoading, error) {
    val progress: Float get() = (progressDegrees / FULL_TURN).coerceIn(0f, 1f)

    companion object {
        const val FULL_TURN = 360f
        const val ZONES = 24
    }
}
