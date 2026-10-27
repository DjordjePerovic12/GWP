package com.global_water_polo.play_abroad.presentation.app.travel.components

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.IntrinsicSize
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.domain.model.Trip
import com.global_water_polo.play_abroad.domain.model.WaterPoloMap
import com.global_water_polo.play_abroad.presentation.app.globe.components.TripChip
import com.global_water_polo.play_abroad.presentation.app.globe.components.TripSummary
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** `.tk-st .pend`: a warm beige cell with brown text for what waits on a partner. */
private val PENDING_BG = Color(0xFFF6EEDD)
private val PENDING_TEXT = Color(0xFF8A6A2F)

private val STEPS = listOf(
    Res.string.tk_step_build to Res.string.tk_step_build_sub,
    Res.string.tk_step_verify to Res.string.tk_step_verify_sub,
    Res.string.tk_step_offer to Res.string.tk_step_offer_sub,
    Res.string.tk_step_book to Res.string.tk_step_book_sub,
    Res.string.tk_step_manage to Res.string.tk_step_manage_sub
)

/**
 * A sent trip on Travel (the prototype's `.track`): the trip, where club, apartment, registration and
 * travel stand, and the five steps with "Request verification" the current one.
 */
@Composable
fun TripTracker(trip: Trip, flightsRequested: Boolean) {
    val colors = GWPPATheme.colors
    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colors.colorWhite)
            .border(1.dp, colors.lineGrey, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        TripSummary(trip)
        val statuses = listOf(
            Res.string.tk_club to stringResource(Res.string.tk_club_v),
            Res.string.tk_apartment to stringResource(if (trip.apartmentRequested) Res.string.tk_apt_requested else Res.string.tk_pending),
            Res.string.tk_registration to stringResource(Res.string.tk_after_offer),
            Res.string.tk_travel to stringResource(if (flightsRequested) Res.string.tk_planning else Res.string.fly_not_booked)
        )
        statuses.chunked(2).forEachIndexed { row, pair ->
            // `.tk-st`: both cells in a row as tall as the taller one.
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.height(IntrinsicSize.Min)) {
                pair.forEach { (label, value) ->
                    // Club and apartment wait on partners, so they get the pending tint and brown text.
                    val pending = row == 0
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (pending) PENDING_BG else colors.inkBlack.copy(alpha = 0.05f))
                            .padding(10.dp)
                    ) {
                        Text(
                            stringResource(label).uppercase(),
                            style = GWPPATheme.typography.archivoBold11.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 0.08.em),
                            color = colors.mistGrey
                        )
                        Text(value, style = GWPPATheme.typography.archivoBold13, color = if (pending) PENDING_TEXT else colors.inkBlack)
                    }
                }
            }
        }
        Column {
            STEPS.forEachIndexed { i, (title, sub) ->
                val done = i == 0
                val now = i == 1
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            contentAlignment = Alignment.Center,
                            // Done: navy with a tick. Now: a gold ring with a soft halo (`.tk-steps li.now i`).
                            modifier = Modifier
                                .then(if (now) Modifier.border(4.dp, PENDING_BG, CircleShape).padding(4.dp) else Modifier.padding(4.dp))
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(if (done) colors.chlorineBlue else colors.colorWhite)
                                .border(
                                    2.dp,
                                    when {
                                        done -> Color.Transparent
                                        now -> colors.antiqueGold
                                        else -> colors.inkBlack.copy(alpha = 0.2f)
                                    },
                                    CircleShape
                                )
                        ) {
                            if (done) Text("✓", style = GWPPATheme.typography.archivoBold11, color = colors.colorWhite)
                        }
                        if (i < STEPS.lastIndex) Box(Modifier.width(2.dp).height(22.dp).background(if (done) colors.chlorineBlue else colors.inkBlack.copy(alpha = 0.12f)))
                    }
                    Column(Modifier.padding(bottom = 8.dp)) {
                        Text(stringResource(title), style = GWPPATheme.typography.archivoBold16, color = if (done || now) colors.inkBlack else colors.mistGrey)
                        Text(stringResource(sub), style = GWPPATheme.typography.archivoRegular13, color = colors.mistGrey)
                    }
                }
            }
        }
    }
}

/** A draft on Travel (`.draftrow`): the chip, the city and "Review and send". */
@Composable
fun DraftRow(trip: Trip, onClick: () -> Unit) {
    val colors = GWPPATheme.colors
    val city = WaterPoloMap.city(trip.cityId) ?: return
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.colorWhite)
            .border(1.dp, colors.lineGrey, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        TripChip(sent = false)
        Text("${city.flag} ${city.name}", style = GWPPATheme.typography.archivoBold16, color = colors.inkBlack, modifier = Modifier.weight(1f))
        Text(stringResource(Res.string.cart_review) + " ›", style = GWPPATheme.typography.archivoBold13, color = colors.chlorineBlue)
    }
}
