package com.global_water_polo.play_abroad.presentation.app.profile

import com.global_water_polo.play_abroad.core.components.membership.Membership
import com.global_water_polo.play_abroad.core.components.membership.MembershipSheet
import com.global_water_polo.play_abroad.core.components.membership.membershipOf
import com.global_water_polo.play_abroad.core.utils.FreeYear
import com.global_water_polo.play_abroad.core.utils.NcaaClockCalculator
import com.global_water_polo.play_abroad.core.utils.PathwayCalculator
import com.global_water_polo.play_abroad.domain.model.InfoGroup
import com.global_water_polo.play_abroad.domain.model.Profile
import com.global_water_polo.play_abroad.domain.model.portfolioPercent
import com.global_water_polo.play_abroad.domain.model.Program
import com.global_water_polo.play_abroad.presentation.base.BaseError
import com.global_water_polo.play_abroad.presentation.base.BaseViewState

enum class ProfileSegment(val key: String) {
    PATH("path"),
    LAB("lab"),
    PORTFOLIO("port");

    companion object {
        fun of(key: String): ProfileSegment = entries.firstOrNull { it.key == key } ?: PATH
    }
}

/** Which gallery items show on the Portfolio: everything, photos only or videos only. */
enum class GalleryFilter {
    ALL,
    PHOTOS,
    VIDEOS
}

/** The bottom sheet open on the Profile tab. */
sealed interface ProfileSheet {
    data object BuildPathway : ProfileSheet
    data class ProgramDetail(val program: Program) : ProfileSheet
    data class Inquiry(val program: Program, val sent: Boolean = false) : ProfileSheet
    data class LabTest(val index: Int) : ProfileSheet
    data object LabExample : ProfileSheet
    data class Editor(val group: InfoGroup) : ProfileSheet
    data object LetterRequest : ProfileSheet
    data class GalleryItem(val id: Long) : ProfileSheet
}

/** A sample clinic listing: name, date, city, format and photo key. */
data class Clinic(val name: String, val date: String, val city: String, val format: String, val image: String)

data class ProfileState(
    override val isLoading: Boolean = false,
    override val error: BaseError? = null,
    val profile: Profile = Profile(),
    val segment: ProfileSegment = ProfileSegment.PATH,
    val preview: Boolean = false,
    val galleryFilter: GalleryFilter = GalleryFilter.ALL,
    val sheet: ProfileSheet? = null,
    val membershipSheet: MembershipSheet? = null
) : BaseViewState(isLoading, error) {

    val membership: Membership get() = membershipOf(profile)

    val freeYear: FreeYear?
        get() {
            val dob = profile.birthday ?: return null
            val month = profile.gradMonth ?: return null
            val year = profile.gradYear ?: return null
            return PathwayCalculator.freeYear(dob, month, year, "college" in profile.now)
        }

    val clockYear: Int? get() = profile.birthday?.let { NcaaClockCalculator.clockOf(it).year }

    val recommended: Program
        get() = Program.of(profile.appliedProgram) ?: PathwayCalculator.recommend(freeYear, profile.goal, profile.now, profile.travelStartOffset)

    /** A program can be requested with Explore on any plan, the rest with Elite. */
    fun locked(program: Program): Boolean = program.needsElite && !membership.elite

    /** GWP LAB: "none", "part", "ready" or "wait" for test [index]. */
    fun labState(index: Int): String {
        val angles = profile.labAngles[index].orEmpty()
        return when {
            index in profile.labSent -> "wait"
            angles.size >= 2 -> "ready"
            angles.isNotEmpty() -> "part"
            else -> "none"
        }
    }

    val labFilmed: Int get() = LAB_TESTS.indices.count { profile.labAngles[it].orEmpty().size >= 2 }

    val portfolioPercent: Int get() = profile.portfolioPercent

    companion object {
        /** GWP LAB's eight in-water tests: name and what the coach looks at. TODO: Sample copy until GWP LAB ships. */
        val LAB_TESTS = listOf(
            "Body position" to "How your hips, chest and head sit in the water while you hold position.",
            "Foot position and footwork" to "Your eggbeater: knee width, foot path and rhythm, seen from below.",
            "Hand position 1" to "Sculling hands while you hold position and receive the ball.",
            "Hand position 2" to "Hands and balance as you rise to pass or shoot.",
            "6 m foul shot" to "Setup, lift and release from 6 metres.",
            "Fake shot" to "Selling the fake while you stay high and balanced.",
            "Cross-face shot" to "Rotation and release when you shoot across your body.",
            "Strong-side shot" to "Lift, arm path and release on your strong side."
        )

        //TODO: Sample listings until GWP publishes clinics.
        val CLINICS = listOf(
            Clinic("Center play clinic", "Nov 14", "Los Angeles", "Small group with an Olympian", "p_residency"),
            Clinic("Shooting clinic", "Dec 5", "Belgrade", "Small group with a pro player", "p_immersion"),
            Clinic("Goalkeeping clinic", "Jan 9", "Budapest", "Small group with an Olympian", "p_semester")
        )
    }
}
