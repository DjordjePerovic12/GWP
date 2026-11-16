package com.global_water_polo.play_abroad.presentation.app.inbox

import com.global_water_polo.play_abroad.core.components.AppTab
import com.global_water_polo.play_abroad.core.utils.LockAction
import com.global_water_polo.play_abroad.presentation.base.BaseEvent

sealed class InboxEvent : BaseEvent {
    data class OnTabClick(val tab: AppTab) : InboxEvent()
    data class OnLock(val action: LockAction) : InboxEvent()
    data class OnTranscriptAnswer(val approved: Boolean) : InboxEvent()
}
