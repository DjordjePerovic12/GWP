package com.global_water_polo.play_abroad.presentation.app.profile

import io.github.vinceglb.filekit.PlatformFile
import com.global_water_polo.play_abroad.core.components.AppTab
import com.global_water_polo.play_abroad.core.components.membership.MembershipSheet
import com.global_water_polo.play_abroad.domain.model.InfoGroup
import com.global_water_polo.play_abroad.domain.model.PersonalInfo
import com.global_water_polo.play_abroad.domain.model.Program
import com.global_water_polo.play_abroad.domain.model.ProfileVisibility
import com.global_water_polo.play_abroad.presentation.base.BaseEvent

sealed class ProfileEvent : BaseEvent {
    data class OnTabClick(val tab: AppTab) : ProfileEvent()
    data class OnSegmentClick(val segment: ProfileSegment) : ProfileEvent()
    data class OnMembershipSheet(val sheet: MembershipSheet?) : ProfileEvent()
    data class OnStartPlan(val plan: String, val yearly: Boolean) : ProfileEvent()
    data object OnSwitchToNext : ProfileEvent()
    data object OnCancelPlan : ProfileEvent()
    data object OnSheetDismiss : ProfileEvent()

    data object OnBuildPathwayClick : ProfileEvent()
    data class OnBuildPathway(
        val goal: String,
        val level: String,
        val education: String,
        val timing: Set<String>,
        val destinations: Set<String>
    ) : ProfileEvent()
    data object OnExploreGlobeClick : ProfileEvent()
    data class OnProgramClick(val program: Program) : ProfileEvent()
    data class OnProgramCtaClick(val program: Program) : ProfileEvent()
    data class OnSendInquiry(val program: Program) : ProfileEvent()
    data class OnCityClick(val cityId: String) : ProfileEvent()
    data class OnClinicClick(val index: Int) : ProfileEvent()

    data class OnLabTestClick(val index: Int) : ProfileEvent()
    data class OnLabAnglePicked(val index: Int, val angle: String, val file: PlatformFile) : ProfileEvent()
    data class OnLabAngleRemove(val index: Int, val angle: String) : ProfileEvent()
    data class OnLabSendClick(val index: Int) : ProfileEvent()
    data object OnLabExampleClick : ProfileEvent()

    data class OnPreviewToggle(val preview: Boolean) : ProfileEvent()
    data class OnEditInfo(val group: InfoGroup) : ProfileEvent()
    data class OnInfoSaved(val info: PersonalInfo) : ProfileEvent()
    data class OnPositionsSaved(val positions: List<String>, val leftHanded: Boolean) : ProfileEvent()
    data class OnPhotoPicked(val file: PlatformFile) : ProfileEvent()
    data class OnHighlightPicked(val file: PlatformFile) : ProfileEvent()
    data class OnTranscriptPicked(val file: PlatformFile) : ProfileEvent()
    data object OnLetterRequestClick : ProfileEvent()
    data class OnLetterRequested(val from: String, val club: String) : ProfileEvent()
    data class OnLetterUploaded(val from: String, val file: PlatformFile) : ProfileEvent()
    data class OnLetterRemove(val id: Long) : ProfileEvent()
    data class OnGalleryAdd(val files: List<PlatformFile>) : ProfileEvent()
    data class OnGalleryFilter(val filter: GalleryFilter) : ProfileEvent()
    data class OnGalleryItemClick(val id: Long) : ProfileEvent()
    data class OnUseOnProfile(val id: Long) : ProfileEvent()
    data class OnGalleryRemove(val id: Long) : ProfileEvent()
    data class OnVisibilityClick(val visibility: ProfileVisibility) : ProfileEvent()
}
