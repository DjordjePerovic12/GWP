package com.global_water_polo.play_abroad.presentation.opener

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.LocalSound
import com.global_water_polo.play_abroad.core.components.LocalStatusBarOverride
import com.global_water_polo.play_abroad.core.components.Sfx
import com.global_water_polo.play_abroad.core.components.globe.LocalGlobeController
import com.global_water_polo.play_abroad.core.components.globe.badgeGlobeCircle
import com.global_water_polo.play_abroad.core.components.model3d.BallModelView
import com.global_water_polo.play_abroad.presentation.intro.QuickStartSheet
import com.global_water_polo.play_abroad.presentation.opener.components.BallSpinMemory
import com.global_water_polo.play_abroad.presentation.opener.components.OpenerTimeline
import com.global_water_polo.play_abroad.presentation.opener.components.SpaceBackdrop
import com.global_water_polo.play_abroad.resources.Res
import com.global_water_polo.play_abroad.resources.intro_skip
import com.global_water_polo.play_abroad.resources.opener_begin
import com.global_water_polo.play_abroad.resources.opener_name
import com.global_water_polo.play_abroad.resources.opener_tag_1
import com.global_water_polo.play_abroad.resources.opener_tag_2
import com.global_water_polo.play_abroad.resources.opener_welcome
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

private val IVORY = Color(0xFFF2EDE3)
private val GOLD = Color(0xFFDDB45C)
private val BUTTON = Color(0xFFE0B85F)
private val BUTTON_TEXT = Color(0xFF0F2834)

@Composable
fun OpenerScreenContent(
    modifier: Modifier = Modifier,
    state: OpenerState,
    onEvent: (OpenerEvent) -> Unit
) {
    val globe = LocalGlobeController.current
    val haptics = LocalHapticFeedback.current
    val sound = LocalSound.current
    val statusTop = with(LocalDensity.current) { WindowInsets.statusBars.getTop(this).toDp() }
    val current by rememberUpdatedState(state)
    var time by remember { mutableFloatStateOf(0f) }

    BoxWithConstraints(modifier.fillMaxSize()) {
        val w = maxWidth.value
        val h = maxHeight.value
        val timeline = remember(w, h, statusTop) {
            val (cy, d) = badgeGlobeCircle(w, h, statusTop.value)
            OpenerTimeline(w, h, cy, d)
        }
        val px = with(LocalDensity.current) { 1.dp.toPx() }
        fun u() = current.tappedAt?.let { time - it }

        // The clock: seconds since the opener appeared. Skip jumps to the held ball; the end hands over once.
        LaunchedEffect(timeline) {
            var last = withFrameNanos { it }
            var splashed = false
            while (true) {
                val now = withFrameNanos { it }
                time += ((now - last) / 1e9f).coerceIn(0f, 0.05f)
                last = now
                if (current.skipped && current.tappedAt == null && time < OpenerTimeline.AUTO_END) time = OpenerTimeline.AUTO_END
                val after = u()
                if (after != null && after >= 2.0f && !splashed) {
                    splashed = true
                    if (globe.hapticsOn) sound.play(Sfx.SPLASH)
                }
                if (timeline.done(after) && !current.finished) {
                    BallSpinMemory.spin = timeline.spin(time, after)
                    onEvent(OpenerEvent.OnFinished)
                }
            }
        }

        // White status bar icons on the dark sky; dark again once ivory has spread.
        val statusOverride = LocalStatusBarOverride.current
        val dark = timeline.ivory(u()) < 0.5f
        DisposableEffect(dark) {
            statusOverride.value = dark
            onDispose { statusOverride.value = null }
        }

        SpaceBackdrop(
            time = { time },
            petrol = { timeline.petrol(time) },
            stars = { timeline.stars(time, u()) },
            deepSpace = { timeline.deepSpace(time) },
            halo = { timeline.halo(time, u()) },
            ivory = { timeline.ivory(u()) },
            ballCy = { timeline.ballCy(time, u()) * px },
            ballRadius = { timeline.ballRadius(time, u()) * px },
            modifier = Modifier.fillMaxSize()
        )
        BallModelView(frame = { timeline.frame(time, u()) }, modifier = Modifier.fillMaxSize())

        val large = GWPPATheme.typography.archivoExpandedExtraBold23.copy(fontSize = 22.sp, lineHeight = 1.08.em, textAlign = TextAlign.Center)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
            modifier = Modifier
                .offset(y = timeline.welcomeTop.dp)
                .fillMaxWidth()
                .height(58.dp)
                .rise { timeline.welcome(time, u()) }
        ) {
            Text(
                stringResource(Res.string.opener_welcome).uppercase(),
                style = GWPPATheme.typography.archivoExpandedBold12.copy(fontSize = 14.sp, letterSpacing = 0.3.em),
                color = GOLD
            )
            Text(stringResource(Res.string.opener_name).uppercase(), style = large, color = IVORY)
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .offset(y = timeline.taglineTop.dp)
                .fillMaxWidth()
                .height(OpenerTimeline.TXT_H.dp)
                .rise { timeline.tagline(time, u()) }
        ) {
            Text(stringResource(Res.string.opener_tag_1).uppercase(), style = large.copy(lineHeight = 1.24.em), color = GOLD)
            Text(stringResource(Res.string.opener_tag_2).uppercase(), style = large.copy(lineHeight = 1.24.em), color = GOLD)
        }
        val buttonLive = timeline.button(time, u()) > 0.9f && state.tappedAt == null
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = timeline.buttonTop.dp)
                .rise { timeline.button(time, u()) }
                .widthIn(min = 244.dp)
                .height(OpenerTimeline.BTN_H.dp)
                .clip(CircleShape)
                .background(BUTTON)
                .clickable(enabled = buttonLive) {
                    if (globe.hapticsOn) {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        sound.play(Sfx.SWOOSH)
                    }
                    onEvent(OpenerEvent.OnBeginClick(time))
                }
                .padding(horizontal = 30.dp)
        ) {
            Text(
                stringResource(Res.string.opener_begin),
                style = GWPPATheme.typography.archivoSemiBold18.copy(fontSize = 17.sp, fontWeight = FontWeight.SemiBold),
                color = BUTTON_TEXT
            )
        }

        val skip = timeline.skip(time, u())
        if (skip > 0.02f) {
            Text(
                stringResource(Res.string.intro_skip),
                style = GWPPATheme.typography.archivoRegular13.copy(fontSize = 13.5.sp, fontWeight = FontWeight.Medium),
                color = IVORY,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = statusTop + 18.dp, end = 20.dp)
                    .graphicsLayer { alpha = skip }
                    .clip(CircleShape)
                    .background(IVORY.copy(alpha = 0.10f))
                    .clickable { onEvent(OpenerEvent.OnSkipClick) }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
        // Testing shortcut: a long press in the top left corner opens Quick start (only when DEMO_SHORTCUTS is on).
        Box(
            Modifier
                .padding(top = statusTop)
                .size(width = 120.dp, height = 72.dp)
                .pointerInput(Unit) { detectTapGestures(onLongPress = { onEvent(OpenerEvent.OnQuickStartOpen) }) }
        )
        if (state.quickStartOpen) {
            QuickStartSheet(onPick = { onEvent(OpenerEvent.OnQuickStart(it)) }, onDismiss = { onEvent(OpenerEvent.OnQuickStartDismiss) })
        }
    }
}

/** The opener's text cascade: each block fades in while rising 6 dp, all with the same timing. */
private fun Modifier.rise(amount: () -> Float) = graphicsLayer {
    val a = amount()
    alpha = a
    translationY = (1f - a) * 6.dp.toPx()
}
