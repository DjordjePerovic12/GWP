package com.global_water_polo.play_abroad.domain.model

import io.github.vinceglb.filekit.PlatformFile
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
    val now: Set<String> = emptySet(),
    val level: Set<String> = emptySet(),
    val gradMonth: Int? = null,
    val gradYear: Int? = null,
    val goal: String = "",
    val club: String = "",
    val stops: Set<String> = emptySet(),
    val plan: String? = null,
    /** Last day of the free trial; set when a plan starts. */
    val trialEnds: LocalDate? = null,
    val yearlyBilling: Boolean = true,
    val appliedProgram: String = "",
    val looking: Set<String> = emptySet(),
    val travelStartOffset: Int? = null,
    val travelMonths: Int = 0,
    /** Positions played; the first is the main one. */
    val positions: List<String> = emptyList(),
    val leftHanded: Boolean = false,
    val visibility: ProfileVisibility = ProfileVisibility.COACHES,
    val finished: Set<String> = emptySet(),
    val portfolio: Portfolio = Portfolio(),
    val reviewSubmitted: Boolean = false,
    val pathway: Pathway? = null,
    val inquiries: Set<String> = emptySet(),
    val clinicRequests: Set<Int> = emptySet(),
    /** GWP LAB: the video picked for each angle ("above", "under") per test, and the tests sent for coach review. */
    val labAngles: Map<Int, Map<String, PlatformFile>> = emptyMap(),
    val labSent: Set<Int> = emptySet(),
    val saved: Set<String> = emptySet(),
    val likes: Set<String> = emptySet(),
    /** Experiences built on Globe+, drafts and sent inquiries; the first one is the next trip on Travel. */
    val trips: List<Trip> = emptyList(),
    val flightRequests: Set<String> = emptySet(),
    val homeAirport: String = "LAX",
    val travelChecks: Set<String> = emptySet(),
    val notifications: Boolean = true
) {
    /** The main position, or empty. */
    val position: String get() = positions.firstOrNull().orEmpty()
}

enum class ProfileVisibility {
    ALL,
    COACHES,
    PRIVATE
}
