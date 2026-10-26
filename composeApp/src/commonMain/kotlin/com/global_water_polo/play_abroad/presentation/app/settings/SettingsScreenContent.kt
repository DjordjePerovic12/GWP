package com.global_water_polo.play_abroad.presentation.app.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.AppTab
import com.global_water_polo.play_abroad.core.components.AppTabBar
import com.global_water_polo.play_abroad.core.components.InfoPanel
import com.global_water_polo.play_abroad.core.components.InfoRow
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.presentation.app.profile.components.SectionHeader
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** Settings tab (the prototype's settingsTab): profile shortcuts and the account. */
@Composable
fun SettingsScreenContent(
    modifier: Modifier = Modifier,
    state: SettingsState,
    onTabClick: (AppTab) -> Unit,
    onEditProfileClick: () -> Unit,
    onPreviewClick: () -> Unit,
    onSubscriptionClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onHomeAirportClick: () -> Unit,
    onSignOutClick: () -> Unit
) {
    val colors = GWPPATheme.colors
    val m = state.membership
    Column(modifier = modifier.fillMaxSize().background(colors.boneWhite)) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 24.dp)
        ) {
            Text(stringResource(Res.string.set_title), style = GWPPATheme.typography.archivoExpandedBold27, color = colors.inkBlack)
            SectionHeader(title = stringResource(Res.string.set_profile))
            InfoPanel {
                InfoRow(stringResource(Res.string.set_edit), stringResource(Res.string.port_complete, "${state.portfolioPercent}%"), onClick = onEditProfileClick)
                HorizontalDivider(color = colors.lineGrey)
                InfoRow(stringResource(Res.string.set_preview), "", onClick = onPreviewClick)
            }
            SectionHeader(title = stringResource(Res.string.set_account))
            InfoPanel {
                InfoRow(
                    stringResource(Res.string.set_subscription),
                    stringResource(
                        when {
                            m.elite -> Res.string.trial_elite
                            m.next -> Res.string.trial_next
                            else -> Res.string.offers_plan_free
                        }
                    ),
                    onClick = onSubscriptionClick
                )
                HorizontalDivider(color = colors.lineGrey)
                InfoRow(stringResource(Res.string.set_email), state.profile.email.ifEmpty { stringResource(Res.string.not_added) })
                HorizontalDivider(color = colors.lineGrey)
                InfoRow(
                    stringResource(Res.string.set_notifications),
                    stringResource(if (state.profile.notifications) Res.string.set_on else Res.string.set_off),
                    onClick = onNotificationsClick
                )
                HorizontalDivider(color = colors.lineGrey)
                InfoRow(stringResource(Res.string.set_home_airport), state.profile.homeAirport, onClick = onHomeAirportClick)
                HorizontalDivider(color = colors.lineGrey)
                InfoRow(stringResource(Res.string.set_sign_out), "", onClick = onSignOutClick)
            }
            SmallText(text = stringResource(Res.string.set_proto), center = true, modifier = Modifier.fillMaxWidth().padding(top = 18.dp))
        }
        AppTabBar(current = AppTab.SETTINGS, onSelect = onTabClick)
    }
}

@Preview
@Composable
private fun SettingsScreenContentPreview() {
    GWPPATheme {
        SettingsScreenContent(
            state = SettingsState(),
            onTabClick = {},
            onEditProfileClick = {},
            onPreviewClick = {},
            onSubscriptionClick = {},
            onNotificationsClick = {},
            onHomeAirportClick = {},
            onSignOutClick = {}
        )
    }
}
