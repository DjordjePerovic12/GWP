package com.global_water_polo.play_abroad.presentation.app.profile

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
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.AppTab
import com.global_water_polo.play_abroad.core.components.AppTabBar
import com.global_water_polo.play_abroad.core.components.membership.MembershipSheet
import com.global_water_polo.play_abroad.core.components.membership.trialLeft
import com.global_water_polo.play_abroad.domain.model.Profile
import com.global_water_polo.play_abroad.presentation.app.profile.components.LabSection
import com.global_water_polo.play_abroad.presentation.app.profile.components.PathwaySection
import com.global_water_polo.play_abroad.presentation.app.profile.components.PortfolioSection
import com.global_water_polo.play_abroad.presentation.app.profile.components.TodayRow
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** Profile tab (the prototype's meTab): identity, membership, then Pathway, GWP LAB or Portfolio. */
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
            MembershipRow(
                state = state,
                onClick = {
                    onEvent(ProfileEvent.OnMembershipSheet(if (state.membership.plan == null) MembershipSheet.PAYWALL else MembershipSheet.MANAGE))
                }
            )
            if (state.membership.elite) TodayRow(done = state.profile.workoutDone, onClick = { onEvent(ProfileEvent.OnTodayClick) })
            SegmentBar(current = state.segment, onClick = { onEvent(ProfileEvent.OnSegmentClick(it)) })
            when (state.segment) {
                ProfileSegment.PATH -> PathwaySection(
                    state = state,
                    onBuildClick = { onEvent(ProfileEvent.OnBuildPathwayClick) },
                    onGlobeClick = { onEvent(ProfileEvent.OnExploreGlobeClick) },
                    onProgramClick = { onEvent(ProfileEvent.OnProgramClick(it)) },
                    onCityClick = { onEvent(ProfileEvent.OnCityClick(it)) },
                    onClinicClick = { onEvent(ProfileEvent.OnClinicClick(it)) }
                )
                ProfileSegment.LAB -> LabSection(
                    state = state,
                    onTestClick = { onEvent(ProfileEvent.OnLabTestClick(it)) },
                    onExampleClick = { onEvent(ProfileEvent.OnLabExampleClick) }
                )
                ProfileSegment.PORTFOLIO -> PortfolioSection(state = state, onEvent = onEvent)
            }
        }
        AppTabBar(current = AppTab.PROFILE, onSelect = { onEvent(ProfileEvent.OnTabClick(it)) })
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
            Text(initials, style = GWPPATheme.typography.archivoExpandedBold21, color = colors.colorWhite)
        }
        Column {
            Text(name, style = GWPPATheme.typography.archivoExpandedBold27, color = colors.inkBlack)
            Text(meta.joinToString("  ·  "), style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey)
        }
    }
}

@Composable
private fun MembershipRow(state: ProfileState, onClick: () -> Unit) {
    val colors = GWPPATheme.colors
    val m = state.membership
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

/** Pathway / GWP LAB / Portfolio, the prototype's `.seg3`. */
@Composable
private fun SegmentBar(current: ProfileSegment, onClick: (ProfileSegment) -> Unit) {
    val colors = GWPPATheme.colors
    Row(
        modifier = Modifier
            .padding(top = 16.dp, bottom = 16.dp)
            .fillMaxWidth()
            .clip(CircleShape)
            .background(colors.lineGrey.copy(alpha = 0.6f))
            .padding(4.dp)
    ) {
        ProfileSegment.entries.forEach { segment ->
            val on = segment == current
            Text(
                text = stringResource(
                    when (segment) {
                        ProfileSegment.PATH -> Res.string.me_seg_path
                        ProfileSegment.LAB -> Res.string.me_seg_lab
                        ProfileSegment.PORTFOLIO -> Res.string.me_seg_port
                    }
                ),
                style = GWPPATheme.typography.archivoBold13,
                color = if (on) colors.colorWhite else colors.inkBlack,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .clip(CircleShape)
                    .background(if (on) colors.inkBlack else colors.colorWhite.copy(alpha = 0f))
                    .clickable { onClick(segment) }
                    .padding(vertical = 10.dp)
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
