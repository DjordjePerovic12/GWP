package com.global_water_polo.play_abroad.presentation.app.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.AppSheet
import com.global_water_polo.play_abroad.core.components.BulletList
import com.global_water_polo.play_abroad.core.components.ChoiceChip
import com.global_water_polo.play_abroad.core.components.PhotoHero
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.SheetHeading
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.domain.model.Program
import com.global_water_polo.play_abroad.domain.model.WaterPoloMap
import com.global_water_polo.play_abroad.domain.model.details
import com.global_water_polo.play_abroad.presentation.app.profile.ProfileState
import com.global_water_polo.play_abroad.presentation.onboarding.match.components.image
import com.global_water_polo.play_abroad.presentation.onboarding.match.components.title
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/**
 * A program's full page (the prototype's progDetail): facts, the months it runs, purpose, who it is for,
 * what it develops, what is included, where it could happen and how to apply. Locked programs can be
 * read freely; requesting one needs GWP Elite.
 */
@Composable
fun ProgramDetailSheet(
    program: Program,
    state: ProfileState,
    onCityClick: (String) -> Unit,
    onCtaClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = GWPPATheme.colors
    val d = program.details
    val locked = state.locked(program)
    val sent = program.key in state.profile.inquiries
    AppSheet(onDismiss = onDismiss) {
        PhotoHero(image = program.image, title = stringResource(program.title), sub = d.idea, height = 220.dp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Fact(stringResource(Res.string.pd_duration), d.duration, Modifier.weight(1.3f))
            Fact(stringResource(Res.string.pd_fee), stringResource(Res.string.pd_fee_value), Modifier.weight(1f))
            Fact(stringResource(Res.string.pd_entry), stringResource(if (d.application) Res.string.pd_entry_application else Res.string.pd_entry_inquiry), Modifier.weight(1f))
        }
        MonthsStrip(d.months)
        SheetHeading(stringResource(Res.string.pd_purpose))
        Text(d.purpose, style = GWPPATheme.typography.archivoRegular15, color = colors.inkBlack)
        SheetHeading(stringResource(Res.string.pd_ideal))
        BulletList(d.ideal)
        SheetHeading(stringResource(Res.string.pd_development))
        BulletList(d.development)
        SheetHeading(stringResource(Res.string.pd_inc_opt))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(stringResource(Res.string.pd_included), style = GWPPATheme.typography.archivoBold13, color = colors.slateGrey)
                BulletList(d.included)
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(stringResource(Res.string.pd_optional), style = GWPPATheme.typography.archivoBold13, color = colors.slateGrey)
                BulletList(d.optional)
            }
        }
        SmallText(text = stringResource(Res.string.pd_inc_fine))
        SheetHeading(stringResource(Res.string.pd_where))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            d.destinations.forEach { id ->
                val city = WaterPoloMap.city(id) ?: return@forEach
                val clubs = WaterPoloMap.clubsIn(id).size
                val note = when (clubs) {
                    0 -> stringResource(Res.string.pd_gwp_matches)
                    1 -> stringResource(Res.string.pd_club_one)
                    else -> stringResource(Res.string.pd_clubs, clubs)
                }
                ChoiceChip(text = "${city.flag} ${city.name} · $note", selected = false, onClick = { onCityClick(id) })
            }
        }
        SmallText(text = stringResource(Res.string.pd_where_fine))
        SheetHeading(stringResource(if (d.application) Res.string.pd_how_apply else Res.string.pd_how))
        d.steps.forEachIndexed { i, step ->
            Text("${i + 1}. $step", style = GWPPATheme.typography.archivoRegular15, color = colors.inkBlack)
        }
        PrimaryButton(
            text = stringResource(
                when {
                    sent -> Res.string.path_inquiry_sent
                    locked -> Res.string.pd_unlock
                    d.application -> Res.string.pd_start_app
                    else -> Res.string.pd_request
                }
            ),
            onClick = onCtaClick,
            enabled = !sent,
            modifier = Modifier.padding(top = 14.dp)
        )
        if (locked && !sent) SmallText(text = stringResource(Res.string.pd_locked_note), center = true, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun Fact(label: String, value: String, modifier: Modifier = Modifier) {
    val colors = GWPPATheme.colors
    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier.clip(RoundedCornerShape(14.dp)).background(colors.colorWhite).padding(10.dp)
    ) {
        Text(label, style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey)
        Text(value, style = GWPPATheme.typography.archivoBold13, color = colors.inkBlack)
    }
}

/** Twelve month initials with the program's months lit, or "Any 4–6 weeks" for flexible programs. */
@Composable
private fun MonthsStrip(months: List<Int>) {
    val colors = GWPPATheme.colors
    val initials = stringArrayResource(Res.array.months_short).map { it.take(1) }
    Box {
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp), modifier = Modifier.fillMaxWidth()) {
            initials.forEachIndexed { i, m ->
                val on = i in months
                Text(
                    m,
                    style = GWPPATheme.typography.archivoBold12,
                    color = if (on) colors.colorWhite else colors.mistGrey,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (on) colors.chlorineBlue else colors.colorWhite)
                        .padding(vertical = 6.dp)
                )
            }
        }
        if (months.isEmpty()) {
            Text(
                stringResource(Res.string.pd_any_weeks),
                style = GWPPATheme.typography.archivoBold12,
                color = colors.chlorineBlue,
                modifier = Modifier.align(Alignment.Center).clip(RoundedCornerShape(6.dp)).background(colors.colorWhite).padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}
