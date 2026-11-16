package com.global_water_polo.play_abroad.presentation.host

import com.global_water_polo.play_abroad.domain.model.Profile
import com.global_water_polo.play_abroad.presentation.base.BaseError
import com.global_water_polo.play_abroad.presentation.base.BaseViewState

data class HostState(
    override val isLoading: Boolean = false,
    override val error: BaseError? = null,
    val profile: Profile = Profile()
) : BaseViewState(isLoading, error) {
    companion object {
        //TODO: Sample week and placements until partner clubs upload theirs.
        val WEEK = listOf(
            "Mon 07:00" to "Swim and legs",
            "Mon 18:30" to "Team practice",
            "Tue 18:30" to "Team practice",
            "Wed 07:00" to "Gym",
            "Thu 18:30" to "Tactics, 6 on 5",
            "Sat 11:00" to "Scrimmage"
        )
        val PLACED = listOf(
            "A. R., driver, class of 2027" to "Arrives Jun 15",
            "J. M., center, class of 2026" to "Inquiry"
        )
    }
}
