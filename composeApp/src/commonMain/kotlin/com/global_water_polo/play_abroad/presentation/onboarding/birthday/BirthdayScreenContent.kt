package com.global_water_polo.play_abroad.presentation.onboarding.birthday

import com.global_water_polo.play_abroad.presentation.onboarding.progress
import com.global_water_polo.play_abroad.presentation.onboarding.FlowStep
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.BarStart
import com.global_water_polo.play_abroad.core.components.Headline
import com.global_water_polo.play_abroad.core.components.HelpText
import com.global_water_polo.play_abroad.core.components.OnboardingLayout
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.utils.NcaaClockCalculator
import com.global_water_polo.play_abroad.core.utils.BirthdayCheck
import com.global_water_polo.play_abroad.core.utils.LaneCalculator
import com.global_water_polo.play_abroad.domain.model.Lane
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingState
import com.global_water_polo.play_abroad.core.components.BirthdayWheels
import com.global_water_polo.play_abroad.resources.Res
import com.global_water_polo.play_abroad.resources.birthday_day
import com.global_water_polo.play_abroad.resources.birthday_help_athlete
import com.global_water_polo.play_abroad.resources.birthday_help_parent
import com.global_water_polo.play_abroad.resources.birthday_month
import com.global_water_polo.play_abroad.resources.birthday_problem_future
import com.global_water_polo.play_abroad.resources.birthday_problem_invalid
import com.global_water_polo.play_abroad.resources.birthday_problem_old
import com.global_water_polo.play_abroad.resources.birthday_problem_young
import com.global_water_polo.play_abroad.resources.birthday_title_athlete
import com.global_water_polo.play_abroad.resources.birthday_title_parent
import com.global_water_polo.play_abroad.resources.birthday_year
import com.global_water_polo.play_abroad.resources.common_continue
import com.global_water_polo.play_abroad.resources.months
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

@Composable
fun BirthdayScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    onBirthdayChange: (month: Int?, day: Int?, year: Int?, fromUser: Boolean) -> Unit,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    val athlete = state.lane == Lane.ATHLETE
    val years = remember { LaneCalculator.birthYears() }
    OnboardingLayout(
        modifier = modifier,
        barStart = { BarStart(onBackClick = onBackClick, progress = state.progress(FlowStep.BIRTHDAY)) },
        bottom = {
            PrimaryButton(text = stringResource(Res.string.common_continue), onClick = onContinueClick, enabled = state.window != null)
        }
    ) {
        // Name comes first now, so the question can greet them.
        val name = state.firstName.trim()
        Headline(
            text = when {
                name.isEmpty() -> stringResource(if (athlete) Res.string.birthday_title_athlete else Res.string.birthday_title_parent)
                athlete -> stringResource(Res.string.birthday_title_athlete_named, name)
                else -> stringResource(Res.string.birthday_title_parent_named, name)
            }
        )
        HelpText(text = stringResource(if (athlete) Res.string.birthday_help_athlete else Res.string.birthday_help_parent))
        BirthdayWheels(
            months = stringArrayResource(Res.array.months),
            years = years,
            month = state.birthMonth,
            day = state.birthDay,
            year = state.birthYear,
            monthLabel = stringResource(Res.string.birthday_month),
            dayLabel = stringResource(Res.string.birthday_day),
            yearLabel = stringResource(Res.string.birthday_year),
            onChange = onBirthdayChange,
            tapToSelect = true,
            modifier = Modifier.padding(top = 18.dp)
        )
        Text(
            text = problemText(state),
            style = GWPPATheme.typography.archivoSemiBold14,
            color = GWPPATheme.colors.liveRed,
            modifier = Modifier.padding(top = 4.dp).heightIn(min = 20.dp)
        )
    }
}

/** v29's second look at a birthday: out of range, or an age that doesn't fit the other answers. */
@Composable
private fun problemText(state: OnboardingState): String {
    val parent = state.isParent
    val age = state.birthYear?.let { y -> state.birthMonth?.let { m -> state.birthDay?.let { d ->
        NcaaClockCalculator.dateOrNull(y, m, d)?.let { NcaaClockCalculator.ageOn(it, NcaaClockCalculator.today()) }
    } } } ?: 0
    return when (state.birthdayProblem) {
        BirthdayCheck.INVALID_DATE -> stringResource(Res.string.birthday_problem_invalid)
        BirthdayCheck.IN_FUTURE -> stringResource(Res.string.birthday_problem_future)
        BirthdayCheck.TOO_YOUNG -> stringResource(Res.string.birthday_problem_young)
        BirthdayCheck.NEEDS_PARENT -> stringResource(Res.string.birthday_problem_parent)
        BirthdayCheck.TOO_OLD -> stringResource(Res.string.birthday_problem_old)
        BirthdayCheck.OLD_FOR_HIGH_SCHOOL -> stringResource(if (parent) Res.string.birthday_problem_hs_parent else Res.string.birthday_problem_hs, age)
        BirthdayCheck.YOUNG_FOR_COLLEGE -> stringResource(if (parent) Res.string.birthday_problem_college_parent else Res.string.birthday_problem_college, age)
        BirthdayCheck.OLD_FOR_COMMITTED -> stringResource(if (parent) Res.string.birthday_problem_committed_parent else Res.string.birthday_problem_committed, age)
        null -> ""
    }
}

@Preview(showBackground = true)
@Composable
private fun BirthdayScreenContentPreview() {
    GWPPATheme {
        BirthdayScreenContent(state = OnboardingState(), onBirthdayChange = { _, _, _, _ -> }, onBackClick = {}, onContinueClick = {})
    }
}
