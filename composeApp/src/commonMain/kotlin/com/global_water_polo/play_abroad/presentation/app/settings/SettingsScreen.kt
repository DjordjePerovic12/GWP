package com.global_water_polo.play_abroad.presentation.app.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.global_water_polo.play_abroad.core.components.membership.MembershipSheet
import com.global_water_polo.play_abroad.core.components.membership.MembershipSheetHost
import com.global_water_polo.play_abroad.core.utils.observeWithLifecycle
import com.global_water_polo.play_abroad.presentation.base.BaseContentView

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel,
    showSnackBar: (String) -> Unit
) {
    val state by viewModel.settingsState.collectAsStateWithLifecycle()

    viewModel.snackBarChannel.observeWithLifecycle { message ->
        showSnackBar(message)
    }

    BaseContentView(
        modifier = modifier,
        viewState = state,
        applySystemBarsPadding = false
    ) {
        SettingsScreenContent(
            state = state,
            onTabClick = { viewModel.onEvent(SettingsEvent.OnTabClick(it)) },
            onEditProfileClick = { viewModel.onEvent(SettingsEvent.OnEditProfileClick) },
            onPreviewClick = { viewModel.onEvent(SettingsEvent.OnPreviewClick) },
            onSubscriptionClick = { viewModel.onEvent(SettingsEvent.OnMembershipSheet(MembershipSheet.MANAGE)) },
            onNotificationsClick = { viewModel.onEvent(SettingsEvent.OnNotificationsClick) },
            onHomeAirportClick = { viewModel.onEvent(SettingsEvent.OnHomeAirportClick) },
            onSignOutClick = { viewModel.onEvent(SettingsEvent.OnSignOutClick) }
        )
    }

    MembershipSheetHost(
        sheet = state.membershipSheet,
        membership = state.membership,
        onSheetChange = { viewModel.onEvent(SettingsEvent.OnMembershipSheet(it)) },
        onStart = { plan, yearly -> viewModel.onEvent(SettingsEvent.OnStartPlan(plan, yearly)) },
        onSwitchToNext = { viewModel.onEvent(SettingsEvent.OnSwitchToNext) },
        onCancel = { viewModel.onEvent(SettingsEvent.OnCancelPlan) }
    )
}
