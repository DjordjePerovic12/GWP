package com.global_water_polo.play_abroad.domain.model

import kotlinx.serialization.Serializable
import io.github.vinceglb.filekit.PlatformFile

/** The water polo positions an athlete can pick; the first one picked is the main position. */
val POSITIONS = listOf("Goalkeeper", "Center", "Center defender", "Driver", "Utility")

/** Personal info rows on the portfolio, each typed in by the athlete. Empty = not added. */
@Serializable
data class PersonalInfo(
    val height: String = "",
    val wingspan: String = "",
    /** 50 free, in seconds. */
    val fifty: String = "",
    val club: String = "",
    val coach: String = "",
    val coachEmail: String = "",
    val hometown: String = "",
    val highSchool: String = "",
    val gpa: String = "",
    val classOf: String = "",
    val ncaaId: String = ""
)

/** The four personal info editors (the prototype's body, club, home and school), plus positions. */
enum class InfoGroup {
    POSITION,
    BODY,
    CLUB,
    HOME,
    SCHOOL
}

/** A recommendation letter: requested from a coach (no [file] yet) or uploaded by the athlete. */
@Serializable
data class Letter(val id: Long, val from: String, val club: String = "", val file: PlatformFile? = null)

/** A photo or clip in the gallery; any of them can become the profile photo or the highlight. */
@Serializable
data class GalleryItem(val id: Long, val file: PlatformFile, val video: Boolean)

/**
 * Everything on the portfolio besides positions (the prototype's `S.pf`, `S.prof` and `S.gal`). Files stay
 * on the device; nothing is uploaded yet.
 */
@Serializable
data class Portfolio(
    val info: PersonalInfo = PersonalInfo(),
    /** Gallery item ids used as the profile photo and the highlight video. */
    val photoId: Long? = null,
    val highlightId: Long? = null,
    val transcript: PlatformFile? = null,
    val letters: List<Letter> = emptyList(),
    val gallery: List<GalleryItem> = emptyList()
) {
    val photo: GalleryItem? get() = gallery.find { it.id == photoId }
    val highlight: GalleryItem? get() = gallery.find { it.id == highlightId }
}

/** The prototype's pfItems: what the completion count looks at, in order. */
enum class PortfolioItem {
    PHOTO,
    HIGHLIGHT,
    POSITION,
    PERSONAL,
    TRANSCRIPT,
    LETTERS
}

fun Profile.isDone(item: PortfolioItem): Boolean = when (item) {
    PortfolioItem.PHOTO -> portfolio.photo != null
    PortfolioItem.HIGHLIGHT -> portfolio.highlight != null
    PortfolioItem.POSITION -> positions.isNotEmpty()
    PortfolioItem.PERSONAL -> portfolio.info.let { it.height.isNotEmpty() || it.club.isNotEmpty() || it.hometown.isNotEmpty() }
    PortfolioItem.TRANSCRIPT -> portfolio.transcript != null
    PortfolioItem.LETTERS -> portfolio.letters.isNotEmpty()
}

/** Done items plus one for having a profile at all, out of seven (the prototype's pfPct). */
val Profile.portfolioPercent: Int
    get() = (PortfolioItem.entries.count { isDone(it) } + 1) * 100 / (PortfolioItem.entries.size + 1)

/** Class of: typed in the school editor, else the graduation year from onboarding. */
val Profile.classOf: String get() = portfolio.info.classOf.ifEmpty { gradYear?.toString().orEmpty() }
