package com.global_water_polo.play_abroad.presentation.onboarding.tripplan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.number
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.BarStart
import com.global_water_polo.play_abroad.core.components.ButtonStyle
import com.global_water_polo.play_abroad.core.components.Headline
import com.global_water_polo.play_abroad.core.components.OnboardingLayout
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.core.utils.PathwayCalculator
import com.global_water_polo.play_abroad.domain.model.Lane
import com.global_water_polo.play_abroad.presentation.onboarding.FlowStep
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingState
import com.global_water_polo.play_abroad.presentation.onboarding.progress
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** A parent's team trip: the dates and length they picked, sent as an inquiry for a GWP trip lead. */
@Composable
fun TripPlanScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    val colors = GWPPATheme.colors
    val shortMonths = stringArrayResource(Res.array.months_short)
    val start = state.travelStartOffset?.let { PathwayCalculator.monthAt(it) }
        ?.let { "${shortMonths[it.month.number - 1]} ${it.year}" }
        ?: stringResource(Res.string.match_not_set)
    val length = when {
        state.travelMonths <= 0 -> stringResource(Res.string.match_not_set)
        state.travelMonths >= 10 -> stringResource(Res.string.tripplan_full_season)
        else -> pluralStringResource(Res.plurals.travel_months, state.travelMonths, state.travelMonths)
    }
    val shape = RoundedCornerShape(18.dp)
    OnboardingLayout(
        modifier = modifier,
        barStart = { BarStart(onBackClick = onBackClick, progress = state.progress(FlowStep.TRIP_PLAN)) },
        bottom = { PrimaryButton(text = stringResource(Res.string.common_continue), onClick = onContinueClick, style = ButtonStyle.BALL) }
    ) {
        Headline(text = stringResource(Res.string.tripplan_title))
        Column(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth()
                .clip(shape)
                .background(colors.colorWhite)
                .border(1.dp, colors.lineGrey, shape)
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            PanelRow(stringResource(Res.string.match_earliest), start)
            PanelRow(stringResource(Res.string.tripplan_length), length)
        }
        SmallText(text = stringResource(Res.string.tripplan_note), modifier = Modifier.padding(top = 12.dp))
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
private fun TripPlanScreenContentPreview() {
    GWPPATheme {
        TripPlanScreenContent(state = OnboardingState(lane = Lane.PARENT, now = setOf("trip"), travelStartOffset = 3, travelMonths = 1), onBackClick = {}, onContinueClick = {})
    }
}
