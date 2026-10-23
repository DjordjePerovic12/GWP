package com.global_water_polo.play_abroad.presentation.home.athlete

import com.global_water_polo.play_abroad.domain.model.ProfileVisibility
import com.global_water_polo.play_abroad.presentation.base.BaseEvent

sealed class AthleteHomeEvent : BaseEvent {
    data class OnVisibilityClick(val visibility: ProfileVisibility) : AthleteHomeEvent()
    data class OnTodoClick(val key: String) : AthleteHomeEvent()
    data object OnReviewClick : AthleteHomeEvent()
    data object OnBookSessionClick : AthleteHomeEvent()
    data object OnPlanAbroadClick : AthleteHomeEvent()
}
