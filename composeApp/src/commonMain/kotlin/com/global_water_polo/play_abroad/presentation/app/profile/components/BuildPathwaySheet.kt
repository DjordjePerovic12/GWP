package com.global_water_polo.play_abroad.presentation.app.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.AppSheet
import com.global_water_polo.play_abroad.core.components.ChoiceChip
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.SheetHeading
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.core.utils.NcaaClockCalculator
import com.global_water_polo.play_abroad.presentation.app.profile.ProfileState
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

private val GOALS = listOf("college" to Res.string.goal_college, "pro" to Res.string.goal_pro, "nt" to Res.string.goal_nt, "oly" to Res.string.goal_oly)
private val LEVELS = listOf("hs" to Res.string.level_hs, "club" to Res.string.level_club, "odp" to Res.string.level_odp, "college" to Res.string.level_college, "senior" to Res.string.level_senior)
private val EDUCATION = listOf("hs" to Res.string.build_edu_hs, "grad" to Res.string.build_edu_grad, "college" to Res.string.build_edu_college)
private val TIMING = listOf(
    "weeks" to Res.string.build_when_weeks,
    "summer" to Res.string.build_when_summer,
    "semester" to Res.string.build_when_semester,
    "year" to Res.string.build_when_year,
    "unsure" to Res.string.build_when_unsure
)
private val DESTINATIONS = listOf("eu" to Res.string.build_dest_eu, "am" to Res.string.build_dest_am, "world" to Res.string.build_dest_world)

/**
 * "Build my pathway": goal, level, education, how long and where, prefilled from onboarding.
 * "Not sure yet" and "Anywhere" stand alone in their groups, like the prototype.
 */
@Composable
fun BuildPathwaySheet(
    state: ProfileState,
    onBuild: (goal: String, level: String, education: String, timing: Set<String>, destinations: Set<String>) -> Unit,
    onDismiss: () -> Unit
) {
    val p = state.profile
    val saved = p.pathway
    var goal by remember { mutableStateOf(saved?.goal ?: p.goal) }
    var level by remember { mutableStateOf(saved?.level ?: p.level.firstOrNull().orEmpty()) }
    var education by remember {
        mutableStateOf(
            saved?.education ?: when {
                "college" in p.now -> "college"
                p.gradYear != null && p.gradYear < NcaaClockCalculator.today().year -> "grad"
                else -> "hs"
            }
        )
    }
    var timing by remember { mutableStateOf(saved?.timing ?: emptySet()) }
    var destinations by remember { mutableStateOf(saved?.destinations ?: setOf("eu")) }

    AppSheet(onDismiss = onDismiss) {
        Text(stringResource(Res.string.path_kicker), style = GWPPATheme.typography.archivoBold12, color = GWPPATheme.colors.eliteGold)
        Text(stringResource(Res.string.build_title), style = GWPPATheme.typography.archivoExpandedBold27, color = GWPPATheme.colors.inkBlack)
        SmallText(text = stringResource(Res.string.build_from))
        ChipGroup(stringResource(Res.string.build_goal), GOALS, setOf(goal)) { goal = it }
        ChipGroup(stringResource(Res.string.build_level), LEVELS, setOf(level)) { level = it }
        ChipGroup(stringResource(Res.string.build_edu), EDUCATION, setOf(education)) { education = it }
        ChipGroup(stringResource(Res.string.build_when), TIMING, timing) { timing = toggle(timing, it, alone = "unsure") }
        ChipGroup(stringResource(Res.string.build_dest), DESTINATIONS, destinations) { destinations = toggle(destinations, it, alone = "world") }
        PrimaryButton(
            text = stringResource(Res.string.build_cta),
            onClick = { onBuild(goal, level, education, timing, destinations) },
            enabled = goal.isNotEmpty() && timing.isNotEmpty(),
            modifier = Modifier.padding(top = 12.dp)
        )
        SmallText(text = stringResource(Res.string.build_fine))
    }
}

private fun toggle(current: Set<String>, key: String, alone: String): Set<String> = when {
    key == alone -> if (key in current) emptySet() else setOf(key)
    key in current -> current - key
    else -> current - alone + key
}

@Composable
private fun ChipGroup(label: String, options: List<Pair<String, StringResource>>, selected: Set<String>, onClick: (String) -> Unit) {
    SheetHeading(label)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { (key, text) -> ChoiceChip(text = stringResource(text), selected = key in selected, onClick = { onClick(key) }) }
    }
}
