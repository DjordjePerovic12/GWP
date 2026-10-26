package com.global_water_polo.play_abroad.presentation.app.settings

import com.global_water_polo.play_abroad.core.components.membership.Membership
import com.global_water_polo.play_abroad.core.components.membership.MembershipSheet
import com.global_water_polo.play_abroad.core.components.membership.membershipOf
import com.global_water_polo.play_abroad.domain.model.Profile
import com.global_water_polo.play_abroad.presentation.base.BaseError
import com.global_water_polo.play_abroad.presentation.base.BaseViewState

data class SettingsState(
    override val isLoading: Boolean = false,
    override val error: BaseError? = null,
    val profile: Profile = Profile(),
    val membershipSheet: MembershipSheet? = null
) : BaseViewState(isLoading, error) {
    val membership: Membership get() = membershipOf(profile)

    /** Same count as the Portfolio: six items plus one for having a profile. */
    val portfolioPercent: Int
        get() {
            val done = listOf("photo", "video", "personal", "transcript", "letters").count { it in profile.uploads } +
                (if (profile.position.isNotEmpty() || "position" in profile.uploads) 1 else 0)
            return (done + 1) * 100 / 7
        }
}
