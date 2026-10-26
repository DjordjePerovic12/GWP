package com.global_water_polo.play_abroad.presentation.app.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.AppSheet
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.SheetHeading
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.presentation.app.profile.ProfileState
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/**
 * One GWP LAB test (the prototype's labTest): both camera angles, then the places the coach's feedback,
 * priorities, retest and side-by-side will appear. Sending needs both angles and GWP Elite.
 */
@Composable
fun LabTestSheet(
    index: Int,
    state: ProfileState,
    onAngleClick: (String) -> Unit,
    onSendClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = GWPPATheme.colors
    val (name, how) = ProfileState.LAB_TESTS[index]
    val angles = state.profile.labAngles[index].orEmpty()
    val labState = state.labState(index)
    val sent = labState == "wait"
    AppSheet(onDismiss = onDismiss) {
        Text(if (index < 4) "\"PART I\"" else "\"PART II\"", style = GWPPATheme.typography.archivoBold12, color = colors.eliteGold)
        Text(name, style = GWPPATheme.typography.archivoExpandedBold27, color = colors.inkBlack)
        Text(how, style = GWPPATheme.typography.archivoRegular15, color = colors.slateGrey)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 6.dp)) {
            AngleSlot(stringResource(Res.string.lab_above), "above" in angles, enabled = !sent, onClick = { onAngleClick("above") }, modifier = Modifier.weight(1f))
            AngleSlot(stringResource(Res.string.lab_under), "under" in angles, enabled = !sent, onClick = { onAngleClick("under") }, modifier = Modifier.weight(1f))
        }
        SheetHeading(stringResource(Res.string.lab_feedback))
        SmallText(text = stringResource(if (sent) Res.string.lab_feedback_sent else Res.string.lab_feedback_empty))
        SheetHeading(stringResource(Res.string.lab_priorities))
        SmallText(text = stringResource(Res.string.lab_priorities_empty))
        SheetHeading(stringResource(Res.string.lab_retest))
        SmallText(text = stringResource(Res.string.lab_retest_empty))
        SheetHeading(stringResource(Res.string.lab_sbs))
        SmallText(text = stringResource(Res.string.lab_sbs_empty))
        PrimaryButton(
            text = stringResource(
                when (labState) {
                    "wait" -> Res.string.lab_sent
                    "ready" -> Res.string.lab_send
                    else -> Res.string.lab_send_both
                }
            ),
            onClick = onSendClick,
            enabled = labState == "ready",
            modifier = Modifier.padding(top = 14.dp)
        )
        SmallText(text = stringResource(Res.string.lab_proto), center = true, modifier = Modifier.fillMaxWidth())
    }
}

/** One camera angle: "Add video" until it is filmed. */
@Composable
private fun AngleSlot(label: String, filmed: Boolean, enabled: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = GWPPATheme.colors
    val shape = RoundedCornerShape(16.dp)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        modifier = modifier
            .height(120.dp)
            .clip(shape)
            .background(if (filmed) colors.nightNavy else colors.colorWhite)
            .border(1.5.dp, if (filmed) colors.chlorineBlue else colors.lineGrey, shape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(10.dp)
    ) {
        Text(if (filmed) "▶" else "＋", style = GWPPATheme.typography.archivoExpandedBold21, color = if (filmed) colors.colorWhite else colors.chlorineBlue)
        Text(label, style = GWPPATheme.typography.archivoBold13, color = if (filmed) colors.colorWhite else colors.inkBlack)
        Text(
            stringResource(if (filmed) Res.string.lab_added else Res.string.lab_add_video),
            style = GWPPATheme.typography.archivoRegular13,
            color = if (filmed) colors.colorWhite.copy(alpha = 0.7f) else colors.slateGrey
        )
    }
}
