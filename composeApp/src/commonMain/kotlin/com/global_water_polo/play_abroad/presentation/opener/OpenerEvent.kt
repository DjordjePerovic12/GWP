package com.global_water_polo.play_abroad.presentation.opener

import com.global_water_polo.play_abroad.domain.model.Lane
import com.global_water_polo.play_abroad.presentation.base.BaseEvent

sealed class OpenerEvent : BaseEvent {
    data class OnBeginClick(val at: Float) : OpenerEvent()
    data object OnSkipClick : OpenerEvent()
    /** The ball has settled into the onboarding badge and ivory covers the screen. */
    data object OnFinished : OpenerEvent()
    data object OnQuickStartOpen : OpenerEvent()
    data object OnQuickStartDismiss : OpenerEvent()
    data class OnQuickStart(val lane: Lane) : OpenerEvent()
}
