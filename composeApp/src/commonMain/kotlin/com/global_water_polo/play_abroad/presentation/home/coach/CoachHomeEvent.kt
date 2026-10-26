package com.global_water_polo.play_abroad.presentation.home.coach

import com.global_water_polo.play_abroad.presentation.base.BaseEvent

sealed class CoachHomeEvent : BaseEvent {
    data class OnFilterClick(val filter: ScoutFilter) : CoachHomeEvent()
    data class OnRequestClick(val id: String) : CoachHomeEvent()
    data object OnBackToStartClick : CoachHomeEvent()
}
