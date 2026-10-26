package com.global_water_polo.play_abroad.core.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** The prototype's `.fbar`: a short track next to the back button that fills as the flow goes on. */
@Composable
fun FlowBar(progress: Float, modifier: Modifier = Modifier) {
    val fill by animateFloatAsState(progress.coerceIn(0f, 1f), tween(420))
    Box(
        modifier = modifier
            .width(64.dp)
            .height(4.dp)
            .clip(CircleShape)
            .background(GWPPATheme.colors.lineGrey)
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(fill)
                .clip(CircleShape)
                .background(GWPPATheme.colors.inkBlack)
        )
    }
}
