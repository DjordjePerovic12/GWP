package com.global_water_polo.play_abroad.core.components.membership

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.core.utils.NcaaClockCalculator
import com.global_water_polo.play_abroad.domain.model.Profile
import com.global_water_polo.play_abroad.core.components.TextLink
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** The membership on [profile] today; days left count down to the trial's last day. */
fun membershipOf(profile: Profile, today: LocalDate = NcaaClockCalculator.today()): Membership = Membership(
    plan = profile.plan,
    trialEnds = profile.trialEnds,
    daysLeft = profile.trialEnds?.let { (it.toEpochDays() - today.toEpochDays()).toInt().coerceAtLeast(0) } ?: 0,
    yearly = profile.yearlyBilling
)

/** The athlete's membership as the sheets need it. */
data class Membership(val plan: String?, val trialEnds: LocalDate?, val daysLeft: Int, val yearly: Boolean = true) {
    val elite: Boolean get() = plan == PLAN_ELITE
    val next: Boolean get() = plan == PLAN_NEXT

    companion object {
        const val PLAN_ELITE = "elite"
        const val PLAN_NEXT = "next"
    }
}

/**
 * Manage subscription (the prototype's manageSub): trial status, the two plans and cancel.
 * With no plan yet it reads "Choose your membership" and leads with Elite.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageSubscriptionSheet(
    membership: Membership,
    onEliteClick: () -> Unit,
    onNextClick: () -> Unit,
    onCancelClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = GWPPATheme.colors
    val months = stringArrayResource(Res.array.months)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.boneWhite
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 32.dp)
        ) {
            Image(painter = painterResource(Res.drawable.gwp_logo), contentDescription = null, modifier = Modifier.size(52.dp))
            Text(
                text = stringResource(
                    when {
                        membership.elite -> Res.string.trial_elite
                        membership.next -> Res.string.trial_next
                        else -> Res.string.manage_choose
                    }
                ),
                style = GWPPATheme.typography.archivoExpandedBold27,
                color = colors.inkBlack,
                textAlign = TextAlign.Center
            )
            if (membership.plan == null) {
                Text(stringResource(Res.string.manage_start_elite), style = GWPPATheme.typography.archivoRegular15, color = colors.slateGrey, textAlign = TextAlign.Center)
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.colorWhite)
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    StatusRow(stringResource(Res.string.manage_status), trialLeft(membership.daysLeft))
                    membership.trialEnds?.let { StatusRow(stringResource(Res.string.manage_ends), "${months[it.month.number - 1]} ${it.day}, ${it.year}") }
                    StatusRow(
                        stringResource(Res.string.manage_then),
                        stringResource(
                            when {
                                membership.elite -> Res.string.manage_then_elite
                                membership.yearly -> Res.string.manage_then_year
                                else -> Res.string.manage_then_month
                            }
                        )
                    )
                }
            }
            PlanRow(
                title = stringResource(Res.string.trial_elite),
                sub = stringResource(Res.string.manage_elite_sub),
                tail = stringResource(if (membership.elite) Res.string.manage_current else Res.string.manage_elite_price),
                current = membership.elite,
                elite = true,
                onClick = onEliteClick
            )
            PlanRow(
                title = stringResource(Res.string.trial_next),
                sub = stringResource(Res.string.manage_next_sub),
                tail = stringResource(
                    when {
                        membership.next -> Res.string.manage_current
                        membership.elite -> Res.string.manage_switch_next
                        else -> Res.string.manage_next_price
                    }
                ),
                current = membership.next,
                elite = false,
                onClick = onNextClick
            )
            if (membership.plan != null) TextLink(text = stringResource(Res.string.manage_cancel), onClick = onCancelClick)
            SmallText(text = stringResource(Res.string.elite_proto), center = true)
        }
    }
}

/** "Free trial, 6 days left". */
@Composable
fun trialLeft(days: Int): String =
    if (days == 1) stringResource(Res.string.plan_trial_left_one) else stringResource(Res.string.plan_trial_left, days)

@Composable
private fun StatusRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label, style = GWPPATheme.typography.archivoRegular14, color = GWPPATheme.colors.slateGrey, modifier = Modifier.weight(1f))
        Text(value, style = GWPPATheme.typography.archivoBold13, color = GWPPATheme.colors.inkBlack)
    }
}

@Composable
private fun PlanRow(title: String, sub: String, tail: String, current: Boolean, elite: Boolean, onClick: () -> Unit) {
    val colors = GWPPATheme.colors
    val shape = RoundedCornerShape(16.dp)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (elite) colors.nightNavy else colors.colorWhite)
            .border(if (current) 2.5.dp else 1.dp, if (current) (if (elite) colors.eliteGold else colors.inkBlack) else colors.lineGrey, shape)
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = GWPPATheme.typography.archivoExpandedBold16, color = if (elite) colors.colorWhite else colors.inkBlack)
            Text(sub, style = GWPPATheme.typography.archivoRegular13, color = if (elite) colors.colorWhite.copy(alpha = 0.7f) else colors.slateGrey)
        }
        Text(tail, style = GWPPATheme.typography.archivoBold12, color = if (elite) colors.eliteGold else colors.chlorineBlue, textAlign = TextAlign.End, modifier = Modifier.padding(start = 8.dp))
    }
}

/** Which membership sheet is open. PAYWALL is the Elite offer with the GWP Next alternative under it. */
enum class MembershipSheet {
    PAYWALL,
    ELITE,
    NEXT,
    MANAGE
}

/**
 * Shows [sheet] and moves between the membership sheets the way the prototype does:
 * paywall -> Next, Next -> Elite, Manage -> either plan or cancel. [onStart] starts a 7-day trial.
 */
@Composable
fun MembershipSheetHost(
    sheet: MembershipSheet?,
    membership: Membership,
    onSheetChange: (MembershipSheet?) -> Unit,
    onStart: (plan: String, yearly: Boolean) -> Unit,
    onSwitchToNext: () -> Unit,
    onCancel: () -> Unit
) {
    if (sheet != null) key(sheet) {
        MembershipSheetContent(sheet, membership, onSheetChange, onStart, onSwitchToNext, onCancel)
    }
}

@Composable
private fun MembershipSheetContent(
    sheet: MembershipSheet,
    membership: Membership,
    onSheetChange: (MembershipSheet?) -> Unit,
    onStart: (plan: String, yearly: Boolean) -> Unit,
    onSwitchToNext: () -> Unit,
    onCancel: () -> Unit
) {
    when (sheet) {
        MembershipSheet.PAYWALL, MembershipSheet.ELITE -> EliteSheet(
            onStartClick = {
                onStart(Membership.PLAN_ELITE, true)
                onSheetChange(null)
            },
            onDismiss = { onSheetChange(null) },
            onNextClick = if (sheet == MembershipSheet.PAYWALL) ({ onSheetChange(MembershipSheet.NEXT) }) else null
        )
        MembershipSheet.NEXT -> NextSheet(
            onStartClick = { billing ->
                onStart(Membership.PLAN_NEXT, billing == Billing.YEAR)
                onSheetChange(null)
            },
            onEliteClick = { onSheetChange(MembershipSheet.ELITE) },
            onDismiss = { onSheetChange(null) }
        )
        MembershipSheet.MANAGE -> ManageSubscriptionSheet(
            membership = membership,
            onEliteClick = { if (!membership.elite) onSheetChange(MembershipSheet.ELITE) },
            onNextClick = {
                when {
                    membership.next -> Unit
                    membership.elite -> {
                        onSwitchToNext()
                        onSheetChange(null)
                    }
                    else -> onSheetChange(MembershipSheet.NEXT)
                }
            },
            onCancelClick = {
                onCancel()
                onSheetChange(null)
            },
            onDismiss = { onSheetChange(null) }
        )
    }
}

/** The plan card under the name (Profile, parent home): Free with See plans, or the trial with Manage. */
@Composable
fun MembershipCard(membership: Membership, onClick: () -> Unit) {
    val colors = GWPPATheme.colors
    val m = membership
    val shape = RoundedCornerShape(16.dp)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
            .clip(shape)
            .background(if (m.elite) colors.nightNavy else colors.colorWhite)
            .border(1.dp, if (m.elite) colors.eliteGold else colors.lineGrey, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                stringResource(
                    when {
                        m.elite -> Res.string.trial_elite
                        m.next -> Res.string.trial_next
                        else -> Res.string.plan_free
                    }
                ),
                style = GWPPATheme.typography.archivoExpandedBold16,
                color = if (m.elite) colors.colorWhite else colors.inkBlack
            )
            Text(
                if (m.plan != null) trialLeft(m.daysLeft) else stringResource(Res.string.plan_free_sub),
                style = GWPPATheme.typography.archivoRegular13,
                color = if (m.elite) colors.colorWhite.copy(alpha = 0.7f) else colors.slateGrey
            )
        }
        Text(
            stringResource(if (m.plan != null) Res.string.plan_manage else Res.string.plan_see),
            style = GWPPATheme.typography.archivoBold13,
            color = if (m.elite) colors.eliteGold else colors.chlorineBlue
        )
    }
}
