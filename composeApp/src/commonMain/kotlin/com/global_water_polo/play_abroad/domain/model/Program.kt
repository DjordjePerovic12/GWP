package com.global_water_polo.play_abroad.domain.model

import kotlinx.serialization.Serializable

/**
 * GWP programs abroad, from the prototype's PROGRAMS list.
 * [seasonStartMonth] is the 1-based month a seasonal program starts (null = flexible dates).
 * Every program except Explore needs GWP Elite.
 */
enum class Program(
    val key: String,
    val months: Int,
    val flagship: Boolean = false,
    val seasonStartMonth: Int? = null
) {
    EXPLORE("explore", months = 1),
    FALL("fall", months = 4, seasonStartMonth = 8),
    SPRING("spring", months = 5, seasonStartMonth = 1),
    SUMMER("summer", months = 3, seasonStartMonth = 6),
    GAP_YEAR("gapyear", months = 10, flagship = true);

    val needsElite: Boolean get() = this != EXPLORE

    companion object {
        fun of(key: String): Program? = entries.firstOrNull { it.key == key }
    }
}

/**
 * The long program page (the prototype's PD): one idea, purpose, ideal athlete, development, what is
 * included and optional, where it could happen and how to apply. [months] are 0-based calendar months.
 * TODO: Sample program content until GWP publishes the real catalogue.
 */
data class ProgramDetails(
    val idea: String,
    val duration: String,
    val application: Boolean,
    val months: List<Int>,
    val purpose: String,
    val ideal: List<String>,
    val development: List<String>,
    val included: List<String>,
    val optional: List<String>,
    val destinations: List<String>,
    val steps: List<String>
)

/**
 * One step of "my pathway": a program, when it would happen and why it fits. [pick] is v29's ten-week
 * season ("fall", "winter", "spring"), shown by its own name instead of the program's.
 */
@Serializable
data class PathwayStep(
    val program: Program,
    val year: Int,
    val month: Int,
    val timing: PathwayTiming,
    val why: PathwayWhy,
    val pick: String = ""
)

/** How a step's timing is worded. */
enum class PathwayTiming {
    NEXT_BREAK,
    SUMMER,
    SPRING,
    FALL,
    /** December to February, across two years. */
    WINTER,
    SEASON
}

/** Which reason a step gives. */
enum class PathwayWhy {
    BREAK,
    BREAK_COLLEGE,
    AFTER_JO,
    BETWEEN_YEARS,
    CHECK_CREDITS,
    AFTER_GRADUATION,
    CLUB_BLOCK,
    FITS_BEFORE_CLOCK,
    CONFIRM_NCAA,
    /** A ten-week season inside the free year. */
    IN_FREE_YEAR
}

/**
 * The athlete's own pathway, built from the "Build my pathway" sheet: goal, level, education, how long
 * (any of weeks, summer, semester, year, unsure) and where (eu, am, world).
 */
@Serializable
data class Pathway(
    val goal: String,
    val level: String,
    val education: String,
    val timing: Set<String>,
    val destinations: Set<String>,
    val steps: List<PathwayStep>
)

val Program.details: ProgramDetails
    get() = when (this) {
        Program.EXPLORE -> ProgramDetails(
            idea = "A first taste of training abroad, on your schedule.",
            duration = "Any 4–6 weeks that fit your calendar",
            application = false,
            months = listOf(),
            purpose = "A short international block: train with a host club, live in a new city and see how the game is played somewhere else. Built around your school calendar.",
            ideal = listOf("Training abroad for the first time", "High school or college athletes with a break in their calendar", "Anyone deciding whether a longer program is right"),
            development = listOf("Daily training with a host club", "A different style of play and coaching", "Independence away from home"),
            included = listOf("Club placement and training coordination", "A GWP contact before and during your stay", "Arrival and orientation support"),
            optional = listOf("Apartment", "Meals", "Private lessons", "Activities", "Flights, booked separately"),
            destinations = listOf("kotor", "split", "athens", "barcelona", "budapest", "socal"),
            steps = listOf("Send an inquiry with your dates", "Short call with GWP", "GWP proposes clubs and a quote", "Confirm, then plan travel")
        )
        Program.FALL -> ProgramDetails(
            idea = "A full fall semester inside a club's season.",
            duration = "August or September to December",
            application = false,
            months = listOf(7, 8, 9, 10, 11),
            purpose = "Spend the fall training and competing with a club abroad, with structure for school where you need it.",
            ideal = listOf("Graduated seniors before college", "Athletes taking a semester out", "Players ready for daily high-level training"),
            development = listOf("A full block of club training", "Competition where eligible", "Technical and tactical work with club coaches", "Academic coordination where available"),
            included = listOf("Club placement and season coordination", "A GWP contact throughout", "Registration support with the club"),
            optional = listOf("Apartment", "Meals", "Gym and pool fees", "Private lessons", "Flights, booked separately"),
            destinations = listOf("belgrade", "budapest", "athens", "recco", "barcelona", "split"),
            steps = listOf("Send an inquiry", "Call with GWP", "Video review of your game", "Club proposal and quote", "Confirm and register")
        )
        Program.SPRING -> ProgramDetails(
            idea = "Train and compete through the spring season.",
            duration = "January to May",
            application = false,
            months = listOf(0, 1, 2, 3, 4),
            purpose = "Join a club for the second half of its season, when matches matter most, and train at that level every day.",
            ideal = listOf("Athletes with a spring free before college", "College players on an approved semester away", "Players chasing match minutes at a higher level"),
            development = listOf("Daily club training in season", "League and cup competition where eligible", "Strength and conditioning with the club"),
            included = listOf("Club placement and season coordination", "A GWP contact throughout", "Registration support with the club"),
            optional = listOf("Apartment", "Meals", "Gym and pool fees", "Private lessons", "Flights, booked separately"),
            destinations = listOf("split", "budva", "piraeus", "savona", "marseille", "budapest"),
            steps = listOf("Send an inquiry", "Call with GWP", "Video review of your game", "Club proposal and quote", "Confirm and register")
        )
        Program.SUMMER -> ProgramDetails(
            idea = "Summer abroad, sized to your break.",
            duration = "June to September, flexible length",
            application = false,
            months = listOf(5, 6, 7, 8),
            purpose = "Train outdoors with clubs in summer season and build your stay around Junior Olympics, school and family plans.",
            ideal = listOf("High school athletes between school years", "College players on summer break", "Athletes who want sea pools and outdoor training"),
            development = listOf("Twice-daily summer training where clubs run it", "Scrimmages and summer tournaments", "Culture and language alongside the pool"),
            included = listOf("Club placement and training coordination", "A GWP contact before and during your stay", "Arrival and orientation support"),
            optional = listOf("Apartment", "Meals", "Private lessons", "Activities", "Flights, booked separately"),
            destinations = listOf("kotor", "herceg-novi", "dubrovnik", "vouliagmeni", "valletta", "miami"),
            steps = listOf("Send an inquiry with your dates", "Short call with GWP", "GWP proposes clubs and a quote", "Confirm, then plan travel")
        )
        Program.GAP_YEAR -> ProgramDetails(
            idea = "Ten months of complete athlete development.",
            duration = "About 10 months, August to May",
            application = true,
            months = listOf(7, 8, 9, 10, 11, 0, 1, 2, 3, 4),
            purpose = "GWP's most complete program: a full season with a host club, plus GWP LAB, strength and conditioning, nutrition, mental performance and Olympian mentorship.",
            ideal = listOf("Athletes with a free year before their NCAA clock starts", "Players aiming for college starts, pro water polo or national teams", "Athletes ready for a full season away from home"),
            development = listOf("A full club season, training and competition", "GWP LAB Underwater Insight testing and retests", "Strength, nutrition and mental performance programming", "Mentorship and a recruiting portfolio"),
            included = listOf("Club placement and season-long coordination", "GWP LAB assessments and coach reviews", "Performance programming", "Mentorship sessions"),
            optional = listOf("Apartment", "Meals", "Private lessons", "Activities", "Flights, booked separately"),
            destinations = listOf("kotor", "budapest", "belgrade", "athens", "barcelona", "recco"),
            steps = listOf("Start your application", "Interview with GWP", "Video review of your game", "Club proposal and quote", "Confirm your season")
        )
    }
