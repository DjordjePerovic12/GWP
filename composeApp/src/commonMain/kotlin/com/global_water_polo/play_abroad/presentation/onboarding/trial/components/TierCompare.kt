package com.global_water_polo.play_abroad.presentation.onboarding.trial.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** One plan card: Elite is dark with a gold edge and the "Recommended" badge, Next is white. */
@Composable
fun TierCard(
    elite: Boolean,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GWPPATheme.colors
    val shape = RoundedCornerShape(20.dp)
    val ink = if (elite) colors.colorWhite else colors.inkBlack
    Box(modifier = modifier.padding(top = 10.dp)) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .clip(shape)
                .background(if (elite) colors.nightNavy else colors.colorWhite)
                .border(
                    width = if (selected) 2.5.dp else 1.dp,
                    color = when {
                        selected && elite -> colors.eliteGold
                        selected -> colors.inkBlack
                        else -> colors.lineGrey
                    },
                    shape = shape
                )
                .clickable(onClick = onClick)
                .padding(start = 14.dp, end = 14.dp, top = 22.dp, bottom = 16.dp)
        ) {
            Text(stringResource(if (elite) Res.string.trial_elite else Res.string.trial_next), style = GWPPATheme.typography.archivoExpandedBold17, color = ink)
            Text(
                stringResource(Res.string.trial_free),
                style = GWPPATheme.typography.archivoExpandedExtraBold24,
                color = if (elite) colors.eliteGold else colors.inkBlack,
                modifier = Modifier.padding(top = 6.dp)
            )
            Text(
                stringResource(if (elite) Res.string.trial_elite_then else Res.string.trial_next_then),
                style = GWPPATheme.typography.archivoRegular13,
                color = ink.copy(alpha = 0.7f)
            )
            Text(
                stringResource(if (elite) Res.string.trial_elite_sum else Res.string.trial_next_sum),
                style = GWPPATheme.typography.archivoRegular14,
                color = ink.copy(alpha = 0.9f),
                modifier = Modifier.padding(top = 10.dp)
            )
        }
        if (elite) {
            Text(
                text = stringResource(Res.string.trial_recommended).uppercase(),
                style = GWPPATheme.typography.archivoBold11,
                color = colors.nightNavy,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-10).dp)
                    .clip(CircleShape)
                    .background(colors.eliteGold)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}

/** Rows of the Next vs Elite table: the first four come with both plans, the rest only with Elite. */
val TIER_ROWS: List<Pair<StringResource, Boolean>> = listOf(
    Res.string.trial_row_1 to true,
    Res.string.trial_row_2 to true,
    Res.string.trial_row_3 to true,
    Res.string.trial_row_4 to true,
    Res.string.trial_row_5 to false,
    Res.string.trial_row_6 to false,
    Res.string.trial_row_7 to false,
    Res.string.trial_row_8 to false,
    Res.string.trial_row_9 to false,
    Res.string.trial_row_10 to false
)

@Composable
fun TierCompareTable(modifier: Modifier = Modifier) {
    val colors = GWPPATheme.colors
    val shape = RoundedCornerShape(18.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.colorWhite)
            .border(1.dp, colors.lineGrey, shape)
    ) {
        Row(Modifier.padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.weight(1f))
            HeaderCell(stringResource(Res.string.trial_col_next), colors.mistGrey)
            HeaderCell(stringResource(Res.string.trial_col_elite), colors.eliteGold)
        }
        TIER_ROWS.forEach { (label, inNext) ->
            Box(Modifier.fillMaxWidth().height(1.dp).background(colors.lineGrey))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .background(if (inNext) Color.Transparent else colors.ballYellowSoft.copy(alpha = 0.45f))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(stringResource(label), style = GWPPATheme.typography.archivoRegular14, color = colors.inkBlack, modifier = Modifier.weight(1f))
                Box(Modifier.width(52.dp), contentAlignment = Alignment.Center) {
                    if (inNext) Tick(colors.mistGrey) else Text("–", style = GWPPATheme.typography.archivoBold16, color = colors.mistGrey)
                }
                Box(Modifier.width(52.dp), contentAlignment = Alignment.Center) { Tick(colors.eliteGold) }
            }
        }
    }
}

@Composable
private fun HeaderCell(text: String, color: Color) {
    Text(
        text = text.uppercase(),
        style = GWPPATheme.typography.archivoBold12,
        color = color,
        textAlign = TextAlign.Center,
        modifier = Modifier.width(52.dp)
    )
}

/** Filled circle with a white check, like the prototype's `yes` icon. */
@Composable
private fun Tick(color: Color) {
    Canvas(Modifier.size(20.dp)) {
        drawCircle(color)
        val w = size.width
        val check = Path().apply {
            moveTo(w * 0.3f, w * 0.5f)
            lineTo(w * 0.43f, w * 0.63f)
            lineTo(w * 0.7f, w * 0.37f)
        }
        drawPath(check, Color.White, style = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}
