package com.global_water_polo.play_abroad.presentation.app.globe.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.domain.model.WaterPoloMap
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/**
 * Globe+ filters (the prototype's g-filt): a country dropdown, then Men's and Women's toggles. They narrow
 * the city and club lists together with the search; picking a country also flies the globe there.
 */
@Composable
fun GlobeFilters(
    country: String,
    sex: String,
    onCountryClick: (String) -> Unit,
    onSexClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GWPPATheme.colors
    var open by remember { mutableStateOf(false) }
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Box(Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(colors.nightNavy.copy(alpha = 0.55f))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                    .clickable { open = true }
                    .padding(horizontal = 14.dp, vertical = 9.dp)
            ) {
                Text(
                    country.ifEmpty { stringResource(Res.string.globe_all_countries) },
                    style = GWPPATheme.typography.archivoBold13,
                    color = colors.colorWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Text("  ▾", style = GWPPATheme.typography.archivoBold13, color = colors.colorWhite)
            }
            DropdownMenu(expanded = open, onDismissRequest = { open = false }, containerColor = colors.colorWhite) {
                (listOf("") + WaterPoloMap.countries).forEach { c ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                c.ifEmpty { stringResource(Res.string.globe_all_countries) },
                                style = if (c == country) GWPPATheme.typography.archivoBold16 else GWPPATheme.typography.archivoRegular16,
                                color = colors.inkBlack
                            )
                        },
                        onClick = {
                            open = false
                            onCountryClick(c)
                        }
                    )
                }
            }
        }
        listOf("men" to Res.string.globe_mens, "women" to Res.string.globe_womens).forEach { (key, label) ->
            val on = sex == key
            Text(
                stringResource(label),
                style = GWPPATheme.typography.archivoBold13,
                color = if (on) colors.inkBlack else colors.colorWhite,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (on) colors.colorWhite else colors.nightNavy.copy(alpha = 0.55f))
                    .border(1.dp, Color.White.copy(alpha = if (on) 0f else 0.2f), CircleShape)
                    .clickable { onSexClick(key) }
                    .padding(horizontal = 14.dp, vertical = 9.dp)
            )
        }
    }
}

/** The zoom buttons on the right of the globe (`.g-zoom`). */
@Composable
fun GlobeZoomButtons(onZoomIn: () -> Unit, onZoomOut: () -> Unit, modifier: Modifier = Modifier) {
    val colors = GWPPATheme.colors
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = modifier) {
        listOf("+" to onZoomIn, "−" to onZoomOut).forEach { (label, onClick) ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.nightNavy.copy(alpha = 0.8f))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .clickable(onClick = onClick)
            ) {
                Text(label, style = GWPPATheme.typography.archivoExpandedBold21, color = colors.colorWhite)
            }
        }
    }
}
