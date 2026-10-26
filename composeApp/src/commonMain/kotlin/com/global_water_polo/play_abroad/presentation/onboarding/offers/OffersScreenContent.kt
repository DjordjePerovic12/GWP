package com.global_water_polo.play_abroad.presentation.onboarding.offers

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.BarStart
import com.global_water_polo.play_abroad.core.components.ButtonStyle
import com.global_water_polo.play_abroad.core.components.HelpText
import com.global_water_polo.play_abroad.core.components.OnboardingLayout
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.core.components.TextLink
import com.global_water_polo.play_abroad.domain.model.Lane
import com.global_water_polo.play_abroad.domain.model.Program
import com.global_water_polo.play_abroad.presentation.home.components.ProfileCard
import com.global_water_polo.play_abroad.presentation.onboarding.FlowStep
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingState
import com.global_water_polo.play_abroad.presentation.onboarding.match.components.duration
import com.global_water_polo.play_abroad.presentation.onboarding.match.components.image
import com.global_water_polo.play_abroad.presentation.onboarding.match.components.text
import com.global_water_polo.play_abroad.presentation.onboarding.match.components.title
import com.global_water_polo.play_abroad.presentation.onboarding.progress
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

private val Program.verb: StringResource
    get() = when (this) {
        Program.EXPLORE -> Res.string.prog_verb_explore
        Program.FALL -> Res.string.prog_verb_fall
        Program.SPRING -> Res.string.prog_verb_spring
        Program.SUMMER -> Res.string.prog_verb_summer
        Program.GAP_YEAR -> Res.string.prog_verb_gapyear
    }

private val ELITE_PERKS = listOf(
    Res.string.elite_perk_1, Res.string.elite_perk_2, Res.string.elite_perk_3, Res.string.elite_perk_4,
    Res.string.elite_perk_5, Res.string.elite_perk_6, Res.string.elite_perk_7, Res.string.elite_perk_8
)

/**
 * All offers: with the full NCAA window left, every GWP program fits. Explore comes with any membership;
 * the rest stay locked until GWP Elite. Nothing is charged in the prototype.
 */
@Composable
fun OffersScreenContent(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    showEliteSheet: Boolean,
    onProgramClick: (Program) -> Unit,
    onPrimaryClick: () -> Unit,
    onLaterClick: () -> Unit,
    onEliteStartClick: () -> Unit,
    onEliteDismiss: () -> Unit,
    onBackClick: () -> Unit
) {
    val colors = GWPPATheme.colors
    val parent = state.lane == Lane.PARENT
    val pick = state.offerPick
    val first = state.firstName.trim().substringBefore(" ")
    val primary = when {
        state.plan == null -> stringResource(Res.string.offers_start_trial)
        pick != null -> stringResource(Res.string.offers_apply_for, stringResource(pick.title))
        state.isElite -> stringResource(if (parent) Res.string.common_continue else Res.string.offers_continue_profile)
        else -> stringResource(Res.string.offers_unlock_all)
    }
    val later = when {
        state.plan == null -> stringResource(Res.string.trial_not_now)
        !state.isElite -> stringResource(Res.string.offers_continue_next)
        else -> null
    }
    OnboardingLayout(
        modifier = modifier,
        barStart = { BarStart(onBackClick = onBackClick, progress = state.progress(FlowStep.OFFERS)) },
        bottom = {
            PrimaryButton(text = primary, onClick = onPrimaryClick, style = ButtonStyle.BALL)
            if (later != null) {
                TextLink(text = later, onClick = onLaterClick, modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 6.dp))
            }
        }
    ) {
        Text(
            text = if (first.isNotEmpty()) stringResource(Res.string.offers_welcome_named, first) else stringResource(Res.string.offers_welcome),
            style = GWPPATheme.typography.archivoExpandedBold27,
            color = colors.inkBlack
        )
        SmallText(text = stringResource(if (parent) Res.string.offers_ready_parent else Res.string.offers_ready_athlete))
        if (state.plan != null) TrialChip(elite = state.isElite)
        ProfileCard(
            name = if (parent) state.kidName.ifBlank { stringResource(Res.string.offers_your_athlete) }
            else listOf(state.firstName.trim(), state.lastName.trim()).filter { it.isNotEmpty() }.joinToString(" ").ifEmpty { stringResource(Res.string.offers_your_profile) },
            meta = { Text(stringResource(Res.string.offers_profile_ready), style = GWPPATheme.typography.archivoRegular14, color = Color.White.copy(alpha = 0.7f)) },
            stats = listOf(
                stringResource(Res.string.offers_stat_clock) to (state.freeYear?.let { stringResource(Res.string.match_fall, it.clockYear) } ?: "-"),
                stringResource(Res.string.offers_stat_seasons) to stringResource(Res.string.home_of_five, 5),
                stringResource(Res.string.offers_stat_plan) to stringResource(
                    when {
                        state.isElite -> Res.string.trial_elite
                        state.plan != null -> Res.string.trial_next
                        else -> Res.string.offers_plan_free
                    }
                )
            )
        )
        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxWidth().padding(top = 22.dp)) {
            Text(stringResource(Res.string.offers_title), style = GWPPATheme.typography.archivoExpandedBold21, color = colors.inkBlack, modifier = Modifier.weight(1f))
            Text(
                stringResource(
                    when {
                        state.isElite -> Res.string.offers_note_all
                        state.plan != null -> Res.string.offers_note_next
                        else -> Res.string.offers_note_none
                    }
                ),
                style = GWPPATheme.typography.archivoSemiBold13,
                color = colors.slateGrey
            )
        }
        HelpText(text = stringResource(if (parent) Res.string.offers_help_parent else Res.string.offers_help_athlete))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 14.dp)) {
            OFFER_PROGRAMS.forEach { program ->
                val locked = program.needsElite && !state.isElite
                OfferTile(
                    program = program,
                    locked = locked,
                    selected = !locked && state.program == program.key,
                    foot = when {
                        locked -> stringResource(Res.string.offers_unlock_elite)
                        program == Program.EXPLORE && state.plan == null -> stringResource(Res.string.offers_in_next)
                        program.flagship -> stringResource(Res.string.offers_application)
                        else -> ""
                    },
                    onClick = { onProgramClick(program) }
                )
            }
        }
        SmallText(
            text = stringResource(if (parent) Res.string.offers_fine_parent else Res.string.offers_fine_athlete),
            modifier = Modifier.padding(top = 12.dp)
        )
    }
    if (showEliteSheet) EliteSheet(onStartClick = onEliteStartClick, onDismiss = onEliteDismiss)
}

@Composable
private fun TrialChip(elite: Boolean) {
    val colors = GWPPATheme.colors
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 10.dp)) {
        Text(
            stringResource(if (elite) Res.string.trial_elite else Res.string.trial_next),
            style = GWPPATheme.typography.archivoExpandedBold12,
            color = colors.colorWhite,
            modifier = Modifier.clip(CircleShape).background(colors.inkBlack).padding(horizontal = 10.dp, vertical = 4.dp)
        )
        Text(stringResource(Res.string.offers_trial_chip), style = GWPPATheme.typography.archivoSemiBold13, color = colors.slateGrey, modifier = Modifier.padding(start = 8.dp))
    }
}

/** The prototype's `.prog` tile: photo with the duration, then verb, name, one paragraph, a foot line and a radio. */
@Composable
private fun OfferTile(program: Program, locked: Boolean, selected: Boolean, foot: String, onClick: () -> Unit) {
    val colors = GWPPATheme.colors
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.colorWhite)
            .border(
                width = if (selected || program.flagship) 2.dp else 1.dp,
                color = when {
                    selected -> colors.chlorineBlue
                    program.flagship -> colors.ballYellow
                    else -> colors.lineGrey
                },
                shape = shape
            )
            .clickable(onClick = onClick)
    ) {
        Row(Modifier.height(IntrinsicSize.Min).alpha(if (locked) 0.55f else 1f)) {
            Box(Modifier.width(104.dp).fillMaxHeight()) {
                Image(
                    painter = painterResource(program.image),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Text(
                    stringResource(program.duration),
                    style = GWPPATheme.typography.archivoBold12,
                    color = colors.colorWhite,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .clip(CircleShape)
                        .background(colors.nightNavy.copy(alpha = 0.8f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
            Column(Modifier.weight(1f).padding(14.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(stringResource(program.verb).uppercase(), style = GWPPATheme.typography.archivoBold11, color = colors.mistGrey)
                    if (program.flagship) {
                        Text(
                            stringResource(Res.string.offers_flagship),
                            style = GWPPATheme.typography.archivoBold11,
                            color = colors.inkBlack,
                            modifier = Modifier.clip(CircleShape).background(colors.ballYellow).padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(stringResource(program.title), style = GWPPATheme.typography.archivoExpandedBold16, color = colors.inkBlack)
                Text(stringResource(program.text), style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Text(foot, style = GWPPATheme.typography.archivoSemiBold12, color = colors.chlorineBlue, modifier = Modifier.weight(1f))
                    Radio(selected)
                }
            }
        }
        if (locked) Padlock(Modifier.align(Alignment.TopEnd).padding(10.dp))
    }
}

@Composable
private fun Radio(selected: Boolean) {
    val colors = GWPPATheme.colors
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .border(1.5.dp, if (selected) colors.chlorineBlue else colors.mistGrey, CircleShape)
    ) {
        if (selected) Box(Modifier.size(10.dp).clip(CircleShape).background(colors.chlorineBlue))
    }
}

/** The lock on programs that need GWP Elite. */
@Composable
private fun Padlock(modifier: Modifier = Modifier) {
    val colors = GWPPATheme.colors
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(30.dp).clip(CircleShape).background(colors.nightNavy)
    ) {
        Canvas(Modifier.size(14.dp)) {
            val w = size.width
            drawRoundRect(colors.eliteGold, Offset(w * 0.15f, w * 0.45f), Size(w * 0.7f, w * 0.45f), CornerRadius(2.dp.toPx()))
            val shackle = Path().apply {
                moveTo(w * 0.3f, w * 0.45f)
                lineTo(w * 0.3f, w * 0.3f)
                cubicTo(w * 0.3f, w * 0.05f, w * 0.7f, w * 0.05f, w * 0.7f, w * 0.3f)
                lineTo(w * 0.7f, w * 0.45f)
            }
            drawPath(shackle, colors.eliteGold, style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
    }
}

/** "Unlock everything.": the GWP Elite offer sheet (the prototype's eliteSheet). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EliteSheet(onStartClick: () -> Unit, onDismiss: () -> Unit) {
    val colors = GWPPATheme.colors
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.boneWhite
    ) {
        Column(Modifier.padding(start = 24.dp, end = 24.dp, bottom = 32.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(18.dp))) {
                Image(
                    painter = painterResource(Res.drawable.elite_hero),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Text(
                    stringResource(Res.string.trial_elite),
                    style = GWPPATheme.typography.archivoExpandedBold12,
                    color = colors.nightNavy,
                    modifier = Modifier.align(Alignment.BottomStart).padding(12.dp).clip(CircleShape).background(colors.eliteGold).padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
            Text(stringResource(Res.string.elite_title), style = GWPPATheme.typography.archivoExpandedBold27, color = colors.inkBlack, modifier = Modifier.padding(top = 6.dp))
            Text(stringResource(Res.string.elite_sub), style = GWPPATheme.typography.archivoRegular15, color = colors.slateGrey)
            ELITE_PERKS.forEach { perk ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Canvas(Modifier.size(18.dp)) {
                        drawCircle(colors.eliteGold)
                        val w = size.width
                        val check = Path().apply {
                            moveTo(w * 0.3f, w * 0.5f)
                            lineTo(w * 0.43f, w * 0.63f)
                            lineTo(w * 0.7f, w * 0.37f)
                        }
                        drawPath(check, Color.White, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
                    }
                    Text(stringResource(perk), style = GWPPATheme.typography.archivoRegular14, color = colors.inkBlack)
                }
            }
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                Text(stringResource(Res.string.elite_price), style = GWPPATheme.typography.archivoExpandedExtraBold32, color = colors.inkBlack)
                Column {
                    Text(stringResource(Res.string.elite_per_year), style = GWPPATheme.typography.archivoSemiBold14, color = colors.inkBlack)
                    Text(stringResource(Res.string.elite_per_month), style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey)
                }
            }
            PrimaryButton(text = stringResource(Res.string.offers_start_trial), onClick = onStartClick, style = ButtonStyle.BALL, modifier = Modifier.padding(top = 6.dp))
            SmallText(text = stringResource(Res.string.elite_fine))
            SmallText(text = stringResource(Res.string.elite_proto), center = true, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OffersScreenContentPreview() {
    GWPPATheme {
        OffersScreenContent(
            state = OnboardingState(firstName = "Alex", plan = OnboardingState.PLAN_NEXT),
            showEliteSheet = false,
            onProgramClick = {},
            onPrimaryClick = {},
            onLaterClick = {},
            onEliteStartClick = {},
            onEliteDismiss = {},
            onBackClick = {}
        )
    }
}
