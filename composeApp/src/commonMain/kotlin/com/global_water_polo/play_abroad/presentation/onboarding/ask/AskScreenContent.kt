package com.global_water_polo.play_abroad.presentation.onboarding.ask

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.number
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.BarStart
import com.global_water_polo.play_abroad.core.components.ClockChip
import com.global_water_polo.play_abroad.core.components.Headline
import com.global_water_polo.play_abroad.core.components.HelpText
import com.global_water_polo.play_abroad.core.components.OnboardingLayout
import com.global_water_polo.play_abroad.core.components.PickCard
import com.global_water_polo.play_abroad.core.components.PickMarker
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.utils.PathwayCalculator
import com.global_water_polo.play_abroad.presentation.onboarding.AskStep
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingState
import com.global_water_polo.play_abroad.presentation.onboarding.ask.components.askQuestion
import com.global_water_polo.play_abroad.presentation.onboarding.progress
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** The prototype's `askScreen`: progress, the question, one tap per answer, the honest line under it, Continue. */
@Composable
fun AskScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    step: AskStep,
    onPick: (String) -> Unit,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    val question = askQuestion(step, state)
    val picks = state.picks(step)
    val sayLines = sayLines(step, state)
    OnboardingLayout(
        modifier = modifier,
        barStart = { BarStart(onBackClick = onBackClick, progress = state.progress(step.flowStep)) },
        barEnd = { if (state.window != null) ClockChip(countdown = state.countdown, clockYear = state.window.clock.year) },
        bottom = { PrimaryButton(text = stringResource(Res.string.common_continue), onClick = onContinueClick, enabled = picks.isNotEmpty()) }
    ) {
        Headline(text = stringResource(question.title))
        question.help?.let { HelpText(text = stringResource(it)) }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 20.dp)) {
            question.options.forEach { option ->
                PickCard(
                    title = stringResource(option.title),
                    subtitle = option.sub?.let { stringResource(it) },
                    marker = PickMarker.CHECK,
                    selected = option.key in picks,
                    compact = true,
                    onClick = { onPick(option.key) }
                )
            }
        }
        AnimatedVisibility(
            visible = sayLines.isNotEmpty(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            SayPanel(lines = sayLines)
        }
    }
}

@Composable
private fun sayLines(step: AskStep, state: OnboardingState): List<String> {
    val question = askQuestion(step, state)
    return when (step) {
        AskStep.CLUB -> {
            val offset = state.travelStartOffset
            if (state.club.isEmpty() || offset == null) emptyList() else {
                val start = PathwayCalculator.monthAt(offset)
                val month = stringArrayResource(Res.array.months_short)[start.month.number - 1]
                listOf(stringResource(Res.string.club_say_start, "$month ${start.year}"))
            }
        }
        AskStep.GOAL, AskStep.PROBE, AskStep.STOP -> question.options
            .filter { it.key in state.picks(step) }
            .mapNotNull { option -> option.say?.let { stringResource(it) } }
        AskStep.NOW, AskStep.LEVEL, AskStep.FACILITIES -> emptyList()
    }
}

/** The prototype's `.reveal.say`: what GWP says back, one line per answer. */
@Composable
private fun SayPanel(lines: List<String>) {
    val colors = GWPPATheme.colors
    val shape = RoundedCornerShape(16.dp)
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .padding(top = 14.dp)
            .fillMaxWidth()
            .background(colors.ballYellowSoft, shape)
            .border(1.dp, colors.ballYellow.copy(alpha = 0.5f), shape)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        lines.forEach { line ->
            Text(text = line, style = GWPPATheme.typography.archivoSemiBold15, color = colors.inkBlack)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AskScreenContentPreview() {
    GWPPATheme {
        AskScreenContent(state = OnboardingState(goal = "pro"), step = AskStep.GOAL, onPick = {}, onBackClick = {}, onContinueClick = {})
    }
}
