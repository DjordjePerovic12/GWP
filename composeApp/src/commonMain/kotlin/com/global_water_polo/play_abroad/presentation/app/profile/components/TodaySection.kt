package com.global_water_polo.play_abroad.presentation.app.profile.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.AppSheet
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** One block of the day: time, what, and a line on it. */
data class DayBlock(val time: String, val title: String, val detail: String)

/** One exercise of the workout of the day, with its sets. */
data class Exercise(val name: String, val sets: String)

/** The prototype's sample day and workout. TODO: Sample plan until the athlete's GWP performance coach builds the real one. */
val TODAY = listOf(
    DayBlock("06:30", "Swim", "Aerobic set, 3,000 m"),
    DayBlock("09:00", "Strength", "Lower body and core, 50 min"),
    DayBlock("16:30", "Team practice", "Club session, 2 hours"),
    DayBlock("20:00", "Recovery", "Mobility, lights out by 22:30")
)

val WORKOUT = listOf(
    Exercise("Goblet squat", "4 × 8"),
    Exercise("Single-leg RDL", "3 × 8 each"),
    Exercise("Weighted eggbeater hold", "6 × 30 s"),
    Exercise("Med-ball rotational throw", "3 × 10 each"),
    Exercise("Dead bug", "3 × 12")
)

/** The block shown as now, like the prototype (its second one). */
private const val NOW = 1

/** The "Today" row under the membership card (the prototype's todayMini), for GWP Elite. */
@Composable
fun TodayRow(done: Set<Int>, onClick: () -> Unit) {
    val colors = GWPPATheme.colors
    val now = TODAY[NOW]
    val shape = RoundedCornerShape(14.dp)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
            .clip(shape)
            .background(colors.colorWhite)
            .border(1.dp, colors.lineGrey, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Text(stringResource(Res.string.today_label).uppercase(), style = GWPPATheme.typography.archivoBold11, color = colors.eliteGold)
        Column(Modifier.weight(1f)) {
            Text("${now.time} ${now.title}", style = GWPPATheme.typography.archivoBold13, color = colors.inkBlack)
            Text(now.detail, style = GWPPATheme.typography.archivoRegular13, color = colors.mistGrey)
        }
        Text("${done.size}/${WORKOUT.size}", style = GWPPATheme.typography.archivoBold13, color = colors.slateGrey)
        Text("›", style = GWPPATheme.typography.archivoBold16, color = colors.mistGrey)
    }
}

/**
 * Your day (the prototype's todaySheet): the schedule with the current block marked, then the workout of the
 * day to tick off as it is done.
 */
@Composable
fun TodaySheet(done: Set<Int>, onToggle: (Int) -> Unit, onDismiss: () -> Unit) {
    val colors = GWPPATheme.colors
    AppSheet(onDismiss = onDismiss) {
        Text("\"${stringResource(Res.string.today_label).uppercase()}\"", style = GWPPATheme.typography.archivoBold12, color = colors.eliteGold)
        Text(stringResource(Res.string.today_title), style = GWPPATheme.typography.archivoExpandedBold27, color = colors.inkBlack)
        Column(
            modifier = Modifier
                .padding(top = 6.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(colors.colorWhite)
                .border(1.dp, colors.lineGrey, RoundedCornerShape(18.dp))
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            TODAY.forEachIndexed { i, block ->
                if (i > 0) HorizontalDivider(color = colors.lineGrey)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(vertical = 10.dp)) {
                    Text(block.time, style = GWPPATheme.typography.archivoBold13, color = colors.slateGrey, modifier = Modifier.width(48.dp))
                    val now = i == NOW
                    Box(
                        Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (now) colors.chlorineBlue else Color.Transparent)
                            .border(2.dp, if (now) colors.chlorineBlue else colors.mistGrey, CircleShape)
                    )
                    Column {
                        Text(block.title, style = GWPPATheme.typography.archivoBold16, color = colors.inkBlack)
                        Text(block.detail, style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey)
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .padding(top = 6.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(colors.nightNavy)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 6.dp)) {
                Column(Modifier.weight(1f)) {
                    Text(stringResource(Res.string.today_wod), style = GWPPATheme.typography.archivoBold16, color = colors.colorWhite)
                    Text(stringResource(Res.string.today_wod_sub), style = GWPPATheme.typography.archivoRegular13, color = colors.colorWhite.copy(alpha = 0.6f))
                }
                Text("${done.size}/${WORKOUT.size}", style = GWPPATheme.typography.archivoExpandedBold16, color = colors.ballYellow)
            }
            WORKOUT.forEachIndexed { i, exercise ->
                val on = i in done
                HorizontalDivider(color = colors.colorWhite.copy(alpha = 0.1f))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth().clickable { onToggle(i) }.padding(vertical = 11.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(if (on) colors.ballYellow else Color.Transparent)
                            .border(2.dp, if (on) colors.ballYellow else colors.colorWhite.copy(alpha = 0.35f), CircleShape)
                    ) {
                        if (on) Text("✓", style = GWPPATheme.typography.archivoBold11, color = colors.nightNavy)
                    }
                    Text(
                        exercise.name,
                        style = GWPPATheme.typography.archivoRegular15.copy(textDecoration = if (on) TextDecoration.LineThrough else null),
                        color = if (on) colors.colorWhite.copy(alpha = 0.5f) else colors.colorWhite,
                        modifier = Modifier.weight(1f)
                    )
                    Text(exercise.sets, style = GWPPATheme.typography.archivoBold13, color = colors.colorWhite.copy(alpha = 0.7f))
                }
            }
            Text(stringResource(Res.string.today_fine), style = GWPPATheme.typography.archivoRegular13, color = colors.colorWhite.copy(alpha = 0.5f), modifier = Modifier.padding(top = 8.dp))
        }
    }
}
