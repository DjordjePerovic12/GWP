package com.global_water_polo.play_abroad.presentation.onboarding.gradsuggest

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.BarStart
import com.global_water_polo.play_abroad.core.components.ButtonStyle
import com.global_water_polo.play_abroad.core.components.Headline
import com.global_water_polo.play_abroad.core.components.HelpText
import com.global_water_polo.play_abroad.core.components.OnboardingLayout
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.TextLink
import com.global_water_polo.play_abroad.core.utils.LaneCalculator
import com.global_water_polo.play_abroad.core.utils.NcaaClockCalculator
import com.global_water_polo.play_abroad.presentation.onboarding.FlowStep
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingState
import com.global_water_polo.play_abroad.presentation.onboarding.progress
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/**
 * Finish school, suggested from the birthday (v29's a-grad3): confirm with one tap, pick a different date,
 * or say they've already finished.
 */
@Composable
fun GradSuggestScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    onYesClick: () -> Unit,
    onDifferentClick: () -> Unit,
    onDoneClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val colors = GWPPATheme.colors
    val parent = state.isParent
    val dob = state.birthday ?: return
    val (month, year) = LaneCalculator.gradGuess(dob)
    val past = LocalDate(year, month, 20) < NcaaClockCalculator.today()
    val label = "${stringArrayResource(Res.array.months)[month - 1]} $year"
    OnboardingLayout(
        modifier = modifier,
        barStart = { BarStart(onBackClick = onBackClick, progress = state.progress(FlowStep.GRAD_SUGGEST)) },
        bottom = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                PrimaryButton(text = stringResource(Res.string.gradsug_yes, label), onClick = onYesClick)
                PrimaryButton(text = stringResource(Res.string.gradsug_different), onClick = onDifferentClick, style = ButtonStyle.GHOST)
                if (!past) {
                    TextLink(text = stringResource(if (parent) Res.string.gradsug_done_parent else Res.string.gradsug_done), onClick = onDoneClick)
                }
            }
        }
    ) {
        Headline(
            text = stringResource(
                when {
                    past && parent -> Res.string.gradsug_title_past_parent
                    past -> Res.string.gradsug_title_past
                    parent -> Res.string.gradsug_title_future_parent
                    else -> Res.string.gradsug_title_future
                }
            )
        )
        HelpText(
            text = stringResource(
                when {
                    past && parent -> Res.string.gradsug_help_past_parent
                    past -> Res.string.gradsug_help_past
                    parent -> Res.string.gradsug_help_future_parent
                    else -> Res.string.gradsug_help_future
                },
                label
            )
        )
        // `.g28`: the suggestion in a card.
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth()
                .background(colors.colorWhite, RoundedCornerShape(18.dp))
                .border(1.dp, colors.lineGrey, RoundedCornerShape(18.dp))
                .padding(18.dp)
        ) {
            Text(
                stringResource(if (past) Res.string.gradsug_finished else Res.string.gradsug_finishing).uppercase(),
                style = GWPPATheme.typography.archivoBold11.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 0.12.em),
                color = colors.antiqueGold
            )
            Text(label, style = GWPPATheme.typography.archivoExpandedBold27, color = colors.inkBlack)
            Text(stringResource(if (parent) Res.string.gradsug_from_their else Res.string.gradsug_from_your), style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GradSuggestScreenContentPreview() {
    GWPPATheme {
        GradSuggestScreenContent(
            state = OnboardingState(birthMonth = 10, birthDay = 3, birthYear = 2008),
            onYesClick = {},
            onDifferentClick = {},
            onDoneClick = {},
            onBackClick = {}
        )
    }
}
