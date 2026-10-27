package com.global_water_polo.play_abroad.domain.model

import kotlinx.serialization.Serializable
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlin.math.roundToInt

/** Where the athlete lives during the stay (the prototype's ACC). */
enum class Home {
    ROOM,
    STUDIO,
    ONE_BEDROOM
}

/** One-off extras (the prototype's ADDONS). */
enum class AddOn {
    AIRPORT_PICKUP,
    SIM_CARD,
    BIKE,
    LANGUAGE,
    GEAR,
    LAUNDRY
}

/**
 * An experience built on Globe+ (the prototype's draft2): a city, a club or "let GWP match me" (empty
 * [clubId]), the dates, a home and the extras. A draft until [sent]; sending makes it an availability
 * inquiry, never a booking.
 */
@Serializable
data class Trip(
    val id: Long,
    val cityId: String,
    val clubId: String = "",
    val arrive: LocalDate,
    val leave: LocalDate,
    val home: Home = Home.ROOM,
    val clubMembership: Boolean = true,
    val gym: Boolean = true,
    val meals: Boolean = false,
    val activities: Boolean = false,
    /** Private lessons a week, 0 to [MAX_LESSONS]. */
    val lessons: Int = 1,
    val addOns: Set<AddOn> = emptySet(),
    val notes: String = "",
    /** Asked for a place like the one in the apartment tour. */
    val apartmentRequested: Boolean = false,
    val sent: Boolean = false
) {
    /** Whole months between the dates, 1 to 12 (a month is 30.44 days, as in the prototype). */
    val months: Int get() = (arrive.daysUntil(leave) / 30.44).roundToInt().coerceIn(MIN_MONTHS, MAX_MONTHS)

    companion object {
        const val MIN_MONTHS = 1
        const val MAX_MONTHS = 12
        const val MAX_LESSONS = 5

        /**
         * A new draft: three months from the 15th, [startOffset] months after next month (the travel
         * window the athlete picked in onboarding), club and gym on, one lesson a week.
         */
        fun draft(id: Long, cityId: String, clubId: String, today: LocalDate, startOffset: Int): Trip {
            val firstOfNextMonth = today.minus(DatePeriod(days = today.day - 1)).plus(DatePeriod(months = 1))
            val arrive = firstOfNextMonth.plus(DatePeriod(months = startOffset, days = 14))
            return Trip(id = id, cityId = cityId, clubId = clubId, arrive = arrive, leave = arrive.plus(DatePeriod(months = 3)))
        }
    }
}

/** The groups the review sheet breaks the estimate into. */
enum class CostGroup {
    PROGRAM,
    HOUSING,
    OPTIONAL
}

/** What a line is for; the UI words it. [AddOn] lines carry the add-on. */
enum class CostItem {
    CLUB,
    GYM,
    HOME,
    MEALS,
    LESSONS,
    ACTIVITIES,
    ADD_ON
}

/** One estimate line; [amount] null means "On inquiry". */
data class CostLine(val group: CostGroup, val item: CostItem, val amount: Double?, val addOn: AddOn? = null)

/** The estimate: its lines, the GWP fee and the total, each null until every price is known. */
data class Estimate(val lines: List<CostLine>, val fee: Double?, val total: Double?)

/**
 * GWP's prices in USD. All null for now, so every line reads "On inquiry", exactly as in the prototype.
 * TODO: Fill in (or load from the backend) once GWP sets prices.
 */
object TripPrices {
    val home: Map<Home, Double?> = mapOf(Home.ROOM to null, Home.STUDIO to null, Home.ONE_BEDROOM to null)
    /** Per month. */
    val club: Double? = null
    val gym: Double? = null
    val meals: Double? = null
    val activities: Double? = null
    /** Per lesson. */
    val lesson: Double? = null
    /** Each, one-off. */
    val addOn: Double? = null
    /** GWP service fee, percent of the subtotal. */
    val feePercent: Double? = null
}

/** The prototype's estimate(): monthly prices times months, lessons by the week, add-ons once. */
fun Trip.estimate(prices: TripPrices = TripPrices): Estimate {
    val m = months
    val weeks = (m * 4.33).roundToInt()
    fun monthly(price: Double?) = price?.let { it * m }
    val lines = buildList {
        if (clubMembership) add(CostLine(CostGroup.PROGRAM, CostItem.CLUB, monthly(prices.club)))
        if (gym) add(CostLine(CostGroup.PROGRAM, CostItem.GYM, monthly(prices.gym)))
        add(CostLine(CostGroup.HOUSING, CostItem.HOME, monthly(prices.home[home])))
        if (meals) add(CostLine(CostGroup.OPTIONAL, CostItem.MEALS, monthly(prices.meals)))
        if (lessons > 0) add(CostLine(CostGroup.OPTIONAL, CostItem.LESSONS, prices.lesson?.let { it * lessons * weeks }))
        if (activities) add(CostLine(CostGroup.OPTIONAL, CostItem.ACTIVITIES, monthly(prices.activities)))
        AddOn.entries.filter { it in addOns }.forEach { add(CostLine(CostGroup.OPTIONAL, CostItem.ADD_ON, prices.addOn, it)) }
    }
    val subtotal = lines.sumOf { it.amount ?: 0.0 }
    val fee = prices.feePercent?.let { subtotal * it / 100 }
    val total = if (fee != null && lines.all { it.amount != null }) subtotal + fee else null
    return Estimate(lines, fee, total)
}

/** Lessons over the whole stay, for "2 a week (26)". */
val Trip.totalLessons: Int get() = lessons * (months * 4.33).roundToInt()
