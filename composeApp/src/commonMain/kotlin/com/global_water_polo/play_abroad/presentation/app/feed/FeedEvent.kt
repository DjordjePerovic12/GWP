package com.global_water_polo.play_abroad.presentation.app.feed

import com.global_water_polo.play_abroad.core.components.AppTab
import com.global_water_polo.play_abroad.core.components.membership.MembershipSheet
import com.global_water_polo.play_abroad.presentation.base.BaseEvent

sealed class FeedEvent : BaseEvent {
    data class OnTabClick(val tab: AppTab) : FeedEvent()
    data class OnFeedTabClick(val tab: FeedTab) : FeedEvent()
    data class OnLikeClick(val story: Int) : FeedEvent()
    data class OnSaveStoryClick(val story: Int) : FeedEvent()
    data class OnStoryActionClick(val story: Int) : FeedEvent()
    data class OnCourseClick(val course: Int) : FeedEvent()
    data class OnCourseCtaClick(val course: Int) : FeedEvent()
    data object OnCourseDismiss : FeedEvent()
    data class OnMembershipSheet(val sheet: MembershipSheet?) : FeedEvent()
    data class OnStartPlan(val plan: String, val yearly: Boolean) : FeedEvent()
    data object OnSwitchToNext : FeedEvent()
    data object OnCancelPlan : FeedEvent()
}
