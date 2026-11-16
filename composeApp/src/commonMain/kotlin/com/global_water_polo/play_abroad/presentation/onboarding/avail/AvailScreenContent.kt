package com.global_water_polo.play_abroad.presentation.onboarding.avail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.BarStart
import com.global_water_polo.play_abroad.core.components.Headline
import com.global_water_polo.play_abroad.core.components.HelpText
import com.global_water_polo.play_abroad.core.components.OnboardingLayout
import com.global_water_polo.play_abroad.core.components.PickCard
import com.global_water_polo.play_abroad.core.components.PickMarker
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.presentation.onboarding.FlowStep
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingState
import com.global_water_polo.play_abroad.presentation.onboarding.progress
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme
import kotlin.math.min

/**
 * When are you free (v29's a-avail): after school, after Junior Olympics or straight into preseason. That
 * gives the real summer gap, and under it the first step side by side: a summer abroad, ten weeks in a
 * season, or a year abroad.
 */
@Composable
fun AvailScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    onAvailabilityClick: (String) -> Unit,
    onFirstStepClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    val parent = state.isParent
    OnboardingLayout(
        modifier = modifier,
        barStart = { BarStart(onBackClick = onBackClick, progress = state.progress(FlowStep.AVAIL)) },
        bottom = { PrimaryButton(text = stringResource(Res.string.common_continue), onClick = onContinueClick, enabled = state.availability.isNotEmpty()) }
    ) {
        Headline(
            text = stringResource(
                when {
                    state.inCollege && parent -> Res.string.avail_title_college_parent
                    state.inCollege -> Res.string.avail_title_college
                    parent -> Res.string.avail_title_parent
                    else -> Res.string.avail_title
                }
            )
        )
        HelpText(text = stringResource(Res.string.avail_help))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 20.dp)) {
            listOf(
                Triple("grad", if (parent) Res.string.avail_grad_parent else Res.string.avail_grad, null),
                Triple("jo", Res.string.avail_jo, Res.string.avail_jo_sub),
                Triple("preseason", Res.string.avail_preseason, Res.string.avail_preseason_sub)
            ).forEach { (key, title, sub) ->
                PickCard(
                    title = stringResource(title),
                    subtitle = sub?.let { stringResource(it) },
                    marker = PickMarker.CHECK,
                    selected = state.availability == key,
                    compact = true,
                    onClick = { onAvailabilityClick(key) }
                )
            }
        }
        state.summerGap?.let { gap ->
            GapCard(parent = parent, from = gap.from, to = gap.to, weeks = gap.weeks)
            val free = state.freeYear?.let { !it.running && it.free >= 1 } == true
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 12.dp)) {
                StepCard(
                    tag = Res.string.avail_small,
                    title = stringResource(Res.string.avail_summer),
                    line = if (gap.weeks >= 3) {
                        stringResource(if (parent) Res.string.avail_summer_fits_parent else Res.string.avail_summer_fits, min(8, gap.weeks))
                    } else stringResource(Res.string.avail_summer_next),
                    selected = state.firstStep == "summer",
                    onClick = { onFirstStepClick("summer") }
                )
                // The three ten-week seasons sit side by side.
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.height(IntrinsicSize.Min)) {
                    listOf(
                        Triple("fall", Res.string.avail_fall, Res.string.avail_fall_when),
                        Triple("winter", Res.string.avail_winter, Res.string.avail_winter_when),
                        Triple("spring", Res.string.avail_spring, Res.string.avail_spring_when)
                    ).forEach { (key, title, line) ->
                        StepCard(
                            tag = Res.string.avail_ten,
                            title = stringResource(title),
                            line = stringResource(line),
                            selected = state.firstStep == key,
                            onClick = { onFirstStepClick(key) },
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                    }
                }
                StepCard(
                    tag = Res.string.avail_big,
                    title = stringResource(Res.string.avail_year),
                    line = stringResource(
                        when {
                            free && parent -> Res.string.avail_year_free_parent
                            free -> Res.string.avail_year_free
                            else -> Res.string.avail_year_window
                        }
                    ),
                    selected = state.firstStep == "year",
                    onClick = { onFirstStepClick("year") }
                )
            }
            SmallText(
                text = stringResource(if (parent) Res.string.avail_pick_note_parent else Res.string.avail_pick_note),
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}

/** `.gap28`: their summer gap, dates and weeks. */
@Composable
private fun GapCard(parent: Boolean, from: LocalDate, to: LocalDate, weeks: Int) {
    val colors = GWPPATheme.colors
    val months = stringArrayResource(Res.array.months)
    fun short(d: LocalDate) = "${months[d.month.ordinal]} ${d.day}"
    Column(
        verticalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier
            .padding(top = 18.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colors.inkBlack)
            .padding(16.dp)
    ) {
        Text(
            stringResource(if (parent) Res.string.avail_gap_their else Res.string.avail_gap_your).uppercase(),
            style = GWPPATheme.typography.archivoBold11.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 0.14.em),
            color = colors.eliteGold
        )
        Text(
            if (weeks > 0) stringResource(Res.string.avail_gap_dates, short(from), short(to)) else stringResource(Res.string.avail_gap_none),
            style = GWPPATheme.typography.archivoExpandedBold21,
            color = colors.colorWhite
        )
        Text(
            if (weeks > 0) stringResource(Res.string.avail_gap_weeks, weeks) else stringResource(Res.string.avail_gap_none_sub),
            style = GWPPATheme.typography.archivoRegular13,
            color = colors.colorWhite.copy(alpha = 0.72f)
        )
    }
}

/** `.opt28-c`: one first step, its size on top in gold capitals. */
@Composable
private fun StepCard(tag: StringResource, title: String, line: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = GWPPATheme.colors
    val shape = RoundedCornerShape(16.dp)
    Column(
        verticalArrangement = Arrangement.spacedBy(3.dp),
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (selected) colors.chlorineBlueSoft else colors.colorWhite)
            .border(if (selected) 2.dp else 1.dp, if (selected) colors.inkBlack else colors.lineGrey, shape)
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Text(
            stringResource(tag).uppercase(),
            style = GWPPATheme.typography.archivoBold11.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 0.1.em),
            color = colors.antiqueGold
        )
        Text(title, style = GWPPATheme.typography.archivoBold16, color = colors.inkBlack)
        Text(line, style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey)
    }
}

@Preview(showBackground = true)
@Composable
private fun AvailScreenContentPreview() {
    GWPPATheme {
        AvailScreenContent(
            state = OnboardingState(gradMonth = 6, gradYear = 2027, availability = "jo"),
            onAvailabilityClick = {},
            onFirstStepClick = {},
            onBackClick = {},
            onContinueClick = {}
        )
    }
}
