package com.global_water_polo.play_abroad.core.components.membership

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.ButtonStyle
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.core.components.TextLink
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

private val ELITE_PERKS = listOf(
    Res.string.elite_perk_1, Res.string.elite_perk_2, Res.string.elite_perk_3, Res.string.elite_perk_4,
    Res.string.elite_perk_5, Res.string.elite_perk_6, Res.string.elite_perk_7, Res.string.elite_perk_8
)

/** "Unlock everything.": the GWP Elite offer sheet (the prototype's eliteSheet). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EliteSheet(onStartClick: () -> Unit, onDismiss: () -> Unit, onNextClick: (() -> Unit)? = null) {
    val colors = GWPPATheme.colors
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.boneWhite
    ) {
        Column(
            Modifier.verticalScroll(rememberScrollState()).padding(start = 24.dp, end = 24.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
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
            // Opened as a paywall (no plan yet), the sheet also offers the cheaper plan, like the prototype.
            if (onNextClick != null) {
                TextLink(text = stringResource(Res.string.next_or), onClick = onNextClick, modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            SmallText(text = stringResource(Res.string.elite_proto), center = true, modifier = Modifier.fillMaxWidth())
        }
    }
}
