package com.global_water_polo.play_abroad.core.utils

import kotlinx.datetime.LocalDate
import com.global_water_polo.play_abroad.domain.model.Lane
import com.global_water_polo.play_abroad.domain.model.Profile
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** v29's lock and inbox rules. */
class AccountGateTest {

    private val today = LocalDate(2026, 11, 16)
    private val adult = Profile(lane = Lane.ATHLETE, birthday = LocalDate(2006, 3, 9))
    private val minor = Profile(lane = Lane.ATHLETE, birthday = LocalDate(2010, 5, 1))

    @Test
    fun profilesFromBeforeV29OpenUnlocked() {
        assertEquals(Lock.NONE, AccountGate.lockOf(adult, today))
        assertFalse(AccountGate.locked(minor, today))
    }

    @Test
    fun anUnconfirmedEmailLocksFirst() {
        val p = minor.copy(emailVerified = false, parentConfirmed = false)
        assertEquals(Lock.EMAIL, AccountGate.lockOf(p, today))
        assertEquals(listOf(InboxNeed.VERIFY_EMAIL, InboxNeed.PARENT_CONFIRM, InboxNeed.TRANSCRIPT), AccountGate.needs(p, today))
    }

    @Test
    fun aMinorWaitsForTheParentAfterTheEmail() {
        val p = minor.copy(emailVerified = true, parentConfirmed = false)
        assertEquals(Lock.PARENT, AccountGate.lockOf(p, today))
    }

    @Test
    fun parentConfirmationOnlyMattersForMinorAthletes() {
        assertFalse(AccountGate.locked(adult.copy(parentConfirmed = false), today))
        assertFalse(AccountGate.locked(minor.copy(lane = Lane.PARENT, parentConfirmed = false), today))
    }

    @Test
    fun theSampleTranscriptRequestKeepsTheBadgeAtOneUntilAnswered() {
        assertEquals(1, AccountGate.badge(adult, today))
        assertTrue(AccountGate.needs(adult.copy(transcriptRequest = "declined"), today).isEmpty())
    }
}
