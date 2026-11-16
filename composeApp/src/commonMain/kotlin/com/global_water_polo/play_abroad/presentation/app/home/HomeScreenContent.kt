package com.global_water_polo.play_abroad.presentation.app.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.AppTab
import com.global_water_polo.play_abroad.core.components.AppTabBar
import com.global_water_polo.play_abroad.core.components.ClockChip
import com.global_water_polo.play_abroad.core.utils.LaneKind
import com.global_water_polo.play_abroad.core.utils.LockAction
import com.global_water_polo.play_abroad.domain.model.Profile
import com.global_water_polo.play_abroad.presentation.app.profile.components.PathwaySection
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme
import kotlin.time.Clock

/** Home (v29's home28): greeting, NCAA chip, then "Your next chapter", the plan, programs and clinics. */
@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    state: HomeState,
    onEvent: (HomeEvent) -> Unit
) {
    val colors = GWPPATheme.colors
    Column(modifier = modifier.fillMaxSize().background(colors.boneWhite)) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 24.dp)
        ) {
            Header(state)
            PathwaySection(
                state = state.pathway,
                onBuildClick = { onEvent(HomeEvent.OnPlanClick) },
                onGlobeClick = { onEvent(HomeEvent.OnGlobeClick) },
                onProgramClick = { onEvent(HomeEvent.OnProgramClick(it)) },
                onCityClick = { onEvent(HomeEvent.OnCityClick(it)) },
                onClinicClick = { onEvent(HomeEvent.OnClinicClick(it)) },
                heroImage = Res.drawable.gwp_montenegro
            )
        }
        AppTabBar(
            current = AppTab.HOME,
            onSelect = { onEvent(HomeEvent.OnTabClick(it)) },
            inboxBadge = state.inboxBadge,
            lock = state.lock,
            onLockClick = { onEvent(HomeEvent.OnLock(LockAction.OPEN)) }
        )
    }
}

/** `.hm28`: "Good morning, Alex" and "Your next chapter", the NCAA chip on the right. */
@Composable
private fun Header(state: HomeState) {
    val colors = GWPPATheme.colors
    val hour = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour }
    val first = state.profile.firstName.trim()
    val hello = stringResource(
        when {
            hour < 12 -> Res.string.home_morning
            hour < 18 -> Res.string.home_afternoon
            else -> Res.string.home_evening
        }
    )
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(if (first.isEmpty()) hello else "$hello, $first", style = GWPPATheme.typography.archivoRegular15, color = colors.slateGrey)
            Text(stringResource(Res.string.home_title), style = GWPPATheme.typography.archivoExpandedBold27, color = colors.inkBlack)
        }
        val lane = state.lane
        when {
            lane == null || lane.lane == LaneKind.PRO || lane.lane == LaneKind.CLOSED -> Unit
            lane.lane == LaneKind.APPROACHING -> ClockChip(countdown = state.countdown, clockYear = lane.clock.year, compact = true)
            else -> Text(
                if (lane.running) stringResource(Res.string.ready_window_running, lane.yearN, lane.clock.closes)
                else stringResource(Res.string.home_chip_opens, lane.clock.year),
                style = GWPPATheme.typography.archivoBold12,
                color = colors.colorWhite,
                modifier = Modifier.clip(CircleShape).background(colors.nightNavy).padding(horizontal = 12.dp, vertical = 7.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenContentPreview() {
    GWPPATheme {
        HomeScreenContent(state = HomeState(profile = Profile(firstName = "Alex", gradYear = 2027)), onEvent = {})
    }
}
