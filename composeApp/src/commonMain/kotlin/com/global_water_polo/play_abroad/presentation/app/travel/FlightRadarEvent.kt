package com.global_water_polo.play_abroad.presentation.app.travel

import com.global_water_polo.play_abroad.core.components.AppTab
import com.global_water_polo.play_abroad.presentation.base.BaseEvent

sealed class FlightRadarEvent : BaseEvent {
    data class OnTabClick(val tab: AppTab) : FlightRadarEvent()
    data class OnHomeAirportClick(val code: String) : FlightRadarEvent()
    data class OnRequestFlightsClick(val cityId: String) : FlightRadarEvent()
    data class OnCheckClick(val key: String) : FlightRadarEvent()
    data object OnOpenGlobeClick : FlightRadarEvent()
    data class OnDraftClick(val tripId: Long) : FlightRadarEvent()
}
