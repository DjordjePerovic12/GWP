package com.global_water_polo.play_abroad.core.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.utils.Lock
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/**
 * v29's `.lk28`: a dark pill floating over the tab bar while the account waits for the email link or a
 * parent. Tapping it opens the lock sheet. Nothing shows when the account is open.
 */
@Composable
fun LockPill(lock: Lock, onClick: () -> Unit, modifier: Modifier = Modifier) {
    if (lock == Lock.NONE) return
    val colors = GWPPATheme.colors
    val shape = RoundedCornerShape(18.dp)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .shadow(10.dp, shape)
            .clip(shape)
            .background(colors.nightNavy)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        ChainIcon(color = colors.eliteGold)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Text(
                stringResource(if (lock == Lock.EMAIL) Res.string.lock_email_title else Res.string.lock_parent_title),
                style = GWPPATheme.typography.archivoBold16,
                color = colors.colorWhite
            )
            Text(
                stringResource(if (lock == Lock.EMAIL) Res.string.lock_email_sub else Res.string.lock_parent_sub),
                style = GWPPATheme.typography.archivoRegular13,
                color = colors.colorWhite.copy(alpha = 0.7f)
            )
        }
        Text("›", style = GWPPATheme.typography.archivoExpandedBold21, color = colors.colorWhite.copy(alpha = 0.6f))
    }
}

/** Two linked rounded rings at an angle: the prototype's chain icon. */
@Composable
fun ChainIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier.size(22.dp)) {
        val w = size.width
        val link = Size(w * 0.56f, w * 0.34f)
        val stroke = Stroke(width = w * 0.09f)
        rotate(-45f) {
            drawRoundRect(color, Offset(w * 0.06f, (w - link.height) / 2), link, CornerRadius(link.height / 2), style = stroke)
            drawRoundRect(color, Offset(w * 0.38f, (w - link.height) / 2), link, CornerRadius(link.height / 2), style = stroke)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LockPillPreview() {
    GWPPATheme { LockPill(lock = Lock.EMAIL, onClick = {}) }
}
