package com.global_water_polo.play_abroad.presentation.home.parent

import com.global_water_polo.play_abroad.presentation.base.BaseEvent

sealed class ParentHomeEvent : BaseEvent {
    data object OnCompareClick : ParentHomeEvent()
    data object OnBookCallClick : ParentHomeEvent()
    data object OnInviteClick : ParentHomeEvent()
}
