package com.global_water_polo.play_abroad.presentation.app.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.AppSheet
import com.global_water_polo.play_abroad.core.components.ChoiceChip
import com.global_water_polo.play_abroad.core.components.FormField
import com.global_water_polo.play_abroad.core.components.InfoPanel
import com.global_water_polo.play_abroad.core.components.InfoRow
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.SheetHeading
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.domain.model.Program
import com.global_water_polo.play_abroad.domain.model.WaterPoloMap
import com.global_water_polo.play_abroad.domain.model.details
import com.global_water_polo.play_abroad.presentation.app.profile.ProfileState
import com.global_water_polo.play_abroad.presentation.onboarding.match.components.title
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/**
 * The inquiry (or Gap Year application) for a program, built from the profile: who, class, clock, goal,
 * level and timing, the cities they'd consider and a note. Prototype: nothing is sent.
 */
@Composable
fun InquirySheet(
    program: Program,
    sent: Boolean,
    state: ProfileState,
    onSendClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = GWPPATheme.colors
    val d = program.details
    val p = state.profile
    val name = stringResource(program.title)
    var picked by remember { mutableStateOf(d.destinations.take(2).toSet()) }
    var message by remember { mutableStateOf("") }
    AppSheet(onDismiss = onDismiss) {
        if (sent) {
            Text("\"SENT\"", style = GWPPATheme.typography.archivoBold12, color = colors.eliteGold)
            Text(
                stringResource(if (d.application) Res.string.inq_sent_app else Res.string.inq_sent),
                style = GWPPATheme.typography.archivoExpandedBold27,
                color = colors.inkBlack
            )
            Text(stringResource(Res.string.inq_sent_help, name), style = GWPPATheme.typography.archivoRegular15, color = colors.slateGrey)
            PrimaryButton(text = stringResource(Res.string.inq_done), onClick = onDismiss, modifier = Modifier.padding(top = 12.dp))
            SmallText(text = stringResource(Res.string.elite_proto))
            return@AppSheet
        }
        Text(stringResource(if (d.application) Res.string.inq_kicker_app else Res.string.inq_kicker), style = GWPPATheme.typography.archivoBold12, color = colors.eliteGold)
        Text(name, style = GWPPATheme.typography.archivoExpandedBold27, color = colors.inkBlack)
        Text(stringResource(Res.string.inq_help), style = GWPPATheme.typography.archivoRegular15, color = colors.slateGrey)
        val step = p.pathway?.steps?.firstOrNull { it.program == program }
        InfoPanel(modifier = Modifier.padding(top = 6.dp)) {
            InfoRow(stringResource(Res.string.inq_athlete), listOf(p.firstName, p.lastName).filter { it.isNotBlank() }.joinToString(" ").ifEmpty { "-" })
            HorizontalDivider(color = colors.lineGrey)
            InfoRow(stringResource(Res.string.inq_class), p.gradYear?.toString() ?: stringResource(Res.string.not_added))
            HorizontalDivider(color = colors.lineGrey)
            InfoRow(stringResource(Res.string.inq_clock), state.clockYear?.let { stringResource(Res.string.inq_clock_value, it) } ?: stringResource(Res.string.match_not_set))
            HorizontalDivider(color = colors.lineGrey)
            InfoRow(stringResource(Res.string.inq_goal), goalLabel(p.goal))
            HorizontalDivider(color = colors.lineGrey)
            InfoRow(stringResource(Res.string.inq_timing), step?.let { timingLabel(it) } ?: d.duration)
        }
        SheetHeading(stringResource(Res.string.inq_dest))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            d.destinations.forEach { id ->
                val city = WaterPoloMap.city(id) ?: return@forEach
                ChoiceChip(
                    text = "${city.flag} ${city.name}",
                    selected = id in picked,
                    onClick = { picked = if (id in picked) picked - id else picked + id }
                )
            }
        }
        FormField(
            label = stringResource(Res.string.inq_msg),
            value = message,
            onValueChange = { message = it },
            placeholder = stringResource(Res.string.inq_msg_hint),
            modifier = Modifier.padding(top = 10.dp)
        )
        PrimaryButton(
            text = stringResource(if (d.application) Res.string.inq_send_app else Res.string.inq_send),
            onClick = onSendClick,
            modifier = Modifier.padding(top = 12.dp)
        )
        SmallText(text = stringResource(Res.string.inq_fine))
    }
}

@Composable
private fun goalLabel(goal: String): String = when (goal) {
    "college" -> stringResource(Res.string.goal_college)
    "pro" -> stringResource(Res.string.goal_pro)
    "nt" -> stringResource(Res.string.goal_nt)
    "oly" -> stringResource(Res.string.goal_oly)
    else -> stringResource(Res.string.match_not_set)
}
