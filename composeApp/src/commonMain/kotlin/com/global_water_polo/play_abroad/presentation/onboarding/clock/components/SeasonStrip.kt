package com.global_water_polo.play_abroad.presentation.onboarding.clock.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.utils.seasonLabel
import com.global_water_polo.play_abroad.resources.Res
import com.global_water_polo.play_abroad.resources.clock_season
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** Five striped season bars (ball yellow / chlorine blue), "Season 1 2027-28" under each. */
@Composable
fun SeasonStrip(firstYear: Int, modifier: Modifier = Modifier) {
    val colors = GWPPATheme.colors
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = modifier.fillMaxWidth()) {
        repeat(5) { i ->
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                StripedBar(colors.ballYellow, colors.chlorineBlue, Modifier.fillMaxWidth().height(8.dp))
                Text(text = stringResource(Res.string.clock_season, i + 1), style = GWPPATheme.typography.archivoBold12, color = colors.colorWhite)
                Text(text = seasonLabel(firstYear + i), style = GWPPATheme.typography.archivoRegular13, color = Color.White.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
fun StripedBar(first: Color, second: Color, modifier: Modifier = Modifier, stripe: Float = 6f) {
    Canvas(modifier.clip(RoundedCornerShape(4.dp))) {
        val w = stripe.dp.toPx()
        var x = 0f
        var i = 0
        while (x < size.width) {
            drawRect(if (i % 2 == 0) first else second, Offset(x, 0f), Size(w, size.height))
            x += w
            i++
        }
    }
}
