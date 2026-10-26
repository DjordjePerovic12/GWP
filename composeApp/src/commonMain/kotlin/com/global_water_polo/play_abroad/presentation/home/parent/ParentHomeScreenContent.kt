package com.global_water_polo.play_abroad.presentation.home.parent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.utils.PathwayCalculator
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.pluralStringResource
import kotlinx.datetime.number
import com.global_water_polo.play_abroad.core.components.BrandBar
import com.global_water_polo.play_abroad.core.components.ClockChip
import com.global_water_polo.play_abroad.core.components.OfferCard
import com.global_water_polo.play_abroad.core.components.SectionTitle
import com.global_water_polo.play_abroad.presentation.home.components.ProfileCard
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

@Composable
fun ParentHomeScreenContent(
    modifier: Modifier = Modifier,
    state: ParentHomeState,
    onCompareClick: () -> Unit,
    onBookCallClick: () -> Unit,
    onInviteClick: () -> Unit
) {
    val colors = GWPPATheme.colors
    val w = state.window
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)
        ) {
            BrandBar(short = true)
            ClockChip(countdown = state.countdown, clockYear = w?.clock?.year, compact = true)
        }
        Text(
            text = stringResource(Res.string.home_hello, state.firstName.ifBlank { stringResource(Res.string.home_there) }),
            style = GWPPATheme.typography.archivoExpandedBold27,
            color = colors.inkBlack
        )
        ProfileCard(
            name = state.kidName.ifBlank { stringResource(Res.string.parent_your_athlete) },
            meta = {
                Text(
                    text = stringResource(if (state.invited) Res.string.parent_invite_sent else Res.string.parent_not_invited),
                    style = GWPPATheme.typography.archivoRegular14,
                    color = Color.White.copy(alpha = 0.7f)
                )
            },
            // A team-trip parent never gave a birthday: the card shows the trip they asked about instead of a clock.
            stats = if (w == null && state.tripStartOffset != null) listOf(
                stringResource(Res.string.parent_trip_card) to stringResource(Res.string.parent_trip_sent),
                stringResource(Res.string.match_earliest) to tripStartLabel(state.tripStartOffset),
                stringResource(Res.string.tripplan_length) to tripLengthLabel(state.tripMonths)
            ) else listOf(
                stringResource(Res.string.home_stat_clock) to (w?.let { stringResource(Res.string.window_fall, it.clock.year) } ?: "-"),
                stringResource(Res.string.parent_straight) to (w?.let { stringResource(Res.string.home_of_five, it.seasonsStraight) } ?: "-"),
                stringResource(Res.string.parent_abroad_first) to (w?.let { stringResource(Res.string.home_of_five, it.seasonsAbroad) } ?: "-")
            )
        )
        SectionTitle(title = stringResource(Res.string.parent_play_abroad))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OfferCard(
                title = stringResource(Res.string.parent_compare),
                subtitle = stringResource(Res.string.parent_compare_sub),
                badge = stringResource(Res.string.home_free_month),
                hero = true,
                onClick = onCompareClick
            )
            OfferCard(
                title = stringResource(Res.string.parent_book_call),
                subtitle = stringResource(Res.string.parent_book_call_sub),
                badge = stringResource(Res.string.home_free_month),
                onClick = onBookCallClick
            )
        }
        if (!state.invited) {
            SectionTitle(title = stringResource(Res.string.parent_next_step))
            OfferCard(
                title = stringResource(Res.string.parent_invite),
                subtitle = stringResource(Res.string.parent_invite_sub),
                onClick = onInviteClick
            )
        }
    }
}

@Composable
private fun tripStartLabel(offset: Int): String {
    val start = PathwayCalculator.monthAt(offset)
    return "${stringArrayResource(Res.array.months_short)[start.month.number - 1]} ${start.year}"
}

@Composable
private fun tripLengthLabel(months: Int): String = when {
    months <= 0 -> "-"
    months >= 10 -> stringResource(Res.string.tripplan_full_season)
    else -> pluralStringResource(Res.plurals.travel_months, months, months)
}

@Preview(showBackground = true)
@Composable
private fun ParentHomeScreenContentPreview() {
    GWPPATheme {
        ParentHomeScreenContent(state = ParentHomeState(firstName = "Sam", kidName = "Alex"), onCompareClick = {}, onBookCallClick = {}, onInviteClick = {})
    }
}
