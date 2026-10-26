package com.global_water_polo.play_abroad.presentation.onboarding.tour

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.TextLink
import com.global_water_polo.play_abroad.domain.model.Lane
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingState
import com.global_water_polo.play_abroad.presentation.onboarding.tour.components.TOUR_CITIES
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** The night panel under the touring globe: kicker, the city, its country, a 20-second bar and Skip. */
@Composable
fun TourScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    stop: Int,
    onSkipClick: () -> Unit
) {
    val colors = GWPPATheme.colors
    val parent = state.lane == Lane.PARENT
    val city = TOUR_CITIES.getOrNull(stop)
    val title = when {
        stop < 0 -> stringResource(Res.string.tour_world)
        city == null -> stringResource(if (parent) Res.string.tour_ready_parent else Res.string.tour_ready_athlete)
        else -> city.name
    }
    val sub = when {
        stop < 0 -> stringResource(Res.string.tour_exploring)
        city == null -> ""
        else -> "${city.flag} ${city.country}"
    }
    val bar = remember { Animatable(0f) }
    LaunchedEffect(Unit) { bar.animateTo(1f, tween(TOUR_MILLIS, easing = LinearEasing)) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Brush.verticalGradient(0f to Color.Transparent, 0.3f to colors.nightNavy.copy(alpha = 0.92f), 1f to colors.nightNavy))
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(start = 32.dp, end = 32.dp, top = 96.dp, bottom = 28.dp)
        ) {
            Text(
                text = stringResource(if (parent) Res.string.tour_building_parent else Res.string.tour_building_athlete),
                style = GWPPATheme.typography.archivoBold13,
                color = colors.ballYellow
            )
            // City and country change together, so the name never sits over the previous city's country.
            AnimatedContent(
                targetState = title to sub,
                transitionSpec = { (fadeIn(tween(220)) + scaleIn(tween(260), initialScale = 0.92f)) togetherWith fadeOut(tween(120)) }
            ) { (name, country) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = name,
                        style = GWPPATheme.typography.archivoExpandedExtraBold30,
                        color = colors.colorWhite,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 18.dp)
                    )
                    Text(text = country, style = GWPPATheme.typography.archivoRegular16, color = colors.colorWhite.copy(alpha = 0.8f))
                }
            }
            Box(
                Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(colors.colorWhite.copy(alpha = 0.16f))
            ) {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(bar.value)
                        .background(colors.ballYellow)
                )
            }
            TextLink(text = stringResource(Res.string.tour_skip), onClick = onSkipClick, light = true)
        }
    }
}

@Preview
@Composable
private fun TourScreenContentPreview() {
    GWPPATheme {
        TourScreenContent(state = OnboardingState(), stop = 1, onSkipClick = {})
    }
}
