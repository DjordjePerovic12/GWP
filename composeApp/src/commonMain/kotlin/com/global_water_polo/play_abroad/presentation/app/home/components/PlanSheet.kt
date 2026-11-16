package com.global_water_polo.play_abroad.presentation.app.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.AppSheet
import com.global_water_polo.play_abroad.core.components.ButtonStyle
import com.global_water_polo.play_abroad.core.components.ChoiceChip
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.SheetHeading
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.core.utils.FreeYear
import com.global_water_polo.play_abroad.core.utils.LaneInfo
import com.global_water_polo.play_abroad.core.utils.LaneKind
import com.global_water_polo.play_abroad.core.utils.NcaaClockCalculator
import com.global_water_polo.play_abroad.core.utils.SummerGap
import com.global_water_polo.play_abroad.core.utils.freeYear
import com.global_water_polo.play_abroad.core.utils.laneInfo
import com.global_water_polo.play_abroad.core.utils.summerGap
import com.global_water_polo.play_abroad.domain.model.Profile
import com.global_water_polo.play_abroad.presentation.app.home.PlanDraft
import com.global_water_polo.play_abroad.presentation.app.profile.components.timingLabel
import com.global_water_polo.play_abroad.presentation.onboarding.match.components.title
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/**
 * "Your plan" (v29's pathBuilder): where they stand, the steps, then "Adjust my plan" to change the answers
 * and "Rebuild my plan".
 */
@Composable
fun PlanSheet(
    profile: Profile,
    adjusting: Boolean,
    draft: PlanDraft,
    onAdjustClick: () -> Unit,
    onDraftNow: (String) -> Unit,
    onDraftGrad: (Int) -> Unit,
    onDraftAvailability: (String) -> Unit,
    onDraftFirstStep: (String) -> Unit,
    onRebuildClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = GWPPATheme.colors
    val first = profile.firstName.trim()
    AppSheet(onDismiss = onDismiss) {
        Text(
            stringResource(Res.string.ready_k_your).uppercase(),
            style = GWPPATheme.typography.archivoBold11.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 0.16.em),
            color = colors.antiqueGold,
            modifier = Modifier.padding(top = 6.dp)
        )
        Text(
            if (first.isEmpty()) stringResource(Res.string.plan_title_plain) else stringResource(Res.string.plan_title, first),
            style = GWPPATheme.typography.archivoExpandedExtraBold30,
            color = colors.inkBlack
        )
        PlanTable(
            parent = false,
            now = profile.now,
            lane = profile.laneInfo(),
            free = profile.freeYear,
            gap = profile.summerGap,
            firstStep = profile.firstStep
        )
        if (!adjusting) {
            val steps = profile.pathway?.steps.orEmpty()
            if (steps.isNotEmpty()) {
                SheetHeading(stringResource(Res.string.plan_steps))
                steps.forEach { step ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(colors.colorWhite)
                            .border(1.dp, colors.lineGrey, RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Text(timingLabel(step).uppercase(), style = GWPPATheme.typography.archivoBold11, color = colors.chlorineBlue)
                        Text(stepName(step.pick, step.program.title), style = GWPPATheme.typography.archivoBold16, color = colors.inkBlack)
                    }
                }
            }
            PrimaryButton(text = stringResource(Res.string.plan_adjust), onClick = onAdjustClick, style = ButtonStyle.GHOST, modifier = Modifier.padding(top = 8.dp))
        } else {
            ChipGroup(
                label = stringResource(Res.string.ready_where_your),
                options = listOf(
                    "committed" to Res.string.now_committed,
                    "looking" to Res.string.now_looking,
                    "college" to Res.string.now_college,
                    "pro" to Res.string.now_pro,
                    "unsure" to Res.string.now_unsure
                ),
                selected = draft.now,
                onClick = onDraftNow
            )
            val months = stringArrayResource(Res.array.months)
            val base = draft.gradYear ?: (NcaaClockCalculator.today().year + 1)
            ChipGroup(
                label = stringResource(Res.string.plan_finish),
                options = ((base - 1)..(base + 2)).map { it.toString() to null },
                labels = { "${months[5]} $it" },
                selected = draft.gradYear?.toString().orEmpty(),
                onClick = { onDraftGrad(it.toInt()) }
            )
            ChipGroup(
                label = stringResource(Res.string.plan_when_free),
                options = listOf("grad" to Res.string.avail_grad, "jo" to Res.string.avail_jo, "preseason" to Res.string.avail_preseason),
                selected = draft.availability,
                onClick = onDraftAvailability
            )
            ChipGroup(
                label = stringResource(Res.string.ready_first),
                options = listOf(
                    "summer" to Res.string.avail_summer,
                    "fall" to Res.string.pick28_fall,
                    "winter" to Res.string.pick28_winter,
                    "spring" to Res.string.pick28_spring,
                    "year" to Res.string.avail_year
                ),
                selected = draft.firstStep,
                onClick = onDraftFirstStep
            )
            PrimaryButton(text = stringResource(Res.string.plan_rebuild), onClick = onRebuildClick, style = ButtonStyle.BALL, modifier = Modifier.padding(top = 8.dp))
        }
        SmallText(text = stringResource(Res.string.plan_fine), modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
private fun stepName(pick: String, title: StringResource): String = when (pick) {
    "fall" -> stringResource(Res.string.pick_fall_name)
    "winter" -> stringResource(Res.string.pick_winter_name)
    "spring" -> stringResource(Res.string.pick_spring_name)
    else -> stringResource(title)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChipGroup(
    label: String,
    options: List<Pair<String, StringResource?>>,
    selected: String,
    onClick: (String) -> Unit,
    labels: @Composable (String) -> String = { it }
) {
    Text(label, style = GWPPATheme.typography.archivoBold13, color = GWPPATheme.colors.slateGrey, modifier = Modifier.padding(top = 10.dp))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { (key, res) ->
            ChoiceChip(text = res?.let { stringResource(it) } ?: labels(key), selected = key == selected, onClick = { onClick(key) })
        }
    }
}

/**
 * v29's planRows: only the rows that apply to this athlete's lane. Shared by "Your plan is ready" and the
 * Home plan sheet.
 */
@Composable
fun PlanTable(parent: Boolean, now: Set<String>, lane: LaneInfo?, free: FreeYear?, gap: SummerGap?, firstStep: String) {
    val colors = GWPPATheme.colors
    Column(
        Modifier
            .padding(top = 14.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colors.colorWhite)
            .border(1.dp, colors.lineGrey, RoundedCornerShape(18.dp))
    ) {
        planRows(parent, now, lane, free, gap, firstStep).forEachIndexed { i, (label, value) ->
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
}

@Composable
private fun planRows(parent: Boolean, now: Set<String>, lane: LaneInfo?, free: FreeYear?, gap: SummerGap?, firstStep: String): List<Pair<String, String>> {
    val months = stringArrayResource(Res.array.months)
    val status = when {
        "committed" in now -> stringResource(Res.string.now_committed)
        "looking" in now -> stringResource(Res.string.now_looking)
        "college" in now -> stringResource(Res.string.now_college)
        "pro" in now -> stringResource(Res.string.now_pro)
        "unsure" in now -> stringResource(Res.string.now_unsure)
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
                when (firstStep) {
                    "fall" -> Res.string.ready_step_fall
                    "winter" -> Res.string.ready_step_winter
                    "spring" -> Res.string.ready_step_spring
                    "year" -> Res.string.avail_year
                    "summer" -> Res.string.avail_summer
                    else -> if ("pro" in now) Res.string.ready_step_pro else Res.string.avail_summer
                }
            )
        )
    }
}
