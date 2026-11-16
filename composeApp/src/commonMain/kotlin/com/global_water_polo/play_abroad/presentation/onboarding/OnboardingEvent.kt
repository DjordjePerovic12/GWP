package com.global_water_polo.play_abroad.presentation.onboarding

import com.global_water_polo.play_abroad.domain.model.Lane
import com.global_water_polo.play_abroad.presentation.base.BaseEvent

sealed class OnboardingEvent : BaseEvent {
    data object NavigateBack : OnboardingEvent()
    data object OnBackToGlobeClick : OnboardingEvent()
    data object OnBackToStartClick : OnboardingEvent()
    data class OnRoleClick(val lane: Lane) : OnboardingEvent()
    /** "college", "team" or "admin" on What do you coach. */
    data class OnCoachTierClick(val tier: String) : OnboardingEvent()
    data object OnFanContinueClick : OnboardingEvent()

    /** Continue on a step that has nothing to validate (free year, tour, link, world). */
    data class OnStepContinueClick(val step: FlowStep) : OnboardingEvent()

    data class OnAskPick(val step: AskStep, val key: String) : OnboardingEvent()
    data class OnAskContinueClick(val step: AskStep) : OnboardingEvent()

    data class OnBirthdayChanged(val month: Int?, val day: Int?, val year: Int?, val fromUser: Boolean) : OnboardingEvent()
    data object OnBirthdayContinueClick : OnboardingEvent()
    data object OnClockContinueClick : OnboardingEvent()

    data class OnFirstNameChanged(val value: String) : OnboardingEvent()
    data class OnLastNameChanged(val value: String) : OnboardingEvent()
    data object OnNameContinueClick : OnboardingEvent()

    data class OnEnrollMonthClick(val month: Int) : OnboardingEvent()
    data class OnEnrollYearClick(val year: Int) : OnboardingEvent()
    data object OnEnrollContinueClick : OnboardingEvent()

    /** The suggested finish: yes, a different date, or already finished. */
    data object OnGradSuggestYesClick : OnboardingEvent()
    data object OnGradSuggestDifferentClick : OnboardingEvent()
    data object OnGradSuggestDoneClick : OnboardingEvent()

    data class OnAvailabilityClick(val value: String) : OnboardingEvent()
    data class OnFirstStepClick(val value: String) : OnboardingEvent()
    data object OnAvailContinueClick : OnboardingEvent()

    data object OnLearnNextClick : OnboardingEvent()
    data object OnLearnBackClick : OnboardingEvent()

    data object OnReadyContinueClick : OnboardingEvent()
    data class OnParentEmailChanged(val value: String) : OnboardingEvent()

    data class OnGradChanged(val month: Int?, val year: Int?) : OnboardingEvent()
    data object OnGradContinueClick : OnboardingEvent()

    data class OnEmailChanged(val value: String) : OnboardingEvent()
    data object OnEmailContinueClick : OnboardingEvent()

    data class OnHeardFromClick(val value: String) : OnboardingEvent()
    data class OnRelationClick(val value: String) : OnboardingEvent()
    data object OnResendLinkClick : OnboardingEvent()

    data class OnTrialPick(val plan: String) : OnboardingEvent()
    data object OnTrialStartClick : OnboardingEvent()
    data object OnTrialLaterClick : OnboardingEvent()

    data class OnProgramPick(val key: String) : OnboardingEvent()
    data object OnMatchApplyClick : OnboardingEvent()
    data object OnMatchLaterClick : OnboardingEvent()

    data class OnLookToggle(val key: String) : OnboardingEvent()
    data object OnLookContinueClick : OnboardingEvent()
    data class OnTravelStartClick(val offset: Int) : OnboardingEvent()
    data class OnTravelLengthChanged(val months: Int) : OnboardingEvent()
    data object OnTravelContinueClick : OnboardingEvent()

    data class OnKidNameChanged(val value: String) : OnboardingEvent()
    data class OnKidContactChanged(val value: String) : OnboardingEvent()
    data object OnInviteClick : OnboardingEvent()
    data object OnInviteDoneClick : OnboardingEvent()

    data object OnSeeAllProgramsClick : OnboardingEvent()
    data class OnOfferPick(val key: String) : OnboardingEvent()
    data object OnOffersPrimaryClick : OnboardingEvent()
    data object OnOffersLaterClick : OnboardingEvent()
    data object OnEliteStartClick : OnboardingEvent()

    data object OnTripPlanContinueClick : OnboardingEvent()

    data class OnClubCityChanged(val value: String) : OnboardingEvent()
    data class OnClubCountryClick(val value: String) : OnboardingEvent()
    data object OnPlaceClubClick : OnboardingEvent()
    data object OnClubPlacedContinueClick : OnboardingEvent()
    data object OnClubPlacedBackClick : OnboardingEvent()
    data class OnStaffCoachChanged(val value: String) : OnboardingEvent()
    data class OnStaffSizeClick(val value: String) : OnboardingEvent()
    data class OnLanguageToggle(val value: String) : OnboardingEvent()
    data object OnStaffContinueClick : OnboardingEvent()

    data class OnOrganizationChanged(val value: String) : OnboardingEvent()
    data object OnCreateAccountClick : OnboardingEvent()

    // Coach paths (v29): college, high school or club, GWP staff.
    data object OnCoachInviteClick : OnboardingEvent()
    data class OnSchoolChanged(val value: String) : OnboardingEvent()
    data class OnDivisionClick(val key: String) : OnboardingEvent()
    data class OnCoachTeamClick(val key: String) : OnboardingEvent()
    data class OnCoachRoleClick(val key: String) : OnboardingEvent()
    data class OnRecruitPositionClick(val key: String) : OnboardingEvent()
    data class OnRecruitClassClick(val year: Int) : OnboardingEvent()
    data class OnIntlClick(val key: String) : OnboardingEvent()
    data class OnHighSchoolChanged(val value: String) : OnboardingEvent()
    data class OnCoachClubChanged(val value: String) : OnboardingEvent()
    data class OnNeedClick(val key: String) : OnboardingEvent()
    data class OnInviteCodeChanged(val value: String) : OnboardingEvent()
    data class OnStaffRoleClick(val key: String) : OnboardingEvent()
    data class OnCoachContinueClick(val step: FlowStep) : OnboardingEvent()
}
