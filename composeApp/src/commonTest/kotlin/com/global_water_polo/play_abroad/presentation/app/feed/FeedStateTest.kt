package com.global_water_polo.play_abroad.presentation.app.feed

import com.global_water_polo.play_abroad.domain.model.Profile
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** v29's saved folders: grouping in Saved and clips saved before folders existed. */
class FeedStateTest {

    @Test
    fun clipsSavedBeforeFoldersLandInFundamentals() {
        val state = FeedState(profile = Profile(saved = setOf("r2", "c0")))
        assertEquals("Fundamentals", state.folderOf(2))
        assertNull(state.folderOf(3))
        assertEquals(listOf("Fundamentals" to listOf(2)), state.savedClips)
        assertEquals(listOf(0), state.savedCourses)
    }

    @Test
    fun savedGroupsFollowTheFolderOrderAndSkipEmptyFolders() {
        val state = FeedState(
            profile = Profile(
                folders = listOf("Pre-game"),
                clipFolders = mapOf("r5" to "Pre-game", "r1" to "Mindset", "r4" to "Mindset")
            )
        )
        assertEquals(listOf("Mindset", "Strength and performance", "Fundamentals", "Pre-game"), state.folders)
        assertEquals(listOf("Mindset" to listOf(1, 4), "Pre-game" to listOf(5)), state.savedClips)
    }
}
