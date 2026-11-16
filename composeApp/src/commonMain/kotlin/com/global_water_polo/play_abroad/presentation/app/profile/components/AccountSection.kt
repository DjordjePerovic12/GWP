package com.global_water_polo.play_abroad.presentation.app.profile.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.InfoPanel
import com.global_water_polo.play_abroad.core.components.InfoRow
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.core.components.membership.Membership
import com.global_water_polo.play_abroad.domain.model.Profile
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/**
 * Profile › Account (v29's account28): subscription, email, parent (under 18), notifications, home airport
 * and how they found GWP; the questions; privacy and sign out. [onSettingsClick] is a demo-only way to the
 * old Settings screen (null hides it).
 */
@Composable
fun AccountSection(
    profile: Profile,
    membership: Membership,
    minor: Boolean,
    openFaq: Int?,
    onSubscriptionClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onHomeAirportClick: () -> Unit,
    onFaqClick: (Int) -> Unit,
    onSignOutClick: () -> Unit,
    onSettingsClick: (() -> Unit)?
) {
    val colors = GWPPATheme.colors
    SectionHeader(title = stringResource(Res.string.set_account))
    InfoPanel {
        InfoRow(
            stringResource(Res.string.set_subscription),
            stringResource(
                when {
                    membership.elite -> Res.string.trial_elite
                    membership.next -> Res.string.trial_next
                    else -> Res.string.offers_plan_free
                }
            ),
            onClick = onSubscriptionClick
        )
        HorizontalDivider(color = colors.lineGrey)
        InfoRow(stringResource(Res.string.set_email), profile.email.ifEmpty { stringResource(Res.string.not_added) })
        if (minor) {
            HorizontalDivider(color = colors.lineGrey)
            InfoRow(stringResource(Res.string.acc_parent), stringResource(if (profile.parentConfirmed) Res.string.acc_parent_ok else Res.string.acc_parent_wait))
        }
        HorizontalDivider(color = colors.lineGrey)
        InfoRow(
            stringResource(Res.string.set_notifications),
            stringResource(if (profile.notifications) Res.string.set_on else Res.string.set_off),
            onClick = onNotificationsClick
        )
        HorizontalDivider(color = colors.lineGrey)
        InfoRow(stringResource(Res.string.set_home_airport), profile.homeAirport, onClick = onHomeAirportClick)
        HorizontalDivider(color = colors.lineGrey)
        InfoRow(stringResource(Res.string.acc_heard), profile.heardFrom.ifEmpty { stringResource(Res.string.acc_not_answered) })
    }

    SectionHeader(title = stringResource(Res.string.acc_questions))
    val questions = stringArrayResource(Res.array.acc_faq_q)
    val answers = stringArrayResource(Res.array.acc_faq_a)
    InfoPanel {
        questions.forEachIndexed { i, q ->
            if (i > 0) HorizontalDivider(color = colors.lineGrey)
            Column(Modifier.fillMaxWidth().clickable { onFaqClick(i) }.padding(vertical = 12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(q, style = GWPPATheme.typography.archivoBold16, color = colors.inkBlack, modifier = Modifier.weight(1f))
                    Text(if (openFaq == i) "–" else "+", style = GWPPATheme.typography.archivoExpandedBold21, color = colors.mistGrey)
                }
                if (openFaq == i) {
                    Text(answers.getOrElse(i) { "" }, style = GWPPATheme.typography.archivoRegular15, color = colors.slateGrey, modifier = Modifier.padding(top = 6.dp))
                }
            }
        }
    }

    SectionHeader(title = stringResource(Res.string.acc_privacy))
    InfoPanel {
        InfoRow(stringResource(Res.string.acc_cookies), stringResource(Res.string.acc_cookies_value))
        if (onSettingsClick != null) {
            HorizontalDivider(color = colors.lineGrey)
            InfoRow(stringResource(Res.string.acc_demo_settings), "", onClick = onSettingsClick)
        }
        HorizontalDivider(color = colors.lineGrey)
        InfoRow(stringResource(Res.string.set_sign_out), "", onClick = onSignOutClick)
    }
    SmallText(text = stringResource(Res.string.set_proto), center = true, modifier = Modifier.fillMaxWidth().padding(top = 18.dp))
}

@Preview(showBackground = true)
@Composable
private fun AccountSectionPreview() {
    GWPPATheme {
        Column {
            AccountSection(
                profile = Profile(email = "alex@example.test"),
                membership = Membership(plan = null, trialEnds = null, daysLeft = 0),
                minor = true,
                openFaq = 0,
                onSubscriptionClick = {},
                onNotificationsClick = {},
                onHomeAirportClick = {},
                onFaqClick = {},
                onSignOutClick = {},
                onSettingsClick = null
            )
        }
    }
}
