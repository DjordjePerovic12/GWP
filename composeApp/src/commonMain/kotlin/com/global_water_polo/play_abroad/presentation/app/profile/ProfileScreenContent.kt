package com.global_water_polo.play_abroad.presentation.app.profile

import com.global_water_polo.play_abroad.core.components.membership.MembershipCard
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.shadow
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.AppTab
import com.global_water_polo.play_abroad.core.components.AppTabBar
import com.global_water_polo.play_abroad.core.components.membership.MembershipSheet
import com.global_water_polo.play_abroad.core.components.membership.trialLeft
import com.global_water_polo.play_abroad.domain.model.Profile
import com.global_water_polo.play_abroad.presentation.app.profile.components.LabSection
import com.global_water_polo.play_abroad.presentation.app.profile.components.AccountSection
import com.global_water_polo.play_abroad.presentation.app.profile.components.MembershipSection
import com.global_water_polo.play_abroad.presentation.app.profile.components.NationalTeamSection
import com.global_water_polo.play_abroad.BuildKonfig
import com.global_water_polo.play_abroad.core.utils.LockAction
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import com.global_water_polo.play_abroad.presentation.app.profile.components.PortfolioSection
import com.global_water_polo.play_abroad.presentation.app.profile.components.TodayRow
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.core.components.PickedImage
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** Profile tab (v29's me28): identity, membership, then Card, GWP LAB, Membership or Account. */
@Composable
fun ProfileScreenContent(
    modifier: Modifier = Modifier,
    state: ProfileState,
    onEvent: (ProfileEvent) -> Unit
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
            Identity(state)
            MembershipCard(
                membership = state.membership,
                onClick = {
                    onEvent(ProfileEvent.OnMembershipSheet(if (state.membership.plan == null) MembershipSheet.PAYWALL else MembershipSheet.MANAGE))
                }
            )
            if (state.membership.elite) TodayRow(done = state.profile.workoutDone, onClick = { onEvent(ProfileEvent.OnTodayClick) })
            SegmentBar(current = state.segment, onClick = { onEvent(ProfileEvent.OnSegmentClick(it)) })
            when (state.segment) {
                ProfileSegment.CARD -> {
                    NationalTeamSection(
                        teams = state.profile.nationalTeams,
                        events = state.profile.nationalEvents,
                        onTeamClick = { onEvent(ProfileEvent.OnNationalTeamClick(it)) },
                        onEventClick = { onEvent(ProfileEvent.OnNationalEventClick(it)) }
                    )
                    Spacer(Modifier.height(18.dp))
                    PortfolioSection(state = state, onEvent = onEvent)
                }
                ProfileSegment.LAB -> LabSection(
                    state = state,
                    onTestClick = { onEvent(ProfileEvent.OnLabTestClick(it)) },
                    onExampleClick = { onEvent(ProfileEvent.OnLabExampleClick) }
                )
                ProfileSegment.MEMBER -> MembershipSection(
                    onNextClick = { onEvent(ProfileEvent.OnMembershipSheet(MembershipSheet.NEXT)) },
                    onEliteClick = { onEvent(ProfileEvent.OnMembershipSheet(MembershipSheet.PAYWALL)) }
                )
                ProfileSegment.ACCOUNT -> AccountSection(
                    profile = state.profile,
                    membership = state.membership,
                    minor = state.minor,
                    openFaq = state.openFaq,
                    onSubscriptionClick = {
                        onEvent(ProfileEvent.OnMembershipSheet(if (state.membership.plan == null) MembershipSheet.PAYWALL else MembershipSheet.MANAGE))
                    },
                    onNotificationsClick = { onEvent(ProfileEvent.OnNotificationsClick) },
                    onHomeAirportClick = { onEvent(ProfileEvent.OnHomeAirportClick) },
                    onFaqClick = { onEvent(ProfileEvent.OnFaqClick(it)) },
                    onSignOutClick = { onEvent(ProfileEvent.OnSignOutClick) },
                    onSettingsClick = if (BuildKonfig.DEMO_SHORTCUTS) ({ onEvent(ProfileEvent.OnSettingsClick) }) else null
                )
            }
        }
        AppTabBar(
            current = AppTab.PROFILE,
            onSelect = { onEvent(ProfileEvent.OnTabClick(it)) },
            inboxBadge = state.inboxBadge,
            lock = state.lock,
            onLockClick = { onEvent(ProfileEvent.OnLock(LockAction.OPEN)) }
        )
    }
}

@Composable
private fun Identity(state: ProfileState) {
    val colors = GWPPATheme.colors
    val p = state.profile
    val name = listOf(p.firstName, p.lastName).filter { it.isNotBlank() }.joinToString(" ").ifEmpty { "Alex" }
    val initials = (p.firstName.take(1) + p.lastName.take(1)).uppercase().ifEmpty { "A" }
    val meta = listOfNotNull(
        p.position.ifEmpty { null },
        p.gradYear?.let { stringResource(Res.string.me_class_of, it) },
        state.clockYear?.let { stringResource(Res.string.me_clock, it) }
    )
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(60.dp).clip(CircleShape).background(colors.nightNavy)) {
            // The portfolio's profile photo once there is one, else the initials.
            val photo = p.portfolio.photo
            if (photo != null) PickedImage(photo.file, Modifier.matchParentSize())
            else Text(initials, style = GWPPATheme.typography.archivoExpandedBold21, color = colors.colorWhite)
        }
        Column {
            Text(name, style = GWPPATheme.typography.archivoExpandedBold27, color = colors.inkBlack)
            Text(meta.joinToString("  ·  "), style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey)
        }
    }
}

/** Card / GWP LAB / Membership / Account, the prototype's `.seg3`. */
@Composable
private fun SegmentBar(current: ProfileSegment, onClick: (ProfileSegment) -> Unit) {
    val colors = GWPPATheme.colors
    // `.seg3`: a beige bar, the open section on a white tab.
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier
            .padding(top = 16.dp, bottom = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.sandBeige.copy(alpha = 0.92f))
            .padding(3.dp)
    ) {
        ProfileSegment.entries.forEach { segment ->
            val on = segment == current
            Text(
                text = stringResource(
                    when (segment) {
                        ProfileSegment.CARD -> Res.string.me_seg_card
                        ProfileSegment.LAB -> Res.string.me_seg_lab
                        ProfileSegment.MEMBER -> Res.string.me_seg_member
                        ProfileSegment.ACCOUNT -> Res.string.me_seg_account
                    }
                ),
                style = GWPPATheme.typography.archivoBold13,
                color = if (on) colors.inkBlack else colors.slateGrey,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .then(if (on) Modifier.shadow(2.dp, RoundedCornerShape(9.dp)) else Modifier)
                    .clip(RoundedCornerShape(9.dp))
                    .background(if (on) colors.colorWhite else Color.Transparent)
                    .clickable { onClick(segment) }
                    .padding(vertical = 9.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenContentPreview() {
    GWPPATheme {
        ProfileScreenContent(state = ProfileState(profile = Profile(firstName = "Alex", lastName = "Rivera", gradYear = 2027)), onEvent = {})
    }
}
