package com.global_water_polo.play_abroad.presentation.home.parent.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.AppSheet
import com.global_water_polo.play_abroad.core.components.ButtonStyle
import com.global_water_polo.play_abroad.core.components.InfoPanel
import com.global_water_polo.play_abroad.core.components.InfoRow
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.core.components.TextLink
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/**
 * Plan a year abroad (the prototype's abroadSheet): the earliest start, length and goal from onboarding, then
 * "Request my proposal". A request, not a booking or a placement. Prototype: nothing is sent.
 */
@Composable
fun AbroadPlanSheet(
    start: String,
    length: String,
    goal: String,
    requested: Boolean,
    onRequestClick: () -> Unit,
    onDismiss: () -> Unit
) {
    AppSheet(onDismiss = onDismiss) {
        Text(stringResource(Res.string.abroad_title), style = GWPPATheme.typography.archivoExpandedBold27, color = GWPPATheme.colors.inkBlack)
        InfoPanel {
            InfoRow(stringResource(Res.string.match_earliest), start)
            HorizontalDivider(color = GWPPATheme.colors.lineGrey)
            InfoRow(stringResource(Res.string.tripplan_length), length)
            HorizontalDivider(color = GWPPATheme.colors.lineGrey)
            InfoRow(stringResource(Res.string.inq_goal), goal)
        }
        SmallText(text = stringResource(Res.string.abroad_fine))
        PrimaryButton(
            text = stringResource(if (requested) Res.string.abroad_requested else Res.string.abroad_request),
            onClick = onRequestClick,
            enabled = !requested,
            style = ButtonStyle.BALL,
            modifier = Modifier.padding(top = 8.dp)
        )
        TextLink(text = stringResource(Res.string.abroad_not_now), onClick = onDismiss, modifier = Modifier.fillMaxWidth())
    }
}

/** The GWP Elite teaser (the prototype's eliteTeaser): the perks blurred behind a lock, tap to see Elite. */
@Composable
fun EliteTeaser(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = GWPPATheme.colors
    val perks = listOf(
        Res.string.elite_perk_3, Res.string.elite_perk_4, Res.string.elite_perk_5,
        Res.string.elite_perk_6, Res.string.elite_perk_7, Res.string.elite_perk_8
    ).map { stringResource(it) }
    Box(
        modifier = modifier
            .padding(top = 18.dp)
            .fillMaxWidth()
            .heightIn(min = 124.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(colors.nightNavy)
            .clickable(onClick = onClick)
    ) {
        Text(
            perks.joinToString("  "),
            style = GWPPATheme.typography.archivoBold16,
            color = colors.colorWhite,
            modifier = Modifier.padding(18.dp).blur(5.dp).alpha(0.55f)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.align(Alignment.Center).padding(16.dp)
        ) {
            Text("🔒", style = GWPPATheme.typography.archivoRegular16)
            Text(stringResource(Res.string.trial_elite), style = GWPPATheme.typography.archivoExpandedBold17, color = colors.colorWhite)
            Text(stringResource(Res.string.elite_teaser_sub), style = GWPPATheme.typography.archivoRegular13, color = colors.colorWhite.copy(alpha = 0.75f), textAlign = TextAlign.Center)
        }
    }
}
