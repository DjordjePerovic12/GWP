package com.global_water_polo.play_abroad.presentation.onboarding.club

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.BackButton
import com.global_water_polo.play_abroad.core.components.BarStart
import com.global_water_polo.play_abroad.core.components.ButtonStyle
import com.global_water_polo.play_abroad.core.components.ChoiceChip
import com.global_water_polo.play_abroad.core.components.FormField
import com.global_water_polo.play_abroad.core.components.Headline
import com.global_water_polo.play_abroad.core.components.HelpText
import com.global_water_polo.play_abroad.core.components.OnboardingLayout
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.presentation.onboarding.FlowStep
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingState
import com.global_water_polo.play_abroad.presentation.onboarding.progress
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme
import kotlin.math.abs
import kotlin.math.round

// Latitude and longitude for each entry of the club_countries array, in the same order (the prototype's COUNTRIES).
private val COUNTRY_SPOTS = listOf(
    34.05f to -118.24f, 45.5f to -73.57f, -22.9f to -43.2f, -33.87f to 151.21f, 42.45f to 18.53f, 43.51f to 16.44f,
    44.82f to 20.46f, 47.5f to 19.04f, 37.98f to 23.73f, 41.9f to 12.5f, 41.39f to 2.17f, 43.3f to 5.37f,
    44.43f to 26.1f, 35.9f to 14.5f, 52.52f to 13.4f, 52.37f to 4.9f
)

/** Where a country from club_countries sits on the globe, or null when none is picked. */
fun countryLocation(countries: List<String>, country: String): Pair<Float, Float>? =
    COUNTRY_SPOTS.getOrNull(countries.indexOf(country))

/** "N 42.45°  E 18.53°", like the prototype's fmtCoord. */
fun formatCoordinates(latitude: Float, longitude: Float): String {
    fun two(v: Float): String {
        val hundredths = round(abs(v) * 100f).toInt()
        return "${hundredths / 100}.${(hundredths % 100).toString().padStart(2, '0')}"
    }
    return "${if (latitude >= 0) "N" else "S"} ${two(latitude)}°  ${if (longitude >= 0) "E" else "W"} ${two(longitude)}°"
}

/** Put your club on the map: name, city and country, then the globe flies there and pins it. */
@Composable
fun ClubPlaceScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    onNameChanged: (String) -> Unit,
    onCityChanged: (String) -> Unit,
    onCountryClick: (String) -> Unit,
    onPlaceClick: () -> Unit,
    onPlacedContinueClick: () -> Unit,
    onPlacedBackClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val countries = stringArrayResource(Res.array.club_countries)
    val spot = countryLocation(countries, state.clubCountry)
    if (state.clubPlaced && spot != null) {
        PlacedOverlay(
            state = state,
            coordinates = formatCoordinates(spot.first, spot.second),
            onContinueClick = onPlacedContinueClick,
            onBackClick = onPlacedBackClick,
            modifier = modifier
        )
        return
    }
    OnboardingLayout(
        modifier = modifier,
        barStart = { BarStart(onBackClick = onBackClick, progress = state.progress(FlowStep.PLACE)) },
        bottom = {
            PrimaryButton(
                text = stringResource(Res.string.club_place_cta),
                onClick = onPlaceClick,
                enabled = state.canPlaceClub,
                style = ButtonStyle.BALL
            )
        }
    ) {
        Headline(text = stringResource(Res.string.club_place_title))
        HelpText(text = stringResource(Res.string.club_place_help))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 24.dp)) {
            FormField(label = stringResource(Res.string.club_name), value = state.organization, onValueChange = onNameChanged, capitalization = KeyboardCapitalization.Words)
            FormField(label = stringResource(Res.string.club_city), value = state.clubCity, onValueChange = onCityChanged, capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Done)
        }
        Text(
            text = stringResource(Res.string.club_country),
            style = GWPPATheme.typography.archivoSemiBold13,
            color = GWPPATheme.colors.slateGrey,
            modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            countries.forEach { country -> ChoiceChip(text = country, selected = state.clubCountry == country, onClick = { onCountryClick(country) }) }
        }
    }
}

/** The night panel under the globe once the club is pinned: name, place, coordinates, Continue. */
@Composable
private fun PlacedOverlay(
    state: OnboardingState,
    coordinates: String,
    onContinueClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GWPPATheme.colors
    Box(modifier = modifier.fillMaxSize()) {
        BackButton(
            onClick = onBackClick,
            dark = true,
            modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars).padding(start = 24.dp, top = 12.dp)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Brush.verticalGradient(0f to Color.Transparent, 0.3f to colors.nightNavy.copy(alpha = 0.92f), 1f to colors.nightNavy))
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(start = 28.dp, end = 28.dp, top = 96.dp, bottom = 28.dp)
        ) {
            Text(stringResource(Res.string.club_place_kicker), style = GWPPATheme.typography.archivoBold13, color = colors.ballYellow)
            Text(
                text = state.organization.trim(),
                style = GWPPATheme.typography.archivoExpandedExtraBold30,
                color = colors.colorWhite,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = listOf(state.clubCity.trim(), state.clubCountry).filter { it.isNotEmpty() }.joinToString(", "),
                style = GWPPATheme.typography.archivoRegular16,
                color = colors.colorWhite.copy(alpha = 0.8f)
            )
            Text(coordinates, style = GWPPATheme.typography.archivoBold13, color = colors.colorWhite.copy(alpha = 0.7f))
            Text(
                text = stringResource(Res.string.club_place_approx),
                style = GWPPATheme.typography.archivoRegular13,
                color = colors.colorWhite.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
            PrimaryButton(
                text = stringResource(Res.string.common_continue),
                onClick = onContinueClick,
                style = ButtonStyle.BALL,
                modifier = Modifier.padding(top = 14.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ClubPlaceScreenContentPreview() {
    GWPPATheme {
        ClubPlaceScreenContent(
            state = OnboardingState(organization = "VK Primorac", clubCity = "Kotor", clubCountry = "Montenegro"),
            onNameChanged = {},
            onCityChanged = {},
            onCountryClick = {},
            onPlaceClick = {},
            onPlacedContinueClick = {},
            onPlacedBackClick = {},
            onBackClick = {}
        )
    }
}
