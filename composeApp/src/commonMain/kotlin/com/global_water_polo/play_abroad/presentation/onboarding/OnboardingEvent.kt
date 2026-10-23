package com.global_water_polo.play_abroad.presentation.onboarding

import com.global_water_polo.play_abroad.domain.model.Lane
import com.global_water_polo.play_abroad.presentation.base.BaseEvent

sealed class OnboardingEvent : BaseEvent {
    data object NavigateBack : OnboardingEvent()
    data object OnBackToGlobeClick : OnboardingEvent()
    data object OnBackToStartClick : OnboardingEvent()
    data class OnRoleClick(val lane: Lane) : OnboardingEvent()

    data class OnBirthdayChanged(val month: Int?, val day: Int?, val year: Int?, val fromUser: Boolean) : OnboardingEvent()
    data object OnBirthdayContinueClick : OnboardingEvent()
    data object OnClockContinueClick : OnboardingEvent()

    data class OnFirstNameChanged(val value: String) : OnboardingEvent()
    data class OnLastNameChanged(val value: String) : OnboardingEvent()
    data object OnNameContinueClick : OnboardingEvent()
    data class OnEmailChanged(val value: String) : OnboardingEvent()
    data object OnEmailContinueClick : OnboardingEvent()

    data object OnShareClick : OnboardingEvent()
    data object OnWindowContinueClick : OnboardingEvent()

    data class OnLookToggle(val key: String) : OnboardingEvent()
    data object OnLookContinueClick : OnboardingEvent()
    data class OnTravelStartClick(val offset: Int) : OnboardingEvent()
    data class OnTravelLengthChanged(val months: Int) : OnboardingEvent()
    data object OnTravelContinueClick : OnboardingEvent()
    data class OnPositionClick(val position: String) : OnboardingEvent()
    data object OnLeftHandedToggle : OnboardingEvent()
    data object OnPositionContinueClick : OnboardingEvent()

    data class OnPasswordChanged(val value: String) : OnboardingEvent()
    data class OnHeardFromClick(val value: String) : OnboardingEvent()
    data class OnRelationClick(val value: String) : OnboardingEvent()
    data object OnSaveClick : OnboardingEvent()

    data class OnKidNameChanged(val value: String) : OnboardingEvent()
    data class OnKidContactChanged(val value: String) : OnboardingEvent()
    data object OnInviteClick : OnboardingEvent()
    data object OnInviteDoneClick : OnboardingEvent()

    data class OnOrganizationChanged(val value: String) : OnboardingEvent()
    data object OnCreateAccountClick : OnboardingEvent()
}
