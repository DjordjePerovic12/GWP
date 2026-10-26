package com.global_water_polo.play_abroad.presentation.onboarding.match

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.number
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.BarStart
import com.global_water_polo.play_abroad.core.components.ButtonStyle
import com.global_water_polo.play_abroad.core.components.ClockChip
import com.global_water_polo.play_abroad.core.components.Headline
import com.global_water_polo.play_abroad.core.components.OnboardingLayout
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.core.components.TextLink
import com.global_water_polo.play_abroad.core.utils.PathwayCalculator
import com.global_water_polo.play_abroad.domain.model.Lane
import com.global_water_polo.play_abroad.domain.model.Program
import com.global_water_polo.play_abroad.presentation.onboarding.FlowStep
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingState
import com.global_water_polo.play_abroad.presentation.onboarding.match.components.ProgramCard
import com.global_water_polo.play_abroad.presentation.onboarding.match.components.duration
import com.global_water_polo.play_abroad.presentation.onboarding.match.components.text
import com.global_water_polo.play_abroad.presentation.onboarding.match.components.title
import com.global_water_polo.play_abroad.presentation.onboarding.progress
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** Your pathway: the program GWP recommends from the answers, why it fits, one alternative and Apply. */
@Composable
fun MatchScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    onProgramPick: (String) -> Unit,
    onApplyClick: () -> Unit,
    onLaterClick: () -> Unit,
    onSeeAllClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val colors = GWPPATheme.colors
    val parent = state.lane == Lane.PARENT
    fun t(athlete: StringResource, forParent: StringResource) = if (parent) forParent else athlete
    val program = state.pathwayProgram
    val recommended = PathwayCalculator.recommend(state.freeYear, state.goal, state.now, state.travelStartOffset)
    val alternative = when {
        program != Program.EXPLORE -> Program.EXPLORE
        recommended != Program.EXPLORE -> recommended
        else -> Program.SUMMER
    }
    val locked = program.needsElite && !state.isElite
    val monthsShort = stringArrayResource(Res.array.months_short)
    val startLabel = state.travelStartOffset?.let { offset ->
        val d = PathwayCalculator.monthAt(offset)
        "${monthsShort[d.month.number - 1]} ${d.year}"
    }
    val duration = stringResource(program.duration)
    val whenLabel = when {
        state.travelStartOffset == null || startLabel == null -> duration
        program.seasonStartMonth != null -> "$duration ${PathwayCalculator.seasonYear(program, state.travelStartOffset)}"
        else -> stringResource(Res.string.match_when_starting, duration, startLabel)
    }
    val goalLabel = when (state.goal) {
        "college" -> stringResource(Res.string.goal_college)
        "pro" -> stringResource(Res.string.goal_pro)
        "nt" -> stringResource(Res.string.goal_nt)
        "oly" -> stringResource(Res.string.goal_oly)
        else -> stringResource(Res.string.match_not_set)
    }
    val f = state.freeYear

    OnboardingLayout(
        modifier = modifier,
        barStart = { BarStart(onBackClick = onBackClick, progress = state.progress(FlowStep.MATCH)) },
        barEnd = { if (state.window != null) ClockChip(countdown = state.countdown, clockYear = state.window.clock.year) },
        bottom = {
            PrimaryButton(
                text = stringResource(if (locked) Res.string.match_unlock_apply else Res.string.match_apply),
                onClick = onApplyClick,
                style = ButtonStyle.BALL
            )
            SmallText(
                text = stringResource(Res.string.match_note),
                center = true,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp)
            )
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                TextLink(text = stringResource(Res.string.match_see_all), onClick = onSeeAllClick)
                TextLink(text = stringResource(Res.string.match_later), onClick = onLaterClick)
            }
        }
    ) {
        Headline(text = stringResource(t(Res.string.match_title_athlete, Res.string.match_title_parent)))
        ProgramCard(
            program = program,
            name = stringResource(program.title),
            tag = stringResource(
                if (state.program.isNotEmpty()) t(Res.string.match_your_pick, Res.string.match_your_pick_parent) else Res.string.match_recommends
            ),
            lockLabel = if (locked) stringResource(Res.string.match_elite_lock) else null,
            whenLabel = whenLabel,
            body = stringResource(program.text),
            modifier = Modifier.padding(top = 18.dp)
        )

        Text(
            text = stringResource(t(Res.string.match_why_athlete, Res.string.match_why_parent)),
            style = GWPPATheme.typography.archivoExpandedBold17,
            color = colors.inkBlack,
            modifier = Modifier.padding(top = 22.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 10.dp)) {
            whyLines(state, startLabel).take(4).forEach { WhyRow(it) }
        }

        Column(
            modifier = Modifier
                .padding(top = 18.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(colors.colorWhite)
                .border(1.dp, colors.lineGrey, RoundedCornerShape(18.dp))
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            PanelRow(stringResource(Res.string.match_clock_starts), f?.let { stringResource(Res.string.match_fall, it.clockYear) } ?: "-")
            PanelRow(stringResource(Res.string.match_earliest), startLabel ?: stringResource(Res.string.match_not_set))
            PanelRow(stringResource(Res.string.match_goal), goalLabel)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(1.5.dp, colors.lineGrey, RoundedCornerShape(18.dp))
                .clickable { onProgramPick(alternative.key) }
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Column(Modifier.weight(1f)) {
                Text(stringResource(Res.string.match_also), style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey)
                Text(stringResource(alternative.title), style = GWPPATheme.typography.archivoBold16, color = colors.inkBlack)
            }
            Text(stringResource(alternative.duration), style = GWPPATheme.typography.archivoSemiBold13, color = colors.chlorineBlue)
        }
    }
}

@Composable
private fun whyLines(state: OnboardingState, startLabel: String?): List<String> {
    val parent = state.lane == Lane.PARENT
    fun t(athlete: StringResource, forParent: StringResource) = if (parent) forParent else athlete
    val f = state.freeYear
    val lines = mutableListOf<String>()
    if (f != null) {
        lines += when {
            f.free >= 1 -> stringResource(
                t(Res.string.match_why_free_athlete, Res.string.match_why_free_parent),
                if (f.free == 1) stringResource(Res.string.match_a_free_year) else stringResource(Res.string.match_n_free_years, f.free),
                f.clockYear
            )
            f.running -> stringResource(t(Res.string.match_why_running_athlete, Res.string.match_why_running_parent))
            else -> stringResource(t(Res.string.match_why_clock_athlete, Res.string.match_why_clock_parent), f.clockYear)
        }
    }
    if (startLabel != null) lines += stringResource(t(Res.string.match_why_start_athlete, Res.string.match_why_start_parent), startLabel)
    val goal = when (state.goal) {
        "college" -> Res.string.match_goal_college
        "pro" -> Res.string.match_goal_pro
        "nt" -> Res.string.match_goal_nt
        "oly" -> Res.string.match_goal_oly
        else -> null
    }
    if (goal != null) lines += stringResource(t(Res.string.match_why_goal_athlete, Res.string.match_why_goal_parent), stringResource(goal))
    if ("cost" in state.stops) lines += stringResource(Res.string.match_why_cost)
    if ("where" in state.stops) lines += stringResource(t(Res.string.match_why_where_athlete, Res.string.match_why_where_parent))
    if ("safety" in state.stops) lines += stringResource(Res.string.match_why_safety)
    return lines
}

@Composable
private fun WhyRow(text: String) {
    val colors = GWPPATheme.colors
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Canvas(Modifier.padding(top = 1.dp).size(20.dp)) {
            drawCircle(colors.inkBlack)
            val w = size.width
            val check = Path().apply {
                moveTo(w * 0.3f, w * 0.5f)
                lineTo(w * 0.43f, w * 0.63f)
                lineTo(w * 0.7f, w * 0.37f)
            }
            drawPath(check, Color.White, style = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
        Text(text = text, style = GWPPATheme.typography.archivoRegular15, color = colors.inkBlack)
    }
}

@Composable
private fun PanelRow(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
        Text(label, style = GWPPATheme.typography.archivoRegular15, color = GWPPATheme.colors.slateGrey, modifier = Modifier.weight(1f))
        Text(value, style = GWPPATheme.typography.archivoExpandedBold16, color = GWPPATheme.colors.inkBlack)
    }
}

@Preview(showBackground = true)
@Composable
private fun MatchScreenContentPreview() {
    GWPPATheme {
        MatchScreenContent(
            state = OnboardingState(goal = "pro", birthMonth = 10, birthDay = 3, birthYear = 2008, gradMonth = 6, gradYear = 2027),
            onProgramPick = {},
            onApplyClick = {},
            onLaterClick = {},
            onSeeAllClick = {},
            onBackClick = {}
        )
    }
}
