package com.global_water_polo.play_abroad.presentation.app.home

import com.global_water_polo.play_abroad.core.components.membership.MembershipSheet
import com.global_water_polo.play_abroad.core.utils.AccountGate
import com.global_water_polo.play_abroad.domain.model.ClockCountdown
import com.global_water_polo.play_abroad.core.utils.LaneInfo
import com.global_water_polo.play_abroad.core.utils.Lock
import com.global_water_polo.play_abroad.core.utils.LockView
import com.global_water_polo.play_abroad.core.utils.laneInfo
import com.global_water_polo.play_abroad.domain.model.Profile
import com.global_water_polo.play_abroad.domain.model.Program
import com.global_water_polo.play_abroad.presentation.app.profile.ProfileState
import com.global_water_polo.play_abroad.presentation.base.BaseError
import com.global_water_polo.play_abroad.presentation.base.BaseViewState

/** The sheet open on Home. */
sealed interface HomeSheet {
    /** "Your plan": the plan rows and steps, or the "Adjust my plan" chips when [adjusting]. */
    data class Plan(val adjusting: Boolean = false) : HomeSheet
    data class ProgramDetail(val program: Program) : HomeSheet
    data class Inquiry(val program: Program, val sent: Boolean = false) : HomeSheet
}

/** "Adjust my plan": the answers being changed, saved by "Rebuild my plan". */
data class PlanDraft(
    val now: String = "",
    val gradYear: Int? = null,
    val availability: String = "",
    val firstStep: String = ""
)

data class HomeState(
    override val isLoading: Boolean = false,
    override val error: BaseError? = null,
    val profile: Profile = Profile(),
    val countdown: ClockCountdown? = null,
    val sheet: HomeSheet? = null,
    val draft: PlanDraft = PlanDraft(),
    val membershipSheet: MembershipSheet? = null,
    val lockView: LockView = LockView()
) : BaseViewState(isLoading, error) {

    val lane: LaneInfo? get() = profile.laneInfo()
    val lock: Lock get() = AccountGate.lockOf(profile)
    val inboxBadge: Int get() = AccountGate.badge(profile)

    /** The Profile tab's state over the same profile: the pathway section and program sheets draw from it. */
    val pathway: ProfileState get() = ProfileState(profile = profile)
}
