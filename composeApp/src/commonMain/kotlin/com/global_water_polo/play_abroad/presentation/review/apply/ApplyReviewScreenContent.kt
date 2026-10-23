package com.global_water_polo.play_abroad.presentation.review.apply

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.BackButton
import com.global_water_polo.play_abroad.core.components.ButtonStyle
import com.global_water_polo.play_abroad.core.components.Headline
import com.global_water_polo.play_abroad.core.components.HelpText
import com.global_water_polo.play_abroad.core.components.OnboardingLayout
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

@Composable
fun ApplyReviewScreenContent(
    modifier: Modifier = Modifier,
    state: ApplyReviewState,
    onBackClick: () -> Unit,
    onClipClick: (String) -> Unit,
    onSubmitClick: () -> Unit
) {
    OnboardingLayout(
        modifier = modifier,
        barStart = { BackButton(onClick = onBackClick) },
        bottom = {
            PrimaryButton(text = stringResource(Res.string.apply_submit), onClick = onSubmitClick, enabled = state.canSubmit, style = ButtonStyle.BLUE)
            SmallText(text = stringResource(Res.string.apply_price_note), center = true, modifier = Modifier.fillMaxWidth().padding(top = 10.dp))
        }
    ) {
        Headline(text = stringResource(Res.string.apply_title))
        HelpText(text = stringResource(Res.string.apply_help))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 20.dp)) {
            ClipRow("ID", stringResource(Res.string.apply_id_clip), stringResource(Res.string.apply_id_clip_sub), ApplyReviewState.CLIP_ID in state.uploads) {
                onClipClick(ApplyReviewState.CLIP_ID)
            }
            ClipRow("1", stringResource(Res.string.apply_water_clip), stringResource(Res.string.apply_water_clip_sub), ApplyReviewState.CLIP_WATER in state.uploads) {
                onClipClick(ApplyReviewState.CLIP_WATER)
            }
        }
        SmallText(text = stringResource(Res.string.apply_more_drills), modifier = Modifier.padding(top = 12.dp))
    }
}

/** Dashed upload row (`.upl button`), solid blue once the clip is added. */
@Composable
private fun ClipRow(thumb: String, title: String, subtitle: String, added: Boolean, onClick: () -> Unit) {
    val colors = GWPPATheme.colors
    val shape = RoundedCornerShape(16.dp)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.colorWhite)
            .then(
                if (added) Modifier.border(1.5.dp, colors.chlorineBlue, shape)
                else Modifier.drawBehind {
                    drawRoundRect(
                        color = colors.mistGrey,
                        cornerRadius = CornerRadius(16.dp.toPx()),
                        style = Stroke(width = 1.5.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 4.dp.toPx())))
                    )
                }
            )
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(48.dp).background(if (added) colors.chlorineBlue else colors.chlorineBlueSoft, RoundedCornerShape(10.dp))
        ) {
            Text(text = thumb, style = GWPPATheme.typography.archivoExpandedExtraBold13, color = if (added) colors.colorWhite else colors.chlorineBlue)
        }
        Column(Modifier.weight(1f)) {
            Text(text = title, style = GWPPATheme.typography.archivoBold16, color = colors.inkBlack)
            Text(text = subtitle, style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey)
        }
        Text(
            text = stringResource(if (added) Res.string.apply_added else Res.string.apply_add),
            style = GWPPATheme.typography.archivoBold12,
            color = colors.chlorineBlue
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ApplyReviewScreenContentPreview() {
    GWPPATheme {
        ApplyReviewScreenContent(state = ApplyReviewState(uploads = setOf("id")), onBackClick = {}, onClipClick = {}, onSubmitClick = {})
    }
}
