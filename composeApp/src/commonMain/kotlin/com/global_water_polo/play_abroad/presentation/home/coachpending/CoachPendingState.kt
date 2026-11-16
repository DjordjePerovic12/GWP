package com.global_water_polo.play_abroad.presentation.home.coachpending

import com.global_water_polo.play_abroad.domain.model.Profile
import com.global_water_polo.play_abroad.presentation.base.BaseError
import com.global_water_polo.play_abroad.presentation.base.BaseViewState

data class CoachPendingState(
    override val isLoading: Boolean = false,
    override val error: BaseError? = null,
    val profile: Profile = Profile()
) : BaseViewState(isLoading, error) {
    val staff: Boolean get() = profile.coachTier == "staff"

    /** A trip, tournament or clinic was picked: trip planning with a GWP lead. */
    val travels: Boolean get() = profile.coachNeeds.any { it in TRAVEL_NEEDS }

    private companion object {
        val TRAVEL_NEEDS = setOf("trip", "tournament", "clinic")
    }
}
