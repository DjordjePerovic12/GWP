package com.global_water_polo.play_abroad.presentation.onboarding.clock

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.stringArrayResource
import com.global_water_polo.play_abroad.core.utils.seasonLabel
import com.global_water_polo.play_abroad.resources.*
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.unit.em
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.BackButton
import com.global_water_polo.play_abroad.core.components.ButtonStyle
import com.global_water_polo.play_abroad.core.components.OnboardingLayout
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.TextLink
import com.global_water_polo.play_abroad.core.utils.NcaaClockCalculator
import com.global_water_polo.play_abroad.core.utils.hmsText
import com.global_water_polo.play_abroad.domain.model.Lane
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingState
import com.global_water_polo.play_abroad.presentation.onboarding.clock.components.OdometerRow
import com.global_water_polo.play_abroad.presentation.onboarding.clock.components.ReelEasing
import com.global_water_polo.play_abroad.presentation.onboarding.clock.components.SeasonStrip
import com.global_water_polo.play_abroad.presentation.onboarding.clock.components.reelPosition
import com.global_water_polo.play_abroad.resources.Res
import com.global_water_polo.play_abroad.resources.clock_change_birthday
import com.global_water_polo.play_abroad.resources.clock_closes
import com.global_water_polo.play_abroad.resources.clock_continue
import com.global_water_polo.play_abroad.resources.clock_days
import com.global_water_polo.play_abroad.resources.clock_fall
import com.global_water_polo.play_abroad.resources.clock_running_their
import com.global_water_polo.play_abroad.resources.clock_running_your
import com.global_water_polo.play_abroad.resources.clock_starts_their
import com.global_water_polo.play_abroad.resources.clock_starts_your
import com.global_water_polo.play_abroad.resources.clock_sub
import com.global_water_polo.play_abroad.resources.haptics_off
import com.global_water_polo.play_abroad.resources.haptics_on
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

@Composable
fun ClockScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    hapticsOn: Boolean,
    onHapticsClick: () -> Unit,
    onDigitLocked: () -> Unit,
    onSettled: () -> Unit,
    onSecond: () -> Unit,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    val colors = GWPPATheme.colors
    val typography = GWPPATheme.typography
    val window = state.window
    val countdown = state.countdown
    if (window == null || countdown == null) return

    val days = countdown.days.toString()
    val hms = hmsText(countdown)
    val digits = (days + hms).filter { it.isDigit() }
    val reels = remember(digits.length) { List(digits.length) { Animatable(0f) } }
    var settled by remember { mutableStateOf(false) }

    // Slot-machine roll: every digit spins through its reel and locks in, left to right.
    LaunchedEffect(reels) {
        reels.forEachIndexed { i, reel ->
            launch {
                reel.animateTo(reelPosition(digits[i].digitToInt()), tween(1100 + i * 170, easing = ReelEasing))
                onDigitLocked()
            }
        }
        delay(1100L + reels.size * 170L + 120L)
        settled = true
        onSettled()
    }
    // Then it ticks: changed digits roll to their new value.
    LaunchedEffect(digits, settled) {
        if (!settled) return@LaunchedEffect
        reels.forEachIndexed { i, reel ->
            val target = reelPosition(digits[i].digitToInt())
            if (reel.targetValue != target) launch { reel.animateTo(target, tween(320, easing = ReelEasing)) }
        }
    }
    val beat = remember { Animatable(0f) }
    LaunchedEffect(countdown.seconds, settled) {
        if (!settled) return@LaunchedEffect
        onSecond()
        beat.snapTo(1f)
        beat.animateTo(0f, tween(1000))
    }

    val after by animateFloatAsState(if (settled) 1f else 0f, tween(700))
    val cta by animateFloatAsState(if (settled) 1f else 0f, tween(600, delayMillis = 500))
    val your = state.lane == Lane.ATHLETE

    OnboardingLayout(
        modifier = modifier,
        barStart = { BackButton(onClick = onBackClick, dark = true) },
        barEnd = {
            Text(
                text = stringResource(if (hapticsOn) Res.string.haptics_on else Res.string.haptics_off),
                style = typography.archivoSemiBold12,
                color = if (hapticsOn) colors.colorWhite else Color.White.copy(alpha = 0.55f),
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
                    .border(1.dp, Color.White.copy(alpha = 0.22f), CircleShape)
                    .clickable(onClick = onHapticsClick)
                    .padding(horizontal = 12.dp, vertical = 7.dp)
            )
        },
        bottom = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.graphicsLayer {
                    alpha = cta
                    translationY = (1f - cta) * 14.dp.toPx()
                }
            ) {
                PrimaryButton(text = stringResource(Res.string.clock_continue), onClick = onContinueClick, style = ButtonStyle.BALL, enabled = settled)
                TextLink(text = stringResource(Res.string.clock_change_birthday), onClick = onBackClick, light = true)
            }
        }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(top = 6.dp)
            ) {
                Canvas(Modifier.size(10.dp)) {
                    val b = beat.value
                    drawCircle(colors.liveRed.copy(alpha = 0.7f * b), radius = size.minDimension / 2f + 14.dp.toPx() * (1f - b), center = center)
                    drawCircle(colors.liveRed.copy(alpha = 0.35f), radius = size.minDimension, center = center)
                    drawCircle(colors.liveRed, radius = size.minDimension / 2f * (1f + 0.5f * b), center = center)
                }
                Text(
                    text = stringResource(
                        when {
                            countdown.running && your -> Res.string.clock_running_your
                            countdown.running -> Res.string.clock_running_their
                            your -> Res.string.clock_starts_your
                            else -> Res.string.clock_starts_their
                        }
                    ),
                    style = typography.archivoSemiBold15,
                    color = Color.White.copy(alpha = 0.78f)
                )
            }
            // The prototype's blue drop-shadow glow, drawn once behind the number (a text shadow would be clipped by the reels).
            val glow = colors.chlorineBlue
            OdometerRow(
                text = days,
                reels = reels,
                firstReel = 0,
                style = typography.archivoExpandedExtraBold104.copy(color = colors.colorWhite),
                separatorColor = colors.colorWhite,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .drawBehind {
                        val c = Offset(size.width / 2f, size.height / 2f + 8.dp.toPx())
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(glow.copy(alpha = 0.42f), glow.copy(alpha = 0.12f), Color.Transparent),
                                center = c,
                                radius = size.width * 0.62f
                            ),
                            radius = size.width * 0.62f,
                            center = c
                        )
                    }
            )
            Text(
                text = pluralStringResource(Res.plurals.clock_days, countdown.days.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()).uppercase(),
                style = typography.archivoExpandedBold17.copy(letterSpacing = 0.3.em),
                color = colors.ballYellow,
                modifier = Modifier.padding(top = 6.dp)
            )
            OdometerRow(
                text = hms,
                reels = reels,
                firstReel = days.length,
                style = typography.archivoExpandedBold37.copy(color = Color.White.copy(alpha = 0.92f)),
                separatorColor = Color.White.copy(alpha = 0.35f),
                modifier = Modifier.padding(top = 18.dp)
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .padding(top = 26.dp)
                    .graphicsLayer {
                        alpha = after
                        translationY = (1f - after) * 14.dp.toPx()
                    }
            ) {
                Text(
                    text = if (countdown.running) {
                        stringResource(Res.string.clock_closes, window.clock.closes)
                    } else {
                        stringResource(Res.string.clock_fall, window.clock.year)
                    },
                    style = typography.archivoExpandedExtraBold32,
                    color = colors.colorWhite,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(Res.string.clock_sub),
                    style = typography.archivoRegular16,
                    color = Color.White.copy(alpha = 0.72f),
                    textAlign = TextAlign.Center
                )
                SeasonStrip(firstYear = window.clock.year, modifier = Modifier.padding(top = 16.dp))
            }
            // Scroll down: the timeline draws in, then the choice, then the fine print (v29's ck28 cards).
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .padding(top = 28.dp)
                    .fillMaxWidth()
                    .graphicsLayer { alpha = after }
            ) {
                TimelineCard(state = state, your = your)
                ChoiceCard(state = state, your = your)
                Text(
                    stringResource(if (your) Res.string.ck_fine else Res.string.ck_fine_parent),
                    style = typography.archivoRegular13,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
        }
    }
}

/** A dark see-through card on the night sky. */
@Composable
private fun NightCard(kicker: String, content: @Composable () -> Unit) {
    val colors = GWPPATheme.colors
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.06f))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Text(
            kicker.uppercase(),
            style = GWPPATheme.typography.archivoBold11.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 0.16.em),
            color = colors.eliteGold
        )
        content()
    }
}

private class TimelineRow(val title: String, val line: String, val at: LocalDate, val accent: Boolean = false)

/** `.tl28`: finish school, the free year, the clock, five seasons, the close, with "You are here" in place. */
@Composable
private fun TimelineCard(state: OnboardingState, your: Boolean) {
    val colors = GWPPATheme.colors
    val lane = state.laneInfo ?: return
    val clock = lane.clock
    val today = NcaaClockCalculator.today()
    val months = stringArrayResource(Res.array.months)
    val free = state.freeYear
    val rows = buildList {
        val gm = state.gradMonth
        val gy = state.gradYear
        if (gm != null && gy != null && !state.inCollege) {
            val at = LocalDate(gy, gm, 20)
            add(TimelineRow(stringResource(if (at < today) Res.string.ck_tl_finished else Res.string.ck_tl_finish), "${months[gm - 1]} $gy", at))
        }
        if (free != null && free.free >= 1 && !lane.running) {
            add(
                TimelineRow(
                    if (free.free == 1) stringResource(Res.string.ck_tl_free) else stringResource(Res.string.ck_tl_free_n, free.free),
                    stringResource(Res.string.ck_tl_free_line, "${free.college}-${clock.year.toString().takeLast(2)}"),
                    LocalDate(free.college, 8, 1),
                    accent = true
                )
            )
        }
        add(
            TimelineRow(
                stringResource(if (lane.running) Res.string.ck_tl_started else Res.string.ck_tl_starts),
                if (lane.openedByEnrollment) {
                    stringResource(Res.string.ck_tl_by_enroll, "${months[clock.start.month.ordinal]} ${clock.start.year}")
                } else stringResource(Res.string.ck_tl_by_age, clock.year),
                clock.start
            )
        )
        repeat(5) { i -> add(TimelineRow(stringResource(Res.string.clock_season, i + 1), seasonLabel(clock.year + i), LocalDate(clock.year + i, 8, 15))) }
        add(TimelineRow(stringResource(Res.string.ck_tl_closes), stringResource(Res.string.ck_tl_closes_line, clock.closes), LocalDate(clock.closes, 6, 1)))
    }
    val here = rows.indexOfFirst { it.at > today }.let { if (it < 0) rows.size else it }
    NightCard(kicker = stringResource(if (your) Res.string.ck_timeline_your else Res.string.ck_timeline_their)) {
        Text(stringResource(Res.string.learn_2_h), style = GWPPATheme.typography.archivoExpandedBold21, color = colors.colorWhite)
        Column(Modifier.padding(top = 4.dp)) {
            rows.forEachIndexed { i, row ->
                if (i == here) HereRow(today, months)
                TimelineLine(row.title, row.line, if (row.accent) colors.ballYellow else colors.colorWhite)
            }
            if (here == rows.size) HereRow(today, months)
        }
    }
}

@Composable
private fun TimelineLine(title: String, line: String, dot: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(vertical = 6.dp)) {
        Box(Modifier.padding(top = 5.dp).size(8.dp).clip(CircleShape).background(dot))
        Column {
            Text(title, style = GWPPATheme.typography.archivoBold16, color = Color.White)
            Text(line, style = GWPPATheme.typography.archivoRegular13, color = Color.White.copy(alpha = 0.62f))
        }
    }
}

@Composable
private fun HereRow(today: LocalDate, months: List<String>) {
    val colors = GWPPATheme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(colors.liveRed.copy(alpha = 0.16f))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(colors.liveRed))
        Text(stringResource(Res.string.ck_tl_here), style = GWPPATheme.typography.archivoBold13, color = Color.White)
        Text("${months[today.month.ordinal]} ${today.year}", style = GWPPATheme.typography.archivoRegular13, color = Color.White.copy(alpha = 0.62f))
    }
}

/** `.cmp28`: straight to college against a year abroad first, and what that costs. */
@Composable
private fun ChoiceCard(state: OnboardingState, your: Boolean) {
    val colors = GWPPATheme.colors
    val lane = state.laneInfo ?: return
    if (lane.running) {
        NightCard(kicker = stringResource(Res.string.ck_between)) {
            Text(stringResource(if (your) Res.string.ck_between_line else Res.string.ck_between_line_parent), style = GWPPATheme.typography.archivoRegular15, color = Color.White.copy(alpha = 0.8f))
        }
        return
    }
    val free = state.freeYear ?: return
    val months = stringArrayResource(Res.array.months)
    NightCard(kicker = stringResource(if (your) Res.string.ck_choice_your else Res.string.ck_choice_their)) {
        state.summerGap?.let { gap ->
            Text(
                if (gap.weeks > 0) {
                    stringResource(Res.string.ck_gap, "${months[gap.from.month.ordinal]} ${gap.from.day}", "${months[gap.to.month.ordinal]} ${gap.to.day}", gap.weeks)
                } else stringResource(Res.string.avail_gap_none),
                style = GWPPATheme.typography.archivoRegular15,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
        SeasonsRow(stringResource(Res.string.ck_straight, free.college), free.straight)
        SeasonsRow(stringResource(Res.string.ck_abroad, free.college + 1), free.abroad)
        Text(
            when {
                free.free >= 1 || free.abroad == free.straight -> stringResource(Res.string.ck_verdict_free)
                free.abroad == 0 -> stringResource(Res.string.ck_verdict_closing)
                else -> stringResource(if (your) Res.string.ck_verdict_uses else Res.string.ck_verdict_uses_parent, free.abroad)
            },
            style = GWPPATheme.typography.archivoBold16,
            color = colors.ballYellow
        )
    }
}

/** A label, five season pips (kept ones lit) and "N of 5 seasons". */
@Composable
private fun SeasonsRow(label: String, kept: Int) {
    val colors = GWPPATheme.colors
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = GWPPATheme.typography.archivoRegular13, color = Color.White.copy(alpha = 0.7f))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(5) { i ->
                Box(Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(4.dp)).background(if (i < kept) colors.ballYellow else Color.White.copy(alpha = 0.14f)))
            }
            Text(
                stringResource(Res.string.ck_of_five, kept),
                style = GWPPATheme.typography.archivoBold13,
                color = Color.White,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF04101E)
@Composable
private fun ClockScreenContentPreview() {
    GWPPATheme {
        val window = NcaaClockCalculator.window(LocalDate(2008, 10, 3))
        ClockScreenContent(
            state = OnboardingState(window = window, countdown = NcaaClockCalculator.countdown(window.clock)),
            hapticsOn = true,
            onHapticsClick = {},
            onDigitLocked = {},
            onSettled = {},
            onSecond = {},
            onBackClick = {},
            onContinueClick = {}
        )
    }
}
