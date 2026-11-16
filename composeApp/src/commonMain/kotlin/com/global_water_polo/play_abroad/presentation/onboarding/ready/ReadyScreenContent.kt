package com.global_water_polo.play_abroad.presentation.onboarding.ready

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.BarStart
import com.global_water_polo.play_abroad.core.components.ButtonStyle
import com.global_water_polo.play_abroad.core.components.OnboardingLayout
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.core.utils.LaneKind
import com.global_water_polo.play_abroad.presentation.onboarding.FlowStep
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingState
import com.global_water_polo.play_abroad.presentation.onboarding.progress
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/**
 * Your plan is ready (v29's a-ready): the reveal after the tour. Where they are, the NCAA window, the free
 * time, the summer and the first step, as a starting point GWP builds on.
 */
@Composable
fun ReadyScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    val colors = GWPPATheme.colors
    val parent = state.isParent
    val name = (if (parent) state.kidName.ifBlank { state.firstName } else state.firstName).trim()
    OnboardingLayout(
        modifier = modifier,
        barStart = { BarStart(onBackClick = onBackClick, progress = state.progress(FlowStep.READY)) },
        bottom = {
            PrimaryButton(
                text = stringResource(if (parent) Res.string.ready_send_parent else Res.string.ready_send),
                onClick = onContinueClick,
                style = ButtonStyle.BALL
            )
        }
    ) {
        // `.rd28-hero`: the harbour photo, darkening to petrol under the headline.
        Box(Modifier.fillMaxWidth().height(260.dp).clip(RoundedCornerShape(24.dp))) {
            Image(painterResource(Res.drawable.f_harbour), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.matchParentSize())
            Box(
                Modifier.matchParentSize().background(
                    Brush.verticalGradient(
                        0f to colors.inkBlack.copy(alpha = 0.05f),
                        0.45f to colors.inkBlack.copy(alpha = 0.5f),
                        1f to colors.inkBlack.copy(alpha = 0.94f)
                    )
                )
            )
            Column(Modifier.align(Alignment.BottomStart).padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    stringResource(if (parent) Res.string.ready_k_their else Res.string.ready_k_your).uppercase(),
                    style = GWPPATheme.typography.archivoBold11.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 0.16.em),
                    color = colors.eliteGold
                )
                Text(
                    when {
                        parent && name.isNotEmpty() -> stringResource(Res.string.ready_h_their, name)
                        parent -> stringResource(Res.string.ready_h_their_plain)
                        name.isNotEmpty() -> stringResource(Res.string.ready_h_your, name)
                        else -> stringResource(Res.string.ready_h_plain)
                    },
                    style = GWPPATheme.typography.archivoExpandedExtraBold30,
                    color = colors.colorWhite
                )
            }
        }
        Column(
            Modifier
                .padding(top = 14.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(colors.colorWhite)
                .border(1.dp, colors.lineGrey, RoundedCornerShape(18.dp))
        ) {
            planRows(state).forEachIndexed { i, (label, value) ->
                if (i > 0) HorizontalDivider(color = colors.lineGrey)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 13.dp)
                ) {
                    Text(label, style = GWPPATheme.typography.archivoRegular15, color = colors.slateGrey, modifier = Modifier.weight(1f))
                    Text(value, style = GWPPATheme.typography.archivoBold16, color = colors.inkBlack, textAlign = TextAlign.End, modifier = Modifier.weight(1.3f))
                }
            }
        }
        SmallText(text = stringResource(if (parent) Res.string.ready_fine_parent else Res.string.ready_fine), modifier = Modifier.padding(top = 12.dp))
    }
}

/** v29's planRows: only the rows that apply to this athlete's lane. */
@Composable
private fun planRows(state: OnboardingState): List<Pair<String, String>> {
    val parent = state.isParent
    val lane = state.laneInfo
    val free = state.freeYear
    val gap = state.summerGap
    val months = stringArrayResource(Res.array.months)
    val status = when {
        "committed" in state.now -> stringResource(Res.string.now_committed)
        "looking" in state.now -> stringResource(Res.string.now_looking)
        "college" in state.now -> stringResource(Res.string.now_college)
        "pro" in state.now -> stringResource(Res.string.now_pro)
        "unsure" in state.now -> stringResource(Res.string.now_unsure)
        else -> stringResource(Res.string.ready_not_set)
    }
    return buildList {
        add(stringResource(if (parent) Res.string.ready_where_their else Res.string.ready_where_your) to status)
        add(
            stringResource(Res.string.ready_window) to when {
                lane == null -> stringResource(Res.string.ready_window_add)
                lane.lane == LaneKind.PRO -> stringResource(Res.string.ready_window_pro)
                lane.lane == LaneKind.CLOSED -> stringResource(Res.string.ready_window_closed)
                lane.running -> stringResource(Res.string.ready_window_running, lane.yearN, lane.clock.closes)
                else -> stringResource(Res.string.ready_window_opens, lane.clock.year)
            }
        )
        if (free != null && free.free >= 1 && lane != null && lane.lane !in setOf(LaneKind.PRO, LaneKind.CLOSED, LaneKind.RUNNING)) {
            add(
                stringResource(Res.string.ready_free) to if (free.free == 1) {
                    stringResource(Res.string.ready_free_one, "${free.college}-${lane.clock.year.toString().takeLast(2)}")
                } else stringResource(Res.string.ck_tl_free_n, free.free)
            )
        }
        if (gap != null) {
            add(
                stringResource(Res.string.ready_summer) to if (gap.weeks > 0) {
                    stringResource(Res.string.ready_summer_dates, "${months[gap.from.month.ordinal]} ${gap.from.day}", "${months[gap.to.month.ordinal]} ${gap.to.day}", gap.weeks)
                } else stringResource(Res.string.ready_summer_none)
            )
        }
        add(
            stringResource(Res.string.ready_first) to stringResource(
                when (state.firstStep) {
                    "fall" -> Res.string.ready_step_fall
                    "winter" -> Res.string.ready_step_winter
                    "spring" -> Res.string.ready_step_spring
                    "year" -> Res.string.avail_year
                    "summer" -> Res.string.avail_summer
                    else -> if ("pro" in state.now) Res.string.ready_step_pro else Res.string.avail_summer
                }
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ReadyScreenContentPreview() {
    GWPPATheme {
        ReadyScreenContent(
            state = OnboardingState(firstName = "Alex", now = setOf("committed"), birthMonth = 10, birthDay = 3, birthYear = 2008, gradMonth = 6, gradYear = 2027, availability = "jo"),
            onBackClick = {},
            onContinueClick = {}
        )
    }
}
