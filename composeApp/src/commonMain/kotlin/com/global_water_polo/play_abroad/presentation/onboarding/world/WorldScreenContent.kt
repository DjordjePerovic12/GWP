package com.global_water_polo.play_abroad.presentation.onboarding.world

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.ButtonStyle
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingState
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

private val TOOLS: List<Pair<StringResource, StringResource>> = listOf(
    Res.string.world_profile to Res.string.world_profile_sub,
    Res.string.world_feed to Res.string.world_feed_sub,
    Res.string.world_globe to Res.string.world_globe_sub,
    Res.string.world_radar to Res.string.world_radar_sub,
    Res.string.world_settings to Res.string.world_settings_sub
)

/** "Your world of water polo": the globe rises at night and the five tools of the app are introduced. */
@Composable
fun WorldScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    onEnterClick: () -> Unit
) {
    val colors = GWPPATheme.colors
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val top = maxHeight * 0.3f
        // The list scrolls under the globe's night panel; Enter stays pinned at the bottom, always in reach.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = top)
                .background(Brush.verticalGradient(0f to Color.Transparent, 0.12f to colors.nightNavy.copy(alpha = 0.9f), 1f to colors.nightNavy))
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clipToBounds()
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp, top = 56.dp, bottom = 8.dp)
        ) {
            Text(
                text = stringResource(Res.string.world_kicker).uppercase(),
                style = GWPPATheme.typography.archivoBold12,
                color = colors.eliteGold
            )
            Text(
                text = stringResource(Res.string.world_title).uppercase(),
                style = GWPPATheme.typography.archivoExpandedExtraBold32,
                color = colors.colorWhite,
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = stringResource(Res.string.world_sub),
                style = GWPPATheme.typography.archivoRegular16,
                color = colors.colorWhite.copy(alpha = 0.78f),
                modifier = Modifier.padding(top = 12.dp)
            )
            Box(
                Modifier
                    .padding(top = 18.dp)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(colors.colorWhite.copy(alpha = 0.18f))
            )
            TOOLS.forEachIndexed { index, (name, line) ->
                Row(modifier = Modifier.padding(vertical = 12.dp)) {
                    Text(
                        text = "0${index + 1}",
                        style = GWPPATheme.typography.archivoBold12,
                        color = colors.eliteGold,
                        modifier = Modifier.width(36.dp).padding(top = 3.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(text = stringResource(name), style = GWPPATheme.typography.archivoExpandedBold16, color = colors.colorWhite)
                        Text(text = stringResource(line), style = GWPPATheme.typography.archivoRegular14, color = colors.colorWhite.copy(alpha = 0.7f))
                    }
                }
                Box(Modifier.fillMaxWidth().height(1.dp).background(colors.colorWhite.copy(alpha = 0.1f)))
            }
        }
            PrimaryButton(
                text = stringResource(Res.string.world_enter).uppercase(),
                onClick = onEnterClick,
                style = ButtonStyle.PAPER,
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 20.dp)
            )
        }
    }
}

@Preview
@Composable
private fun WorldScreenContentPreview() {
    GWPPATheme {
        WorldScreenContent(state = OnboardingState(), onEnterClick = {})
    }
}
