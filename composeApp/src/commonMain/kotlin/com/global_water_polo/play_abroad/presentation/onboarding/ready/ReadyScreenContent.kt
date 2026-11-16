package com.global_water_polo.play_abroad.presentation.onboarding.ready

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.BarStart
import com.global_water_polo.play_abroad.core.components.ButtonStyle
import com.global_water_polo.play_abroad.core.components.OnboardingLayout
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.presentation.app.home.components.PlanTable
import com.global_water_polo.play_abroad.presentation.onboarding.FlowStep
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingState
import com.global_water_polo.play_abroad.presentation.onboarding.progress
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/**
 * Your plan is ready (v29's a-ready): the reveal after the tour. Where they are, the NCAA window, the free
 * time, the summer and the first step, as a starting point GWP builds on.
 */
@Composable
fun ReadyScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    val colors = GWPPATheme.colors
    val parent = state.isParent
    val name = (if (parent) state.kidName.ifBlank { state.firstName } else state.firstName).trim()
    OnboardingLayout(
        modifier = modifier,
        barStart = { BarStart(onBackClick = onBackClick, progress = state.progress(FlowStep.READY)) },
        bottom = {
            PrimaryButton(
                text = stringResource(if (parent) Res.string.ready_send_parent else Res.string.ready_send),
                onClick = onContinueClick,
                style = ButtonStyle.BALL
            )
        }
    ) {
        // `.rd28-hero`: the harbour photo, darkening to petrol under the headline.
        Box(Modifier.fillMaxWidth().height(260.dp).clip(RoundedCornerShape(24.dp))) {
            Image(painterResource(Res.drawable.f_harbour), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.matchParentSize())
            Box(
                Modifier.matchParentSize().background(
                    Brush.verticalGradient(
                        0f to colors.inkBlack.copy(alpha = 0.05f),
                        0.45f to colors.inkBlack.copy(alpha = 0.5f),
                        1f to colors.inkBlack.copy(alpha = 0.94f)
                    )
                )
            )
            Column(Modifier.align(Alignment.BottomStart).padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    stringResource(if (parent) Res.string.ready_k_their else Res.string.ready_k_your).uppercase(),
                    style = GWPPATheme.typography.archivoBold11.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 0.16.em),
                    color = colors.eliteGold
                )
                Text(
                    when {
                        parent && name.isNotEmpty() -> stringResource(Res.string.ready_h_their, name)
                        parent -> stringResource(Res.string.ready_h_their_plain)
                        name.isNotEmpty() -> stringResource(Res.string.ready_h_your, name)
                        else -> stringResource(Res.string.ready_h_plain)
                    },
                    style = GWPPATheme.typography.archivoExpandedExtraBold30,
                    color = colors.colorWhite
                )
            }
        }
        PlanTable(parent = parent, now = state.now, lane = state.laneInfo, free = state.freeYear, gap = state.summerGap, firstStep = state.firstStep)
        SmallText(text = stringResource(if (parent) Res.string.ready_fine_parent else Res.string.ready_fine), modifier = Modifier.padding(top = 12.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun ReadyScreenContentPreview() {
    GWPPATheme {
        ReadyScreenContent(
            state = OnboardingState(firstName = "Alex", now = setOf("committed"), birthMonth = 10, birthDay = 3, birthYear = 2008, gradMonth = 6, gradYear = 2027, availability = "jo"),
            onBackClick = {},
            onContinueClick = {}
        )
    }
}
