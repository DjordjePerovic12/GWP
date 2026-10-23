package com.global_water_polo.play_abroad.presentation.onboarding.window.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.global_water_polo.play_abroad.presentation.onboarding.clock.components.StripedBar
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** Five lane-rope segments; the first [on] are striped ball/chlorine, the rest grey. */
@Composable
fun Rope(on: Int, modifier: Modifier = Modifier) {
    val colors = GWPPATheme.colors
    Row(horizontalArrangement = Arrangement.spacedBy(3.dp), modifier = modifier) {
        repeat(5) { i ->
            if (i < on) {
                StripedBar(colors.ballYellow, colors.chlorineBlue, Modifier.size(20.dp, 9.dp), stripe = 5f)
            } else {
                Box(Modifier.size(20.dp, 9.dp).background(colors.lineGrey, RoundedCornerShape(5.dp)))
            }
        }
    }
}
