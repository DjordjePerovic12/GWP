package com.global_water_polo.play_abroad.presentation.home.athlete.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.global_water_polo.play_abroad.core.components.CheckMark
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** A "finish your profile" item with its round check (`.todo button`). */
@Composable
fun TodoRow(
    title: String,
    subtitle: String,
    done: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GWPPATheme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(colors.colorWhite)
            .border(1.dp, colors.lineGrey, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 13.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(22.dp)
                .background(if (done) colors.chlorineBlue else colors.colorWhite, CircleShape)
                .border(1.5.dp, if (done) colors.chlorineBlue else colors.mistGrey, CircleShape)
        ) {
            if (done) CheckMark(color = colors.colorWhite, modifier = Modifier.size(16.dp))
        }
        Column {
            Text(text = title, style = GWPPATheme.typography.archivoBold16, color = colors.inkBlack)
            Text(text = subtitle, style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey)
        }
    }
}
