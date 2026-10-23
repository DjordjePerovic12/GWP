package com.global_water_polo.play_abroad.presentation.intro

import com.global_water_polo.play_abroad.presentation.base.BaseEvent

sealed class IntroEvent : BaseEvent {
    data object OnAppear : IntroEvent()
    data class OnSpin(val deltaDegrees: Float) : IntroEvent()
    data object OnSkipClick : IntroEvent()
}
