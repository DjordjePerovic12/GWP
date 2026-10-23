package com.global_water_polo.play_abroad.presentation.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import com.global_water_polo.play_abroad.resources.Res
import com.global_water_polo.play_abroad.resources.gwp_ball
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** The dark "how coaches see you" card with the ball peeking in from the corner (`.card-me`). */
@Composable
fun ProfileCard(
    name: String,
    meta: @Composable () -> Unit,
    stats: List<Pair<String, String>>,
    modifier: Modifier = Modifier,
    footer: @Composable ColumnScope.() -> Unit = {}
) {
    val colors = GWPPATheme.colors
    Box(
        modifier = modifier
            .padding(top = 14.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(colors.inkBlack)
    ) {
        Image(
            painter = painterResource(Res.drawable.gwp_ball),
            contentDescription = null,
            modifier = Modifier.align(Alignment.TopEnd).offset(x = 30.dp, y = (-30).dp).size(120.dp).alpha(0.55f)
        )
        Column(verticalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.padding(18.dp)) {
            Column {
                Text(text = name, style = GWPPATheme.typography.archivoExpandedExtraBold24, color = colors.colorWhite)
                meta()
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                stats.forEach { (label, value) ->
                    Column(Modifier.weight(1f)) {
                        Box(Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.2f)))
                        Text(text = label, style = GWPPATheme.typography.archivoRegular13, color = Color.White.copy(alpha = 0.6f), modifier = Modifier.padding(top = 8.dp))
                        Text(text = value, style = GWPPATheme.typography.archivoExpandedBold16, color = colors.colorWhite)
                    }
                }
            }
            footer()
        }
    }
}
