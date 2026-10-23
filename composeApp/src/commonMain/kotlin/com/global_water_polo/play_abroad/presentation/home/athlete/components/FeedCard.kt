package com.global_water_polo.play_abroad.presentation.home.athlete.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** Curated feed tile on a dark gradient; [locked] shows the "Members" tag. */
@Composable
fun FeedCard(
    title: String,
    meta: String,
    tint: Color,
    lockLabel: String?,
    modifier: Modifier = Modifier
) {
    val colors = GWPPATheme.colors
    Box(
        modifier = modifier
            .heightIn(min = 150.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(listOf(tint, colors.inkBlack)))
            .padding(14.dp)
    ) {
        if (lockLabel != null) {
            Text(
                text = lockLabel,
                style = GWPPATheme.typography.archivoBold11,
                color = colors.colorWhite,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(Color.White.copy(alpha = 0.18f), CircleShape)
                    .padding(horizontal = 9.dp, vertical = 4.dp)
            )
        }
        Column(Modifier.align(Alignment.BottomStart).padding(top = 40.dp)) {
            Text(text = title, style = GWPPATheme.typography.archivoExpandedBold16, color = colors.colorWhite)
            Text(text = meta, style = GWPPATheme.typography.archivoRegular13, color = Color.White.copy(alpha = 0.8f))
        }
    }
}
