package com.global_water_polo.play_abroad.presentation.opener

import com.global_water_polo.play_abroad.presentation.base.BaseError
import com.global_water_polo.play_abroad.presentation.base.BaseViewState

data class OpenerState(
    override val isLoading: Boolean = false,
    override val error: BaseError? = null,
    /** Seconds into the opener when "Begin Your Journey" was tapped; null until then. */
    val tappedAt: Float? = null,
    /** "Skip intro" jumps straight to the held ball with its texts. */
    val skipped: Boolean = false,
    val finished: Boolean = false,
    /** The Quick start sheet (testing shortcut, see DemoProfiles). */
    val quickStartOpen: Boolean = false
) : BaseViewState(isLoading, error)
