package com.global_water_polo.play_abroad.presentation.onboarding

import kotlinx.datetime.LocalDate
import com.global_water_polo.play_abroad.domain.model.BirthdayProblem
import com.global_water_polo.play_abroad.domain.model.ClockCountdown
import com.global_water_polo.play_abroad.domain.model.Lane
import com.global_water_polo.play_abroad.domain.model.NcaaWindow
import com.global_water_polo.play_abroad.presentation.base.BaseError
import com.global_water_polo.play_abroad.presentation.base.BaseViewState

/** Everything typed or picked across the onboarding steps of one lane. Shared by every step screen. */
data class OnboardingState(
    override val isLoading: Boolean = false,
    override val error: BaseError? = null,
    val lane: Lane = Lane.ATHLETE,
    val birthMonth: Int? = null,
    val birthDay: Int? = null,
    val birthYear: Int? = null,
    val birthdayProblem: BirthdayProblem? = null,
    val window: NcaaWindow? = null,
    val countdown: ClockCountdown? = null,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
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
    val shareCardReady: Boolean = false
) : BaseViewState(isLoading, error) {

    val birthday: LocalDate?
        get() = if (birthYear != null && birthMonth != null && birthDay != null && birthdayProblem == null) {
            LocalDate(birthYear, birthMonth, birthDay)
        } else null

    val isNameValid: Boolean get() = firstName.isNotBlank() && lastName.isNotBlank()
    val isEmailValid: Boolean get() = EMAIL.matches(email.trim())
    val canSave: Boolean get() = password.length >= 8 && heardFrom.isNotEmpty() && (lane != Lane.PARENT || relation.isNotEmpty())
    val canInvite: Boolean get() = kidName.isNotBlank() && kidContact.isNotBlank()
    val canCreateAccount: Boolean get() = firstName.isNotBlank() && organization.isNotBlank() && isEmailValid
    val canTravelContinue: Boolean get() = travelStartOffset != null && travelMonths > 0

    companion object {
        private val EMAIL = Regex(".+@.+\\..+")
    }
}
