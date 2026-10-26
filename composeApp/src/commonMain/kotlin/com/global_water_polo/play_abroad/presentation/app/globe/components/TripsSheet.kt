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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.AppSheet
import com.global_water_polo.play_abroad.core.components.cityImage
import com.global_water_polo.play_abroad.core.components.shortDate
import com.global_water_polo.play_abroad.domain.model.Trip
import com.global_water_polo.play_abroad.domain.model.WaterPoloMap
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** The club a trip is with, or "GWP matches your club". */
@Composable
fun clubName(trip: Trip): String = WaterPoloMap.club(trip.clubId)?.name ?: stringResource(Res.string.rv_match)

/** A trip's photo, city, club and dates (the prototype's `.tr-top`), used by the review, the cart and Travel. */
@Composable
fun TripSummary(trip: Trip, modifier: Modifier = Modifier) {
    val colors = GWPPATheme.colors
    val city = WaterPoloMap.city(trip.cityId) ?: return
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = modifier) {
        Box(Modifier.size(56.dp).clip(RoundedCornerShape(14.dp)).background(colors.nightNavy)) {
            cityImage(city.id)?.let { Image(painterResource(it), null, contentScale = ContentScale.Crop, modifier = Modifier.matchParentSize()) }
        }
        Column(Modifier.weight(1f)) {
            Text("${city.flag} ${city.name}", style = GWPPATheme.typography.archivoBold16, color = colors.inkBlack)
            Text(clubName(trip), style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey)
            Text(
                stringResource(Res.string.rv_dates, shortDate(trip.arrive), shortDate(trip.leave), pluralStringResource(Res.plurals.travel_months, trip.months, trip.months)),
                style = GWPPATheme.typography.archivoRegular13,
                color = colors.slateGrey
            )
        }
    }
}

/** "Draft" or, filled, "Inquiry sent" (the prototype's `.vchip`). */
@Composable
fun TripChip(sent: Boolean) {
    val colors = GWPPATheme.colors
    Text(
        stringResource(if (sent) Res.string.city_sent else Res.string.cart_draft),
        style = GWPPATheme.typography.archivoBold11,
        color = if (sent) colors.colorWhite else colors.inkBlack,
        modifier = Modifier
            .clip(CircleShape)
            .background(if (sent) colors.chlorineBlue else colors.ballYellowSoft)
            .padding(horizontal = 9.dp, vertical = 4.dp)
    )
}

/** Your trips (the prototype's cartSheet): drafts to review and send, sent ones to track on Travel. */
@Composable
fun TripsSheet(
    trips: List<Trip>,
    onOpenClick: (Long) -> Unit,
    onRemoveClick: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = GWPPATheme.colors
    AppSheet(onDismiss = onDismiss) {
        Text(stringResource(Res.string.cart_title), style = GWPPATheme.typography.archivoExpandedBold27, color = colors.inkBlack)
        if (trips.isEmpty()) {
            Text(stringResource(Res.string.cart_empty), style = GWPPATheme.typography.archivoRegular15, color = colors.slateGrey)
        }
        trips.forEach { trip ->
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(colors.colorWhite)
                    .border(1.dp, colors.lineGrey, RoundedCornerShape(18.dp))
                    .padding(14.dp)
            ) {
                TripSummary(trip)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    TripChip(trip.sent)
                    Box(Modifier.weight(1f))
                    Text(
                        stringResource(if (trip.sent) Res.string.cart_track else Res.string.cart_review),
                        style = GWPPATheme.typography.archivoBold13,
                        color = colors.chlorineBlue,
                        modifier = Modifier.clip(CircleShape).clickable { onOpenClick(trip.id) }.padding(6.dp)
                    )
                    if (!trip.sent) {
                        Text(
                            stringResource(Res.string.cart_remove),
                            style = GWPPATheme.typography.archivoBold13,
                            color = colors.slateGrey,
                            modifier = Modifier.clip(CircleShape).clickable { onRemoveClick(trip.id) }.padding(6.dp)
                        )
                    }
                }
            }
        }
    }
}
