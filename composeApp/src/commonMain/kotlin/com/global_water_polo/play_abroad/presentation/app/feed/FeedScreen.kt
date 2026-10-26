package com.global_water_polo.play_abroad.presentation.app.feed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.global_water_polo.play_abroad.core.components.membership.MembershipSheetHost
import com.global_water_polo.play_abroad.core.utils.observeWithLifecycle
import com.global_water_polo.play_abroad.presentation.app.feed.components.CourseSheet
import com.global_water_polo.play_abroad.presentation.base.BaseContentView

@Composable
fun FeedScreen(
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel,
    showSnackBar: (String) -> Unit
) {
    val state by viewModel.feedState.collectAsStateWithLifecycle()

    viewModel.snackBarChannel.observeWithLifecycle { message ->
        showSnackBar(message)
    }

    BaseContentView(
        modifier = modifier,
        viewState = state,
        applySystemBarsPadding = false
    ) {
        FeedScreenContent(state = state, onEvent = viewModel::onEvent)
    }

    state.course?.let { index ->
        CourseSheet(
            index = index,
            state = state,
            onCtaClick = { viewModel.onEvent(FeedEvent.OnCourseCtaClick(index)) },
            onDismiss = { viewModel.onEvent(FeedEvent.OnCourseDismiss) }
        )
    }

    MembershipSheetHost(
        sheet = state.membershipSheet,
        membership = state.membership,
        onSheetChange = { viewModel.onEvent(FeedEvent.OnMembershipSheet(it)) },
        onStart = { plan, yearly -> viewModel.onEvent(FeedEvent.OnStartPlan(plan, yearly)) },
        onSwitchToNext = { viewModel.onEvent(FeedEvent.OnSwitchToNext) },
        onCancel = { viewModel.onEvent(FeedEvent.OnCancelPlan) }
    )
}
