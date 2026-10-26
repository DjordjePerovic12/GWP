package com.global_water_polo.play_abroad.presentation.app.globe.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.domain.model.ClubGroup
import com.global_water_polo.play_abroad.domain.model.WaterPoloMap
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/**
 * The club pop-up over the globe (the prototype's g-pop): how many clubs sit under a badge and in which
 * cities, then one card per club with its crest, city and founding year. Tapping a club opens its page.
 */
@Composable
fun ClubGroupPopup(group: ClubGroup, onClubClick: (String) -> Unit, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    val colors = GWPPATheme.colors
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(colors.colorWhite)
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    pluralStringResource(Res.plurals.globe_clubs_count, group.clubs.size, group.clubs.size),
                    style = GWPPATheme.typography.archivoBold16,
                    color = colors.inkBlack
                )
                Text(group.cities.joinToString(", "), style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey)
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(32.dp).clip(CircleShape).background(colors.lineGrey.copy(alpha = 0.6f)).clickable(onClick = onDismiss)
            ) {
                Text("✕", style = GWPPATheme.typography.archivoBold13, color = colors.inkBlack)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
            group.clubs.forEach { club ->
                val city = WaterPoloMap.city(club.cityId)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.boneWhite)
                        .border(1.dp, colors.lineGrey, RoundedCornerShape(14.dp))
                        .clickable { onClubClick(club.id) }
                        .padding(start = 8.dp, end = 12.dp, top = 8.dp, bottom = 8.dp)
                ) {
                    Crest(club, 36.dp)
                    Column {
                        Text(club.name, style = GWPPATheme.typography.archivoBold13, color = colors.inkBlack)
                        val sub = listOfNotNull(city?.name, club.founded.ifEmpty { null }).joinToString(", ")
                        Text(sub.ifEmpty { stringResource(Res.string.city_founded_verify) }, style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey)
                    }
                }
            }
        }
    }
}
