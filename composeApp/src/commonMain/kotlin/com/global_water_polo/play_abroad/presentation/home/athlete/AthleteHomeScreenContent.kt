package com.global_water_polo.play_abroad.presentation.home.athlete

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.BrandBar
import com.global_water_polo.play_abroad.core.components.ClockChip
import com.global_water_polo.play_abroad.core.components.OfferCard
import com.global_water_polo.play_abroad.core.components.SectionTitle
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.domain.model.ProfileVisibility
import com.global_water_polo.play_abroad.presentation.home.athlete.components.FeedCard
import com.global_water_polo.play_abroad.presentation.home.athlete.components.TodoRow
import com.global_water_polo.play_abroad.presentation.home.athlete.components.VisibilitySwitch
import com.global_water_polo.play_abroad.presentation.home.components.ProfileCard
import com.global_water_polo.play_abroad.presentation.onboarding.travel.travelMonths
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

@Composable
fun AthleteHomeScreenContent(
    modifier: Modifier = Modifier,
    state: AthleteHomeState,
    onVisibilityClick: (ProfileVisibility) -> Unit,
    onTodoClick: (String) -> Unit,
    onReviewClick: () -> Unit,
    onBookSessionClick: () -> Unit,
    onPlanAbroadClick: () -> Unit
) {
    val colors = GWPPATheme.colors
    val typography = GWPPATheme.typography
    val positionTag = if (state.position.isEmpty()) null else state.position + if (state.leftHanded) ", LH" else ""
    val todos = listOf(
        Triple("pos", stringResource(Res.string.home_todo_position), positionTag ?: stringResource(Res.string.home_todo_position_sub)),
        Triple("club", stringResource(Res.string.home_todo_club), stringResource(Res.string.home_todo_club_sub)),
        Triple("body", stringResource(Res.string.home_todo_body), stringResource(Res.string.home_todo_body_sub)),
        Triple("school", stringResource(Res.string.home_todo_school), stringResource(Res.string.home_todo_school_sub)),
        Triple("goals", stringResource(Res.string.home_todo_goals), stringResource(Res.string.home_todo_goals_sub))
    )
    val months = travelMonths()
    val travel = state.travelStartOffset?.takeIf { state.travelMonths > 0 }?.let { offset ->
        stringResource(Res.string.home_plan_abroad_dates, months.getOrNull(offset)?.second.orEmpty(), state.travelMonths)
    }
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
            ClockChip(countdown = state.countdown, clockYear = state.window?.clock?.year, compact = true)
        }
        Text(text = stringResource(Res.string.home_hello, state.firstName), style = typography.archivoExpandedBold27, color = colors.inkBlack)
        SmallText(text = stringResource(Res.string.home_how_coaches_see))
        ProfileCard(
            name = "${state.firstName} ${state.lastName}",
            meta = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = state.position.ifEmpty { stringResource(Res.string.home_position_missing) },
                        style = typography.archivoRegular14,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    if (state.leftHanded) {
                        Text(
                            text = "LH",
                            style = typography.archivoExpandedExtraBold13,
                            color = colors.inkBlack,
                            modifier = Modifier.padding(start = 6.dp).background(colors.ballYellow, RoundedCornerShape(5.dp)).padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            },
            stats = listOf(
                stringResource(Res.string.home_stat_clock) to stringResource(Res.string.window_fall, state.window?.clock?.year ?: 0),
                stringResource(Res.string.home_stat_seasons) to stringResource(Res.string.home_of_five, state.window?.seasonsStraight ?: 5),
                stringResource(Res.string.home_stat_review) to stringResource(if (state.reviewSubmitted) Res.string.home_review_in else Res.string.home_review_not_yet)
            )
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(text = stringResource(Res.string.home_profile), style = typography.archivoSemiBold14, color = colors.colorWhite)
                    Text(text = "${state.profilePercent}%", style = typography.archivoSemiBold14, color = colors.colorWhite)
                }
                Box(Modifier.fillMaxWidth().height(6.dp).background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(3.dp))) {
                    Box(Modifier.fillMaxHeight().fillMaxWidth(state.profilePercent / 100f).background(colors.ballYellow, RoundedCornerShape(3.dp)))
                }
            }
        }
        VisibilitySwitch(
            options = listOf(
                ProfileVisibility.ALL to stringResource(Res.string.home_visibility_all),
                ProfileVisibility.COACHES to stringResource(Res.string.home_visibility_coaches),
                ProfileVisibility.PRIVATE to stringResource(Res.string.home_visibility_private)
            ),
            selected = state.visibility,
            onClick = onVisibilityClick
        )
        SectionTitle(title = stringResource(Res.string.home_finish_profile), note = stringResource(Res.string.home_n_of_m, state.doneCount, todos.size))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            todos.forEach { (key, title, sub) ->
                TodoRow(title = title, subtitle = sub, done = state.isDone(key), onClick = { onTodoClick(key) })
            }
        }
        SectionTitle(title = stringResource(Res.string.home_feed), note = stringResource(Res.string.home_feed_note))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
            val members = stringResource(Res.string.home_members)
            FeedCard(stringResource(Res.string.home_feed_1), stringResource(Res.string.home_feed_1_meta), colors.chlorineBlue, members, Modifier.width(250.dp))
            FeedCard(stringResource(Res.string.home_feed_2), stringResource(Res.string.home_feed_2_meta), colors.poolTeal, members, Modifier.width(250.dp))
            FeedCard(stringResource(Res.string.home_feed_3), stringResource(Res.string.home_feed_3_meta), colors.graphiteGrey, null, Modifier.width(250.dp))
        }
        SectionTitle(title = stringResource(Res.string.home_go_further), note = stringResource(Res.string.home_go_further_note))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 24.dp)) {
            OfferCard(
                title = stringResource(if (state.reviewSubmitted) Res.string.home_your_review else Res.string.home_apply_review),
                subtitle = stringResource(if (state.reviewSubmitted) Res.string.home_review_reach_out else Res.string.home_apply_review_sub),
                badge = stringResource(if (state.reviewSubmitted) Res.string.home_review_in else Res.string.home_free_month),
                hero = true,
                onClick = onReviewClick
            )
            OfferCard(
                title = stringResource(Res.string.home_book_session),
                subtitle = stringResource(Res.string.home_book_session_sub),
                badge = stringResource(Res.string.home_free_month),
                onClick = onBookSessionClick
            )
            OfferCard(
                title = stringResource(Res.string.home_plan_abroad),
                subtitle = travel ?: stringResource(Res.string.home_plan_abroad_sub),
                badge = stringResource(Res.string.home_free_month),
                onClick = onPlanAbroadClick
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AthleteHomeScreenContentPreview() {
    GWPPATheme {
        AthleteHomeScreenContent(
            state = AthleteHomeState(firstName = "Alex", lastName = "Rivera", position = "Center", leftHanded = true),
            onVisibilityClick = {},
            onTodoClick = {},
            onReviewClick = {},
            onBookSessionClick = {},
            onPlanAbroadClick = {}
        )
    }
}
