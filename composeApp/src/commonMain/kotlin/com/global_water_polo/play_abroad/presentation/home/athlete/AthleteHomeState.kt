package com.global_water_polo.play_abroad.presentation.home.athlete

import com.global_water_polo.play_abroad.domain.model.ClockCountdown
import com.global_water_polo.play_abroad.domain.model.NcaaWindow
import com.global_water_polo.play_abroad.domain.model.ProfileVisibility
import com.global_water_polo.play_abroad.presentation.base.BaseError
import com.global_water_polo.play_abroad.presentation.base.BaseViewState

data class AthleteHomeState(
    override val isLoading: Boolean = false,
    override val error: BaseError? = null,
    val firstName: String = "",
    val lastName: String = "",
    val position: String = "",
    val leftHanded: Boolean = false,
    val window: NcaaWindow? = null,
    val countdown: ClockCountdown? = null,
    val visibility: ProfileVisibility = ProfileVisibility.COACHES,
    val finished: Set<String> = emptySet(),
    val reviewSubmitted: Boolean = false,
    val travelStartOffset: Int? = null,
    val travelMonths: Int = 0
) : BaseViewState(isLoading, error) {

    fun isDone(key: String): Boolean = key in finished || (key == TODO_POSITION && position.isNotEmpty())

    val doneCount: Int get() = TODOS.count { isDone(it) }

    /** Profile strength as the prototype shows it: 20% for signing up, 16% per finished section. */
    val profilePercent: Int get() = 20 + doneCount * 16

    companion object {
        const val TODO_POSITION = "pos"
        val TODOS = listOf(TODO_POSITION, "club", "body", "school", "goals")
    }
}
