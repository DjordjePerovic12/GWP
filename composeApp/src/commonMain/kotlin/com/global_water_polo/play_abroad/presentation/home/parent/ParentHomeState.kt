package com.global_water_polo.play_abroad.presentation.home.parent

import com.global_water_polo.play_abroad.domain.model.ClockCountdown
import com.global_water_polo.play_abroad.domain.model.NcaaWindow
import com.global_water_polo.play_abroad.presentation.base.BaseError
import com.global_water_polo.play_abroad.presentation.base.BaseViewState

data class ParentHomeState(
    override val isLoading: Boolean = false,
    override val error: BaseError? = null,
    val firstName: String = "",
    val kidName: String = "",
    val invited: Boolean = false,
    val window: NcaaWindow? = null,
    val countdown: ClockCountdown? = null,
    /** Set when the parent asked about a team trip instead of their athlete's clock. */
    val tripStartOffset: Int? = null,
    val tripMonths: Int = 0
) : BaseViewState(isLoading, error)
