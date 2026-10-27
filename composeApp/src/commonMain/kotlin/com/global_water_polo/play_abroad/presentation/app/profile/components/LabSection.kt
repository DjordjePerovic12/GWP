package com.global_water_polo.play_abroad.presentation.app.profile.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.em
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.InfoPanel
import com.global_water_polo.play_abroad.core.components.InfoRow
import com.global_water_polo.play_abroad.core.components.PhotoHero
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.presentation.app.profile.ProfileState
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/**
 * GWP LAB, Underwater Insight (the prototype's labDash): eight tests filmed above and below the water,
 * reviewed by a GWP coach. No scores and no automatic measurement.
 */
@Composable
fun LabSection(
    state: ProfileState,
    onTestClick: (Int) -> Unit,
    onExampleClick: () -> Unit
) {
    val colors = GWPPATheme.colors
    val sent = state.profile.labSent
    PhotoHero(
        image = Res.drawable.f_under,
        kicker = stringResource(Res.string.lab_kicker),
        title = stringResource(Res.string.lab_title),
        sub = stringResource(Res.string.lab_sub),
        height = 260.dp
    )
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 12.dp).fillMaxWidth()) {
        Stat("${state.labFilmed}/${ProfileState.LAB_TESTS.size}", stringResource(Res.string.lab_filmed), Modifier.weight(1f))
        Stat(sent.size.toString(), stringResource(Res.string.lab_with_coach), Modifier.weight(1f))
        Stat("0", stringResource(Res.string.lab_reviewed), Modifier.weight(1f))
    }
    // `.lab-how`: four columns, each under a grey rule, a gold 01-04 and the step.
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth().padding(top = 14.dp)) {
        listOf(Res.string.lab_how_1, Res.string.lab_how_2, Res.string.lab_how_3, Res.string.lab_how_4).forEachIndexed { i, step ->
            Column(Modifier.weight(1f)) {
                Box(Modifier.fillMaxWidth().height(2.dp).background(colors.lineGrey))
                Text(
                    "0${i + 1}",
                    style = GWPPATheme.typography.archivoBold12.copy(fontWeight = FontWeight.ExtraBold),
                    color = colors.antiqueGold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                )
                Text(stringResource(step), style = GWPPATheme.typography.archivoRegular13.copy(fontSize = 12.sp, lineHeight = 1.3.em), color = colors.slateGrey)
            }
        }
    }
    SectionHeader(title = stringResource(Res.string.lab_part_1), note = stringResource(Res.string.lab_tests_4))
    TestList(state, 0..3, onTestClick)
    SectionHeader(title = stringResource(Res.string.lab_part_2), note = stringResource(Res.string.lab_tests_4))
    TestList(state, 4..7, onTestClick)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .padding(top = 14.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colors.colorWhite)
            .border(1.dp, colors.lineGrey, RoundedCornerShape(18.dp))
            .clickable(onClick = onExampleClick)
            .padding(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Image(painterResource(Res.drawable.f_under), null, contentScale = ContentScale.Crop, modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)))
            Image(painterResource(Res.drawable.f_shot), null, contentScale = ContentScale.Crop, modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)))
        }
        Column(Modifier.weight(1f)) {
            Text(stringResource(Res.string.lab_example), style = GWPPATheme.typography.archivoBold16, color = colors.inkBlack)
            Text(stringResource(Res.string.lab_example_sub), style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey)
        }
        Text("›", style = GWPPATheme.typography.archivoExpandedBold21, color = colors.mistGrey)
    }
    SectionHeader(
        title = stringResource(Res.string.lab_history),
        note = if (sent.isEmpty()) "" else stringResource(Res.string.lab_history_sent, sent.size)
    )
    if (sent.isEmpty()) {
        SmallText(text = stringResource(Res.string.lab_history_empty))
    } else {
        val months = stringArrayResource(Res.array.months)
        InfoPanel {
            sent.sorted().forEachIndexed { i, test ->
                if (i > 0) HorizontalDivider(color = colors.lineGrey)
                // "Sent October 27, 2026"; tests sent before dates were kept just say they were sent.
                val on = state.profile.labSentOn[test]
                InfoRow(
                    ProfileState.LAB_TESTS[test].first,
                    on?.let { stringResource(Res.string.lab_sent_on, "${months[it.month.ordinal]} ${it.day}, ${it.year}") }
                        ?: stringResource(Res.string.lab_sent_row)
                )
            }
        }
    }
    SmallText(
        text = stringResource(if (state.membership.elite) Res.string.lab_fine_elite else Res.string.lab_fine),
        modifier = Modifier.padding(top = 10.dp)
    )
}

@Composable
private fun Stat(value: String, label: String, modifier: Modifier = Modifier) {
    val colors = GWPPATheme.colors
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clip(RoundedCornerShape(14.dp)).background(colors.colorWhite).padding(vertical = 12.dp)
    ) {
        Text(value, style = GWPPATheme.typography.archivoExpandedBold21, color = colors.inkBlack)
        Text(label, style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey, textAlign = TextAlign.Center)
    }
}

@Composable
private fun TestList(state: ProfileState, range: IntRange, onTestClick: (Int) -> Unit) {
    val colors = GWPPATheme.colors
    InfoPanel {
        range.forEach { i ->
            if (i != range.first) HorizontalDivider(color = colors.lineGrey)
            val labState = state.labState(i)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().clickable { onTestClick(i) }.padding(vertical = 12.dp)
            ) {
                Text((i + 1).toString().padStart(2, '0'), style = GWPPATheme.typography.archivoBold12, color = colors.mistGrey, modifier = Modifier.width(30.dp))
                Column(Modifier.weight(1f)) {
                    Text(ProfileState.LAB_TESTS[i].first, style = GWPPATheme.typography.archivoBold16, color = colors.inkBlack)
                    Text(labStateLabel(labState), style = GWPPATheme.typography.archivoRegular13, color = labStateColor(labState))
                }
                Text("›", style = GWPPATheme.typography.archivoExpandedBold21, color = colors.mistGrey)
            }
        }
    }
}

@Composable
fun labStateLabel(state: String): String = stringResource(
    when (state) {
        "wait" -> Res.string.lab_state_wait
        "ready" -> Res.string.lab_state_ready
        "part" -> Res.string.lab_state_part
        else -> Res.string.lab_state_none
    }
)

@Composable
private fun labStateColor(state: String): Color = when (state) {
    "wait" -> GWPPATheme.colors.eliteGold
    "ready" -> GWPPATheme.colors.chlorineBlue
    "part" -> GWPPATheme.colors.poolTeal
    else -> GWPPATheme.colors.slateGrey
}
