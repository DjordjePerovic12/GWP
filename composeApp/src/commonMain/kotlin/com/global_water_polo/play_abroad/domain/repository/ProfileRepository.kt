package com.global_water_polo.play_abroad.domain.repository

import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.plus
import com.global_water_polo.play_abroad.core.utils.NcaaClockCalculator
import com.global_water_polo.play_abroad.domain.model.Profile

interface ProfileRepository {
    val profile: StateFlow<Profile>

    /** False until the saved profile has been read at launch. */
    val loaded: StateFlow<Boolean>
    fun update(transform: (Profile) -> Profile)
    fun reset()
}

/** Starts a 7-day free trial of [plan] ("elite" or "next"). Prototype: nothing is charged. */
fun ProfileRepository.startPlan(plan: String, yearly: Boolean = true) = update {
    it.copy(plan = plan, yearlyBilling = yearly, trialEnds = it.trialEnds ?: NcaaClockCalculator.today().plus(DatePeriod(days = 7)))
}

/** Switches an Elite trial down to Next, keeping the trial dates. */
fun ProfileRepository.switchToNext() = update { it.copy(plan = "next", yearlyBilling = true) }

fun ProfileRepository.cancelPlan() = update { it.copy(plan = null, trialEnds = null) }
