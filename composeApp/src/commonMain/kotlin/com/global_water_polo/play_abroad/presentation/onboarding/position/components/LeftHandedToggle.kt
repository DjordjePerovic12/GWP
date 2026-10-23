package com.global_water_polo.play_abroad.presentation.onboarding.position.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** "LH" badge + Left-handed + switch: the add-on that becomes part of the athlete's identity. */
@Composable
fun LeftHandedToggle(
    title: String,
    subtitle: String,
    checked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GWPPATheme.colors
    val track by animateColorAsState(if (checked) colors.chlorineBlue else colors.lineGrey)
    val knob by animateDpAsState(if (checked) 18.dp else 0.dp)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.colorWhite)
            .border(1.5.dp, if (checked) colors.inkBlack else colors.lineGrey, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(40.dp).background(if (checked) colors.ballYellow else colors.inkBlack, RoundedCornerShape(8.dp))
        ) {
            Text(text = "LH", style = GWPPATheme.typography.archivoExpandedExtraBold13, color = if (checked) colors.inkBlack else colors.colorWhite)
        }
        Column(Modifier.weight(1f)) {
            Text(text = title, style = GWPPATheme.typography.archivoBold16, color = colors.inkBlack)
            Text(text = subtitle, style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey)
        }
        Box(Modifier.size(46.dp, 28.dp).background(track, CircleShape).padding(3.dp)) {
            Box(Modifier.offset(x = knob).size(22.dp).shadow(2.dp, CircleShape).background(colors.colorWhite, CircleShape))
        }
    }
}
