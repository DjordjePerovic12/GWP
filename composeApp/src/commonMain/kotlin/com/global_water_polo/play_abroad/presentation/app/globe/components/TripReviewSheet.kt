package com.global_water_polo.play_abroad.presentation.app.globe.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.ButtonStyle
import com.global_water_polo.play_abroad.core.components.AppSheet
import com.global_water_polo.play_abroad.core.components.FormField
import com.global_water_polo.play_abroad.core.components.InfoPanel
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.SheetHeading
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.core.components.TextLink
import com.global_water_polo.play_abroad.domain.model.CostGroup
import com.global_water_polo.play_abroad.domain.model.CostItem
import com.global_water_polo.play_abroad.domain.model.CostLine
import com.global_water_polo.play_abroad.domain.model.Trip
import com.global_water_polo.play_abroad.domain.model.estimate
import com.global_water_polo.play_abroad.domain.model.totalLessons
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

private val AVAILABILITY = listOf(
    Res.string.av_club to Res.string.av_club_sub,
    Res.string.av_home to Res.string.av_home_sub,
    Res.string.av_price to Res.string.av_price_sub
)

/**
 * Review your experience (the prototype's reviewSheet): the trip, the cost breakdown with GWP's fee,
 * what still needs checking, "No payment required yet", notes, then Send availability inquiry.
 * A sent trip shows the same page read-only, with every check "Checking".
 */
@Composable
fun TripReviewSheet(
    trip: Trip,
    onEditClick: () -> Unit,
    onNotesChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = GWPPATheme.colors
    val estimate = trip.estimate()
    AppSheet(onDismiss = onDismiss) {
        TextLink(text = stringResource(Res.string.rv_edit), onClick = onEditClick)
        Text(stringResource(Res.string.rv_title), style = GWPPATheme.typography.archivoExpandedBold27, color = colors.inkBlack)
        InfoPanel { TripSummary(trip, Modifier.padding(vertical = 12.dp)) }

        SheetHeading(stringResource(Res.string.rv_breakdown))
        InfoPanel {
            CostGroup.entries.forEach { group ->
                val lines = estimate.lines.filter { it.group == group }
                if (lines.isEmpty()) return@forEach
                GroupTitle(
                    stringResource(
                        when (group) {
                            CostGroup.PROGRAM -> Res.string.rv_program
                            CostGroup.HOUSING -> Res.string.rv_housing
                            CostGroup.OPTIONAL -> Res.string.rv_optional
                        }
                    )
                )
                lines.forEach { line -> Line(lineLabel(trip, line), money(line.amount)) }
                HorizontalDivider(color = colors.lineGrey, modifier = Modifier.padding(top = 8.dp))
            }
            GroupTitle(stringResource(Res.string.rv_fee))
            Line(stringResource(Res.string.rv_fee_sub), money(estimate.fee))
            HorizontalDivider(color = colors.lineGrey, modifier = Modifier.padding(top = 8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 12.dp)) {
                Text(stringResource(Res.string.rv_total), style = GWPPATheme.typography.archivoExpandedBold16, color = colors.inkBlack, modifier = Modifier.weight(1f))
                Text(money(estimate.total), style = GWPPATheme.typography.archivoExpandedBold16, color = colors.inkBlack)
            }
            HorizontalDivider(color = colors.lineGrey)
            Column(Modifier.padding(vertical = 12.dp)) {
                Text(stringResource(Res.string.rv_not_included), style = GWPPATheme.typography.archivoBold13, color = colors.inkBlack)
                Text(stringResource(Res.string.rv_not_included_sub), style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey)
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 10.dp)) {
            Text("05", style = GWPPATheme.typography.archivoExpandedBold12, color = colors.chlorineBlue)
            Text(stringResource(Res.string.rv_avail), style = GWPPATheme.typography.archivoExpandedBold17, color = colors.inkBlack)
        }
        InfoPanel {
            AVAILABILITY.forEachIndexed { i, (title, sub) ->
                if (i > 0) HorizontalDivider(color = colors.lineGrey)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(vertical = 12.dp)) {
                    Box(Modifier.size(10.dp).clip(CircleShape).background(if (trip.sent) colors.chlorineBlue else colors.ballYellow))
                    Column(Modifier.weight(1f)) {
                        Text(stringResource(title), style = GWPPATheme.typography.archivoBold16, color = colors.inkBlack)
                        Text(stringResource(sub), style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey)
                    }
                    Text(
                        stringResource(if (trip.sent) Res.string.av_checking else Res.string.av_pending),
                        style = GWPPATheme.typography.archivoBold12,
                        color = if (trip.sent) colors.chlorineBlue else colors.slateGrey
                    )
                }
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.chlorineBlueSoft)
                .border(1.dp, colors.chlorineBlue.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Text("🛡 " + stringResource(Res.string.rv_nopay), style = GWPPATheme.typography.archivoBold16, color = colors.inkBlack)
            Text(stringResource(Res.string.rv_nopay_sub), style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey)
        }

        if (!trip.sent) {
            FormField(
                label = stringResource(Res.string.rv_notes),
                value = trip.notes,
                onValueChange = onNotesChange,
                placeholder = stringResource(Res.string.rv_notes_hint)
            )
        }
        PrimaryButton(
            text = stringResource(if (trip.sent) Res.string.rv_sent else Res.string.rv_send),
            onClick = onSendClick,
            enabled = !trip.sent,
            style = ButtonStyle.BALL,
            modifier = Modifier.padding(top = 8.dp)
        )
        SmallText(text = stringResource(Res.string.rv_fine))
    }
}

@Composable
private fun lineLabel(trip: Trip, line: CostLine): String = when (line.item) {
    CostItem.CLUB -> stringResource(Res.string.bk_club_m)
    CostItem.GYM -> stringResource(Res.string.bk_gym)
    CostItem.HOME -> stringResource(homeLabel(trip.home))
    CostItem.MEALS -> stringResource(Res.string.bk_meals)
    CostItem.LESSONS -> stringResource(Res.string.rv_lessons, trip.lessons, trip.totalLessons)
    CostItem.ACTIVITIES -> stringResource(Res.string.rv_activities)
    CostItem.ADD_ON -> line.addOn?.let { stringResource(addOnLabel(it)) }.orEmpty()
}

@Composable
private fun GroupTitle(text: String) {
    Text(text.uppercase(), style = GWPPATheme.typography.archivoBold11, color = GWPPATheme.colors.mistGrey, modifier = Modifier.padding(top = 12.dp, bottom = 2.dp))
}

@Composable
private fun Line(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, style = GWPPATheme.typography.archivoRegular14, color = GWPPATheme.colors.inkBlack, modifier = Modifier.weight(1f).padding(end = 12.dp))
        Text(value, style = GWPPATheme.typography.archivoBold13, color = GWPPATheme.colors.inkBlack)
    }
}
