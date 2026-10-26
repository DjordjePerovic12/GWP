package com.global_water_polo.play_abroad.presentation.app.profile.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.AppSheet
import com.global_water_polo.play_abroad.core.components.InfoPanel
import com.global_water_polo.play_abroad.core.components.InfoRow
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.SheetHeading
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** A sample coach review, to show what GWP LAB gives back. Not real athlete data. */
@Composable
fun LabExampleSheet(onFilmClick: () -> Unit, onDismiss: () -> Unit) {
    val colors = GWPPATheme.colors
    AppSheet(onDismiss = onDismiss) {
        Text(stringResource(Res.string.lab_ex_kicker), style = GWPPATheme.typography.archivoBold12, color = colors.eliteGold)
        Text(stringResource(Res.string.lab_ex_title), style = GWPPATheme.typography.archivoExpandedBold27, color = colors.inkBlack)
        SmallText(text = stringResource(Res.string.lab_ex_note))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 6.dp)) {
            Shot(Res.drawable.f_under, stringResource(Res.string.lab_ex_first), Modifier.weight(1f))
            Shot(Res.drawable.f_shot, stringResource(Res.string.lab_ex_retest), Modifier.weight(1f))
        }
        SheetHeading(stringResource(Res.string.lab_feedback))
        Text(stringResource(Res.string.lab_ex_feedback), style = GWPPATheme.typography.archivoRegular15, color = colors.inkBlack)
        SheetHeading(stringResource(Res.string.lab_priorities))
        listOf(Res.string.lab_ex_p1, Res.string.lab_ex_p2, Res.string.lab_ex_p3).forEachIndexed { i, p ->
            Text("${i + 1}. ${stringResource(p)}", style = GWPPATheme.typography.archivoRegular15, color = colors.inkBlack)
        }
        SheetHeading(stringResource(Res.string.lab_retest))
        Text(stringResource(Res.string.lab_ex_retest_text), style = GWPPATheme.typography.archivoRegular15, color = colors.inkBlack)
        SheetHeading(stringResource(Res.string.lab_ex_history))
        InfoPanel {
            InfoRow(stringResource(Res.string.lab_ex_first), stringResource(Res.string.lab_ex_baseline))
            HorizontalDivider(color = colors.lineGrey)
            InfoRow(stringResource(Res.string.lab_ex_retest), stringResource(Res.string.lab_ex_holds))
        }
        PrimaryButton(text = stringResource(Res.string.lab_ex_cta), onClick = onFilmClick, modifier = Modifier.padding(top = 14.dp))
    }
}

@Composable
private fun Shot(image: DrawableResource, caption: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Image(
            painterResource(image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().height(110.dp).clip(RoundedCornerShape(14.dp))
        )
        Text(caption, style = GWPPATheme.typography.archivoSemiBold13, color = GWPPATheme.colors.slateGrey)
    }
}
