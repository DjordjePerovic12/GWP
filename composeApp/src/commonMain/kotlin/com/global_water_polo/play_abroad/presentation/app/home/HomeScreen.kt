package com.global_water_polo.play_abroad.presentation.app.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.global_water_polo.play_abroad.core.components.AccountLockLayer
import com.global_water_polo.play_abroad.core.components.membership.MembershipSheetHost
import com.global_water_polo.play_abroad.core.utils.observeWithLifecycle
import com.global_water_polo.play_abroad.presentation.app.home.components.PlanSheet
import com.global_water_polo.play_abroad.presentation.app.profile.components.InquirySheet
import com.global_water_polo.play_abroad.presentation.app.profile.components.ProgramDetailSheet
import com.global_water_polo.play_abroad.presentation.base.BaseContentView

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    showSnackBar: (String) -> Unit
) {
    val state by viewModel.homeState.collectAsStateWithLifecycle()

    viewModel.snackBarChannel.observeWithLifecycle { message ->
        showSnackBar(message)
    }

    BaseContentView(
        modifier = modifier,
        viewState = state,
        applySystemBarsPadding = false
    ) {
        HomeScreenContent(
            state = state,
            onEvent = viewModel::onEvent
        )
    }

    val sheet = state.sheet
    if (sheet != null) key(sheet) {
        when (sheet) {
            is HomeSheet.Plan -> PlanSheet(
                profile = state.profile,
                adjusting = sheet.adjusting,
                draft = state.draft,
                onAdjustClick = { viewModel.onEvent(HomeEvent.OnAdjustClick) },
                onDraftNow = { viewModel.onEvent(HomeEvent.OnDraftNow(it)) },
                onDraftGrad = { viewModel.onEvent(HomeEvent.OnDraftGrad(it)) },
                onDraftAvailability = { viewModel.onEvent(HomeEvent.OnDraftAvailability(it)) },
                onDraftFirstStep = { viewModel.onEvent(HomeEvent.OnDraftFirstStep(it)) },
                onRebuildClick = { viewModel.onEvent(HomeEvent.OnRebuildClick) },
                onDismiss = { viewModel.onEvent(HomeEvent.OnSheetDismiss) }
            )
            is HomeSheet.ProgramDetail -> ProgramDetailSheet(
                program = sheet.program,
                state = state.pathway,
                onCityClick = { viewModel.onEvent(HomeEvent.OnCityClick(it)) },
                onCtaClick = { viewModel.onEvent(HomeEvent.OnProgramCtaClick(sheet.program)) },
                onDismiss = { viewModel.onEvent(HomeEvent.OnSheetDismiss) }
            )
            is HomeSheet.Inquiry -> InquirySheet(
                program = sheet.program,
                sent = sheet.sent,
                state = state.pathway,
                onSendClick = { viewModel.onEvent(HomeEvent.OnSendInquiry(sheet.program)) },
                onDismiss = { viewModel.onEvent(HomeEvent.OnSheetDismiss) }
            )
        }
    }

    MembershipSheetHost(
        sheet = state.membershipSheet,
        membership = state.pathway.membership,
        onSheetChange = { viewModel.onEvent(HomeEvent.OnMembershipSheet(it)) },
        onStart = { plan, yearly -> viewModel.onEvent(HomeEvent.OnStartPlan(plan, yearly)) },
        onSwitchToNext = { viewModel.onEvent(HomeEvent.OnSwitchToNext) },
        onCancel = { viewModel.onEvent(HomeEvent.OnCancelPlan) }
    )

    AccountLockLayer(profile = state.profile, view = state.lockView, onAction = { viewModel.onEvent(HomeEvent.OnLock(it)) })
}
