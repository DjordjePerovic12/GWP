package com.global_water_polo.play_abroad.presentation.onboarding.learn

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.BarStart
import com.global_water_polo.play_abroad.core.components.ButtonStyle
import com.global_water_polo.play_abroad.core.components.OnboardingLayout
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.presentation.onboarding.FlowStep
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingState
import com.global_water_polo.play_abroad.presentation.onboarding.progress
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** One card: the gold kicker, the headline and the paragraph (athlete and parent wording). */
private class Card(val kicker: StringResource, val headline: StringResource, val body: StringResource, val bodyParent: StringResource)

private val CARDS = listOf(
    Card(Res.string.learn_1_k, Res.string.learn_1_h, Res.string.learn_1_p, Res.string.learn_1_p_parent),
    Card(Res.string.learn_2_k, Res.string.learn_2_h, Res.string.learn_2_p, Res.string.learn_2_p),
    Card(Res.string.learn_3_k, Res.string.learn_3_h, Res.string.learn_3_p, Res.string.learn_3_p_parent)
)

/**
 * Three quiet cards before the clock (v29's a-learn): in charge of the timeline, the rule, the real choice.
 * Next walks through them; Back walks back.
 */
@Composable
fun LearnScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit
) {
    val colors = GWPPATheme.colors
    val parent = state.isParent
    val index = state.learnIndex.coerceIn(0, 2)
    val last = index == 2
    OnboardingLayout(
        modifier = modifier,
        barStart = { BarStart(onBackClick = onBackClick, progress = state.progress(FlowStep.LEARN)) },
        bottom = {
            PrimaryButton(
                text = stringResource(
                    when {
                        !last -> Res.string.learn_next
                        parent -> Res.string.learn_theirs
                        else -> Res.string.learn_yours
                    }
                ),
                onClick = onNextClick,
                style = if (last) ButtonStyle.BALL else ButtonStyle.INK
            )
        }
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 8.dp)) {
            repeat(3) { k ->
                Box(Modifier.width(28.dp).height(4.dp).clip(CircleShape).background(if (k <= index) colors.inkBlack else colors.inkBlack.copy(alpha = 0.14f)))
            }
        }
        AnimatedContent(targetState = index) { i ->
            val card = CARDS[i]
            Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 36.dp)) {
                Text(
                    stringResource(card.kicker).uppercase(),
                    style = GWPPATheme.typography.archivoBold12.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 0.14.em),
                    color = colors.antiqueGold
                )
                Text(stringResource(card.headline), style = GWPPATheme.typography.archivoExpandedExtraBold32, color = colors.inkBlack)
                Text(stringResource(if (parent) card.bodyParent else card.body), style = GWPPATheme.typography.archivoRegular16, color = colors.slateGrey)
                when (i) {
                    1 -> SeasonRope()
                    2 -> BenchVsAbroad()
                }
            }
        }
    }
}

/** `.lr-rope`: five seasons in a row, one per year. */
@Composable
private fun SeasonRope() {
    val colors = GWPPATheme.colors
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 10.dp).fillMaxWidth()) {
        (1..5).forEach { n ->
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Box(Modifier.size(12.dp).clip(CircleShape).background(colors.ballYellow))
                Text(stringResource(Res.string.learn_season, n), style = GWPPATheme.typography.archivoBold11, color = colors.inkBlack, modifier = Modifier.padding(top = 6.dp))
                Text(stringResource(Res.string.learn_year, n), style = GWPPATheme.typography.archivoRegular13, color = colors.mistGrey)
            }
        }
    }
}

/** `.lr-vs`: on the bench against abroad first. */
@Composable
private fun BenchVsAbroad() {
    val colors = GWPPATheme.colors
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 10.dp)) {
        listOf(
            Triple(Res.string.learn_bench, Res.string.learn_bench_sub, false),
            Triple(Res.string.learn_abroad, Res.string.learn_abroad_sub, true)
        ).forEach { (title, sub, on) ->
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (on) colors.inkBlack else colors.colorWhite)
                    .border(1.dp, if (on) colors.inkBlack else colors.lineGrey, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Text(stringResource(title), style = GWPPATheme.typography.archivoBold16, color = if (on) colors.ballYellow else colors.inkBlack)
                Text(stringResource(sub), style = GWPPATheme.typography.archivoRegular13, color = if (on) colors.colorWhite.copy(alpha = 0.75f) else colors.slateGrey)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LearnScreenContentPreview() {
    GWPPATheme {
        LearnScreenContent(state = OnboardingState(learnIndex = 1), onBackClick = {}, onNextClick = {})
    }
}
