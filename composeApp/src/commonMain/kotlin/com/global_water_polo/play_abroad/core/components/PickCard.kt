package com.global_water_polo.play_abroad.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

enum class PickMarker {
    ARROW,
    CHECK
}

/** The prototype's `.pick` card: a title, a line under it, an arrow (navigate) or a check box (select). */
@Composable
fun PickCard(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    marker: PickMarker = PickMarker.ARROW,
    selected: Boolean = false,
    compact: Boolean = false
) {
    val colors = GWPPATheme.colors
    val shape = RoundedCornerShape(18.dp)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (selected) colors.chlorineBlueSoft else colors.colorWhite.copy(alpha = 0.86f))
            .border(1.5.dp, if (selected) colors.chlorineBlue else colors.lineGrey, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = if (compact) 16.dp else 18.dp, vertical = if (compact) 13.dp else 18.dp)
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                style = if (compact) GWPPATheme.typography.archivoExpandedBold17 else GWPPATheme.typography.archivoExpandedBold19,
                color = colors.inkBlack
            )
            if (subtitle != null) {
                Text(text = subtitle, style = GWPPATheme.typography.archivoRegular14, color = colors.slateGrey)
            }
        }
        when (marker) {
            PickMarker.ARROW -> Chevron(color = colors.mistGrey, pointsLeft = false, modifier = Modifier.size(20.dp))
            PickMarker.CHECK -> Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(22.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(if (selected) colors.chlorineBlue else colors.colorWhite.copy(alpha = 0f))
                    .border(1.5.dp, if (selected) colors.chlorineBlue else colors.mistGrey, RoundedCornerShape(7.dp))
            ) {
                if (selected) CheckMark(color = colors.colorWhite, modifier = Modifier.size(18.dp))
            }
        }
    }
}
