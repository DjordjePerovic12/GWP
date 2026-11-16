package com.global_water_polo.play_abroad.core.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** How long "You're in" stays before the app goes Home (v29: about 2.1 s). */
private const val YOURE_IN_MILLIS = 2100L

/**
 * v29's youreIn(): full screen navy, the chain drops away, the ball spins in, "You're in, {first}." Then
 * [onDone] (the screen goes Home).
 */
@Composable
fun YoureInOverlay(firstName: String, onDone: () -> Unit) {
    val colors = GWPPATheme.colors
    val chain = remember { Animatable(0f) }
    val ball = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        listOf(
            async { chain.animateTo(1f, tween(700, easing = FastOutSlowInEasing)) },
            async { ball.animateTo(1f, tween(YOURE_IN_MILLIS.toInt(), easing = LinearEasing)) },
            async { delay(YOURE_IN_MILLIS) }
        ).awaitAll()
        onDone()
    }
    // Drawn over the whole screen (it is edge to edge), not as a dialog, so it also covers the system bars.
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(colors.nightNavy)
            .clickable(remember { MutableInteractionSource() }, indication = null) {}
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(150.dp)) {
                Image(
                    painterResource(Res.drawable.gwp_ball),
                    contentDescription = null,
                    modifier = Modifier.size(132.dp).alpha(ball.value.coerceAtMost(0.25f) * 4).rotate(360f * ball.value)
                )
                ChainIcon(
                    color = colors.eliteGold,
                    modifier = Modifier.offset(y = (120 * chain.value).dp).alpha(1f - chain.value).size(40.dp)
                )
            }
            Text(
                stringResource(Res.string.youre_in_brand).uppercase(),
                style = GWPPATheme.typography.archivoBold11.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 0.16.em),
                color = colors.eliteGold
            )
            Text(
                if (firstName.isBlank()) stringResource(Res.string.youre_in_plain) else stringResource(Res.string.youre_in, firstName.trim()),
                style = GWPPATheme.typography.archivoExpandedExtraBold30,
                color = colors.colorWhite,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }
}

@Preview
@Composable
private fun YoureInOverlayPreview() {
    GWPPATheme { YoureInOverlay(firstName = "Alex", onDone = {}) }
}
