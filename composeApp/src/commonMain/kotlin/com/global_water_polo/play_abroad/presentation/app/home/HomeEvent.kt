package com.global_water_polo.play_abroad.presentation.app.home

import com.global_water_polo.play_abroad.core.components.AppTab
import com.global_water_polo.play_abroad.core.components.membership.MembershipSheet
import com.global_water_polo.play_abroad.core.utils.LockAction
import com.global_water_polo.play_abroad.domain.model.Program
import com.global_water_polo.play_abroad.presentation.base.BaseEvent

sealed class HomeEvent : BaseEvent {
    data class OnTabClick(val tab: AppTab) : HomeEvent()
    data class OnLock(val action: LockAction) : HomeEvent()
    data object OnSheetDismiss : HomeEvent()

    data object OnPlanClick : HomeEvent()
    data object OnAdjustClick : HomeEvent()
    data class OnDraftNow(val key: String) : HomeEvent()
    data class OnDraftGrad(val year: Int) : HomeEvent()
    data class OnDraftAvailability(val key: String) : HomeEvent()
    data class OnDraftFirstStep(val key: String) : HomeEvent()
    data object OnRebuildClick : HomeEvent()

    data object OnGlobeClick : HomeEvent()
    data class OnProgramClick(val program: Program) : HomeEvent()
    data class OnProgramCtaClick(val program: Program) : HomeEvent()
    data class OnSendInquiry(val program: Program) : HomeEvent()
    data class OnCityClick(val cityId: String) : HomeEvent()
    data class OnClinicClick(val index: Int) : HomeEvent()

    data class OnMembershipSheet(val sheet: MembershipSheet?) : HomeEvent()
    data class OnStartPlan(val plan: String, val yearly: Boolean) : HomeEvent()
    data object OnSwitchToNext : HomeEvent()
    data object OnCancelPlan : HomeEvent()
}
