package com.global_water_polo.play_abroad.domain.model

import kotlinx.datetime.LocalDate

/** What the user tells us during onboarding. Kept in memory only; nothing is saved or sent yet. */
data class Profile(
    val lane: Lane? = null,
    val birthday: LocalDate? = null,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val heardFrom: String = "",
    val relation: String = "",
    val kidName: String = "",
    val kidContact: String = "",
    val invited: Boolean = false,
    val organization: String = "",
    val looking: Set<String> = emptySet(),
    val travelStartOffset: Int? = null,
    val travelMonths: Int = 0,
    val position: String = "",
    val leftHanded: Boolean = false,
    val visibility: ProfileVisibility = ProfileVisibility.COACHES,
    val finished: Set<String> = emptySet(),
    val uploads: Set<String> = emptySet(),
    val reviewSubmitted: Boolean = false
)

enum class ProfileVisibility {
    ALL,
    COACHES,
    PRIVATE
}
