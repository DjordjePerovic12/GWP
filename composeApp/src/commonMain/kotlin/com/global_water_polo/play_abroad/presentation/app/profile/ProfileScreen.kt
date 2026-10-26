package com.global_water_polo.play_abroad.presentation.app.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.global_water_polo.play_abroad.core.components.membership.MembershipSheetHost
import com.global_water_polo.play_abroad.core.utils.observeWithLifecycle
import com.global_water_polo.play_abroad.presentation.app.profile.components.BuildPathwaySheet
import com.global_water_polo.play_abroad.presentation.app.profile.components.GalleryItemSheet
import com.global_water_polo.play_abroad.presentation.app.profile.components.InfoEditorSheet
import com.global_water_polo.play_abroad.presentation.app.profile.components.InquirySheet
import com.global_water_polo.play_abroad.presentation.app.profile.components.LetterRequestSheet
import com.global_water_polo.play_abroad.presentation.app.profile.components.LabExampleSheet
import com.global_water_polo.play_abroad.presentation.app.profile.components.LabTestSheet
import com.global_water_polo.play_abroad.presentation.app.profile.components.ProgramDetailSheet
import com.global_water_polo.play_abroad.presentation.base.BaseContentView

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel,
    showSnackBar: (String) -> Unit
) {
    val state by viewModel.profileState.collectAsStateWithLifecycle()

    viewModel.snackBarChannel.observeWithLifecycle { message ->
        showSnackBar(message)
    }

    BaseContentView(
        modifier = modifier,
        viewState = state,
        applySystemBarsPadding = false
    ) {
        ProfileScreenContent(
            state = state,
            onEvent = viewModel::onEvent
        )
    }

    // Each sheet is keyed, so moving from one sheet to the next (program -> inquiry, example -> test)
    // swaps the sheet instead of reusing the one already open.
    val sheet = state.sheet
    if (sheet != null) key(sheet) { ProfileSheetContent(sheet = sheet, state = state, viewModel = viewModel) }

    MembershipSheetHost(
        sheet = state.membershipSheet,
        membership = state.membership,
        onSheetChange = { viewModel.onEvent(ProfileEvent.OnMembershipSheet(it)) },
        onStart = { plan, yearly -> viewModel.onEvent(ProfileEvent.OnStartPlan(plan, yearly)) },
        onSwitchToNext = { viewModel.onEvent(ProfileEvent.OnSwitchToNext) },
        onCancel = { viewModel.onEvent(ProfileEvent.OnCancelPlan) }
    )
}

@Composable
private fun ProfileSheetContent(sheet: ProfileSheet, state: ProfileState, viewModel: ProfileViewModel) {
    when (sheet) {
        ProfileSheet.BuildPathway -> BuildPathwaySheet(
            state = state,
            onBuild = { goal, level, education, timing, destinations ->
                viewModel.onEvent(ProfileEvent.OnBuildPathway(goal, level, education, timing, destinations))
            },
            onDismiss = { viewModel.onEvent(ProfileEvent.OnSheetDismiss) }
        )
        is ProfileSheet.ProgramDetail -> ProgramDetailSheet(
            program = sheet.program,
            state = state,
            onCityClick = { viewModel.onEvent(ProfileEvent.OnCityClick(it)) },
            onCtaClick = { viewModel.onEvent(ProfileEvent.OnProgramCtaClick(sheet.program)) },
            onDismiss = { viewModel.onEvent(ProfileEvent.OnSheetDismiss) }
        )
        is ProfileSheet.Inquiry -> InquirySheet(
            program = sheet.program,
            sent = sheet.sent,
            state = state,
            onSendClick = { viewModel.onEvent(ProfileEvent.OnSendInquiry(sheet.program)) },
            onDismiss = { viewModel.onEvent(ProfileEvent.OnSheetDismiss) }
        )
        is ProfileSheet.LabTest -> LabTestSheet(
            index = sheet.index,
            state = state,
            onAnglePicked = { angle, file -> viewModel.onEvent(ProfileEvent.OnLabAnglePicked(sheet.index, angle, file)) },
            onAngleRemove = { viewModel.onEvent(ProfileEvent.OnLabAngleRemove(sheet.index, it)) },
            onSendClick = { viewModel.onEvent(ProfileEvent.OnLabSendClick(sheet.index)) },
            onDismiss = { viewModel.onEvent(ProfileEvent.OnSheetDismiss) }
        )
        ProfileSheet.LabExample -> LabExampleSheet(
            onFilmClick = { viewModel.onEvent(ProfileEvent.OnLabTestClick(1)) },
            onDismiss = { viewModel.onEvent(ProfileEvent.OnSheetDismiss) }
        )
        is ProfileSheet.Editor -> InfoEditorSheet(
            group = sheet.group,
            profile = state.profile,
            onSaveInfo = { viewModel.onEvent(ProfileEvent.OnInfoSaved(it)) },
            onSavePositions = { positions, leftHanded -> viewModel.onEvent(ProfileEvent.OnPositionsSaved(positions, leftHanded)) },
            onDismiss = { viewModel.onEvent(ProfileEvent.OnSheetDismiss) }
        )
        ProfileSheet.LetterRequest -> LetterRequestSheet(
            onRequest = { from, club -> viewModel.onEvent(ProfileEvent.OnLetterRequested(from, club)) },
            onDismiss = { viewModel.onEvent(ProfileEvent.OnSheetDismiss) }
        )
        is ProfileSheet.GalleryItem -> state.profile.portfolio.gallery.find { it.id == sheet.id }?.let { item ->
            val portfolio = state.profile.portfolio
            GalleryItemSheet(
                item = item,
                inUse = item.id == portfolio.photoId || item.id == portfolio.highlightId,
                onUseClick = { viewModel.onEvent(ProfileEvent.OnUseOnProfile(item.id)) },
                onRemoveClick = { viewModel.onEvent(ProfileEvent.OnGalleryRemove(item.id)) },
                onDismiss = { viewModel.onEvent(ProfileEvent.OnSheetDismiss) }
            )
        }
    }
}
