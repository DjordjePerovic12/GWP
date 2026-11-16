package com.global_water_polo.play_abroad.presentation.app.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.HelpText
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/**
 * Profile › Membership (v29's member28): programs are open to everyone, membership is the support around
 * them. GWP Next and GWP Elite side by side, each opening its sheet.
 */
@Composable
fun MembershipSection(onNextClick: () -> Unit, onEliteClick: () -> Unit) {
    HelpText(text = stringResource(Res.string.mb_lead))
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(top = 14.dp).fillMaxWidth().height(IntrinsicSize.Min)
    ) {
        PlanCard(
            name = stringResource(Res.string.mb_next),
            sub = stringResource(Res.string.mb_next_sub),
            perks = stringArrayResource(Res.array.mb_next_perks),
            button = stringResource(Res.string.mb_see_next),
            elite = false,
            onClick = onNextClick,
            modifier = Modifier.weight(1f).fillMaxHeight()
        )
        PlanCard(
            name = stringResource(Res.string.mb_elite),
            sub = stringResource(Res.string.mb_elite_sub),
            perks = stringArrayResource(Res.array.mb_elite_perks),
            button = stringResource(Res.string.mb_see_elite),
            elite = true,
            onClick = onEliteClick,
            modifier = Modifier.weight(1f).fillMaxHeight()
        )
    }
    SmallText(text = stringResource(Res.string.mb_fine), modifier = Modifier.padding(top = 12.dp))
}

/** `.mb28-c`: a plan's name, line, perks and button; Elite in navy and gold. */
@Composable
private fun PlanCard(name: String, sub: String, perks: List<String>, button: String, elite: Boolean, onClick: () -> Unit, modifier: Modifier) {
    val colors = GWPPATheme.colors
    val shape = RoundedCornerShape(18.dp)
    val text = if (elite) colors.colorWhite else colors.inkBlack
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
            .clip(shape)
            .background(if (elite) colors.nightNavy else colors.colorWhite)
            .border(1.dp, if (elite) colors.eliteGold.copy(alpha = 0.55f) else colors.lineGrey, shape)
            .padding(14.dp)
    ) {
        Text(name, style = GWPPATheme.typography.archivoExpandedBold16, color = if (elite) colors.eliteGold else colors.inkBlack)
        Text(sub, style = GWPPATheme.typography.archivoRegular13, color = if (elite) colors.colorWhite.copy(alpha = 0.7f) else colors.slateGrey)
        perks.forEach { perk ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("✓", style = GWPPATheme.typography.archivoBold12, color = if (elite) colors.eliteGold else colors.chlorineBlue)
                Text(perk, style = GWPPATheme.typography.archivoRegular13, color = text)
            }
        }
        Spacer(Modifier.weight(1f))
        Text(
            button,
            style = GWPPATheme.typography.archivoBold13,
            color = if (elite) colors.inkBlack else colors.colorWhite,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 6.dp)
                .fillMaxWidth()
                .clip(CircleShape)
                .background(if (elite) colors.eliteGold else colors.inkBlack)
                .clickable(onClick = onClick)
                .padding(vertical = 10.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MembershipSectionPreview() {
    GWPPATheme { Column { MembershipSection(onNextClick = {}, onEliteClick = {}) } }
}
