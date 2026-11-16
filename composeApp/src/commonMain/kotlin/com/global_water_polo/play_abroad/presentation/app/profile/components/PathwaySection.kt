package com.global_water_polo.play_abroad.presentation.app.profile.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.em
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.ChoiceChip
import com.global_water_polo.play_abroad.core.components.PhotoHero
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.ButtonStyle
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.core.components.TextLink
import com.global_water_polo.play_abroad.core.components.drawableNamed
import com.global_water_polo.play_abroad.domain.model.PathwayStep
import com.global_water_polo.play_abroad.domain.model.PathwayTiming
import com.global_water_polo.play_abroad.domain.model.PathwayWhy
import com.global_water_polo.play_abroad.domain.model.Program
import com.global_water_polo.play_abroad.domain.model.WaterPoloMap
import com.global_water_polo.play_abroad.domain.model.details
import com.global_water_polo.play_abroad.presentation.app.profile.ProfileState
import com.global_water_polo.play_abroad.presentation.onboarding.match.components.duration
import com.global_water_polo.play_abroad.presentation.onboarding.match.components.image
import com.global_water_polo.play_abroad.presentation.onboarding.match.components.title
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** Pathway (the prototype's pathHTML): the hero, the athlete's own pathway or a suggestion, programs, clinics. */
@Composable
fun PathwaySection(
    state: ProfileState,
    onBuildClick: () -> Unit,
    onGlobeClick: () -> Unit,
    onProgramClick: (Program) -> Unit,
    onCityClick: (String) -> Unit,
    onClinicClick: (Int) -> Unit,
    heroImage: DrawableResource = Res.drawable.f_harbour
) {
    val colors = GWPPATheme.colors
    val pathway = state.profile.pathway
    PhotoHero(
        image = heroImage,
        kicker = stringResource(Res.string.path_kicker),
        title = stringResource(Res.string.path_hero),
        sub = stringResource(Res.string.path_hero_sub),
        height = 380.dp
    ) {
        PrimaryButton(text = stringResource(Res.string.path_explore), onClick = onBuildClick, style = ButtonStyle.PAPER, modifier = Modifier.padding(top = 6.dp))
        TextLink(text = stringResource(Res.string.path_globe), onClick = onGlobeClick, light = true)
    }

    if (pathway != null && pathway.steps.isNotEmpty()) {
        SectionHeader(
            title = stringResource(Res.string.path_yours),
            note = if (pathway.steps.size == 1) stringResource(Res.string.path_step_one) else stringResource(Res.string.path_steps, pathway.steps.size)
        )
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            pathway.steps.forEach { step ->
                StepCard(
                    step = step,
                    destinations = destinationsFor(step.program, pathway.destinations),
                    onClick = { onProgramClick(step.program) },
                    onCityClick = onCityClick
                )
            }
        }
        SmallText(text = stringResource(Res.string.path_basis), modifier = Modifier.padding(top = 8.dp))
    } else {
        val rec = state.recommended
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 14.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(colors.colorWhite)
                .border(1.dp, colors.lineGrey, RoundedCornerShape(18.dp))
                .clickable { onProgramClick(rec) }
                .padding(16.dp)
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(stringResource(Res.string.path_suggested).uppercase(), style = GWPPATheme.typography.archivoBold11, color = colors.eliteGold)
                Text(stringResource(rec.title), style = GWPPATheme.typography.archivoExpandedBold16, color = colors.inkBlack)
                Text(rec.details.idea, style = GWPPATheme.typography.archivoRegular14, color = colors.slateGrey)
            }
            Text("›", style = GWPPATheme.typography.archivoExpandedBold21, color = colors.mistGrey)
        }
    }

    SectionHeader(
        title = stringResource(Res.string.path_programs),
        note = stringResource(if (state.membership.elite) Res.string.path_programs_open else Res.string.path_programs_all)
    )
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Program.entries.forEach { program ->
            ProgramRow(
                program = program,
                locked = state.locked(program),
                sent = program.key in state.profile.inquiries,
                onClick = { onProgramClick(program) }
            )
        }
    }

    SectionHeader(title = stringResource(Res.string.path_clinics), note = stringResource(Res.string.path_clinics_note))
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
        // `.cl3-c`: the photo with its date, then the name, place and button straight on the page (no card).
        ProfileState.CLINICS.forEachIndexed { i, clinic ->
            Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.width(200.dp)) {
                Box(Modifier.fillMaxWidth().height(112.dp).clip(RoundedCornerShape(14.dp)).background(colors.nightNavy)) {
                    drawableNamed(clinic.image)?.let {
                        Image(painterResource(it), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.matchParentSize())
                    }
                    Text(
                        clinic.date,
                        style = GWPPATheme.typography.archivoBold11,
                        color = colors.inkBlack,
                        modifier = Modifier.align(Alignment.TopStart).padding(8.dp).clip(CircleShape).background(colors.colorWhite).padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Text(clinic.name, style = GWPPATheme.typography.archivoBold16, color = colors.inkBlack, modifier = Modifier.padding(top = 4.dp))
                Text("${clinic.city}. ${clinic.format}.", style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey)
                val requested = i in state.profile.clinicRequests
                ChoiceChip(
                    text = stringResource(if (requested) Res.string.path_clinic_requested else Res.string.path_clinic_request),
                    selected = requested,
                    onClick = { onClinicClick(i) },
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
    SmallText(text = stringResource(Res.string.path_clinics_fine), modifier = Modifier.padding(top = 8.dp))
}

/** The prototype's `.sh3`: a section title with a grey note on the right. */
@Composable
fun SectionHeader(title: String, note: String = "") {
    Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 10.dp)) {
        Text(title, style = GWPPATheme.typography.archivoExpandedBold21, color = GWPPATheme.colors.inkBlack, modifier = Modifier.weight(1f))
        Text(note, style = GWPPATheme.typography.archivoSemiBold13, color = GWPPATheme.colors.slateGrey)
    }
}

/** Up to three cities for [program], keeping to the regions the athlete picked ("world" means any). */
fun destinationsFor(program: Program, regions: Set<String>): List<String> {
    val all = program.details.destinations
    if (regions.isEmpty() || "world" in regions) return all.take(3)
    val inRegion = all.filter { WaterPoloMap.city(it)?.region in regions }
    return (inRegion.ifEmpty { WaterPoloMap.cities.filter { it.region in regions }.map { it.id } }).take(3)
}

@Composable
private fun StepCard(step: PathwayStep, destinations: List<String>, onClick: () -> Unit, onCityClick: (String) -> Unit) {
    val colors = GWPPATheme.colors
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colors.colorWhite)
            .border(1.dp, colors.lineGrey, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(timingLabel(step).uppercase(), style = GWPPATheme.typography.archivoBold11, color = colors.chlorineBlue)
        Text(
            when (step.pick) {
                "fall" -> stringResource(Res.string.pick_fall_name)
                "winter" -> stringResource(Res.string.pick_winter_name)
                "spring" -> stringResource(Res.string.pick_spring_name)
                else -> stringResource(step.program.title)
            },
            style = GWPPATheme.typography.archivoExpandedBold16,
            color = colors.inkBlack
        )
        Text(stringResource(whyText(step.why)), style = GWPPATheme.typography.archivoRegular14, color = colors.slateGrey)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 6.dp)) {
            destinations.forEach { id ->
                val city = WaterPoloMap.city(id) ?: return@forEach
                ChoiceChip(text = "${city.flag} ${city.name}", selected = false, onClick = { onCityClick(id) })
            }
        }
    }
}

@Composable
fun timingLabel(step: PathwayStep): String = when (step.timing) {
    PathwayTiming.NEXT_BREAK -> stringResource(Res.string.timing_next_break)
    PathwayTiming.SUMMER -> stringResource(Res.string.timing_summer, step.year)
    PathwayTiming.SPRING -> stringResource(Res.string.timing_spring, step.year)
    PathwayTiming.FALL -> stringResource(Res.string.timing_fall, step.year)
    PathwayTiming.WINTER -> stringResource(Res.string.timing_winter, step.year, step.year + 1)
    PathwayTiming.SEASON -> stringResource(Res.string.timing_season, step.year, step.year + 1)
}

private fun whyText(why: PathwayWhy) = when (why) {
    PathwayWhy.BREAK -> Res.string.why_break
    PathwayWhy.BREAK_COLLEGE -> Res.string.why_break_college
    PathwayWhy.AFTER_JO -> Res.string.why_after_jo
    PathwayWhy.BETWEEN_YEARS -> Res.string.why_between_years
    PathwayWhy.CHECK_CREDITS -> Res.string.why_check_credits
    PathwayWhy.AFTER_GRADUATION -> Res.string.why_after_graduation
    PathwayWhy.CLUB_BLOCK -> Res.string.why_club_block
    PathwayWhy.FITS_BEFORE_CLOCK -> Res.string.why_fits_clock
    PathwayWhy.CONFIRM_NCAA -> Res.string.why_confirm_ncaa
    PathwayWhy.IN_FREE_YEAR -> Res.string.why_in_free_year
}

/**
 * A program card (`.pc3`): a small rounded photo, when (gold capitals, "Flagship" outlined for the gap year),
 * name, the one idea, and a lock or a chevron. The flagship card is navy with a gold edge.
 */
@Composable
private fun ProgramRow(program: Program, locked: Boolean, sent: Boolean, onClick: () -> Unit) {
    val colors = GWPPATheme.colors
    val shape = RoundedCornerShape(18.dp)
    val flag = program.flagship
    val kicker = GWPPATheme.typography.archivoBold11.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 0.1.em)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (flag) colors.nightNavy else colors.colorWhite)
            .border(1.dp, if (flag) colors.eliteGold.copy(alpha = 0.55f) else colors.lineGrey, shape)
            .clickable(onClick = onClick)
            .padding(start = 10.dp, end = 12.dp, top = 10.dp, bottom = 10.dp)
    ) {
        Image(
            painterResource(program.image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(width = 62.dp, height = 76.dp).clip(RoundedCornerShape(12.dp))
        )
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    stringResource(if (flag) Res.string.path_when_gapyear else program.duration).uppercase(),
                    style = kicker,
                    color = if (flag) colors.eliteGold else colors.antiqueGold
                )
                if (flag) {
                    Text(
                        stringResource(Res.string.offers_flagship).uppercase(),
                        style = kicker.copy(fontSize = 9.sp, letterSpacing = 0.08.em),
                        color = colors.eliteGold,
                        modifier = Modifier.border(1.dp, colors.eliteGold, CircleShape).padding(horizontal = 7.dp, vertical = 1.dp)
                    )
                }
            }
            Text(stringResource(program.title), style = GWPPATheme.typography.archivoExpandedBold16, color = if (flag) colors.colorWhite else colors.inkBlack)
            Text(
                program.details.idea,
                style = GWPPATheme.typography.archivoRegular13,
                color = if (flag) colors.colorWhite.copy(alpha = 0.7f) else colors.slateGrey,
                maxLines = 2
            )
            if (sent) Text(stringResource(Res.string.path_inquiry_sent), style = GWPPATheme.typography.archivoBold12, color = if (flag) colors.eliteGold else colors.inkBlack)
        }
        Text(if (locked) "🔒" else "›", style = GWPPATheme.typography.archivoBold16, color = if (flag) colors.colorWhite.copy(alpha = 0.6f) else colors.mistGrey)
    }
}
