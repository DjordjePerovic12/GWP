package com.global_water_polo.play_abroad.presentation.onboarding.birthday.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme
import kotlin.math.abs
import kotlin.math.max

private val ROW = 40.dp

/**
 * The prototype's birthday picker: three iOS-style wheels (month, day, year) that start blank.
 * The first row of each wheel is its label, so nothing is preset.
 * [onChange] gets 1-based indexes (null while a wheel still shows its label) and whether a person moved it.
 */
@Composable
fun BirthdayWheels(
    months: List<String>,
    years: List<Int>,
    month: Int?,
    day: Int?,
    year: Int?,
    monthLabel: String,
    dayLabel: String,
    yearLabel: String,
    onChange: (month: Int?, day: Int?, year: Int?, fromUser: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var m by remember { mutableStateOf(month) }
    var d by remember { mutableStateOf(day) }
    var y by remember { mutableStateOf(year) }
    val colors = GWPPATheme.colors
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(top = 0.dp)
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .drawWithContent {
                drawContent()
                drawRect(
                    Brush.verticalGradient(0f to Color.Transparent, 0.3f to Color.Black, 0.7f to Color.Black, 1f to Color.Transparent),
                    blendMode = BlendMode.DstIn
                )
            }
    ) {
        Box(
            Modifier
                .padding(top = 80.dp)
                .fillMaxWidth()
                .height(ROW)
                .background(colors.colorWhite, RoundedCornerShape(12.dp))
                .border(1.dp, colors.lineGrey, RoundedCornerShape(12.dp))
        )
        Row(Modifier.fillMaxWidth()) {
            Wheel(monthLabel, months, m, Modifier.weight(1.5f)) { v, user -> m = v; onChange(m, d, y, user) }
            Wheel(dayLabel, (1..31).map { it.toString() }, d, Modifier.weight(0.8f)) { v, user -> d = v; onChange(m, d, y, user) }
            Wheel(yearLabel, years.map { it.toString() }, y?.let { years.indexOf(it) + 1 }?.takeIf { it > 0 }, Modifier.weight(1f)) { v, user ->
                y = v?.let { years[it - 1] }
                onChange(m, d, y, user)
            }
        }
    }
}

@Composable
private fun Wheel(
    label: String,
    items: List<String>,
    selected: Int?,
    modifier: Modifier = Modifier,
    onSelected: (Int?, Boolean) -> Unit
) {
    val state = rememberLazyListState(initialFirstVisibleItemIndex = selected ?: 0)
    val fling = rememberSnapFlingBehavior(state)
    val haptics = LocalHapticFeedback.current
    val typography = GWPPATheme.typography
    val colors = GWPPATheme.colors
    var touched by remember { mutableStateOf(false) }
    val center by remember {
        derivedStateOf {
            val info = state.layoutInfo
            val mid = (info.viewportStartOffset + info.viewportEndOffset) / 2
            info.visibleItemsInfo.minByOrNull { abs(it.offset + it.size / 2 - mid) }?.index ?: 0
        }
    }
    LaunchedEffect(state) {
        snapshotFlow { state.isScrollInProgress }.collect { if (it) touched = true }
    }
    LaunchedEffect(state) {
        var last = center
        snapshotFlow { center }.collect { index ->
            if (index != last) {
                last = index
                if (touched) haptics.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                onSelected(if (index == 0) null else index, touched)
            }
        }
    }
    LazyColumn(
        state = state,
        flingBehavior = fling,
        contentPadding = PaddingValues(vertical = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.height(200.dp)
    ) {
        items(items.size + 1) { k ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ROW)
                    .graphicsLayer {
                        val info = state.layoutInfo
                        val item = info.visibleItemsInfo.firstOrNull { it.index == k }
                        if (item != null) {
                            val mid = (info.viewportStartOffset + info.viewportEndOffset) / 2f
                            val off = (item.offset + item.size / 2f - mid) / item.size
                            val a = abs(off)
                            rotationX = -off * 18f
                            scaleX = 1f - a * 0.04f
                            scaleY = scaleX
                            alpha = max(0.12f, 1f - a * 0.3f)
                        }
                    }
            ) {
                Text(
                    text = if (k == 0) label else items[k - 1],
                    style = if (k == 0) typography.archivoRegular16 else typography.archivoSemiBold18,
                    color = if (k == 0) colors.mistGrey else colors.inkBlack,
                    maxLines = 1
                )
            }
        }
    }
}
