package com.global_water_polo.play_abroad.presentation.home.coachpending

import com.global_water_polo.play_abroad.presentation.base.BaseEvent

sealed class CoachPendingEvent : BaseEvent {
    data object OnBackToStartClick : CoachPendingEvent()
}
