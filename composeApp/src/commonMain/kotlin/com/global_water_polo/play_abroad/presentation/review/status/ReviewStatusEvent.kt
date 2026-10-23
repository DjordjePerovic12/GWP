package com.global_water_polo.play_abroad.presentation.review.status

import com.global_water_polo.play_abroad.presentation.base.BaseEvent

sealed class ReviewStatusEvent : BaseEvent {
    data object OnBackToProfileClick : ReviewStatusEvent()
}
