package com.global_water_polo.play_abroad.presentation.host

import com.global_water_polo.play_abroad.presentation.base.BaseEvent

sealed class HostEvent : BaseEvent {
    data object OnUploadClick : HostEvent()
    data object OnBackClick : HostEvent()
}
