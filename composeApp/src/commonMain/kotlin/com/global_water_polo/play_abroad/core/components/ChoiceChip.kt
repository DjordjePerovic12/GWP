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

/** v29's `lbl29` + chips: a small label over a wrapping row of chips ([options] are key to label). */
@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun ChipGroup(
    label: String,
    options: List<Pair<String, String>>,
    isSelected: (String) -> Boolean,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.layout.Column(modifier = modifier.padding(top = 18.dp)) {
        Text(
            label.uppercase(),
            style = GWPPATheme.typography.archivoBold11.copy(letterSpacing = androidx.compose.ui.unit.TextUnit(0.12f, androidx.compose.ui.unit.TextUnitType.Em)),
            color = GWPPATheme.colors.slateGrey,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        androidx.compose.foundation.layout.FlowRow(
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { (key, text) -> ChoiceChip(text = text, selected = isSelected(key), onClick = { onClick(key) }) }
        }
    }
}
