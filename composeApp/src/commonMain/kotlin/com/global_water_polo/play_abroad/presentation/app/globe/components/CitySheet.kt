package com.global_water_polo.play_abroad.presentation.app.globe.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.AppSheet
import com.global_water_polo.play_abroad.core.components.ButtonStyle
import com.global_water_polo.play_abroad.core.components.InfoPanel
import com.global_water_polo.play_abroad.core.components.PhotoHero
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.core.components.cityImage
import com.global_water_polo.play_abroad.domain.model.City
import com.global_water_polo.play_abroad.domain.model.WaterPoloMap
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** A city's page (the prototype's cityPage): its photo, the clubs there, and ask GWP about a stay. */
@Composable
fun CitySheet(
    city: City,
    asked: Boolean,
    onClubClick: (String) -> Unit,
    onAskClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = GWPPATheme.colors
    val clubs = WaterPoloMap.clubsIn(city.id)
    AppSheet(onDismiss = onDismiss) {
        PhotoHero(
            image = cityImage(city.id),
            kicker = "${city.flag} ${city.country}",
            title = city.name,
            sub = when (clubs.size) {
                0 -> stringResource(Res.string.city_matches)
                1 -> stringResource(Res.string.city_club_one)
                else -> stringResource(Res.string.city_clubs, clubs.size)
            },
            height = 220.dp
        )
        if (clubs.isEmpty()) {
            Text(stringResource(Res.string.city_no_clubs, city.name), style = GWPPATheme.typography.archivoRegular15, color = colors.slateGrey)
        } else {
            Text(stringResource(Res.string.city_clubs_in, city.name), style = GWPPATheme.typography.archivoExpandedBold16, color = colors.inkBlack, modifier = Modifier.padding(top = 6.dp))
            InfoPanel {
                clubs.forEachIndexed { i, club ->
                    if (i > 0) HorizontalDivider(color = colors.lineGrey)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth().clickable { onClubClick(club.id) }.padding(vertical = 10.dp)
                    ) {
                        Crest(club)
                        Column(Modifier.weight(1f)) {
                            Text(club.name, style = GWPPATheme.typography.archivoBold16, color = colors.inkBlack)
                            val founded = if (club.founded.isEmpty()) stringResource(Res.string.city_founded_verify) else stringResource(Res.string.city_founded, club.founded)
                            val honour = club.honours.firstOrNull()?.let { ". ${it.title} ${it.year}" }.orEmpty()
                            Text(founded + honour, style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey)
                        }
                        poolImage(club)?.let {
                            Image(painterResource(it), null, contentScale = ContentScale.Crop, modifier = Modifier.size(width = 58.dp, height = 42.dp).clip(RoundedCornerShape(10.dp)))
                        }
                    }
                }
            }
            SmallText(text = stringResource(Res.string.city_clubs_fine))
        }
        PrimaryButton(
            text = if (asked) stringResource(Res.string.city_asked) else stringResource(Res.string.city_ask, city.name),
            onClick = onAskClick,
            style = if (asked) ButtonStyle.GHOST else ButtonStyle.BALL,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
