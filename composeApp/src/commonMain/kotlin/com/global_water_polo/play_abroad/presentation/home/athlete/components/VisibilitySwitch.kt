package com.global_water_polo.play_abroad.presentation.home.athlete.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.global_water_polo.play_abroad.domain.model.ProfileVisibility
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** Who can see the profile: three-way segmented control (`.seg`). */
@Composable
fun VisibilitySwitch(
    options: List<Pair<ProfileVisibility, String>>,
    selected: ProfileVisibility,
    onClick: (ProfileVisibility) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GWPPATheme.colors
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
            .background(colors.colorWhite, RoundedCornerShape(14.dp))
            .border(1.dp, colors.lineGrey, RoundedCornerShape(14.dp))
            .padding(4.dp)
    ) {
        options.forEach { (value, label) ->
            val on = value == selected
            Text(
                text = label,
                style = GWPPATheme.typography.archivoSemiBold12,
                color = if (on) colors.colorWhite else colors.slateGrey,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (on) colors.inkBlack else colors.colorWhite)
                    .clickable { onClick(value) }
                    .padding(vertical = 9.dp, horizontal = 4.dp)
            )
        }
    }
}
