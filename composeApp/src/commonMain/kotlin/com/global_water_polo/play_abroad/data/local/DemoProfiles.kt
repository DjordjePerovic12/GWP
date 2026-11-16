package com.global_water_polo.play_abroad.data.local

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import com.global_water_polo.play_abroad.core.utils.NcaaClockCalculator
import com.global_water_polo.play_abroad.core.utils.PathwayCalculator
import com.global_water_polo.play_abroad.domain.model.Lane
import com.global_water_polo.play_abroad.domain.model.Profile

//TODO: Testing shortcut only (Quick start on the intro). Hidden when DEMO_SHORTCUTS=false in gradle.properties.

/**
 * Ready-made profiles for Quick start: what a finished onboarding would save, so a lane's home opens without
 * going through it. Ages and dates are relative to today, so the NCAA clock always has something to show.
 * Test data only; no real person.
 */
object DemoProfiles {

    /** The lanes Quick start offers. The club lane has no home of its own yet. */
    val lanes = listOf(Lane.ATHLETE, Lane.PARENT, Lane.COACH)

    fun of(lane: Lane, today: LocalDate = NcaaClockCalculator.today()): Profile = when (lane) {
        Lane.ATHLETE, Lane.PARENT -> {
            val gradYear = today.year + 2
            val profile = Profile(
                lane = lane,
                birthday = today.minus(DatePeriod(years = 16, months = 3)),
                firstName = if (lane == Lane.ATHLETE) "Alex" else "Sam",
                lastName = "Test",
                email = "test@example.com",
                now = setOf("looking"),
                level = setOf("hs", "club"),
                gradMonth = 6,
                gradYear = gradYear,
                goal = "college",
                club = "school",
                stops = setOf("cost"),
                travelStartOffset = PathwayCalculator.suggestStart(6, gradYear, "school", today),
                travelMonths = 3,
                onboarded = true
            )
            if (lane == Lane.PARENT) profile.copy(kidName = "Alex", kidContact = "alex@example.com") else profile
        }
        Lane.COACH -> Profile(
            lane = lane,
            firstName = "Jordan",
            lastName = "Test",
            email = "coach@example.com",
            organization = "Test Aquatics Club",
            onboarded = true
        )
        Lane.CLUB -> Profile()
        Lane.FAN -> Profile(lane = lane, firstName = "Sam", email = "fan@example.com", onboarded = true)
    }
}
