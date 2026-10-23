package com.global_water_polo.play_abroad.core.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** The big question at the top of every step (`.q`, `.q.xl`). */
@Composable
fun Headline(text: String, modifier: Modifier = Modifier, large: Boolean = false) {
    Text(
        text = text,
        style = if (large) GWPPATheme.typography.archivoExpandedBold38 else GWPPATheme.typography.archivoExpandedBold32,
        color = GWPPATheme.colors.inkBlack,
        modifier = modifier.padding(top = 4.dp)
    )
}

/** Grey explanation under the question (`.help`). */
@Composable
fun HelpText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = GWPPATheme.typography.archivoRegular16,
        color = GWPPATheme.colors.slateGrey,
        modifier = modifier.padding(top = 10.dp)
    )
}

/** Small grey note (`.small`). */
@Composable
fun SmallText(text: String, modifier: Modifier = Modifier, center: Boolean = false) {
    Text(
        text = text,
        style = GWPPATheme.typography.archivoRegular13,
        color = GWPPATheme.colors.mistGrey,
        textAlign = if (center) TextAlign.Center else TextAlign.Start,
        modifier = modifier
    )
}
