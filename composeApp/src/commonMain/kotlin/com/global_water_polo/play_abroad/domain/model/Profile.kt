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
    val position: String = "",
    val leftHanded: Boolean = false,
    val visibility: ProfileVisibility = ProfileVisibility.COACHES,
    val finished: Set<String> = emptySet(),
    val uploads: Set<String> = emptySet(),
    val reviewSubmitted: Boolean = false,
    val pathway: Pathway? = null,
    val inquiries: Set<String> = emptySet(),
    val clinicRequests: Set<Int> = emptySet(),
    /** GWP LAB: filmed angles ("above", "under") per test, and the tests sent for coach review. */
    val labAngles: Map<Int, Set<String>> = emptyMap(),
    val labSent: Set<Int> = emptySet(),
    val saved: Set<String> = emptySet(),
    val likes: Set<String> = emptySet(),
    /** Cities the athlete asked GWP about from Globe+; the first one is the next trip on Travel. */
    val trips: List<String> = emptyList(),
    val flightRequests: Set<String> = emptySet(),
    val homeAirport: String = "LAX",
    val travelChecks: Set<String> = emptySet(),
    val notifications: Boolean = true
)

enum class ProfileVisibility {
    ALL,
    COACHES,
    PRIVATE
}
