package com.global_water_polo.play_abroad.presentation.onboarding.window

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.BackButton
import com.global_water_polo.play_abroad.core.components.ButtonStyle
import com.global_water_polo.play_abroad.core.components.ClockChip
import com.global_water_polo.play_abroad.core.components.OnboardingLayout
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.core.utils.NcaaClockCalculator
import com.global_water_polo.play_abroad.core.utils.seasonLabel
import com.global_water_polo.play_abroad.domain.model.Lane
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingState
import com.global_water_polo.play_abroad.presentation.onboarding.window.components.Rope
import com.global_water_polo.play_abroad.resources.Res
import com.global_water_polo.play_abroad.resources.common_continue
import com.global_water_polo.play_abroad.resources.gwp_ball
import com.global_water_polo.play_abroad.resources.window_abroad_first
import com.global_water_polo.play_abroad.resources.window_build_profile
import com.global_water_polo.play_abroad.resources.window_college_in
import com.global_water_polo.play_abroad.resources.window_disclaimer
import com.global_water_polo.play_abroad.resources.window_explain_parent
import com.global_water_polo.play_abroad.resources.window_fall
import com.global_water_polo.play_abroad.resources.window_of_athlete
import com.global_water_polo.play_abroad.resources.window_of_named
import com.global_water_polo.play_abroad.resources.window_of_parent
import com.global_water_polo.play_abroad.resources.window_parent_note
import com.global_water_polo.play_abroad.resources.window_share
import com.global_water_polo.play_abroad.resources.window_share_ready
import com.global_water_polo.play_abroad.resources.window_share_sub
import com.global_water_polo.play_abroad.resources.window_share_title
import com.global_water_polo.play_abroad.resources.window_verdict_closing_athlete
import com.global_water_polo.play_abroad.resources.window_verdict_closing_parent
import com.global_water_polo.play_abroad.resources.window_verdict_free_athlete
import com.global_water_polo.play_abroad.resources.window_verdict_free_parent
import com.global_water_polo.play_abroad.resources.window_verdict_keep_athlete
import com.global_water_polo.play_abroad.resources.window_verdict_keep_parent
import com.global_water_polo.play_abroad.resources.window_year_abroad
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

@Composable
fun WindowScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    val window = state.window ?: return
    val colors = GWPPATheme.colors
    val typography = GWPPATheme.typography
    val athlete = state.lane == Lane.ATHLETE
    val s = window.seasonsStraight
    val a = window.seasonsAbroad
    OnboardingLayout(
        modifier = modifier,
        barStart = { BackButton(onClick = onBackClick) },
        barEnd = { ClockChip(countdown = state.countdown, clockYear = window.clock.year) },
        bottom = {
            PrimaryButton(
                text = stringResource(if (athlete) Res.string.window_build_profile else Res.string.common_continue),
                onClick = onContinueClick,
                style = ButtonStyle.BALL
            )
            if (athlete) SmallText(text = stringResource(Res.string.window_disclaimer), center = true, modifier = Modifier.fillMaxWidth().padding(top = 10.dp))
        }
    ) {
        SmallText(
            text = when {
                !athlete -> stringResource(Res.string.window_of_parent)
                state.firstName.isNotBlank() -> stringResource(Res.string.window_of_named, state.firstName.trim())
                else -> stringResource(Res.string.window_of_athlete)
            }
        )
        Text(text = stringResource(Res.string.window_fall, window.clock.year), style = typography.archivoExpandedExtraBold51, color = colors.inkBlack, modifier = Modifier.padding(top = 6.dp))
        Text(
            text = when {
                a == s && athlete -> stringResource(Res.string.window_verdict_free_athlete)
                a == s -> stringResource(Res.string.window_verdict_free_parent)
                a == 0 && athlete -> stringResource(Res.string.window_verdict_closing_athlete)
                a == 0 -> stringResource(Res.string.window_verdict_closing_parent)
                athlete -> stringResource(Res.string.window_verdict_keep_athlete, a)
                else -> stringResource(Res.string.window_verdict_keep_parent, a)
            },
            style = typography.archivoSemiBold18,
            color = colors.inkBlack,
            modifier = Modifier.padding(top = 10.dp)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .padding(top = 18.dp)
                .fillMaxWidth()
                .background(colors.colorWhite.copy(alpha = 0.9f), RoundedCornerShape(18.dp))
                .border(1.dp, colors.lineGrey, RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            if (!athlete) Text(text = stringResource(Res.string.window_explain_parent), style = typography.archivoRegular15, color = colors.inkBlack)
            SeasonRow(label = stringResource(Res.string.window_college_in, window.collegeFall), seasons = s)
            SeasonRow(
                label = if (athlete) stringResource(Res.string.window_year_abroad, window.collegeFall + 1) else stringResource(Res.string.window_abroad_first),
                seasons = a
            )
            if (athlete) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(5) { i ->
                        Column(Modifier.weight(1f)) {
                            Box(Modifier.fillMaxWidth().height(3.dp).background(colors.inkBlack))
                            Text(text = "S${i + 1}", style = typography.archivoBold13, color = colors.inkBlack, modifier = Modifier.padding(top = 6.dp))
                            Text(
                                text = seasonLabel(window.clock.year + i),
                                style = typography.archivoRegular13,
                                color = colors.slateGrey
                            )
                        }
                    }
                }
            }
        }
        if (!athlete) {
            SmallText(text = stringResource(Res.string.window_parent_note), modifier = Modifier.padding(top = 12.dp))
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
                    .background(colors.inkBlack, RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Image(painter = painterResource(Res.drawable.gwp_ball), contentDescription = null, modifier = Modifier.size(44.dp))
                Column(Modifier.weight(1f)) {
                    Text(text = stringResource(Res.string.window_share_title), style = typography.archivoExpandedBold16, color = colors.colorWhite)
                    Text(text = stringResource(Res.string.window_share_sub), style = typography.archivoRegular14, color = colors.colorWhite)
                }
                Text(
                    text = stringResource(if (state.shareCardReady) Res.string.window_share_ready else Res.string.window_share),
                    style = typography.archivoBold13,
                    color = colors.inkBlack,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(colors.ballYellow)
                        .clickable(enabled = !state.shareCardReady, onClick = onShareClick)
                        .padding(horizontal = 14.dp, vertical = 9.dp)
                )
            }
        }
    }
}

@Composable
private fun SeasonRow(label: String, seasons: Int) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = label, style = GWPPATheme.typography.archivoRegular15, color = GWPPATheme.colors.slateGrey, modifier = Modifier.weight(1f))
        Rope(on = seasons)
        Text(
            text = seasons.toString(),
            style = GWPPATheme.typography.archivoExpandedBold21,
            color = GWPPATheme.colors.inkBlack,
            textAlign = TextAlign.End,
            modifier = Modifier.width(18.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WindowScreenContentPreview() {
    GWPPATheme {
        val window = NcaaClockCalculator.window(LocalDate(2008, 10, 3))
        WindowScreenContent(state = OnboardingState(window = window, firstName = "Alex"), onBackClick = {}, onShareClick = {}, onContinueClick = {})
    }
}
