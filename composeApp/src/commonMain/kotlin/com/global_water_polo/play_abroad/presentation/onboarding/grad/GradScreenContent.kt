package com.global_water_polo.play_abroad.presentation.onboarding.grad

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.BarStart
import com.global_water_polo.play_abroad.core.components.ClockChip
import com.global_water_polo.play_abroad.core.components.Headline
import com.global_water_polo.play_abroad.core.components.HelpText
import com.global_water_polo.play_abroad.core.components.MonthYearWheels
import com.global_water_polo.play_abroad.core.components.OnboardingLayout
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.utils.NcaaClockCalculator
import com.global_water_polo.play_abroad.domain.model.Lane
import com.global_water_polo.play_abroad.presentation.onboarding.FlowStep
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingState
import com.global_water_polo.play_abroad.presentation.onboarding.progress
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** When school (or the college semester) ends: with the birthday, this decides the free year. */
@Composable
fun GradScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    onChange: (month: Int?, year: Int?) -> Unit,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    val parent = state.lane == Lane.PARENT
    // Last year through 2032, like the prototype's GRAD_YEARS.
    val years = remember { (NcaaClockCalculator.today().year - 1..2032).toList() }
    OnboardingLayout(
        modifier = modifier,
        barStart = { BarStart(onBackClick = onBackClick, progress = state.progress(FlowStep.GRAD)) },
        barEnd = { if (state.window != null) ClockChip(countdown = state.countdown, clockYear = state.window.clock.year) },
        bottom = { PrimaryButton(text = stringResource(Res.string.common_continue), onClick = onContinueClick, enabled = state.isGradSet) }
    ) {
        Headline(
            text = stringResource(
                when {
                    state.inCollege -> Res.string.grad_title_college
                    parent -> Res.string.grad_title_parent
                    else -> Res.string.grad_title_athlete
                }
            )
        )
        HelpText(
            text = stringResource(
                when {
                    state.inCollege -> Res.string.grad_help_college
                    parent -> Res.string.grad_help_parent
                    else -> Res.string.grad_help_athlete
                }
            )
        )
        MonthYearWheels(
            months = stringArrayResource(Res.array.months),
            years = years,
            month = state.gradMonth,
            year = state.gradYear,
            monthLabel = stringResource(Res.string.grad_month),
            yearLabel = stringResource(Res.string.grad_year),
            onChange = onChange,
            tapToSelect = true,
            modifier = Modifier.padding(top = 28.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GradScreenContentPreview() {
    GWPPATheme {
        GradScreenContent(state = OnboardingState(gradMonth = 6, gradYear = 2027), onChange = { _, _ -> }, onBackClick = {}, onContinueClick = {})
    }
}
