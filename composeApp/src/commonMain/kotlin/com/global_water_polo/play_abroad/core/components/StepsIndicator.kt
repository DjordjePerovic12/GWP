package com.global_water_polo.play_abroad.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

@Composable
fun StepsIndicator(step: Int, of: Int, modifier: Modifier = Modifier) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = modifier) {
        repeat(of) { i ->
            Box(
                Modifier
                    .size(width = 18.dp, height = 4.dp)
                    .background(
                        if (i < step) GWPPATheme.colors.inkBlack else GWPPATheme.colors.inkBlack.copy(alpha = 0.12f),
                        RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}
