package com.global_water_polo.play_abroad.core.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.resources.Res
import com.global_water_polo.play_abroad.resources.brand_long
import com.global_water_polo.play_abroad.resources.brand_short
import com.global_water_polo.play_abroad.resources.gwp_logo
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** Logo + GLOBAL WATER POLO, the left side of the app screens' bar; [short] says GWP where a chip shares the row. */
@Composable
fun BrandBar(modifier: Modifier = Modifier, short: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = modifier) {
        Image(painter = painterResource(Res.drawable.gwp_logo), contentDescription = null, modifier = Modifier.size(26.dp))
        Text(text = stringResource(if (short) Res.string.brand_short else Res.string.brand_long), style = GWPPATheme.typography.archivoExpandedBold12, color = GWPPATheme.colors.inkBlack)
    }
}

/** Section heading with an optional grey note on the right (`.h2`). */
@Composable
fun SectionTitle(title: String, modifier: Modifier = Modifier, note: String? = null) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
        modifier = modifier.fillMaxWidth().padding(top = 24.dp, bottom = 10.dp)
    ) {
        Text(text = title, style = GWPPATheme.typography.archivoExpandedBold17, color = GWPPATheme.colors.inkBlack)
        if (note != null) Text(text = note, style = GWPPATheme.typography.archivoSemiBold13, color = GWPPATheme.colors.mistGrey)
    }
}

/** Premium offer row (`.prem button`); [hero] = the dark one on top. */
@Composable
fun OfferCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badge: String? = null,
    hero: Boolean = false
) {
    val colors = GWPPATheme.colors
    val shape = RoundedCornerShape(16.dp)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (hero) colors.inkBlack else colors.colorWhite)
            .border(1.dp, if (hero) colors.inkBlack else colors.lineGrey, shape)
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = title, style = GWPPATheme.typography.archivoBold16, color = if (hero) colors.colorWhite else colors.inkBlack)
            Text(text = subtitle, style = GWPPATheme.typography.archivoRegular13, color = if (hero) Color.White.copy(alpha = 0.7f) else colors.slateGrey)
        }
        if (badge != null) {
            Text(
                text = badge,
                style = GWPPATheme.typography.archivoBold11,
                color = colors.inkBlack,
                modifier = Modifier
                    .background(if (hero) colors.ballYellow else colors.ballYellowSoft, CircleShape)
                    .padding(horizontal = 9.dp, vertical = 4.dp)
            )
        }
    }
}
