package com.global_water_polo.play_abroad.presentation.app.globe.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.AppSheet
import com.global_water_polo.play_abroad.core.components.ButtonStyle
import com.global_water_polo.play_abroad.core.components.ChoiceChip
import com.global_water_polo.play_abroad.core.components.InfoPanel
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.core.components.cityImage
import com.global_water_polo.play_abroad.core.components.shortDate
import com.global_water_polo.play_abroad.domain.model.AddOn
import com.global_water_polo.play_abroad.domain.model.Home
import com.global_water_polo.play_abroad.domain.model.Trip
import com.global_water_polo.play_abroad.domain.model.WaterPoloMap
import com.global_water_polo.play_abroad.domain.model.estimate
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** Words for each home and add-on, shared by the builder, the review and Travel. */
fun homeLabel(home: Home): StringResource = when (home) {
    Home.ROOM -> Res.string.home_room
    Home.STUDIO -> Res.string.home_studio
    Home.ONE_BEDROOM -> Res.string.home_one
}

fun addOnLabel(addOn: AddOn): StringResource = when (addOn) {
    AddOn.AIRPORT_PICKUP -> Res.string.addon_pickup
    AddOn.SIM_CARD -> Res.string.addon_sim
    AddOn.BIKE -> Res.string.addon_bike
    AddOn.LANGUAGE -> Res.string.addon_language
    AddOn.GEAR -> Res.string.addon_gear
    AddOn.LAUNDRY -> Res.string.addon_laundry
}

/** "$12,400", or "On inquiry" while GWP has no price. */
@Composable
fun money(amount: Double?): String {
    if (amount == null) return stringResource(Res.string.bk_on_inquiry)
    val whole = kotlin.math.round(amount).toLong().toString()
    return "$" + whole.reversed().chunked(3).joinToString(",").reversed()
}

/**
 * Build your world (the prototype's citySheet builder): 01 the club, 02 the dates, 03 the home with the
 * apartment tour, 04 training and extras, then add-ons. The pinned footer shows the estimate and Review.
 * Every change goes out as a new [Trip] through [onChange].
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExperienceSheet(
    trip: Trip,
    onChange: (Trip) -> Unit,
    onDatesClick: () -> Unit,
    onTourClick: () -> Unit,
    onReviewClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = GWPPATheme.colors
    val city = WaterPoloMap.city(trip.cityId) ?: return
    val clubs = WaterPoloMap.clubsIn(city.id)
    val months = pluralStringResource(Res.plurals.travel_months, trip.months, trip.months)
    val estimate = trip.estimate()
    AppSheet(
        onDismiss = onDismiss,
        footer = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Column(Modifier.weight(1f)) {
                    Text(money(estimate.total), style = GWPPATheme.typography.archivoExpandedBold17, color = colors.inkBlack)
                    Text(stringResource(Res.string.bk_items, estimate.lines.size, months), style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey)
                }
                PrimaryButton(text = stringResource(Res.string.bk_review), onClick = onReviewClick, style = ButtonStyle.BALL, modifier = Modifier.width(150.dp))
            }
        }
    ) {
        Box(Modifier.fillMaxWidth().height(170.dp).clip(RoundedCornerShape(22.dp)).background(colors.nightNavy)) {
            cityImage(city.id)?.let { Image(painterResource(it), null, contentScale = ContentScale.Crop, modifier = Modifier.matchParentSize()) }
            Box(Modifier.matchParentSize().background(Brush.verticalGradient(0f to colors.nightNavy.copy(alpha = 0.35f), 1f to colors.nightNavy.copy(alpha = 0.92f))))
            Column(Modifier.align(Alignment.BottomStart).padding(18.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(stringResource(Res.string.bk_kicker).uppercase(), style = GWPPATheme.typography.archivoBold11, color = colors.eliteGold)
                Text(stringResource(Res.string.bk_title), style = GWPPATheme.typography.archivoExpandedExtraBold24, color = colors.colorWhite)
                Text(stringResource(Res.string.bk_sub, months, city.name), style = GWPPATheme.typography.archivoRegular14, color = colors.colorWhite.copy(alpha = 0.8f))
            }
        }

        StepLabel("01", stringResource(Res.string.bk_club))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
            clubs.forEach { club ->
                ClubChoice(
                    name = club.name,
                    image = { poolImage(club)?.let { Image(painterResource(it), null, contentScale = ContentScale.Crop, modifier = Modifier.matchParentSize()) } },
                    crest = { Crest(club, 28.dp) },
                    selected = trip.clubId == club.id,
                    onClick = { onChange(trip.copy(clubId = club.id)) }
                )
            }
            ClubChoice(
                name = stringResource(Res.string.bk_match),
                image = { cityImage(city.id)?.let { Image(painterResource(it), null, contentScale = ContentScale.Crop, modifier = Modifier.matchParentSize()) } },
                crest = { Image(painterResource(Res.drawable.gwp_logo), null, modifier = Modifier.size(28.dp)) },
                selected = trip.clubId.isEmpty(),
                onClick = { onChange(trip.copy(clubId = "")) }
            )
        }
        SmallText(text = stringResource(Res.string.city_clubs_fine))

        StepLabel("02", stringResource(Res.string.bk_dates))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.colorWhite)
                .border(1.dp, colors.lineGrey, RoundedCornerShape(16.dp))
                .clickable(onClick = onDatesClick)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            DateCell(stringResource(Res.string.bk_arrive), shortDate(trip.arrive))
            Text("→", style = GWPPATheme.typography.archivoBold16, color = colors.mistGrey)
            DateCell(stringResource(Res.string.bk_leave), shortDate(trip.leave))
            Box(Modifier.weight(1f))
            Text(months, style = GWPPATheme.typography.archivoBold12, color = colors.chlorineBlue, softWrap = false, modifier = Modifier.clip(CircleShape).background(colors.chlorineBlueSoft).padding(horizontal = 10.dp, vertical = 5.dp))
        }
        SmallText(text = stringResource(Res.string.bk_dates_fine))

        StepLabel("03", stringResource(Res.string.bk_home))
        Box(
            Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(colors.nightNavy)
                .clickable(onClick = onTourClick)
        ) {
            Image(painterResource(Res.drawable.apt_1), null, contentScale = ContentScale.Crop, modifier = Modifier.matchParentSize())
            Box(Modifier.matchParentSize().background(Brush.verticalGradient(0.3f to Color.Transparent, 1f to colors.nightNavy.copy(alpha = 0.85f))))
            Text(
                stringResource(Res.string.bk_concept),
                style = GWPPATheme.typography.archivoSemiBold11,
                color = colors.colorWhite,
                modifier = Modifier.align(Alignment.TopStart).padding(10.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.45f)).padding(horizontal = 8.dp, vertical = 3.dp)
            )
            Column(Modifier.align(Alignment.BottomStart).padding(14.dp)) {
                Text(stringResource(Res.string.bk_tour), style = GWPPATheme.typography.archivoExpandedBold16, color = colors.colorWhite)
                Text(stringResource(Res.string.bk_tour_sub), style = GWPPATheme.typography.archivoRegular13, color = colors.colorWhite.copy(alpha = 0.8f))
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.align(Alignment.BottomEnd).padding(14.dp).size(44.dp).clip(CircleShape).background(colors.ballYellow)
            ) {
                Text("▶\uFE0E", style = GWPPATheme.typography.archivoBold16, color = colors.inkBlack)
            }
        }
        InfoPanel {
            Home.entries.forEachIndexed { i, home ->
                if (i > 0) HorizontalDivider(color = colors.lineGrey)
                Radio(stringResource(homeLabel(home)), selected = trip.home == home, onClick = { onChange(trip.copy(home = home)) })
            }
        }
        if (trip.apartmentRequested) {
            Text(stringResource(Res.string.bk_apt_requested), style = GWPPATheme.typography.archivoBold13, color = colors.chlorineBlue)
        }

        StepLabel("04", stringResource(Res.string.bk_extras))
        InfoPanel {
            ToggleRow(stringResource(Res.string.bk_club_m), stringResource(Res.string.bk_club_m_sub), trip.clubMembership) { onChange(trip.copy(clubMembership = it)) }
            HorizontalDivider(color = colors.lineGrey)
            ToggleRow(stringResource(Res.string.bk_gym), stringResource(Res.string.bk_gym_sub), trip.gym) { onChange(trip.copy(gym = it)) }
            HorizontalDivider(color = colors.lineGrey)
            ToggleRow(stringResource(Res.string.bk_meals), stringResource(Res.string.bk_optional), trip.meals) { onChange(trip.copy(meals = it)) }
            HorizontalDivider(color = colors.lineGrey)
            ToggleRow(stringResource(Res.string.bk_activities), stringResource(Res.string.bk_optional), trip.activities) { onChange(trip.copy(activities = it)) }
            HorizontalDivider(color = colors.lineGrey)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 12.dp)) {
                Column(Modifier.weight(1f)) {
                    Text(stringResource(Res.string.bk_lessons), style = GWPPATheme.typography.archivoBold16, color = colors.inkBlack)
                    Text(
                        if (trip.lessons > 0) stringResource(Res.string.bk_lessons_week, trip.lessons) else stringResource(Res.string.bk_none),
                        style = GWPPATheme.typography.archivoRegular13,
                        color = colors.slateGrey
                    )
                }
                Stepper(
                    value = trip.lessons,
                    canDecrease = trip.lessons > 0,
                    canIncrease = trip.lessons < Trip.MAX_LESSONS,
                    onChange = { onChange(trip.copy(lessons = (trip.lessons + it).coerceIn(0, Trip.MAX_LESSONS))) }
                )
            }
        }

        StepLabel(null, stringResource(Res.string.bk_addons))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            AddOn.entries.forEach { addOn ->
                val on = addOn in trip.addOns
                ChoiceChip(
                    text = stringResource(addOnLabel(addOn)),
                    selected = on,
                    onClick = { onChange(trip.copy(addOns = if (on) trip.addOns - addOn else trip.addOns + addOn)) }
                )
            }
        }
    }
}

/** "01 Your club" (the prototype's `.bk-l` with its number). */
@Composable
private fun StepLabel(number: String?, title: String) {
    val colors = GWPPATheme.colors
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 14.dp)) {
        if (number != null) Text(number, style = GWPPATheme.typography.archivoExpandedBold12, color = colors.chlorineBlue)
        Text(title, style = GWPPATheme.typography.archivoExpandedBold17, color = colors.inkBlack)
    }
}

/** A club card in the builder's row (`.xc`): photo on top, crest and name under it. */
@Composable
private fun ClubChoice(
    name: String,
    image: @Composable BoxScope.() -> Unit,
    crest: @Composable () -> Unit,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = GWPPATheme.colors
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = Modifier
            .width(150.dp)
            .clip(shape)
            .background(colors.colorWhite)
            .border(if (selected) 2.dp else 1.dp, if (selected) colors.inkBlack else colors.lineGrey, shape)
            .clickable(onClick = onClick)
    ) {
        Box(Modifier.fillMaxWidth().height(78.dp).background(colors.nightNavy)) { image() }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(10.dp)) {
            crest()
            Text(name, style = GWPPATheme.typography.archivoBold13, color = colors.inkBlack, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun DateCell(label: String, value: String) {
    Column {
        Text(label, style = GWPPATheme.typography.archivoRegular13, color = GWPPATheme.colors.slateGrey)
        Text(value, style = GWPPATheme.typography.archivoBold16, color = GWPPATheme.colors.inkBlack)
    }
}

@Composable
private fun Radio(label: String, selected: Boolean, onClick: () -> Unit) {
    val colors = GWPPATheme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(22.dp).clip(CircleShape).border(1.5.dp, if (selected) colors.inkBlack else colors.mistGrey, CircleShape)
        ) {
            if (selected) Box(Modifier.size(12.dp).clip(CircleShape).background(colors.inkBlack))
        }
        Text(label, style = GWPPATheme.typography.archivoRegular15, color = colors.inkBlack)
    }
}

/** A row with an iOS-style switch (`.bk-tg`). */
@Composable
private fun ToggleRow(title: String, sub: String, on: Boolean, onToggle: (Boolean) -> Unit) {
    val colors = GWPPATheme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().clickable { onToggle(!on) }.padding(vertical = 12.dp)
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = GWPPATheme.typography.archivoBold16, color = colors.inkBlack)
            Text(sub, style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey)
        }
        Box(
            contentAlignment = if (on) Alignment.CenterEnd else Alignment.CenterStart,
            modifier = Modifier
                .size(width = 46.dp, height = 28.dp)
                .clip(CircleShape)
                .background(if (on) colors.chlorineBlue else colors.lineGrey)
                .padding(3.dp)
        ) {
            Box(Modifier.size(22.dp).clip(CircleShape).background(colors.colorWhite))
        }
    }
}

@Composable
private fun Stepper(value: Int, canDecrease: Boolean, canIncrease: Boolean, onChange: (Int) -> Unit) {
    val colors = GWPPATheme.colors
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        StepButton("−", enabled = canDecrease) { onChange(-1) }
        Text(value.toString(), style = GWPPATheme.typography.archivoBold16, color = colors.inkBlack)
        StepButton("+", enabled = canIncrease) { onChange(1) }
    }
}

@Composable
private fun StepButton(text: String, enabled: Boolean, onClick: () -> Unit) {
    val colors = GWPPATheme.colors
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(34.dp)
            .alpha(if (enabled) 1f else 0.3f)
            .clip(CircleShape)
            .border(1.5.dp, colors.lineGrey, CircleShape)
            .clickable(enabled = enabled, onClick = onClick)
    ) {
        Text(text, style = GWPPATheme.typography.archivoBold17, color = colors.inkBlack)
    }
}
