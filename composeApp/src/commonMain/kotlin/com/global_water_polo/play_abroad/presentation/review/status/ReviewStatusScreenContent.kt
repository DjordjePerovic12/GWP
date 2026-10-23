package com.global_water_polo.play_abroad.presentation.review.status

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.BrandBar
import com.global_water_polo.play_abroad.core.components.Headline
import com.global_water_polo.play_abroad.core.components.HelpText
import com.global_water_polo.play_abroad.core.components.OnboardingLayout
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

@Composable
fun ReviewStatusScreenContent(
    modifier: Modifier = Modifier,
    state: ReviewStatusState,
    onBackToProfileClick: () -> Unit
) {
    val steps = listOf(
        stringResource(Res.string.review_step_sent) to stringResource(Res.string.review_step_sent_sub),
        stringResource(Res.string.review_step_coach) to stringResource(Res.string.review_step_coach_sub),
        stringResource(Res.string.review_step_level) to stringResource(Res.string.review_step_level_sub),
        stringResource(Res.string.review_step_public) to stringResource(Res.string.review_step_public_sub)
    )
    OnboardingLayout(
        modifier = modifier,
        barStart = { BrandBar() },
        bottom = { PrimaryButton(text = stringResource(Res.string.review_back_to_profile), onClick = onBackToProfileClick) }
    ) {
        Headline(text = stringResource(Res.string.review_title), large = true)
        HelpText(text = stringResource(Res.string.review_help))
        Column(modifier = Modifier.padding(top = 24.dp)) {
            steps.forEachIndexed { i, (title, sub) ->
                TimelineRow(
                    title = title,
                    subtitle = sub,
                    done = i < state.doneSteps,
                    now = i == state.doneSteps,
                    last = i == steps.lastIndex
                )
            }
        }
    }
}

@Composable
private fun TimelineRow(title: String, subtitle: String, done: Boolean, now: Boolean, last: Boolean) {
    val colors = GWPPATheme.colors
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.height(IntrinsicSize.Min)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(22.dp).fillMaxHeight()) {
            Box(
                Modifier
                    .size(22.dp)
                    .background(
                        when {
                            done -> colors.inkBlack
                            now -> colors.chlorineBlueSoft
                            else -> colors.boneWhite
                        },
                        CircleShape
                    )
                    .border(2.dp, if (done) colors.inkBlack else if (now) colors.chlorineBlue else colors.lineGrey, CircleShape)
            )
            if (!last) Box(Modifier.width(2.dp).weight(1f).background(colors.lineGrey))
        }
        Column(Modifier.padding(bottom = 18.dp)) {
            Text(text = title, style = GWPPATheme.typography.archivoBold16, color = colors.inkBlack)
            Text(text = subtitle, style = GWPPATheme.typography.archivoRegular14, color = colors.slateGrey)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReviewStatusScreenContentPreview() {
    GWPPATheme {
        ReviewStatusScreenContent(state = ReviewStatusState(), onBackToProfileClick = {})
    }
}
