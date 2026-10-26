package com.global_water_polo.play_abroad.presentation.app.globe.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.ButtonStyle
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.shortDate
import com.global_water_polo.play_abroad.domain.model.Trip
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme
import kotlin.math.roundToInt

private const val MONTHS_SHOWN = 24

/**
 * Your dates (the prototype's datePicker): two taps on one scrolling calendar, arrive then leave. The
 * leave date must be one to twelve months after arriving; dates start next month.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerSheet(
    cityName: String,
    arrive: LocalDate,
    leave: LocalDate,
    today: LocalDate,
    onApply: (LocalDate, LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = GWPPATheme.colors
    var a by remember { mutableStateOf(arrive) }
    var b by remember { mutableStateOf<LocalDate?>(leave) }
    var pickingLeave by remember { mutableStateOf(false) }
    val firstMonth = remember(today) { today.minus(DatePeriod(days = today.day - 1)).plus(DatePeriod(months = 1)) }
    val startIndex = remember { ((arrive.year - firstMonth.year) * 12 + arrive.month.ordinal - firstMonth.month.ordinal).coerceIn(0, MONTHS_SHOWN - 1) }
    val list = rememberLazyListState(initialFirstVisibleItemIndex = startIndex)
    val months = b?.let { (a.daysUntil(it) / 30.44).roundToInt().coerceIn(Trip.MIN_MONTHS, Trip.MAX_MONTHS) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.boneWhite
    ) {
        Column(Modifier.fillMaxHeight(0.92f)) {
            Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(Res.string.dp_title), style = GWPPATheme.typography.archivoExpandedBold21, color = colors.inkBlack)
                    Text(cityName, style = GWPPATheme.typography.archivoRegular14, color = colors.slateGrey)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Picked(stringResource(Res.string.bk_arrive), shortDate(a, withYear = false), on = !pickingLeave, modifier = Modifier.weight(1f)) { pickingLeave = false }
                    Text("→", style = GWPPATheme.typography.archivoBold16, color = colors.mistGrey)
                    Picked(stringResource(Res.string.bk_leave), b?.let { shortDate(it, withYear = false) } ?: stringResource(Res.string.dp_select), on = pickingLeave, modifier = Modifier.weight(1f)) { pickingLeave = true }
                }
                Row {
                    stringArrayResource(Res.array.dp_weekdays).forEach {
                        Text(it, style = GWPPATheme.typography.archivoBold12, color = colors.mistGrey, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                    }
                }
            }
            LazyColumn(state = list, modifier = Modifier.weight(1f).padding(horizontal = 20.dp)) {
                items(MONTHS_SHOWN) { m ->
                    MonthGrid(
                        first = firstMonth.plus(DatePeriod(months = m)),
                        arrive = a,
                        leave = b,
                        enabled = { day -> !pickingLeave || (day >= a.plus(DatePeriod(months = 1)) && day <= a.plus(DatePeriod(months = 12))) },
                        onPick = { day ->
                            when {
                                !pickingLeave -> { a = day; b = null; pickingLeave = true }
                                day < a.plus(DatePeriod(months = 1)) -> { a = day; b = null }
                                else -> b = day
                            }
                        }
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.colorWhite)
                    .border(1.dp, colors.lineGrey)
                    .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 28.dp)
            ) {
                val leaveDate = b
                Column(Modifier.weight(1f)) {
                    Text(
                        if (leaveDate != null) "${shortDate(a, withYear = false)} – ${shortDate(leaveDate, withYear = false)}" else stringResource(Res.string.dp_pick_leave),
                        style = GWPPATheme.typography.archivoBold16,
                        color = colors.inkBlack
                    )
                    Text(
                        if (months != null) stringResource(Res.string.dp_in, pluralStringResource(Res.plurals.travel_months, months, months), cityName) else stringResource(Res.string.dp_range_note),
                        style = GWPPATheme.typography.archivoRegular13,
                        color = colors.slateGrey
                    )
                }
                PrimaryButton(
                    text = stringResource(Res.string.dp_apply),
                    onClick = { if (leaveDate != null) onApply(a, leaveDate) },
                    enabled = leaveDate != null,
                    style = ButtonStyle.INK,
                    modifier = Modifier.width(120.dp)
                )
            }
        }
    }
}

@Composable
private fun Picked(label: String, value: String, on: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val colors = GWPPATheme.colors
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(colors.colorWhite)
            .border(if (on) 2.dp else 1.dp, if (on) colors.inkBlack else colors.lineGrey, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(label, style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey)
        Text(value, style = GWPPATheme.typography.archivoBold16, color = colors.inkBlack)
    }
}

/** One month: its name, then the days Monday first, with the stay drawn as a band from arrive to leave. */
@Composable
private fun MonthGrid(first: LocalDate, arrive: LocalDate, leave: LocalDate?, enabled: (LocalDate) -> Boolean, onPick: (LocalDate) -> Unit) {
    val colors = GWPPATheme.colors
    val monthName = stringArrayResource(Res.array.months)[first.month.ordinal]
    val length = first.daysUntil(first.plus(DatePeriod(months = 1)))
    val cells = List(first.dayOfWeek.isoDayNumber - 1) { null } + (0 until length).map { first.plus(DatePeriod(days = it)) }
    Column(Modifier.padding(top = 16.dp)) {
        Text("$monthName ${first.year}", style = GWPPATheme.typography.archivoExpandedBold16, color = colors.inkBlack, modifier = Modifier.padding(bottom = 8.dp))
        cells.chunked(7).forEach { week ->
            Row {
                (0 until 7).forEach { i ->
                    val day = week.getOrNull(i)
                    Box(Modifier.weight(1f).aspectRatio(1.1f), contentAlignment = Alignment.Center) {
                        if (day != null) {
                            val isEnd = day == arrive || day == leave
                            val inRange = leave != null && day > arrive && day < leave
                            val ok = enabled(day)
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .fillMaxHeight()
                                    .background(if (inRange) colors.chlorineBlueSoft else Color.Transparent)
                            )
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .aspectRatio(1f)
                                    .padding(2.dp)
                                    .alpha(if (ok || isEnd) 1f else 0.3f)
                                    .clip(CircleShape)
                                    .background(if (isEnd) colors.inkBlack else Color.Transparent)
                                    .clickable(enabled = ok) { onPick(day) }
                            ) {
                                Text(
                                    day.day.toString(),
                                    style = GWPPATheme.typography.archivoSemiBold14,
                                    color = if (isEnd) colors.colorWhite else colors.inkBlack
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
