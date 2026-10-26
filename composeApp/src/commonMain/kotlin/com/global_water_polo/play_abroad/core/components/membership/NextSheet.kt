package com.global_water_polo.play_abroad.core.components.membership

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.ButtonStyle
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

private val NEXT_PERKS: List<StringResource> = listOf(
    Res.string.next_perk_1, Res.string.next_perk_2, Res.string.next_perk_3, Res.string.next_perk_4, Res.string.next_perk_5
)

/** Monthly or yearly billing for GWP Next. */
enum class Billing {
    MONTH,
    YEAR
}

/**
 * The GWP Next paywall (the prototype's paywallHTML): billing toggle, what's in it, a locked look at
 * GWP Elite and the 7-day trial. Prototype: nothing is charged.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NextSheet(
    onStartClick: (Billing) -> Unit,
    onEliteClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = GWPPATheme.colors
    var billing by remember { mutableStateOf(Billing.YEAR) }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.boneWhite
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.verticalScroll(rememberScrollState()).padding(start = 24.dp, end = 24.dp, bottom = 32.dp)
        ) {
            Image(painter = painterResource(Res.drawable.gwp_logo), contentDescription = null, modifier = Modifier.size(52.dp))
            Text(stringResource(Res.string.next_title), style = GWPPATheme.typography.archivoExpandedBold27, color = colors.inkBlack)
            Text(stringResource(Res.string.next_sub), style = GWPPATheme.typography.archivoRegular15, color = colors.slateGrey, textAlign = TextAlign.Center)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max).padding(top = 6.dp)) {
                BillingCard(
                    title = stringResource(Res.string.next_monthly),
                    price = stringResource(Res.string.next_monthly_price),
                    note = stringResource(Res.string.next_monthly_note),
                    badge = null,
                    selected = billing == Billing.MONTH,
                    onClick = { billing = Billing.MONTH },
                    modifier = Modifier.weight(1f)
                )
                BillingCard(
                    title = stringResource(Res.string.next_yearly),
                    price = stringResource(Res.string.next_yearly_price),
                    note = stringResource(Res.string.next_yearly_note),
                    badge = stringResource(Res.string.next_save),
                    selected = billing == Billing.YEAR,
                    onClick = { billing = Billing.YEAR },
                    modifier = Modifier.weight(1f)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                NEXT_PERKS.forEach { perk -> PerkRow(stringResource(perk), colors.inkBlack) }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.nightNavy)
                    .clickable(onClick = onEliteClick)
                    .padding(14.dp)
            ) {
                Column(Modifier.weight(1f)) {
                    Text(stringResource(Res.string.trial_elite), style = GWPPATheme.typography.archivoExpandedBold16, color = colors.colorWhite)
                    Text(stringResource(Res.string.next_elite_up), style = GWPPATheme.typography.archivoRegular13, color = colors.colorWhite.copy(alpha = 0.7f))
                }
                Text(stringResource(Res.string.next_see_it), style = GWPPATheme.typography.archivoBold13, color = colors.eliteGold)
            }
            PrimaryButton(text = stringResource(Res.string.offers_start_trial), onClick = { onStartClick(billing) }, style = ButtonStyle.BALL)
            SmallText(
                text = stringResource(if (billing == Billing.YEAR) Res.string.next_fine_year else Res.string.next_fine_month),
                center = true
            )
            SmallText(text = stringResource(Res.string.elite_proto), center = true)
        }
    }
}

@Composable
private fun BillingCard(
    title: String,
    price: String,
    note: String,
    badge: String?,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GWPPATheme.colors
    val shape = RoundedCornerShape(16.dp)
    Box(modifier = modifier.padding(top = 10.dp)) {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .clip(shape)
                .background(colors.colorWhite)
                .border(if (selected) 2.5.dp else 1.dp, if (selected) colors.inkBlack else colors.lineGrey, shape)
                .clickable(onClick = onClick)
                .padding(start = 14.dp, end = 14.dp, top = 18.dp, bottom = 14.dp)
        ) {
            Text(title, style = GWPPATheme.typography.archivoBold16, color = colors.inkBlack)
            Text(price, style = GWPPATheme.typography.archivoExpandedBold16, color = colors.inkBlack)
            Text(note, style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey)
        }
        if (badge != null) {
            Text(
                badge,
                style = GWPPATheme.typography.archivoBold11,
                color = colors.inkBlack,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .clip(CircleShape)
                    .background(colors.ballYellow)
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            )
        }
    }
}

/** A perk with a round check, shared by the membership sheets. */
@Composable
fun PerkRow(text: String, color: Color, tick: Color = GWPPATheme.colors.chlorineBlue) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Canvas(Modifier.size(18.dp)) {
            drawCircle(tick)
            val w = size.width
            val check = Path().apply {
                moveTo(w * 0.3f, w * 0.5f)
                lineTo(w * 0.43f, w * 0.63f)
                lineTo(w * 0.7f, w * 0.37f)
            }
            drawPath(check, Color.White, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
        Text(text, style = GWPPATheme.typography.archivoRegular14, color = color)
    }
}
