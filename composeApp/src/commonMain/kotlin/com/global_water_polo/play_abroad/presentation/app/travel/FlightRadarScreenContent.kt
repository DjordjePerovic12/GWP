package com.global_water_polo.play_abroad.presentation.app.travel

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.AppTab
import com.global_water_polo.play_abroad.core.components.AppTabBar
import com.global_water_polo.play_abroad.core.components.ButtonStyle
import com.global_water_polo.play_abroad.core.components.ChoiceChip
import com.global_water_polo.play_abroad.core.components.InfoPanel
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.domain.model.City
import com.global_water_polo.play_abroad.presentation.app.profile.components.SectionHeader
import com.global_water_polo.play_abroad.presentation.app.travel.components.RouteMap
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** Travel tab (the prototype's flyTab): the radar, the home airport, the trips and "before you fly". */
@Composable
fun FlightRadarScreenContent(
    modifier: Modifier = Modifier,
    state: FlightRadarState,
    onEvent: (FlightRadarEvent) -> Unit
) {
    val colors = GWPPATheme.colors
    val dest = state.destination
    val home = state.profile.homeAirport
    Column(modifier = modifier.fillMaxSize().background(colors.boneWhite)) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 24.dp)
        ) {
            Text(stringResource(Res.string.fly_title), style = GWPPATheme.typography.archivoExpandedBold27, color = colors.inkBlack)
            Text(stringResource(Res.string.fly_sub), style = GWPPATheme.typography.archivoRegular15, color = colors.slateGrey)
            RouteMap(
                from = state.home,
                to = dest.latitude to dest.longitude,
                fromLabel = home,
                toLabel = dest.airport,
                caption = stringResource(if (state.trips.isEmpty()) Res.string.fly_sample else Res.string.fly_next_trip),
                title = "${dest.flag} ${dest.name}",
                sub = "",
                modifier = Modifier.padding(top = 14.dp)
            )
            Text(stringResource(Res.string.fly_home), style = GWPPATheme.typography.archivoSemiBold13, color = colors.slateGrey, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                FlightRadarState.HOME_AIRPORTS.keys.forEach { code ->
                    ChoiceChip(text = code, selected = code == home, onClick = { onEvent(FlightRadarEvent.OnHomeAirportClick(code)) })
                }
            }
            SectionHeader(title = stringResource(Res.string.fly_trips), note = if (state.trips.isEmpty()) "" else state.trips.size.toString())
            if (state.trips.isEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(colors.colorWhite)
                        .border(1.dp, colors.lineGrey, RoundedCornerShape(18.dp))
                        .padding(18.dp)
                ) {
                    Text(stringResource(Res.string.fly_empty), style = GWPPATheme.typography.archivoBold16, color = colors.inkBlack)
                    Text(stringResource(Res.string.fly_empty_sub), style = GWPPATheme.typography.archivoRegular14, color = colors.slateGrey)
                    PrimaryButton(text = stringResource(Res.string.fly_open_globe), onClick = { onEvent(FlightRadarEvent.OnOpenGlobeClick) }, style = ButtonStyle.BALL, modifier = Modifier.padding(top = 8.dp))
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    state.trips.forEach { city ->
                        TripCard(
                            city = city,
                            home = home,
                            requested = city.id in state.profile.flightRequests,
                            onRequestClick = { onEvent(FlightRadarEvent.OnRequestFlightsClick(city.id)) }
                        )
                    }
                }
            }
            SectionHeader(title = stringResource(Res.string.fly_before))
            InfoPanel {
                FlightRadarState.CHECKS.forEachIndexed { i, key ->
                    if (i > 0) HorizontalDivider(color = colors.lineGrey)
                    val done = key in state.profile.travelChecks
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth().clickable { onEvent(FlightRadarEvent.OnCheckClick(key)) }.padding(vertical = 12.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(22.dp)
                                .clip(RoundedCornerShape(7.dp))
                                .background(if (done) colors.chlorineBlue else colors.colorWhite)
                                .border(1.5.dp, if (done) colors.chlorineBlue else colors.mistGrey, RoundedCornerShape(7.dp))
                        ) {
                            if (done) Text("✓", style = GWPPATheme.typography.archivoBold12, color = colors.colorWhite)
                        }
                        Text(stringResource(checkLabel(key)), style = GWPPATheme.typography.archivoRegular15, color = colors.inkBlack)
                    }
                }
            }
            SmallText(text = stringResource(Res.string.fly_fine), modifier = Modifier.padding(top = 12.dp))
        }
        AppTabBar(current = AppTab.TRAVEL, onSelect = { onEvent(FlightRadarEvent.OnTabClick(it)) })
    }
}

private fun checkLabel(key: String) = when (key) {
    "pass" -> Res.string.fly_chk_pass
    "visa" -> Res.string.fly_chk_visa
    "ins" -> Res.string.fly_chk_ins
    else -> Res.string.fly_chk_pick
}

@Composable
private fun TripCard(city: City, home: String, requested: Boolean, onRequestClick: () -> Unit) {
    val colors = GWPPATheme.colors
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colors.colorWhite)
            .border(1.dp, colors.lineGrey, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(home, style = GWPPATheme.typography.archivoExpandedBold21, color = colors.inkBlack)
            Text("✈", style = GWPPATheme.typography.archivoExpandedBold16, color = colors.chlorineBlue)
            Text(city.airport, style = GWPPATheme.typography.archivoExpandedBold21, color = colors.inkBlack)
        }
        Text("${city.flag} ${city.name}, ${city.country}", style = GWPPATheme.typography.archivoRegular14, color = colors.slateGrey)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column {
                Text(stringResource(Res.string.fly_depart), style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey)
                Text(stringResource(Res.string.fly_dates_tbc), style = GWPPATheme.typography.archivoBold13, color = colors.inkBlack)
            }
            Column {
                Text(stringResource(Res.string.fly_status), style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey)
                Text(stringResource(if (requested) Res.string.fly_requested else Res.string.fly_not_booked), style = GWPPATheme.typography.archivoBold13, color = colors.inkBlack)
            }
        }
        Text(
            stringResource(if (requested) Res.string.fly_requested else Res.string.fly_request),
            style = GWPPATheme.typography.archivoBold13,
            color = if (requested) colors.colorWhite else colors.inkBlack,
            modifier = Modifier
                .clip(CircleShape)
                .background(if (requested) colors.inkBlack else colors.ballYellow)
                .clickable(onClick = onRequestClick)
                .padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@Preview
@Composable
private fun FlightRadarScreenContentPreview() {
    GWPPATheme {
        FlightRadarScreenContent(state = FlightRadarState(), onEvent = {})
    }
}
