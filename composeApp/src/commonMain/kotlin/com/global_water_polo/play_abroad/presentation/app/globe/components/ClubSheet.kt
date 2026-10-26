package com.global_water_polo.play_abroad.presentation.app.globe.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.AppSheet
import com.global_water_polo.play_abroad.core.components.InfoPanel
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.ButtonStyle
import com.global_water_polo.play_abroad.core.components.SheetHeading
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.core.components.TextLink
import com.global_water_polo.play_abroad.core.components.cityImage
import com.global_water_polo.play_abroad.core.components.drawableNamed
import com.global_water_polo.play_abroad.domain.model.Club
import com.global_water_polo.play_abroad.domain.model.WaterPoloMap
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** A concept pool photo for [club] (one of eight), until clubs share their own. */
fun poolImage(club: Club) = drawableNamed("pool_${WaterPoloMap.clubs.indexOf(club) % 8 + 1}")

/** A navy shield-style crest with the club's monogram; real logos come once clubs verify. */
@Composable
fun Crest(club: Club, size: Dp = 40.dp) {
    val colors = GWPPATheme.colors
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = size / 2, bottomEnd = size / 2))
            .background(colors.nightNavy)
            .border(1.5.dp, colors.eliteGold, RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = size / 2, bottomEnd = size / 2))
    ) {
        Text(WaterPoloMap.monogram(club.name), style = GWPPATheme.typography.archivoExpandedBold12, color = colors.eliteGold)
    }
}

/**
 * A club's page (the prototype's clubPage): a concept pool photo, crest and city, a "Not verified" note,
 * key facts, history, public honours, and the training a club could host, all through GWP.
 */
@Composable
fun ClubSheet(
    club: Club,
    asked: Boolean,
    onCityClick: () -> Unit,
    onAskClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = GWPPATheme.colors
    val city = WaterPoloMap.city(club.cityId) ?: return
    AppSheet(onDismiss = onDismiss) {
        TextLink(text = "‹ ${city.name}", onClick = onCityClick)
        Box(Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(20.dp)).background(colors.nightNavy)) {
            poolImage(club)?.let { Image(painterResource(it), null, contentScale = ContentScale.Crop, modifier = Modifier.matchParentSize()) }
            Text(
                stringResource(Res.string.club_concept),
                style = GWPPATheme.typography.archivoSemiBold11,
                color = colors.colorWhite,
                modifier = Modifier.align(Alignment.TopEnd).padding(10.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.45f)).padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Crest(club, 46.dp)
            Column {
                Text(club.name, style = GWPPATheme.typography.archivoExpandedBold21, color = colors.inkBlack)
                Text("${city.flag} ${city.name}, ${city.country}", style = GWPPATheme.typography.archivoRegular14, color = colors.slateGrey)
            }
        }
        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                stringResource(Res.string.club_not_verified),
                style = GWPPATheme.typography.archivoBold11,
                color = colors.inkBlack,
                modifier = Modifier.clip(CircleShape).background(colors.ballYellowSoft).padding(horizontal = 8.dp, vertical = 3.dp)
            )
            Text(stringResource(Res.string.club_verify_note), style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Fact(stringResource(Res.string.club_founded), club.founded.ifEmpty { stringResource(Res.string.club_to_verify) }, Modifier.weight(1f))
            Fact(stringResource(Res.string.club_city_label), city.name, Modifier.weight(1f))
            Fact(stringResource(Res.string.club_airport), city.airport, Modifier.weight(1f))
        }
        Box(
            Modifier
                .fillMaxWidth()
                .height(90.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(colors.nightNavy)
                .clickable(onClick = onCityClick)
        ) {
            cityImage(city.id)?.let { Image(painterResource(it), null, contentScale = ContentScale.Crop, modifier = Modifier.matchParentSize()) }
            Box(Modifier.matchParentSize().background(Brush.horizontalGradient(0f to colors.nightNavy.copy(alpha = 0.85f), 1f to Color.Transparent)))
            Column(Modifier.align(Alignment.CenterStart).padding(16.dp)) {
                Text("${city.flag} ${city.name}", style = GWPPATheme.typography.archivoExpandedBold16, color = colors.colorWhite)
                Text(stringResource(Res.string.club_see_city), style = GWPPATheme.typography.archivoBold13, color = colors.eliteGold)
            }
        }
        SheetHeading(stringResource(Res.string.club_history))
        Text(club.history, style = GWPPATheme.typography.archivoRegular15, color = colors.inkBlack)
        SheetHeading(stringResource(Res.string.club_honours))
        InfoPanel {
            club.honours.forEachIndexed { i, honour ->
                if (i > 0) HorizontalDivider(color = colors.lineGrey)
                Row(Modifier.padding(vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(honour.year, style = GWPPATheme.typography.archivoExpandedBold12, color = colors.chlorineBlue, modifier = Modifier.width(84.dp))
                    Column {
                        Text(honour.title, style = GWPPATheme.typography.archivoBold16, color = colors.inkBlack)
                        Text(honour.detail, style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey)
                    }
                }
            }
        }
        SmallText(text = stringResource(Res.string.club_honours_fine))
        SheetHeading(stringResource(Res.string.club_staff))
        SmallText(text = stringResource(Res.string.club_staff_note))
        SheetHeading(stringResource(Res.string.club_pool))
        SmallText(text = stringResource(Res.string.club_pool_note))
        SheetHeading(stringResource(Res.string.club_training))
        InfoPanel {
            listOf(
                Res.string.club_opp_stay to Res.string.club_opp_stay_sub,
                Res.string.club_opp_season to Res.string.club_opp_season_sub,
                Res.string.club_opp_trip to Res.string.club_opp_trip_sub
            ).forEachIndexed { i, (title, sub) ->
                if (i > 0) HorizontalDivider(color = colors.lineGrey)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 12.dp)) {
                    Column(Modifier.weight(1f)) {
                        Text(stringResource(title), style = GWPPATheme.typography.archivoBold16, color = colors.inkBlack)
                        Text(stringResource(sub), style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey)
                    }
                    Text(stringResource(Res.string.club_ask_gwp), style = GWPPATheme.typography.archivoBold12, color = colors.chlorineBlue)
                }
            }
        }
        SmallText(text = stringResource(Res.string.club_training_fine))
        PrimaryButton(
            text = if (asked) stringResource(Res.string.city_asked) else stringResource(Res.string.city_ask, city.name),
            onClick = onAskClick,
            style = if (asked) ButtonStyle.GHOST else ButtonStyle.BALL,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun Fact(label: String, value: String, modifier: Modifier = Modifier) {
    val colors = GWPPATheme.colors
    Column(modifier = modifier.clip(RoundedCornerShape(14.dp)).background(colors.colorWhite).padding(10.dp)) {
        Text(label, style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey)
        Text(value, style = GWPPATheme.typography.archivoBold13, color = colors.inkBlack)
    }
}
