package com.global_water_polo.play_abroad.presentation.review.apply

import com.global_water_polo.play_abroad.presentation.base.BaseError
import com.global_water_polo.play_abroad.presentation.base.BaseViewState

data class ApplyReviewState(
    override val isLoading: Boolean = false,
    override val error: BaseError? = null,
    val uploads: Set<String> = emptySet()
) : BaseViewState(isLoading, error) {
    val canSubmit: Boolean get() = CLIP_ID in uploads && CLIP_WATER in uploads

    companion object {
        const val CLIP_ID = "id"
        const val CLIP_WATER = "water"
    }
}
