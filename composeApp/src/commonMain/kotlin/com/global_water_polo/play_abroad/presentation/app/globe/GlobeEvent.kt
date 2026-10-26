package com.global_water_polo.play_abroad.presentation.app.globe

import kotlinx.datetime.LocalDate
import com.global_water_polo.play_abroad.core.components.AppTab
import com.global_water_polo.play_abroad.domain.model.Trip
import com.global_water_polo.play_abroad.presentation.base.BaseEvent

sealed class GlobeEvent : BaseEvent {
    data class OnTabClick(val tab: AppTab) : GlobeEvent()
    data class OnQueryChanged(val query: String) : GlobeEvent()
    data class OnRegionClick(val region: String) : GlobeEvent()
    data class OnCityClick(val cityId: String) : GlobeEvent()
    data class OnClubClick(val clubId: String) : GlobeEvent()
    data object OnSheetDismiss : GlobeEvent()
    data class OnCountryClick(val country: String) : GlobeEvent()
    data class OnSexClick(val sex: String) : GlobeEvent()
    data class OnGroupClick(val index: Int) : GlobeEvent()
    data object OnPopupDismiss : GlobeEvent()

    // Build your experience
    data class OnBuildClick(val cityId: String, val clubId: String = "") : GlobeEvent()
    data class OnDraftChanged(val draft: Trip) : GlobeEvent()
    data object OnDatesClick : GlobeEvent()
    data class OnDatesApplied(val arrive: LocalDate, val leave: LocalDate) : GlobeEvent()
    data object OnTourClick : GlobeEvent()
    data object OnApartmentRequestClick : GlobeEvent()
    /** Closes the date picker or the tour and goes back to the builder. */
    data object OnBackToBuild : GlobeEvent()
    data object OnReviewClick : GlobeEvent()

    // Review and the cart
    data class OnEditTripClick(val tripId: Long) : GlobeEvent()
    data class OnNotesChanged(val tripId: Long, val notes: String) : GlobeEvent()
    data class OnSendInquiryClick(val tripId: Long) : GlobeEvent()
    data object OnTripsClick : GlobeEvent()
    data class OnTripOpenClick(val tripId: Long) : GlobeEvent()
    data class OnTripRemoveClick(val tripId: Long) : GlobeEvent()
}
