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
import androidx.compose.foundation.layout.size
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
import com.global_water_polo.play_abroad.core.components.MediaKind
import com.global_water_polo.play_abroad.core.components.rememberMediaPicker
import io.github.vinceglb.filekit.PlatformFile
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.graphics.Color
import com.global_water_polo.play_abroad.core.components.VideoPlayer
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
    onAnglePicked: (String, PlatformFile) -> Unit,
    onAngleRemove: (String) -> Unit,
    onSendClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = GWPPATheme.colors
    val (name, how) = ProfileState.LAB_TESTS[index]
    val angles = state.profile.labAngles[index].orEmpty()
    val labState = state.labState(index)
    val sent = labState == "wait"
    val picker = rememberMediaPicker(onAnglePicked)
    AppSheet(onDismiss = onDismiss) {
        Text(if (index < 4) "\"PART I\"" else "\"PART II\"", style = GWPPATheme.typography.archivoBold12, color = colors.eliteGold)
        Text(name, style = GWPPATheme.typography.archivoExpandedBold27, color = colors.inkBlack)
        Text(how, style = GWPPATheme.typography.archivoRegular15, color = colors.slateGrey)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 6.dp)) {
            listOf("above" to Res.string.lab_above, "under" to Res.string.lab_under).forEach { (angle, label) ->
                AngleSlot(
                    label = stringResource(label),
                    file = angles[angle],
                    enabled = !sent,
                    onClick = { picker.open(angle, MediaKind.VIDEO) },
                    onRemove = { onAngleRemove(angle) },
                    modifier = Modifier.weight(1f)
                )
            }
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

/**
 * One camera angle (the prototype's `.vs3`): "Add video" opens the video picker; once filmed the clip plays in
 * place, labelled, with Replace (and Remove) until the test is sent.
 */
@Composable
private fun AngleSlot(label: String, file: PlatformFile?, enabled: Boolean, onClick: () -> Unit, onRemove: () -> Unit, modifier: Modifier = Modifier) {
    val colors = GWPPATheme.colors
    val shape = RoundedCornerShape(16.dp)
    if (file != null) {
        Box(modifier = modifier.aspectRatio(3f / 4f).clip(shape).background(Color.Black).border(1.5.dp, colors.chlorineBlue, shape)) {
            VideoPlayer(file, Modifier.fillMaxSize())
            Text(
                label,
                style = GWPPATheme.typography.archivoBold11,
                color = colors.colorWhite,
                modifier = Modifier.align(Alignment.TopStart).padding(8.dp).clip(CircleShape).background(colors.nightNavy.copy(alpha = 0.6f)).padding(horizontal = 8.dp, vertical = 3.dp)
            )
            if (enabled) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp)) {
                    listOf(Res.string.pf_replace to onClick, Res.string.port_remove to onRemove).forEach { (text, action) ->
                        Text(
                            stringResource(text),
                            style = GWPPATheme.typography.archivoBold11,
                            color = colors.inkBlack,
                            modifier = Modifier.clip(CircleShape).background(colors.colorWhite).clickable(onClick = action).padding(horizontal = 9.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }
        return
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        modifier = modifier
            .aspectRatio(3f / 4f)
            .clip(shape)
            .background(colors.colorWhite)
            .border(1.5.dp, colors.lineGrey, shape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(10.dp)
    ) {
        Text("＋", style = GWPPATheme.typography.archivoExpandedBold21, color = colors.chlorineBlue)
        Text(label, style = GWPPATheme.typography.archivoBold13, color = colors.inkBlack)
        Text(stringResource(Res.string.lab_add_video), style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey)
    }
}
