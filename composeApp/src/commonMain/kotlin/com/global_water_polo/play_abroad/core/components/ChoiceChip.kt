package com.global_water_polo.play_abroad.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** Single-choice chip: white with a grey edge, filled ink when picked. */
@Composable
fun ChoiceChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GWPPATheme.colors
    Text(
        text = text,
        style = GWPPATheme.typography.archivoSemiBold14,
        color = if (selected) colors.colorWhite else colors.inkBlack,
        modifier = modifier
            .clip(CircleShape)
            .background(if (selected) colors.inkBlack else colors.colorWhite)
            .border(1.5.dp, if (selected) colors.inkBlack else colors.lineGrey, CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp)
    )
}
