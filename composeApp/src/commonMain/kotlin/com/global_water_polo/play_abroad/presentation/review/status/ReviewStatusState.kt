package com.global_water_polo.play_abroad.presentation.review.status

import com.global_water_polo.play_abroad.presentation.base.BaseError
import com.global_water_polo.play_abroad.presentation.base.BaseViewState

/** The review has a fixed timeline for now: sent (done), coach review (now), level, public profile. */
data class ReviewStatusState(
    override val isLoading: Boolean = false,
    override val error: BaseError? = null,
    val doneSteps: Int = 1
) : BaseViewState(isLoading, error)
