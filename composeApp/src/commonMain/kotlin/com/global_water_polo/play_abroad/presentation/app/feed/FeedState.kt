package com.global_water_polo.play_abroad.presentation.app.feed

import com.global_water_polo.play_abroad.core.components.membership.Membership
import com.global_water_polo.play_abroad.core.components.membership.MembershipSheet
import com.global_water_polo.play_abroad.core.components.membership.membershipOf
import com.global_water_polo.play_abroad.domain.model.Profile
import com.global_water_polo.play_abroad.presentation.base.BaseError
import com.global_water_polo.play_abroad.presentation.base.BaseViewState

enum class FeedTab {
    FOR_YOU,
    LIBRARY,
    SAVED
}

/**
 * A story in For you. [action] is where its button leads: "lab", "globe" or "city:<id>".
 * [elite] stories are only for GWP Elite members.
 */
data class Story(val category: String, val title: String, val meta: String, val image: String, val action: String? = null, val actionLabel: String? = null, val elite: Boolean = false)

/** A Library course and its lessons; [free] courses are open to everyone. */
data class Course(val title: String, val category: String, val image: String, val lessons: List<String>, val free: Boolean = false)

data class FeedState(
    override val isLoading: Boolean = false,
    override val error: BaseError? = null,
    val profile: Profile = Profile(),
    val tab: FeedTab = FeedTab.FOR_YOU,
    val course: Int? = null,
    val membershipSheet: MembershipSheet? = null
) : BaseViewState(isLoading, error) {

    val membership: Membership get() = membershipOf(profile)

    fun storyLocked(story: Story): Boolean = story.elite && !membership.elite
    fun courseLocked(course: Course): Boolean = !course.free && !membership.elite

    companion object {
        //TODO: Sample stories and courses until GWP films and publishes them.
        val STORIES = listOf(
            Story("GWP LAB", "What your legs do underwater", "0:48", "f_under", "lab", "Open GWP LAB"),
            Story("Club stories", "Where the sea meets the old town", "Kotor, Montenegro", "f_harbour", "city:kotor", "Explore Kotor"),
            Story("Coaching", "Win the position before the ball arrives", "2:10", "f_coach"),
            Story("Abroad", "The first morning in a new city", "Athlete story", "f_pier", "globe", "See clubs on the globe"),
            Story("Training", "Goalkeeping: read the hips, not the arm", "3:05", "f_keeper", elite = true),
            Story("GWP LAB", "The shot, seen from below", "1:02", "f_shot", "lab", "Open GWP LAB"),
            Story("Strength", "Rotational power for the outside shot", "6 min", "f_gym", elite = true),
            Story("Club stories", "Night games and the huddle after", "Club story", "f_huddle", "globe", "See clubs on the globe"),
            Story("Abroad", "Team dinners are part of the program", "Athlete story", "f_dinner", "globe", "Plan a stay abroad")
        )

        val COURSES = listOf(
            Course("Eggbeater foundations", "Technique", "f_under", listOf("Knee width and hip height", "The recovery path", "Rhythm under load", "Hands free: holding height", "Moving sideways", "Eggbeater under contact"), free = true),
            Course("Your first 30 days abroad", "Life abroad", "f_pier", listOf("Before you fly", "Your first training week", "Living on a club schedule", "Food, sleep and recovery", "Staying on top of school"), free = true),
            Course("Center play", "Position", "p_residency", listOf("Winning position early", "Sealing the defender", "Reading the entry pass", "Turning on the strong side", "The backhand", "Drawing exclusions", "Working with drivers", "Center play when tired")),
            Course("Goalkeeping reads", "Position", "f_keeper", listOf("Base position", "Reading the hips", "Lateral push", "Penalty reads", "The counterattack outlet", "Calling the press", "Saves under the bar")),
            Course("Strength for water polo", "Performance", "f_gym", listOf("Movement screen", "Hip power", "Rotational power", "Shoulder health", "Single-leg strength", "Core under contact", "From the gym to the pool", "Deload weeks", "Workouts on the road", "Testing week")),
            Course("Film room: counterattack", "Tactics", "f_coach", listOf("Reading the turnover", "The first three strokes", "Lanes and spacing", "The trailer", "Defending the transition", "Decisions at speed")),
            Course("Shooting mechanics", "Technique", "f_shot", listOf("Lift and base", "Arm path", "Wrist and release", "Fakes that sell", "Shooting off the pass", "Shooting tired")),
            Course("Recovery and fueling", "Performance", "f_dinner", listOf("Fueling double days", "Hydration", "Sleep on the road", "Recovery between games"))
        )
    }
}
