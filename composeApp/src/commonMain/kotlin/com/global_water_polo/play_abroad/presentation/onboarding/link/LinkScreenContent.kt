package com.global_water_polo.play_abroad.presentation.onboarding.link

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.core.components.TextLink
import com.global_water_polo.play_abroad.domain.model.Lane
import com.global_water_polo.play_abroad.presentation.onboarding.FlowStep
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingState
import com.global_water_polo.play_abroad.presentation.onboarding.progress
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** Check your inbox: a verification link instead of a password, plus how they heard about GWP. */
@Composable
fun LinkScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    onRelationClick: (String) -> Unit,
    onHeardFromClick: (String) -> Unit,
    onResendClick: () -> Unit,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    val parent = state.lane == Lane.PARENT
    OnboardingLayout(
        modifier = modifier,
        barStart = { BarStart(onBackClick = onBackClick, progress = state.progress(FlowStep.LINK)) },
        bottom = {
            PrimaryButton(text = stringResource(Res.string.common_continue), onClick = onContinueClick)
            TextLink(
                text = stringResource(Res.string.link_resend),
                onClick = onResendClick,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 6.dp)
            )
            SmallText(
                text = stringResource(if (state.linkResent) Res.string.link_resent else Res.string.link_proto),
                center = true,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    ) {
        MailIcon()
        Headline(text = stringResource(Res.string.link_title), modifier = Modifier.padding(top = 16.dp))
        HelpText(text = stringResource(if (parent) Res.string.link_help_parent else Res.string.link_help_athlete, state.email.trim()))
        if (parent) {
            ChipGroup(
                label = stringResource(Res.string.link_relation),
                options = stringArrayResource(Res.array.relations),
                selected = state.relation,
                onClick = onRelationClick
            )
        }
        ChipGroup(
            label = stringResource(Res.string.link_heard),
            options = stringArrayResource(Res.array.heard_from),
            selected = state.heardFrom,
            onClick = onHeardFromClick
        )
    }
}

@Composable
private fun ChipGroup(label: String, options: List<String>, selected: String, onClick: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 24.dp)) {
        Text(text = label, style = GWPPATheme.typography.archivoSemiBold13, color = GWPPATheme.colors.slateGrey)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { option -> ChoiceChip(text = option, selected = selected == option, onClick = { onClick(option) }) }
        }
    }
}

/** The prototype's `.mail`: an envelope in a soft blue circle. */
@Composable
private fun MailIcon() {
    val colors = GWPPATheme.colors
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(56.dp)
            .background(colors.chlorineBlueSoft, CircleShape)
    ) {
        Canvas(Modifier.size(26.dp)) {
            val stroke = Stroke(width = 2.dp.toPx(), join = StrokeJoin.Round)
            val w = size.width
            val h = size.height
            drawRoundRect(colors.chlorineBlue, Offset(w * 0.125f, h * 0.21f), Size(w * 0.75f, h * 0.58f), CornerRadius(3.dp.toPx()), style = stroke)
            val flap = Path().apply {
                moveTo(w * 0.17f, h * 0.29f)
                lineTo(w * 0.5f, h * 0.54f)
                lineTo(w * 0.83f, h * 0.29f)
            }
            drawPath(flap, colors.chlorineBlue, style = stroke)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LinkScreenContentPreview() {
    GWPPATheme {
        LinkScreenContent(
            state = OnboardingState(lane = Lane.PARENT, email = "sam@example.com"),
            onRelationClick = {},
            onHeardFromClick = {},
            onResendClick = {},
            onBackClick = {},
            onContinueClick = {}
        )
    }
}
