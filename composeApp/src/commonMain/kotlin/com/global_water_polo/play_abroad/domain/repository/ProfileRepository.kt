package com.global_water_polo.play_abroad.domain.repository

import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.plus
import com.global_water_polo.play_abroad.core.utils.AccountGate
import com.global_water_polo.play_abroad.core.utils.LockAction
import com.global_water_polo.play_abroad.core.utils.LockView
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

/** Demo "Open the link": marks the email confirmed. True when that unlocks the app (no parent still waiting). */
fun ProfileRepository.verifyEmail(): Boolean {
    update { it.copy(emailVerified = true) }
    return !AccountGate.locked(profile.value)
}

/** Demo "They confirmed": the parent confirmed. True when that unlocks the app. */
fun ProfileRepository.confirmParent(): Boolean {
    update { it.copy(parentConfirmed = true) }
    return !AccountGate.locked(profile.value)
}

/**
 * One step of the lock layer. Confirming the email while a parent still has to confirm keeps the sheet open
 * (it switches to "Waiting for your parent"); the last confirmation shows "You're in". DONE is handled by
 * the screen (it goes Home).
 */
fun ProfileRepository.lockStep(action: LockAction, view: LockView): LockView = when (action) {
    LockAction.OPEN -> view.copy(sheet = true)
    LockAction.DISMISS -> view.copy(sheet = false)
    LockAction.OPEN_LINK -> if (verifyEmail()) LockView(youreIn = true) else view.copy(sheet = true)
    LockAction.PARENT_CONFIRMED -> if (confirmParent()) LockView(youreIn = true) else view
    LockAction.DONE -> LockView()
}
