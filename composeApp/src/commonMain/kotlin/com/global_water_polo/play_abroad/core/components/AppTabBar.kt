package com.global_water_polo.play_abroad.core.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** The athlete app's five tabs, in tab bar order; Globe+ sits raised in the middle. */
enum class AppTab(val label: StringResource, val route: Screen) {
    PROFILE(Res.string.tab_profile, Screen.ProfileScreen()),
    FEED(Res.string.tab_feed, Screen.FeedScreen),
    GLOBE(Res.string.tab_globe, Screen.GlobeScreen()),
    TRAVEL(Res.string.tab_travel, Screen.FlightRadarScreen),
    SETTINGS(Res.string.tab_settings, Screen.SettingsScreen)
}

/** The prototype's `.tabbar`: four icon tabs and the raised Globe+ button in the centre. */
@Composable
fun AppTabBar(
    current: AppTab,
    onSelect: (AppTab) -> Unit,
    modifier: Modifier = Modifier,
    dark: Boolean = false
) {
    val colors = GWPPATheme.colors
    val background = if (dark) colors.nightNavy else colors.colorWhite
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(background)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Box(Modifier.fillMaxWidth().height(1.dp).background(if (dark) Color.White.copy(alpha = 0.1f) else colors.lineGrey))
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().height(62.dp)
        ) {
            AppTab.entries.forEach { tab ->
                if (tab == AppTab.GLOBE) {
                    GlobeButton(selected = current == tab, onClick = { onSelect(tab) })
                } else {
                    TabItem(tab = tab, selected = current == tab, dark = dark, onClick = { onSelect(tab) })
                }
            }
        }
    }
}

@Composable
private fun TabItem(tab: AppTab, selected: Boolean, dark: Boolean, onClick: () -> Unit) {
    val colors = GWPPATheme.colors
    val tint = when {
        selected && dark -> colors.colorWhite
        selected -> colors.inkBlack
        dark -> colors.colorWhite.copy(alpha = 0.5f)
        else -> colors.mistGrey
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier
            .clip(CircleShape)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Canvas(Modifier.size(24.dp)) { drawTabIcon(tab, tint) }
        Text(stringResource(tab.label), style = GWPPATheme.typography.archivoSemiBold11, color = tint)
    }
}

@Composable
private fun GlobeButton(selected: Boolean, onClick: () -> Unit) {
    val colors = GWPPATheme.colors
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .offset(y = (-12).dp)
            .size(58.dp)
            .shadow(8.dp, CircleShape)
            .clip(CircleShape)
            .background(if (selected) colors.chlorineBlue else colors.inkBlack)
            .clickable(onClick = onClick)
    ) {
        Canvas(Modifier.size(28.dp)) { drawTabIcon(AppTab.GLOBE, Color.White) }
    }
}

/** Line icons drawn on a 24-unit grid, like the prototype's inline SVGs. */
private fun DrawScope.drawTabIcon(tab: AppTab, color: Color) {
    val u = size.width / 24f
    val stroke = Stroke(width = 1.8f * u, cap = StrokeCap.Round, join = StrokeJoin.Round)
    fun p(x: Float, y: Float) = Offset(x * u, y * u)
    when (tab) {
        AppTab.PROFILE -> {
            // Graduation cap.
            val cap = Path().apply {
                moveTo(2f * u, 9f * u); lineTo(12f * u, 4.5f * u); lineTo(22f * u, 9f * u); lineTo(12f * u, 13.5f * u); close()
            }
            drawPath(cap, color, style = stroke)
            val body = Path().apply {
                moveTo(6f * u, 11f * u); lineTo(6f * u, 16f * u)
                cubicTo(6f * u, 18f * u, 18f * u, 18f * u, 18f * u, 16f * u); lineTo(18f * u, 11f * u)
            }
            drawPath(body, color, style = stroke)
        }
        AppTab.FEED -> {
            drawRoundRect(color, p(4f, 4f), Size(16f * u, 16f * u), androidx.compose.ui.geometry.CornerRadius(3f * u), style = stroke)
            val play = Path().apply { moveTo(10f * u, 9f * u); lineTo(15.5f * u, 12f * u); lineTo(10f * u, 15f * u); close() }
            drawPath(play, color)
        }
        AppTab.GLOBE -> {
            drawCircle(color, radius = 9f * u, center = p(12f, 12f), style = stroke)
            drawLine(color, p(3f, 12f), p(21f, 12f), stroke.width, StrokeCap.Round)
            drawOval(color, p(8f, 3f), Size(8f * u, 18f * u), style = stroke)
        }
        AppTab.TRAVEL -> {
            val plane = Path().apply {
                moveTo(3f * u, 13f * u); lineTo(21f * u, 5f * u); lineTo(15f * u, 21f * u); lineTo(11f * u, 14f * u); close()
                moveTo(11f * u, 14f * u); lineTo(21f * u, 5f * u)
            }
            drawPath(plane, color, style = stroke)
        }
        AppTab.SETTINGS -> {
            drawCircle(color, radius = 3f * u, center = p(12f, 12f), style = stroke)
            for (i in 0 until 8) {
                val a = i * kotlin.math.PI.toFloat() / 4f
                drawLine(
                    color,
                    p(12f + 6.5f * kotlin.math.cos(a), 12f + 6.5f * kotlin.math.sin(a)),
                    p(12f + 9f * kotlin.math.cos(a), 12f + 9f * kotlin.math.sin(a)),
                    stroke.width,
                    StrokeCap.Round
                )
            }
            drawCircle(color, radius = 6.5f * u, center = p(12f, 12f), style = stroke)
        }
    }
}
