package com.global_water_polo.play_abroad.presentation.app.globe

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.AppTab
import com.global_water_polo.play_abroad.core.components.AppTabBar
import com.global_water_polo.play_abroad.core.components.FormField
import com.global_water_polo.play_abroad.presentation.app.globe.components.Crest
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme
import kotlin.math.min

/**
 * Globe+ (the prototype's globeTab): the night globe under the header, then search, regions and two
 * rows to pick from, cities and clubs. Dragging across the globe spins it.
 */
@Composable
fun GlobeScreenContent(
    modifier: Modifier = Modifier,
    state: GlobeState,
    onEvent: (GlobeEvent) -> Unit,
    onDragStart: () -> Unit,
    onDrag: (Float) -> Unit,
    onDragEnd: (Float) -> Unit
) {
    val colors = GWPPATheme.colors
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        // Same ball size as GlobeStage's EXPLORE layout, so the drag area sits exactly over the globe.
        val ball = min(maxWidth.value * 0.86f, maxHeight.value * 0.38f).dp
        val statusTop = with(LocalDensity.current) { WindowInsets.statusBars.getTop(this).toDp() }
        Column(Modifier.fillMaxSize()) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(statusTop + 96.dp + ball)
                    .pointerInput(Unit) {
                        val degreesPerPx = 220f / min(size.width / density, 700f) / density
                        val tracker = VelocityTracker()
                        detectHorizontalDragGestures(
                            onDragStart = {
                                tracker.resetTracking()
                                onDragStart()
                            },
                            onDragEnd = { onDragEnd(tracker.calculateVelocity().x * degreesPerPx / 1000f) },
                            onDragCancel = { onDragEnd(0f) },
                            onHorizontalDrag = { change, dx ->
                                tracker.addPosition(change.uptimeMillis, change.position)
                                onDrag(dx * degreesPerPx)
                            }
                        )
                    }
            ) {
                Column(Modifier.padding(top = statusTop + 14.dp, start = 22.dp, end = 22.dp)) {
                    Text(stringResource(Res.string.globe_kicker).uppercase(), style = GWPPATheme.typography.archivoBold12, color = colors.eliteGold)
                    Text(stringResource(Res.string.globe_title), style = GWPPATheme.typography.archivoExpandedBold27, color = colors.colorWhite)
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(0f to Color.Transparent, 0.08f to colors.nightNavy.copy(alpha = 0.9f), 1f to colors.nightNavy))
                    .verticalScroll(rememberScrollState())
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 14.dp)
            ) {
                FormField(
                    label = stringResource(Res.string.globe_search),
                    value = state.query,
                    onValueChange = { onEvent(GlobeEvent.OnQueryChanged(it)) }
                )
                Regions(current = state.region, onClick = { onEvent(GlobeEvent.OnRegionClick(it)) })
                if (state.cities.isEmpty()) {
                    Text(stringResource(Res.string.globe_none), style = GWPPATheme.typography.archivoRegular14, color = colors.colorWhite.copy(alpha = 0.7f))
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                        state.cities.forEach { city ->
                            val clubs = state.clubs.count { it.cityId == city.id }
                            Pill(
                                text = "${city.flag} ${city.name}" + if (clubs > 0) "  $clubs" else "",
                                selected = city.id in state.profile.trips || city.id == state.focus?.id,
                                onClick = { onEvent(GlobeEvent.OnCityClick(city.id)) }
                            )
                        }
                    }
                    if (state.clubs.isNotEmpty()) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                            state.clubs.forEach { club ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.08f))
                                        .clickable { onEvent(GlobeEvent.OnClubClick(club.id)) }
                                        .padding(start = 6.dp, end = 14.dp, top = 6.dp, bottom = 6.dp)
                                ) {
                                    Crest(club, 30.dp)
                                    Text(club.name, style = GWPPATheme.typography.archivoBold13, color = colors.colorWhite)
                                }
                            }
                        }
                    }
                }
                Text(
                    stringResource(Res.string.globe_hint),
                    style = GWPPATheme.typography.archivoRegular13,
                    color = colors.colorWhite.copy(alpha = 0.55f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            AppTabBar(current = AppTab.GLOBE, onSelect = { onEvent(GlobeEvent.OnTabClick(it)) }, dark = true)
        }
    }
}

@Composable
private fun Regions(current: String, onClick: (String) -> Unit) {
    val colors = GWPPATheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.1f))
            .padding(3.dp)
    ) {
        listOf("eu" to Res.string.globe_eu, "am" to Res.string.globe_am, "world" to Res.string.globe_world).forEach { (key, label) ->
            val on = key == current
            Text(
                stringResource(label),
                style = GWPPATheme.typography.archivoBold13,
                color = if (on) colors.nightNavy else colors.colorWhite,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .clip(CircleShape)
                    .background(if (on) colors.colorWhite else Color.Transparent)
                    .clickable { onClick(key) }
                    .padding(vertical = 9.dp)
            )
        }
    }
}

@Composable
private fun Pill(text: String, selected: Boolean, onClick: () -> Unit) {
    val colors = GWPPATheme.colors
    Text(
        text,
        style = GWPPATheme.typography.archivoBold13,
        color = if (selected) colors.nightNavy else colors.colorWhite,
        modifier = Modifier
            .clip(CircleShape)
            .background(if (selected) colors.ballYellow else Color.White.copy(alpha = 0.08f))
            .border(1.dp, Color.White.copy(alpha = if (selected) 0f else 0.15f), CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp)
    )
}

@Preview
@Composable
private fun GlobeScreenContentPreview() {
    GWPPATheme {
        GlobeScreenContent(state = GlobeState(), onEvent = {}, onDragStart = {}, onDrag = {}, onDragEnd = {})
    }
}
