package com.global_water_polo.play_abroad.presentation.onboarding.travel

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.BarStart
import com.global_water_polo.play_abroad.presentation.onboarding.FlowStep
import com.global_water_polo.play_abroad.presentation.onboarding.progress
import com.global_water_polo.play_abroad.core.components.ChoiceChip
import com.global_water_polo.play_abroad.core.components.ClockChip
import com.global_water_polo.play_abroad.core.components.DotSlider
import com.global_water_polo.play_abroad.core.components.Headline
import com.global_water_polo.play_abroad.core.components.HelpText
import com.global_water_polo.play_abroad.core.components.OnboardingLayout
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.core.utils.NcaaClockCalculator
import com.global_water_polo.play_abroad.domain.model.Lane
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingState
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** The next 12 months, starting next month: (offset, "Nov 2026"). */
@Composable
fun travelMonths(): List<Pair<Int, String>> {
    val short = stringArrayResource(Res.array.months_short)
    return remember(short) {
        val today = NcaaClockCalculator.today()
        val first = LocalDate(today.year, today.month.number, 1).plus(1, DateTimeUnit.MONTH)
        (0 until 12).map { i ->
            val d = first.plus(i, DateTimeUnit.MONTH)
            i to "${short[d.month.number - 1]} ${d.year}"
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TravelScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    onStartClick: (Int) -> Unit,
    onLengthChanged: (Int) -> Unit,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    val colors = GWPPATheme.colors
    val typography = GWPPATheme.typography
    val lane = state.lane
    val length = if (state.travelMonths > 0) state.travelMonths else 3
    OnboardingLayout(
        modifier = modifier,
        barStart = { BarStart(onBackClick = onBackClick, progress = state.progress(FlowStep.TRAVEL)) },
        barEnd = { if (lane == Lane.ATHLETE || lane == Lane.PARENT) ClockChip(countdown = state.countdown, clockYear = state.window?.clock?.year) },
        bottom = { PrimaryButton(text = stringResource(Res.string.common_continue), onClick = onContinueClick, enabled = state.canTravelContinue) }
    ) {
        Headline(
            text = stringResource(
                when (lane) {
                    Lane.ATHLETE -> Res.string.travel_title_athlete
                    Lane.PARENT -> Res.string.travel_title_parent
                    Lane.COACH, Lane.FAN -> Res.string.travel_title_coach
                    Lane.CLUB -> Res.string.travel_title_club
                }
            )
        )
        HelpText(
            text = stringResource(
                when (lane) {
                    Lane.ATHLETE -> Res.string.travel_help_athlete
                    Lane.PARENT -> Res.string.travel_help_parent
                    Lane.COACH, Lane.CLUB, Lane.FAN -> Res.string.travel_help_team
                }
            )
        )
        Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 24.dp)) {
            Text(text = stringResource(Res.string.travel_earliest), style = typography.archivoSemiBold13, color = colors.slateGrey)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                travelMonths().forEach { (offset, label) ->
                    ChoiceChip(text = label, selected = state.travelStartOffset == offset, onClick = { onStartClick(offset) })
                }
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 10.dp)) {
            Text(text = stringResource(Res.string.travel_how_long), style = typography.archivoSemiBold13, color = colors.slateGrey)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ChoiceChip(text = pluralStringResource(Res.plurals.travel_months, 1, 1), selected = state.travelMonths == 1, onClick = { onLengthChanged(1) })
                ChoiceChip(text = pluralStringResource(Res.plurals.travel_months, 3, 3), selected = state.travelMonths == 3, onClick = { onLengthChanged(3) })
                ChoiceChip(text = stringResource(Res.string.travel_full_season), selected = state.travelMonths == 10, onClick = { onLengthChanged(10) })
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .background(colors.colorWhite, RoundedCornerShape(14.dp))
                    .border(1.dp, colors.lineGrey, RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(text = stringResource(Res.string.travel_custom), style = typography.archivoSemiBold13, color = colors.slateGrey)
                DotSlider(
                    value = length,
                    range = 1..12,
                    onValueChange = onLengthChanged,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = pluralStringResource(Res.plurals.travel_months, length, length),
                    style = typography.archivoExpandedBold16,
                    color = colors.inkBlack,
                    textAlign = TextAlign.End
                )
            }
        }
        SmallText(text = stringResource(Res.string.travel_note), modifier = Modifier.padding(top = 12.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun TravelScreenContentPreview() {
    GWPPATheme {
        TravelScreenContent(state = OnboardingState(travelStartOffset = 1, travelMonths = 3), onStartClick = {}, onLengthChanged = {}, onBackClick = {}, onContinueClick = {})
    }
}
