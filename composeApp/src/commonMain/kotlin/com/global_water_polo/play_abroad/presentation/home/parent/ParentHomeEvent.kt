package com.global_water_polo.play_abroad.presentation.home.parent

import com.global_water_polo.play_abroad.core.components.membership.MembershipSheet
import com.global_water_polo.play_abroad.presentation.base.BaseEvent

sealed class ParentHomeEvent : BaseEvent {
    data object OnCompareClick : ParentHomeEvent()
    data object OnBookCallClick : ParentHomeEvent()
    data object OnInviteClick : ParentHomeEvent()
    data object OnAbroadRequestClick : ParentHomeEvent()
    data object OnSheetDismiss : ParentHomeEvent()
    data class OnMembershipSheet(val sheet: MembershipSheet?) : ParentHomeEvent()
    data class OnStartPlan(val plan: String, val yearly: Boolean) : ParentHomeEvent()
    data object OnSwitchToNext : ParentHomeEvent()
    data object OnCancelPlan : ParentHomeEvent()
}
