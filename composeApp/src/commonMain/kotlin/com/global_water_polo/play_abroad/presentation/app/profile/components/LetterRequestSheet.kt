package com.global_water_polo.play_abroad.presentation.app.profile.components

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
import com.global_water_polo.play_abroad.core.components.FormField
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/**
 * Request a letter (the prototype's letterSheet): the coach, where they coached you and their email.
 * GWP would send the coach a link to write it. Prototype: no email is sent.
 */
@Composable
fun LetterRequestSheet(onRequest: (from: String, club: String) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var club by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    val ok = name.isNotBlank() && Regex(".+@.+\\..+").matches(email.trim())
    AppSheet(onDismiss = onDismiss) {
        Text(stringResource(Res.string.pf_letter_title), style = GWPPATheme.typography.archivoExpandedBold27, color = GWPPATheme.colors.inkBlack)
        SmallText(text = stringResource(Res.string.pf_letter_sub))
        FormField(stringResource(Res.string.pf_letter_coach), name, { name = it }, placeholder = stringResource(Res.string.pf_full_name))
        FormField(stringResource(Res.string.pf_letter_club), club, { club = it }, placeholder = stringResource(Res.string.pf_letter_club_hint))
        FormField(stringResource(Res.string.pf_coach_email_short), email, { email = it }, placeholder = "coach@club.com", keyboardType = KeyboardType.Email)
        PrimaryButton(
            text = stringResource(Res.string.pf_letter_send),
            onClick = { onRequest(name.trim(), club.trim()) },
            enabled = ok,
            modifier = Modifier.padding(top = 10.dp)
        )
        SmallText(text = stringResource(Res.string.pf_letter_fine), center = true, modifier = Modifier.fillMaxWidth())
    }
}
