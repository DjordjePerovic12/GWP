package com.global_water_polo.play_abroad.core.utils

import kotlinx.datetime.LocalDate
import com.global_water_polo.play_abroad.domain.model.Profile

/** Why the app is locked (v29): the email isn't confirmed yet, or a minor's parent hasn't confirmed. */
enum class Lock {
    NONE,
    EMAIL,
    PARENT
}

/** What the Inbox's "Needs you" lists, in order. */
enum class InboxNeed {
    VERIFY_EMAIL,
    PARENT_CONFIRM,
    /** Sample: a college coach asks for the transcript. */
    TRANSCRIPT
}

/**
 * v29's lock rules (`locked`, `inboxNeeds`). Nothing is hidden while locked: a pill sits over the tab bar, and
 * saving, inquiring, uploading and subscribing open the lock sheet instead.
 */
object AccountGate {

    fun lockOf(profile: Profile, today: LocalDate = NcaaClockCalculator.today()): Lock = when {
        !profile.emailVerified -> Lock.EMAIL
        profile.isMinor(today) && !profile.parentConfirmed -> Lock.PARENT
        else -> Lock.NONE
    }

    fun locked(profile: Profile, today: LocalDate = NcaaClockCalculator.today()): Boolean = lockOf(profile, today) != Lock.NONE

    fun needs(profile: Profile, today: LocalDate = NcaaClockCalculator.today()): List<InboxNeed> = buildList {
        if (!profile.emailVerified) add(InboxNeed.VERIFY_EMAIL)
        if (profile.isMinor(today) && !profile.parentConfirmed) add(InboxNeed.PARENT_CONFIRM)
        if (profile.transcriptRequest.isEmpty()) add(InboxNeed.TRANSCRIPT)
    }

    /** The Inbox tab's badge: everything that needs them. */
    fun badge(profile: Profile, today: LocalDate = NcaaClockCalculator.today()): Int = needs(profile, today).size
}

/** What the lock layer on a tab can do: open or close the sheet, the two demo confirmations, the overlay ending. */
enum class LockAction {
    OPEN,
    DISMISS,
    OPEN_LINK,
    PARENT_CONFIRMED,
    DONE
}

/** The lock layer on a tab: the lock sheet, or the "You're in" overlay once everything is confirmed. */
data class LockView(val sheet: Boolean = false, val youreIn: Boolean = false)
