package com.global_water_polo.play_abroad.presentation.onboarding.enroll

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.BarStart
import com.global_water_polo.play_abroad.core.components.ChoiceChip
import com.global_water_polo.play_abroad.core.components.Headline
import com.global_water_polo.play_abroad.core.components.HelpText
import com.global_water_polo.play_abroad.core.components.OnboardingLayout
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.utils.NcaaClockCalculator
import com.global_water_polo.play_abroad.presentation.onboarding.FlowStep
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingState
import com.global_water_polo.play_abroad.presentation.onboarding.progress
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/**
 * First full-time college enrollment (v29's a-enroll), only for athletes already in college: it opens the
 * five-year window if it came before turning 19.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EnrollScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    onMonthClick: (Int) -> Unit,
    onYearClick: (Int) -> Unit,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    val parent = state.isParent
    val years = remember { NcaaClockCalculator.today().year.let { y -> (y - 6..y).toList() } }
    OnboardingLayout(
        modifier = modifier,
        barStart = { BarStart(onBackClick = onBackClick, progress = state.progress(FlowStep.ENROLL)) },
        bottom = { PrimaryButton(text = stringResource(Res.string.common_continue), onClick = onContinueClick, enabled = state.isEnrollSet) }
    ) {
        Headline(text = stringResource(if (parent) Res.string.enroll_title_parent else Res.string.enroll_title_athlete))
        HelpText(text = stringResource(if (parent) Res.string.enroll_help_parent else Res.string.enroll_help_athlete))
        Label(stringResource(Res.string.enroll_month), Modifier.padding(top = 24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
            stringArrayResource(Res.array.months).forEachIndexed { i, month ->
                ChoiceChip(text = month.take(3), selected = state.enrollMonth == i + 1, onClick = { onMonthClick(i + 1) })
            }
        }
        Label(stringResource(Res.string.enroll_year), Modifier.padding(top = 18.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            years.forEach { year ->
                ChoiceChip(text = year.toString(), selected = state.enrollYear == year, onClick = { onYearClick(year) })
            }
        }
    }
}

@Composable
private fun Label(text: String, modifier: Modifier = Modifier) {
    Text(text = text, style = GWPPATheme.typography.archivoSemiBold13, color = GWPPATheme.colors.slateGrey, modifier = modifier.padding(bottom = 8.dp))
}

@Preview(showBackground = true)
@Composable
private fun EnrollScreenContentPreview() {
    GWPPATheme {
        EnrollScreenContent(state = OnboardingState(now = setOf("college")), onMonthClick = {}, onYearClick = {}, onBackClick = {}, onContinueClick = {})
    }
}
