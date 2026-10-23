package com.global_water_polo.play_abroad.presentation.example

import com.global_water_polo.play_abroad.presentation.base.BaseEvent

sealed class ExampleEvent : BaseEvent {
    data object OnDataResponseClick : ExampleEvent()
    data object OnEmptyResponseClick : ExampleEvent()
    data object OnHTMLResponseClick : ExampleEvent()
    data object OnCustomMessageResponseClick : ExampleEvent()
    data object OnLoginDemoClick : ExampleEvent()
}
