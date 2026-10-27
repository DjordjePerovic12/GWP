package com.global_water_polo.play_abroad.presentation.app.settings

import com.global_water_polo.play_abroad.core.components.AppTab
import com.global_water_polo.play_abroad.core.components.membership.MembershipSheet
import com.global_water_polo.play_abroad.presentation.base.BaseEvent

sealed class SettingsEvent : BaseEvent {
    data class OnTabClick(val tab: AppTab) : SettingsEvent()
    data object OnEditProfileClick : SettingsEvent()
    data object OnPreviewClick : SettingsEvent()
    data class OnMembershipSheet(val sheet: MembershipSheet?) : SettingsEvent()
    data class OnStartPlan(val plan: String, val yearly: Boolean) : SettingsEvent()
    data object OnSwitchToNext : SettingsEvent()
    data object OnCancelPlan : SettingsEvent()
    data object OnNotificationsClick : SettingsEvent()
    data object OnHomeAirportClick : SettingsEvent()
    data object OnSignOutClick : SettingsEvent()
    data object OnStudioRequestClick : SettingsEvent()
}
