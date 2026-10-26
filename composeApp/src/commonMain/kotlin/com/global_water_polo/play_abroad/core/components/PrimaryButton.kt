package com.global_water_polo.play_abroad.core.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

enum class ButtonStyle {
    INK,
    BALL,
    BLUE,
    GHOST,
    PAPER
}

/** The prototype's `.cta`: a 58dp pill, ink by default, ball-yellow for the big moments. */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: ButtonStyle = ButtonStyle.INK
) {
    val colors = GWPPATheme.colors
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val (background, content) = when (style) {
        ButtonStyle.INK -> colors.inkBlack to colors.colorWhite
        ButtonStyle.BALL -> colors.ballYellow to colors.inkBlack
        ButtonStyle.BLUE -> colors.chlorineBlue to colors.colorWhite
        ButtonStyle.GHOST -> Color.Transparent to colors.inkBlack
        ButtonStyle.PAPER -> colors.boneWhite to colors.inkBlack
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 58.dp)
            .scale(if (pressed) 0.985f else 1f)
            .alpha(if (enabled) 1f else 0.22f)
            .clip(CircleShape)
            .background(background)
            .then(if (style == ButtonStyle.GHOST) Modifier.border(BorderStroke(1.5.dp, colors.lineGrey), CircleShape) else Modifier)
            .clickable(enabled = enabled, interactionSource = interaction, indication = null, onClick = onClick)
    ) {
        Text(text = text, style = GWPPATheme.typography.archivoBold17, color = content, textAlign = TextAlign.Center)
    }
}

/** Underlined text button ("Skip intro", "Do it later"). */
@Composable
fun TextLink(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    light: Boolean = false
) {
    Text(
        text = text,
        style = GWPPATheme.typography.archivoRegular14.copy(textDecoration = TextDecoration.Underline),
        color = if (light) Color.White.copy(alpha = 0.7f) else GWPPATheme.colors.slateGrey,
        textAlign = TextAlign.Center,
        modifier = modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .padding(8.dp)
    )
}
