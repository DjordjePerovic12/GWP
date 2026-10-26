package com.global_water_polo.play_abroad.presentation.app.globe

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import com.global_water_polo.play_abroad.domain.model.WaterPoloMap
import com.global_water_polo.play_abroad.presentation.app.globe.components.ClubGroupPopup
import com.global_water_polo.play_abroad.presentation.app.globe.components.GlobeFilters
import com.global_water_polo.play_abroad.presentation.app.globe.components.GlobeZoomButtons
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
 * Globe+ (the prototype's globeTab): the night globe under the header, then search, filters, regions and
 * two rows to pick from, cities and clubs. Drag to turn and lean the globe, pinch or use the buttons to
 * zoom, tap a country, city or club badge.
 */
@Composable
fun GlobeScreenContent(
    modifier: Modifier = Modifier,
    state: GlobeState,
    onEvent: (GlobeEvent) -> Unit,
    zoom: Float,
    onDragStart: () -> Unit,
    onDrag: (degrees: Float, tiltRadians: Float) -> Unit,
    onDragEnd: (Float) -> Unit,
    onPinch: (Float) -> Unit,
    onTap: (Offset) -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit
) {
    val colors = GWPPATheme.colors
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        // Same ball size as GlobeStage's EXPLORE layout, so the drag area sits exactly over the globe.
        val ball = min(maxWidth.value * 0.86f, maxHeight.value * 0.38f).dp
        val statusTop = with(LocalDensity.current) { WindowInsets.statusBars.getTop(this).toDp() }
        var boxOrigin by remember { mutableStateOf(Offset.Zero) }
        val currentZoom = rememberUpdatedState(zoom)
        Column(Modifier.fillMaxSize()) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(statusTop + 96.dp + ball)
                    .onGloballyPositioned { boxOrigin = it.positionInRoot() }
                    .pointerInput(ball) {
                        // Google Earth style: one finger turns the ball (sideways) and leans it (up and down), two
                        // fingers pinch to zoom, a quick tap picks what is under the finger.
                        val tracker = VelocityTracker()
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            val start = down.position
                            var moved = false
                            var pinched = false
                            tracker.resetTracking()
                            tracker.addPosition(down.uptimeMillis, down.position)
                            do {
                                val event = awaitPointerEvent()
                                val pressed = event.changes.filter { it.pressed }
                                // The finger is on the ball's surface: a radius of travel turns it one radian.
                                val radiusPx = ball.toPx() * currentZoom.value / 2f
                                if (pressed.size >= 2) {
                                    if (!moved) onDragStart()
                                    moved = true
                                    pinched = true
                                    val factor = event.calculateZoom()
                                    if (factor != 1f) onPinch(factor)
                                } else if (pressed.size == 1 && !pinched) {
                                    val change = pressed[0]
                                    if (!moved && (change.position - start).getDistance() > viewConfiguration.touchSlop) {
                                        moved = true
                                        onDragStart()
                                    }
                                    if (moved) {
                                        val d = change.positionChange()
                                        tracker.addPosition(change.uptimeMillis, change.position)
                                        onDrag(d.x / radiusPx * 57.3f, d.y / radiusPx)
                                    }
                                }
                                if (moved) event.changes.forEach { if (it.positionChange() != Offset.Zero) it.consume() }
                            } while (event.changes.any { it.pressed })
                            val radiusPx = ball.toPx() * currentZoom.value / 2f
                            when {
                                !moved -> onTap(boxOrigin + start)
                                pinched -> onDragEnd(0f)
                                else -> onDragEnd(tracker.calculateVelocity().x / radiusPx * 57.3f / 1000f)
                            }
                        }
                    }
            ) {
                Row(Modifier.padding(top = statusTop + 14.dp, start = 22.dp, end = 16.dp), verticalAlignment = Alignment.Top) {
                    Column(Modifier.weight(1f)) {
                        Text(stringResource(Res.string.globe_kicker).uppercase(), style = GWPPATheme.typography.archivoBold12, color = colors.eliteGold)
                        Text(stringResource(Res.string.globe_title), style = GWPPATheme.typography.archivoExpandedBold27, color = colors.colorWhite)
                    }
                    TripsButton(count = state.profile.trips.size, onClick = { onEvent(GlobeEvent.OnTripsClick) })
                }
                GlobeZoomButtons(onZoomIn = onZoomIn, onZoomOut = onZoomOut, modifier = Modifier.align(Alignment.CenterEnd).padding(end = 12.dp, top = statusTop + 40.dp))
                state.popup?.let { WaterPoloMap.clubGroups.getOrNull(it) }?.let { group ->
                    ClubGroupPopup(
                        group = group,
                        onClubClick = { onEvent(GlobeEvent.OnClubClick(it)) },
                        onDismiss = { onEvent(GlobeEvent.OnPopupDismiss) },
                        modifier = Modifier.align(Alignment.BottomCenter).padding(horizontal = 12.dp, vertical = 8.dp)
                    )
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
                GlobeFilters(
                    country = state.country,
                    sex = state.sex,
                    onCountryClick = { onEvent(GlobeEvent.OnCountryClick(it)) },
                    onSexClick = { onEvent(GlobeEvent.OnSexClick(it)) }
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
                                selected = state.tripIn(city.id) != null || city.id == state.focus?.id,
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

/** The bag in the header (`.g-cart`): opens Your trips, with the count on it. */
@Composable
private fun TripsButton(count: Int, onClick: () -> Unit) {
    val colors = GWPPATheme.colors
    Box {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.12f))
                .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                .clickable(onClickLabel = stringResource(Res.string.globe_trips), onClick = onClick)
        ) {
            Canvas(Modifier.size(20.dp)) {
                val w = size.width
                val stroke = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                val bag = Path().apply {
                    moveTo(w * 0.2f, w * 0.33f)
                    lineTo(w * 0.8f, w * 0.33f)
                    lineTo(w * 0.75f, w * 0.88f)
                    lineTo(w * 0.25f, w * 0.88f)
                    close()
                }
                drawPath(bag, colors.colorWhite, style = stroke)
                drawArc(colors.colorWhite, 180f, 180f, false, topLeft = Offset(w * 0.37f, w * 0.12f), size = Size(w * 0.26f, w * 0.42f), style = stroke)
            }
        }
        if (count > 0) {
            Text(
                count.toString(),
                style = GWPPATheme.typography.archivoBold11,
                color = colors.inkBlack,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.TopEnd).size(18.dp).clip(CircleShape).background(colors.ballYellow)
            )
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
        GlobeScreenContent(
            state = GlobeState(),
            onEvent = {},
            zoom = 1f,
            onDragStart = {},
            onDrag = { _, _ -> },
            onDragEnd = {},
            onPinch = {},
            onTap = {},
            onZoomIn = {},
            onZoomOut = {}
        )
    }
}
