package com.global_water_polo.play_abroad.presentation.onboarding.birthday

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
import com.global_water_polo.play_abroad.domain.model.BirthdayProblem
import com.global_water_polo.play_abroad.domain.model.Lane
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingState
import com.global_water_polo.play_abroad.presentation.onboarding.birthday.components.BirthdayWheels
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
    val years = remember { NcaaClockCalculator.birthYears() }
    OnboardingLayout(
        modifier = modifier,
        barStart = { BarStart(onBackClick = onBackClick, step = 1, of = 3) },
        bottom = {
            PrimaryButton(text = stringResource(Res.string.common_continue), onClick = onContinueClick, enabled = state.window != null)
        }
    ) {
        Headline(text = stringResource(if (athlete) Res.string.birthday_title_athlete else Res.string.birthday_title_parent))
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
            modifier = Modifier.padding(top = 18.dp)
        )
        Text(
            text = when (state.birthdayProblem) {
                BirthdayProblem.INVALID_DATE -> stringResource(Res.string.birthday_problem_invalid)
                BirthdayProblem.IN_FUTURE -> stringResource(Res.string.birthday_problem_future)
                BirthdayProblem.TOO_YOUNG -> stringResource(Res.string.birthday_problem_young)
                BirthdayProblem.TOO_OLD -> stringResource(Res.string.birthday_problem_old)
                null -> ""
            },
            style = GWPPATheme.typography.archivoSemiBold14,
            color = GWPPATheme.colors.liveRed,
            modifier = Modifier.padding(top = 4.dp).heightIn(min = 20.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BirthdayScreenContentPreview() {
    GWPPATheme {
        BirthdayScreenContent(state = OnboardingState(), onBirthdayChange = { _, _, _, _ -> }, onBackClick = {}, onContinueClick = {})
    }
}
