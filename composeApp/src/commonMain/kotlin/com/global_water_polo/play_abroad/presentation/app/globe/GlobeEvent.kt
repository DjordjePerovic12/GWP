package com.global_water_polo.play_abroad.presentation.app.globe

import com.global_water_polo.play_abroad.core.components.AppTab
import com.global_water_polo.play_abroad.presentation.base.BaseEvent

sealed class GlobeEvent : BaseEvent {
    data class OnTabClick(val tab: AppTab) : GlobeEvent()
    data class OnQueryChanged(val query: String) : GlobeEvent()
    data class OnRegionClick(val region: String) : GlobeEvent()
    data class OnCityClick(val cityId: String) : GlobeEvent()
    data class OnClubClick(val clubId: String) : GlobeEvent()
    data class OnAskClick(val cityId: String) : GlobeEvent()
    data object OnSheetDismiss : GlobeEvent()
}
