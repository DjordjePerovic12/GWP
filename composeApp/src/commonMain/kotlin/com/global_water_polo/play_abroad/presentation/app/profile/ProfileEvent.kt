package com.global_water_polo.play_abroad.presentation.app.profile

import com.global_water_polo.play_abroad.core.components.AppTab
import com.global_water_polo.play_abroad.core.components.membership.MembershipSheet
import com.global_water_polo.play_abroad.domain.model.Program
import com.global_water_polo.play_abroad.domain.model.ProfileVisibility
import com.global_water_polo.play_abroad.presentation.base.BaseEvent

sealed class ProfileEvent : BaseEvent {
    data class OnTabClick(val tab: AppTab) : ProfileEvent()
    data class OnSegmentClick(val segment: ProfileSegment) : ProfileEvent()
    data class OnMembershipSheet(val sheet: MembershipSheet?) : ProfileEvent()
    data class OnStartPlan(val plan: String, val yearly: Boolean) : ProfileEvent()
    data object OnSwitchToNext : ProfileEvent()
    data object OnCancelPlan : ProfileEvent()
    data object OnSheetDismiss : ProfileEvent()

    data object OnBuildPathwayClick : ProfileEvent()
    data class OnBuildPathway(
        val goal: String,
        val level: String,
        val education: String,
        val timing: Set<String>,
        val destinations: Set<String>
    ) : ProfileEvent()
    data object OnExploreGlobeClick : ProfileEvent()
    data class OnProgramClick(val program: Program) : ProfileEvent()
    data class OnProgramCtaClick(val program: Program) : ProfileEvent()
    data class OnSendInquiry(val program: Program) : ProfileEvent()
    data class OnCityClick(val cityId: String) : ProfileEvent()
    data class OnClinicClick(val index: Int) : ProfileEvent()

    data class OnLabTestClick(val index: Int) : ProfileEvent()
    data class OnLabAngleClick(val index: Int, val angle: String) : ProfileEvent()
    data class OnLabSendClick(val index: Int) : ProfileEvent()
    data object OnLabExampleClick : ProfileEvent()

    data class OnPreviewToggle(val preview: Boolean) : ProfileEvent()
    data class OnPortfolioItemClick(val key: String) : ProfileEvent()
    data class OnVisibilityClick(val visibility: ProfileVisibility) : ProfileEvent()
}
