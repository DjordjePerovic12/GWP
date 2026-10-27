package com.global_water_polo.play_abroad.presentation.home.parent

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.global_water_polo.play_abroad.core.components.membership.MembershipSheet
import com.global_water_polo.play_abroad.core.components.membership.MembershipSheetHost
import com.global_water_polo.play_abroad.core.utils.observeWithLifecycle
import com.global_water_polo.play_abroad.presentation.app.profile.components.goalLabel
import com.global_water_polo.play_abroad.presentation.home.parent.components.AbroadPlanSheet
import com.global_water_polo.play_abroad.resources.Res
import com.global_water_polo.play_abroad.resources.match_not_set
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.presentation.base.BaseContentView

@Composable
fun ParentHomeScreen(
    modifier: Modifier = Modifier,
    viewModel: ParentHomeViewModel,
    showSnackBar: (String) -> Unit
) {
    val state by viewModel.parentHomeState.collectAsStateWithLifecycle()

    viewModel.snackBarChannel.observeWithLifecycle { message ->
        showSnackBar(message)
    }

    BaseContentView(
        modifier = modifier,
        viewState = state
    ) {
        ParentHomeScreenContent(
            state = state,
            onCompareClick = { viewModel.onEvent(ParentHomeEvent.OnCompareClick) },
            onBookCallClick = { viewModel.onEvent(ParentHomeEvent.OnBookCallClick) },
            onInviteClick = { viewModel.onEvent(ParentHomeEvent.OnInviteClick) },
            onMembershipClick = {
                viewModel.onEvent(ParentHomeEvent.OnMembershipSheet(if (state.membership.plan == null) MembershipSheet.PAYWALL else MembershipSheet.MANAGE))
            },
            onEliteClick = { viewModel.onEvent(ParentHomeEvent.OnMembershipSheet(MembershipSheet.ELITE)) }
        )
    }

    if (state.abroadOpen) {
        val p = state.profile
        AbroadPlanSheet(
            start = tripStartLabel(p.travelStartOffset ?: 0).takeIf { p.travelStartOffset != null } ?: stringResource(Res.string.match_not_set),
            length = tripLengthLabel(p.travelMonths),
            goal = goalLabel(p.goal),
            requested = p.abroadRequested,
            onRequestClick = { viewModel.onEvent(ParentHomeEvent.OnAbroadRequestClick) },
            onDismiss = { viewModel.onEvent(ParentHomeEvent.OnSheetDismiss) }
        )
    }

    MembershipSheetHost(
        sheet = state.membershipSheet,
        membership = state.membership,
        onSheetChange = { viewModel.onEvent(ParentHomeEvent.OnMembershipSheet(it)) },
        onStart = { plan, yearly -> viewModel.onEvent(ParentHomeEvent.OnStartPlan(plan, yearly)) },
        onSwitchToNext = { viewModel.onEvent(ParentHomeEvent.OnSwitchToNext) },
        onCancel = { viewModel.onEvent(ParentHomeEvent.OnCancelPlan) }
    )
}
