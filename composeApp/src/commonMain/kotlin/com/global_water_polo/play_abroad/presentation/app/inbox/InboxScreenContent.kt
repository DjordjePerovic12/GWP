package com.global_water_polo.play_abroad.presentation.app.inbox

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.AppTab
import com.global_water_polo.play_abroad.core.components.AppTabBar
import com.global_water_polo.play_abroad.core.components.ChoiceChip
import com.global_water_polo.play_abroad.presentation.app.profile.components.SectionHeader
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.core.utils.InboxNeed
import com.global_water_polo.play_abroad.core.utils.LockAction
import com.global_water_polo.play_abroad.domain.model.Profile
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** Inbox (v29's inbox28): "Needs you", then "Activity". */
@Composable
fun InboxScreenContent(
    modifier: Modifier = Modifier,
    state: InboxState,
    onEvent: (InboxEvent) -> Unit
) {
    val colors = GWPPATheme.colors
    Column(modifier = modifier.fillMaxSize().background(colors.boneWhite)) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 24.dp)
        ) {
            Text(stringResource(Res.string.inbox_title), style = GWPPATheme.typography.archivoExpandedBold27, color = colors.inkBlack)
            val needs = state.needs
            SectionHeader(
                title = stringResource(Res.string.inbox_needs),
                note = if (needs.isEmpty()) stringResource(Res.string.inbox_all_clear) else needs.size.toString()
            )
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                needs.forEach { need -> NeedCard(need = need, state = state, onEvent = onEvent) }
                if (needs.isEmpty()) {
                    val answer = state.profile.transcriptRequest
                    Text(
                        stringResource(
                            when (answer) {
                                "approved" -> Res.string.inbox_transcript_shared
                                "declined" -> Res.string.inbox_transcript_declined
                                else -> Res.string.inbox_nothing
                            }
                        ),
                        style = GWPPATheme.typography.archivoRegular15,
                        color = colors.slateGrey
                    )
                }
            }
            SectionHeader(title = stringResource(Res.string.inbox_activity), note = stringResource(Res.string.inbox_sample))
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(colors.colorWhite)
                    .border(1.dp, colors.lineGrey, RoundedCornerShape(18.dp))
            ) {
                InboxState.ACTIVITY.forEachIndexed { i, row ->
                    if (i > 0) HorizontalDivider(color = colors.lineGrey)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 13.dp)
                    ) {
                        Text(row.text, style = GWPPATheme.typography.archivoRegular15, color = colors.inkBlack, modifier = Modifier.weight(1f))
                        Text(row.time, style = GWPPATheme.typography.archivoRegular13, color = colors.mistGrey)
                    }
                }
            }
            SmallText(
                text = stringResource(Res.string.inbox_fine) + if (state.minor) " " + stringResource(Res.string.inbox_fine_minor) else "",
                modifier = Modifier.padding(top = 12.dp)
            )
        }
        AppTabBar(
            current = AppTab.INBOX,
            onSelect = { onEvent(InboxEvent.OnTabClick(it)) },
            inboxBadge = state.needs.size,
            lock = state.lock,
            onLockClick = { onEvent(InboxEvent.OnLock(LockAction.OPEN)) }
        )
    }
}

/** `.ib28-c`: one thing that needs them, with its buttons. */
@Composable
private fun NeedCard(need: InboxNeed, state: InboxState, onEvent: (InboxEvent) -> Unit) {
    val colors = GWPPATheme.colors
    val uri = LocalUriHandler.current
    val remindText = stringResource(Res.string.lock_remind_sms)
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colors.colorWhite)
            .border(1.dp, colors.lineGrey, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        if (need == InboxNeed.TRANSCRIPT) {
            Text(
                stringResource(Res.string.inbox_sample).uppercase(),
                style = GWPPATheme.typography.archivoBold11.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 0.12.em),
                color = colors.antiqueGold,
                modifier = Modifier.border(1.dp, colors.antiqueGold, CircleShape).padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
        Text(
            stringResource(
                when (need) {
                    InboxNeed.VERIFY_EMAIL -> Res.string.inbox_verify_title
                    InboxNeed.PARENT_CONFIRM -> Res.string.inbox_parent_title
                    InboxNeed.TRANSCRIPT -> Res.string.inbox_transcript_title
                }
            ),
            style = GWPPATheme.typography.archivoBold16,
            color = colors.inkBlack
        )
        Text(
            when (need) {
                InboxNeed.VERIFY_EMAIL -> stringResource(Res.string.inbox_verify_body, state.profile.email)
                InboxNeed.PARENT_CONFIRM -> stringResource(Res.string.inbox_parent_body, state.profile.parentEmail)
                InboxNeed.TRANSCRIPT -> stringResource(Res.string.inbox_transcript_body)
            },
            style = GWPPATheme.typography.archivoRegular14,
            color = colors.slateGrey
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
            when (need) {
                InboxNeed.VERIFY_EMAIL -> ChoiceChip(
                    text = stringResource(Res.string.lock_open_link),
                    selected = true,
                    onClick = { onEvent(InboxEvent.OnLock(LockAction.OPEN_LINK)) }
                )
                InboxNeed.PARENT_CONFIRM -> {
                    ChoiceChip(
                        text = stringResource(Res.string.lock_remind),
                        selected = true,
                        onClick = { runCatching { uri.openUri("sms:?&body=" + remindText.replace(" ", "%20")) } }
                    )
                    ChoiceChip(
                        text = stringResource(Res.string.lock_parent_confirmed),
                        selected = false,
                        onClick = { onEvent(InboxEvent.OnLock(LockAction.PARENT_CONFIRMED)) }
                    )
                }
                InboxNeed.TRANSCRIPT -> {
                    ChoiceChip(text = stringResource(Res.string.inbox_approve), selected = true, onClick = { onEvent(InboxEvent.OnTranscriptAnswer(true)) })
                    ChoiceChip(text = stringResource(Res.string.inbox_decline), selected = false, onClick = { onEvent(InboxEvent.OnTranscriptAnswer(false)) })
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InboxScreenContentPreview() {
    GWPPATheme {
        InboxScreenContent(state = InboxState(profile = Profile(firstName = "Alex", email = "alex@example.test", emailVerified = false)), onEvent = {})
    }
}
