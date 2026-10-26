package com.global_water_polo.play_abroad.presentation.app.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.AppSheet
import com.global_water_polo.play_abroad.core.components.ChoiceChip
import com.global_water_polo.play_abroad.core.components.FormField
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.core.components.TextLink
import com.global_water_polo.play_abroad.domain.model.InfoGroup
import com.global_water_polo.play_abroad.domain.model.POSITIONS
import com.global_water_polo.play_abroad.domain.model.PersonalInfo
import com.global_water_polo.play_abroad.domain.model.Profile
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/**
 * The prototype's profile editors in one sheet: positions (with Left-handed) or one group of personal
 * info (body and swim, club and coach, hometown and school, academics). Save keeps it; Skip closes.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InfoEditorSheet(
    group: InfoGroup,
    profile: Profile,
    onSaveInfo: (PersonalInfo) -> Unit,
    onSavePositions: (List<String>, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = GWPPATheme.colors
    var info by remember { mutableStateOf(profile.portfolio.info) }
    var positions by remember { mutableStateOf(profile.positions) }
    var leftHanded by remember { mutableStateOf(profile.leftHanded) }
    AppSheet(onDismiss = onDismiss) {
        Text(
            stringResource(
                when (group) {
                    InfoGroup.POSITION -> Res.string.pf_ed_position
                    InfoGroup.BODY -> Res.string.pf_ed_body
                    InfoGroup.CLUB -> Res.string.pf_ed_club
                    InfoGroup.HOME -> Res.string.pf_ed_home
                    InfoGroup.SCHOOL -> Res.string.pf_ed_school
                }
            ),
            style = GWPPATheme.typography.archivoExpandedBold27,
            color = colors.inkBlack
        )
        when (group) {
            InfoGroup.POSITION -> {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    POSITIONS.forEach { p ->
                        val on = p in positions
                        ChoiceChip(text = p, selected = on, onClick = { positions = if (on) positions - p else positions + p })
                    }
                    ChoiceChip(text = stringResource(Res.string.pf_left_handed), selected = leftHanded, onClick = { leftHanded = !leftHanded })
                }
                SmallText(text = stringResource(Res.string.pf_ed_position_note))
            }
            InfoGroup.BODY -> {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    FormField(stringResource(Res.string.pf_height), info.height, { info = info.copy(height = it) }, Modifier.weight(1f), placeholder = "6'2\"")
                    FormField(stringResource(Res.string.pf_wingspan), info.wingspan, { info = info.copy(wingspan = it) }, Modifier.weight(1f), placeholder = "6'5\"")
                }
                FormField(stringResource(Res.string.pf_fifty_label), info.fifty, { info = info.copy(fifty = it) }, placeholder = "24.8", keyboardType = KeyboardType.Decimal)
            }
            InfoGroup.CLUB -> {
                FormField(stringResource(Res.string.pf_club), info.club, { info = info.copy(club = it) }, placeholder = stringResource(Res.string.pf_club_hint))
                FormField(stringResource(Res.string.pf_coach), info.coach, { info = info.copy(coach = it) }, placeholder = stringResource(Res.string.pf_full_name))
                FormField(stringResource(Res.string.pf_coach_email), info.coachEmail, { info = info.copy(coachEmail = it) }, placeholder = "coach@club.com", keyboardType = KeyboardType.Email)
                SmallText(text = stringResource(Res.string.pf_coach_note))
            }
            InfoGroup.HOME -> {
                FormField(stringResource(Res.string.pf_hometown), info.hometown, { info = info.copy(hometown = it) }, placeholder = stringResource(Res.string.pf_hometown_hint))
                FormField(stringResource(Res.string.pf_high_school), info.highSchool, { info = info.copy(highSchool = it) }, placeholder = stringResource(Res.string.pf_school_hint))
            }
            InfoGroup.SCHOOL -> {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    FormField(stringResource(Res.string.pf_gpa), info.gpa, { info = info.copy(gpa = it) }, Modifier.weight(1f), placeholder = "3.7", keyboardType = KeyboardType.Decimal)
                    FormField(
                        stringResource(Res.string.pf_class_label),
                        info.classOf.ifEmpty { profile.gradYear?.toString().orEmpty() },
                        { info = info.copy(classOf = it) },
                        Modifier.weight(1f),
                        placeholder = "2027",
                        keyboardType = KeyboardType.Number
                    )
                }
                FormField(stringResource(Res.string.pf_ncaa_id), info.ncaaId, { info = info.copy(ncaaId = it) }, placeholder = stringResource(Res.string.pf_ncaa_hint), keyboardType = KeyboardType.Number)
            }
        }
        PrimaryButton(
            text = stringResource(Res.string.pf_save),
            onClick = { if (group == InfoGroup.POSITION) onSavePositions(positions, leftHanded) else onSaveInfo(info.trimmed()) },
            modifier = Modifier.padding(top = 10.dp)
        )
        TextLink(text = stringResource(Res.string.pf_skip), onClick = onDismiss, modifier = Modifier.fillMaxWidth())
    }
}

private fun PersonalInfo.trimmed() = PersonalInfo(
    height = height.trim(), wingspan = wingspan.trim(), fifty = fifty.trim(), club = club.trim(), coach = coach.trim(),
    coachEmail = coachEmail.trim(), hometown = hometown.trim(), highSchool = highSchool.trim(), gpa = gpa.trim(),
    classOf = classOf.trim(), ncaaId = ncaaId.trim()
)
