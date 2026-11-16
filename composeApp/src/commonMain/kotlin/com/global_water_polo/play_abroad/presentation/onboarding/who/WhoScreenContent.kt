package com.global_water_polo.play_abroad.presentation.onboarding.who

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.OnboardingLayout
import com.global_water_polo.play_abroad.core.components.TextLink
import com.global_water_polo.play_abroad.domain.model.Lane
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingState
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

private data class Role(val lane: Lane, val name: StringResource, val action: StringResource)

private val ROLES = listOf(
    Role(Lane.ATHLETE, Res.string.entry_athlete_name, Res.string.entry_athlete_action),
    Role(Lane.PARENT, Res.string.entry_parent_name, Res.string.entry_parent_action),
    Role(Lane.COACH, Res.string.entry_coach_name, Res.string.entry_coach_action),
    // Club admins pick "I coach", then Club admin.
    Role(Lane.FAN, Res.string.entry_fan_name, Res.string.entry_fan_action)
)

/** "How are you part of the game?": four numbered ways in, like the prototype's entry rows. */
@Composable
fun WhoScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    onRoleClick: (Lane) -> Unit,
    onBackToGlobeClick: () -> Unit
) {
    val colors = GWPPATheme.colors
    OnboardingLayout(
        modifier = modifier,
        showBar = false,
        bottom = {
            Column {
                ROLES.forEachIndexed { index, role ->
                    Divider()
                    RoleRow(number = "0${index + 1}", role = role, onClick = { onRoleClick(role.lane) })
                }
                Divider()
            }
            TextLink(
                text = stringResource(Res.string.who_back_to_globe),
                onClick = onBackToGlobeClick,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 12.dp)
            )
        }
    ) {
        Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
            RuleText(stringResource(Res.string.entry_rule_left), Modifier.weight(1f))
            RuleText(stringResource(Res.string.entry_rule_right))
        }
        Text(
            text = stringResource(Res.string.entry_title).uppercase(),
            style = GWPPATheme.typography.archivoExpandedExtraBold32,
            color = colors.inkBlack,
            modifier = Modifier.padding(top = 14.dp)
        )
    }
}

@Composable
private fun RuleText(text: String, modifier: Modifier = Modifier) {
    Text(text = text.uppercase(), style = GWPPATheme.typography.archivoBold11, color = GWPPATheme.colors.slateGrey, modifier = modifier)
}

@Composable
private fun Divider() {
    Box(Modifier.fillMaxWidth().height(1.dp).background(GWPPATheme.colors.inkBlack.copy(alpha = 0.14f)))
}

@Composable
private fun RoleRow(number: String, role: Role, onClick: () -> Unit) {
    val colors = GWPPATheme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp)
    ) {
        // v29's `.role`: a gold number, the name in wide capitals, the line in small spaced capitals.
        Text(
            text = number,
            style = GWPPATheme.typography.archivoBold11.copy(letterSpacing = 0.1.em),
            color = colors.antiqueGold,
            modifier = Modifier.width(36.dp).align(Alignment.Top).padding(top = 5.dp)
        )
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = stringResource(role.name).uppercase(), style = GWPPATheme.typography.archivoExpandedExtraBold23, color = colors.inkBlack)
            Text(
                text = stringResource(role.action).uppercase(),
                style = GWPPATheme.typography.archivoBold11.copy(letterSpacing = 0.14.em),
                color = colors.slateGrey
            )
        }
        Canvas(Modifier.size(22.dp)) {
            val stroke = 2.dp.toPx()
            val y = size.height / 2f
            drawLine(colors.inkBlack, Offset(size.width * 0.15f, y), Offset(size.width * 0.85f, y), stroke, StrokeCap.Round)
            drawLine(colors.inkBlack, Offset(size.width * 0.58f, size.height * 0.25f), Offset(size.width * 0.85f, y), stroke, StrokeCap.Round)
            drawLine(colors.inkBlack, Offset(size.width * 0.58f, size.height * 0.75f), Offset(size.width * 0.85f, y), stroke, StrokeCap.Round)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WhoScreenContentPreview() {
    GWPPATheme {
        WhoScreenContent(state = OnboardingState(), onRoleClick = {}, onBackToGlobeClick = {})
    }
}
