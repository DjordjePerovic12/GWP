package com.global_water_polo.play_abroad.presentation.onboarding.free

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.BarStart
import com.global_water_polo.play_abroad.core.components.ButtonStyle
import com.global_water_polo.play_abroad.core.components.ClockChip
import com.global_water_polo.play_abroad.core.components.Headline
import com.global_water_polo.play_abroad.core.components.HelpText
import com.global_water_polo.play_abroad.core.components.OnboardingLayout
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.domain.model.Lane
import com.global_water_polo.play_abroad.presentation.onboarding.FlowStep
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingState
import com.global_water_polo.play_abroad.presentation.onboarding.free.components.FreeTimeline
import com.global_water_polo.play_abroad.presentation.onboarding.free.components.SegmentKind
import com.global_water_polo.play_abroad.presentation.onboarding.free.components.TimelineSegment
import com.global_water_polo.play_abroad.presentation.onboarding.progress
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/**
 * The free year: calculated, not asked. One of three versions shows (a free year, no free year,
 * the clock is already running), with the timeline from graduation to the clock.
 */
@Composable
fun FreeYearScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    val parent = state.lane == Lane.PARENT
    fun t(athlete: org.jetbrains.compose.resources.StringResource, forParent: org.jetbrains.compose.resources.StringResource) = if (parent) forParent else athlete
    val months = stringArrayResource(Res.array.months)
    val f = state.freeYear
    OnboardingLayout(
        modifier = modifier,
        barStart = { BarStart(onBackClick = onBackClick, progress = state.progress(FlowStep.FREE)) },
        barEnd = { if (state.window != null) ClockChip(countdown = state.countdown, clockYear = state.window.clock.year) },
        bottom = { PrimaryButton(text = stringResource(Res.string.common_continue), onClick = onContinueClick, style = ButtonStyle.BALL) }
    ) {
        if (f == null) return@OnboardingLayout
        val day = { d: LocalDate -> "${months[d.month.number - 1]} ${d.day}, ${d.year}" }
        val graduated = "${months[state.gradMonth!! - 1]} ${state.gradYear}"
        when {
            f.running -> {
                Headline(text = stringResource(t(Res.string.free_running_athlete, Res.string.free_running_parent)), large = true)
                Verdict(stringResource(Res.string.free_running_line))
                HelpText(text = stringResource(t(Res.string.free_running_sub_athlete, Res.string.free_running_sub_parent)))
            }
            f.free > 0 -> {
                Headline(
                    text = if (f.free == 1) stringResource(t(Res.string.free_one_athlete, Res.string.free_one_parent))
                    else stringResource(t(Res.string.free_many_athlete, Res.string.free_many_parent), f.free),
                    large = true
                )
                Verdict(stringResource(t(Res.string.free_line_athlete, Res.string.free_line_parent), day(f.turns19), graduated, f.clockYear))
                HelpText(text = stringResource(Res.string.free_sub))
            }
            else -> {
                Headline(text = stringResource(t(Res.string.free_none_athlete, Res.string.free_none_parent)), large = true)
                Verdict(stringResource(t(Res.string.free_none_line_athlete, Res.string.free_none_line_parent), day(f.turns19), f.clockYear))
                HelpText(text = stringResource(t(Res.string.free_none_sub_athlete, Res.string.free_none_sub_parent)))
            }
        }
        if (!f.running) {
            FreeTimeline(
                segments = listOfNotNull(
                    TimelineSegment(SegmentKind.GRADUATE, stringResource(Res.string.free_seg_graduate), graduated, 1f),
                    if (f.free > 0) {
                        TimelineSegment(
                            SegmentKind.FREE,
                            stringResource(if (f.free == 1) Res.string.free_seg_free_one else Res.string.free_seg_free_many),
                            "${f.college}-${f.clockYear.toString().takeLast(2)}",
                            2f
                        )
                    } else null,
                    TimelineSegment(SegmentKind.CLOCK, stringResource(Res.string.free_seg_clock), stringResource(Res.string.match_fall, f.clockYear), 1f)
                ),
                modifier = Modifier.padding(top = 24.dp)
            )
        }
        SmallText(
            text = (if (f.free > 0) stringResource(t(Res.string.free_note_free_athlete, Res.string.free_note_free_parent)) else "") +
                stringResource(Res.string.ncaa_estimate),
            modifier = Modifier.padding(top = 18.dp)
        )
    }
}

/** The prototype's `.verdict`: the one bold line under the headline. */
@Composable
private fun Verdict(text: String) {
    Text(
        text = text,
        style = GWPPATheme.typography.archivoSemiBold18,
        color = GWPPATheme.colors.inkBlack,
        modifier = Modifier.padding(top = 14.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun FreeYearScreenContentPreview() {
    GWPPATheme {
        FreeYearScreenContent(
            state = OnboardingState(birthMonth = 10, birthDay = 3, birthYear = 2008, gradMonth = 6, gradYear = 2027),
            onBackClick = {},
            onContinueClick = {}
        )
    }
}
