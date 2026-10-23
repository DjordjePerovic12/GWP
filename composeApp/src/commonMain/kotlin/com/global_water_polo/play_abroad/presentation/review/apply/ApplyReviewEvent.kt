package com.global_water_polo.play_abroad.presentation.review.apply

import com.global_water_polo.play_abroad.presentation.base.BaseEvent

sealed class ApplyReviewEvent : BaseEvent {
    data object NavigateBack : ApplyReviewEvent()
    data class OnClipClick(val clip: String) : ApplyReviewEvent()
    data object OnSubmitClick : ApplyReviewEvent()
}
