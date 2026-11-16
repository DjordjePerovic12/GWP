package com.global_water_polo.play_abroad.presentation.app.inbox

import com.global_water_polo.play_abroad.core.utils.AccountGate
import com.global_water_polo.play_abroad.core.utils.InboxNeed
import com.global_water_polo.play_abroad.core.utils.Lock
import com.global_water_polo.play_abroad.core.utils.LockView
import com.global_water_polo.play_abroad.core.utils.isMinor
import com.global_water_polo.play_abroad.domain.model.Profile
import com.global_water_polo.play_abroad.presentation.base.BaseError
import com.global_water_polo.play_abroad.presentation.base.BaseViewState

/** One row of "Activity": what happened and when. */
data class Activity(val text: String, val time: String)

data class InboxState(
    override val isLoading: Boolean = false,
    override val error: BaseError? = null,
    val profile: Profile = Profile(),
    val lockView: LockView = LockView()
) : BaseViewState(isLoading, error) {

    val needs: List<InboxNeed> get() = AccountGate.needs(profile)
    val lock: Lock get() = AccountGate.lockOf(profile)
    val minor: Boolean get() = profile.isMinor()

    companion object {
        //TODO: Sample activity until GWP sends real notifications.
        val ACTIVITY = listOf(
            Activity("A college coach viewed your card", "2 hours ago"),
            Activity("New in the library: Center play", "Today"),
            Activity("Your Kotor inquiry is with GWP for review", "Yesterday"),
            Activity("A coach you follow posted a skills video", "Monday")
        )
    }
}
