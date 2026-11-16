package com.global_water_polo.play_abroad.presentation.onboarding.ask.components

import org.jetbrains.compose.resources.StringResource
import com.global_water_polo.play_abroad.domain.model.Lane
import com.global_water_polo.play_abroad.presentation.onboarding.AskStep
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingState
import com.global_water_polo.play_abroad.presentation.onboarding.ProbeKind
import com.global_water_polo.play_abroad.resources.*

/** One answer: [say] is the honest line that appears under the list once it is picked. */
data class AskOption(
    val key: String,
    val title: StringResource,
    val sub: StringResource? = null,
    val say: StringResource? = null
)

data class AskQuestion(
    val title: StringResource,
    val help: StringResource?,
    val options: List<AskOption>
)

/** The prototype's NOW_OPTS, LEVEL_OPTS, GOAL_OPTS, CLUB_OPTS, probeCfg and STOP, per lane. */
fun askQuestion(step: AskStep, state: OnboardingState): AskQuestion {
    val parent = state.lane == Lane.PARENT
    fun t(athlete: StringResource, forParent: StringResource) = if (parent) forParent else athlete
    return when (step) {
        AskStep.NOW -> AskQuestion(
            title = t(Res.string.now_title_athlete, Res.string.now_title_parent),
            help = Res.string.pick_one,
            // v29: five clean answers, pick one. It decides which screens follow.
            options = listOfNotNull(
                AskOption("committed", Res.string.now_committed),
                AskOption("looking", Res.string.now_looking),
                AskOption("college", Res.string.now_college),
                AskOption("pro", Res.string.now_pro, Res.string.now_pro_sub),
                AskOption("unsure", Res.string.now_unsure),
                if (parent) AskOption("trip", Res.string.now_trip, Res.string.now_trip_sub) else null
            )
        )
        AskStep.LEVEL -> AskQuestion(
            title = t(Res.string.level_title_athlete, Res.string.level_title_parent),
            help = Res.string.pick_all_that_apply,
            // Only the levels that fit where they are now.
            options = listOf(
                AskOption("hs", Res.string.level_hs),
                AskOption("club", Res.string.level_club),
                AskOption("odp", Res.string.level_odp, Res.string.level_odp_sub),
                AskOption("college", Res.string.level_college),
                AskOption("senior", Res.string.level_senior)
            ).filter { it.key in levelKeys(state.now) }
        )
        AskStep.GOAL -> AskQuestion(
            title = t(Res.string.goal_title_athlete, Res.string.goal_title_parent),
            help = null,
            options = listOf(
                AskOption("college", Res.string.goal_college, say = t(Res.string.goal_say_college_athlete, Res.string.goal_say_college_parent)),
                AskOption("pro", Res.string.goal_pro, say = t(Res.string.goal_say_pro_athlete, Res.string.goal_say_pro_parent)),
                AskOption("nt", Res.string.goal_nt, say = t(Res.string.goal_say_nt_athlete, Res.string.goal_say_nt_parent)),
                AskOption("oly", Res.string.goal_oly, say = Res.string.goal_say_oly)
            )
        )
        AskStep.CLUB -> AskQuestion(
            title = t(Res.string.club_title_athlete, Res.string.club_title_parent),
            help = t(Res.string.club_help_athlete, Res.string.club_help_parent),
            options = listOf(
                AskOption("jo", Res.string.club_jo, Res.string.club_jo_sub),
                AskOption("summer", Res.string.club_summer, Res.string.club_summer_sub),
                AskOption("school", t(Res.string.club_school_athlete, Res.string.club_school_parent)),
                AskOption("any", t(Res.string.club_any_athlete, Res.string.club_any_parent)),
                AskOption("none", t(Res.string.club_none_athlete, Res.string.club_none_parent))
            )
        )
        AskStep.PROBE -> probeQuestion(state.probeKind ?: ProbeKind.LOOKING)
        AskStep.FACILITIES -> AskQuestion(
            title = Res.string.fac_title,
            help = Res.string.fac_help,
            options = FACILITIES
        )
        AskStep.STOP -> if (parent) {
            AskQuestion(
                title = Res.string.stop_title_parent,
                help = Res.string.stop_help_parent,
                options = listOf(
                    AskOption("cost", Res.string.stop_cost, say = Res.string.stop_say_cost),
                    AskOption("safety", Res.string.stop_safety, say = Res.string.stop_say_safety),
                    AskOption("school", Res.string.stop_school, say = Res.string.stop_say_school),
                    AskOption("care", Res.string.stop_care, say = Res.string.stop_say_care),
                    AskOption("where", Res.string.stop_where_parent, say = Res.string.stop_say_where),
                    AskOption("nothing", Res.string.stop_nothing_parent, say = Res.string.stop_say_nothing)
                )
            )
        } else {
            AskQuestion(
                title = Res.string.stop_title_athlete,
                help = Res.string.stop_help_athlete,
                options = listOf(
                    AskOption("cost", Res.string.stop_cost, say = Res.string.stop_say_cost),
                    AskOption("parents", Res.string.stop_parents, say = Res.string.stop_say_parents),
                    AskOption("home", Res.string.stop_home, say = Res.string.stop_say_home),
                    AskOption("language", Res.string.stop_language, say = Res.string.stop_say_language),
                    AskOption("where", Res.string.stop_where_athlete, say = Res.string.stop_say_where),
                    AskOption("nothing", Res.string.stop_nothing_athlete, say = Res.string.stop_say_nothing)
                )
            )
        }
    }
}

/** The prototype's FAC_OPTS; the club profile lists the picked ones. */
val FACILITIES = listOf(
    AskOption("indoor", Res.string.fac_indoor),
    AskOption("outdoor", Res.string.fac_outdoor),
    AskOption("p50", Res.string.fac_p50),
    AskOption("p33", Res.string.fac_p33),
    AskOption("p25", Res.string.fac_p25),
    AskOption("gym", Res.string.fac_gym),
    AskOption("video", Res.string.fac_video),
    AskOption("housing", Res.string.fac_housing),
    AskOption("school", Res.string.fac_school)
)

private fun probeQuestion(kind: ProbeKind): AskQuestion = when (kind) {
    ProbeKind.PARENT -> AskQuestion(
        title = Res.string.probe_parent_title,
        help = null,
        options = listOf(
            AskOption("together", Res.string.probe_together, say = Res.string.probe_say_together),
            AskOption("theirs", Res.string.probe_theirs, say = Res.string.probe_say_theirs),
            AskOption("mine", Res.string.probe_mine, say = Res.string.probe_say_mine),
            AskOption("exploring", Res.string.probe_exploring, say = Res.string.probe_say_exploring)
        )
    )
    ProbeKind.COMMITTED -> AskQuestion(
        title = Res.string.probe_committed_title,
        help = null,
        options = listOf(
            AskOption("minutes", Res.string.probe_minutes, say = Res.string.probe_say_minutes),
            AskOption("starting", Res.string.probe_starting, say = Res.string.probe_say_starting)
        )
    )
    ProbeKind.LOOKING -> AskQuestion(
        title = Res.string.probe_looking_title,
        help = null,
        options = listOf(
            AskOption("film", Res.string.probe_film, say = Res.string.probe_say_film),
            AskOption("level", Res.string.probe_level, say = Res.string.probe_say_level),
            AskOption("strength", Res.string.probe_strength, say = Res.string.probe_say_strength),
            AskOption("unknown", Res.string.probe_unknown, say = Res.string.probe_say_unknown)
        )
    )
    // From the Google Doc; the prototype has no probe for this answer.
    ProbeKind.BEYOND -> AskQuestion(
        title = Res.string.probe_beyond_title,
        help = null,
        options = listOf(
            AskOption("better", Res.string.probe_better),
            AskOption("same", Res.string.probe_same),
            AskOption("best", Res.string.probe_best, say = Res.string.probe_say_best)
        )
    )
}

/** v29's levelKeys: the levels that fit the answer to Where are you now. */
fun levelKeys(now: Set<String>): Set<String> = when {
    "college" in now -> setOf("club", "odp", "college", "senior")
    "pro" in now -> setOf("hs", "club", "odp", "senior")
    "committed" in now || "looking" in now -> setOf("hs", "club", "odp")
    else -> setOf("hs", "club", "odp", "college", "senior")
}
