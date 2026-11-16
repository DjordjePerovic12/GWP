package com.global_water_polo.play_abroad.core.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.utils.AccountGate
import com.global_water_polo.play_abroad.core.utils.Lock
import com.global_water_polo.play_abroad.core.utils.LockAction
import com.global_water_polo.play_abroad.core.utils.LockView
import com.global_water_polo.play_abroad.domain.model.Profile
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/**
 * The lock layer every app tab draws: the lock sheet (v29's lockSheet) and the "You're in" overlay.
 * The pill itself sits with the tab bar.
 */
@Composable
fun AccountLockLayer(profile: Profile, view: LockView, onAction: (LockAction) -> Unit) {
    if (view.sheet) {
        LockSheet(
            lock = AccountGate.lockOf(profile),
            email = profile.email,
            onOpenLink = { onAction(LockAction.OPEN_LINK) },
            onParentConfirmed = { onAction(LockAction.PARENT_CONFIRMED) },
            onDismiss = { onAction(LockAction.DISMISS) }
        )
    }
    if (view.youreIn) {
        YoureInOverlay(firstName = profile.firstName, onDone = { onAction(LockAction.DONE) })
    }
}

/**
 * "Verify your email to unlock this." or "Waiting for your parent." Prototype: the demo buttons stand in for
 * the email link and the parent's confirmation; nothing is sent.
 */
@Composable
fun LockSheet(
    lock: Lock,
    email: String,
    onOpenLink: () -> Unit,
    onParentConfirmed: () -> Unit,
    onDismiss: () -> Unit
) {
    if (lock == Lock.NONE) return
    val colors = GWPPATheme.colors
    val uri = LocalUriHandler.current
    var resent by remember { mutableStateOf(false) }
    val remindText = stringResource(Res.string.lock_remind_sms)
    AppSheet(onDismiss = onDismiss) {
        ChainIcon(color = colors.antiqueGold, modifier = Modifier.padding(top = 8.dp))
        Headline(text = stringResource(if (lock == Lock.EMAIL) Res.string.lock_sheet_email else Res.string.lock_sheet_parent))
        if (lock == Lock.EMAIL) {
            val body = stringResource(Res.string.lock_sheet_email_body, email.ifBlank { "your email" })
            Text(
                buildAnnotatedString {
                    val at = body.indexOf(email)
                    if (email.isBlank() || at < 0) append(body) else {
                        append(body.substring(0, at))
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = colors.inkBlack)) { append(email) }
                        append(body.substring(at + email.length))
                    }
                },
                style = GWPPATheme.typography.archivoRegular16,
                color = colors.slateGrey
            )
        } else {
            HelpText(text = stringResource(Res.string.lock_sheet_parent_body))
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 10.dp)) {
            if (lock == Lock.EMAIL) {
                PrimaryButton(text = stringResource(Res.string.lock_open_link), onClick = onOpenLink, style = ButtonStyle.BALL)
                PrimaryButton(
                    text = stringResource(if (resent) Res.string.lock_sent_again else Res.string.lock_send_again),
                    onClick = { resent = true },
                    style = ButtonStyle.GHOST,
                    enabled = !resent
                )
            } else {
                PrimaryButton(
                    text = stringResource(Res.string.lock_remind),
                    onClick = { runCatching { uri.openUri("sms:?&body=" + remindText.replace(" ", "%20")) } },
                    style = ButtonStyle.BALL
                )
                PrimaryButton(text = stringResource(Res.string.lock_parent_confirmed), onClick = onParentConfirmed, style = ButtonStyle.GHOST)
            }
        }
        SmallText(text = stringResource(Res.string.lock_fine), center = true, modifier = Modifier.padding(top = 4.dp))
    }
}
