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
import androidx.compose.ui.text.style.TextAlign
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.core.utils.Lock
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** The athlete app's five tabs, in tab bar order (v29); Globe+ sits raised in the middle. */
enum class AppTab(val label: StringResource, val route: Screen) {
    HOME(Res.string.tab_home, Screen.HomeScreen),
    FEED(Res.string.tab_feed, Screen.FeedScreen),
    GLOBE(Res.string.tab_globe, Screen.GlobeScreen()),
    INBOX(Res.string.tab_inbox, Screen.InboxScreen),
    PROFILE(Res.string.tab_profile, Screen.ProfileScreen())
}

/**
 * The prototype's `.tabbar`: four icon tabs and the raised Globe+ button in the centre, Inbox with a gold
 * count of what needs them. While the account is locked, the lock pill floats just above the bar.
 */
@Composable
fun AppTabBar(
    current: AppTab?,
    onSelect: (AppTab) -> Unit,
    modifier: Modifier = Modifier,
    dark: Boolean = false,
    inboxBadge: Int = 0,
    lock: Lock = Lock.NONE,
    onLockClick: () -> Unit = {}
) {
    val colors = GWPPATheme.colors
    val background = if (dark) colors.nightNavy else colors.colorWhite
    Column(modifier = modifier.fillMaxWidth()) {
        LockPill(lock = lock, onClick = onLockClick)
        Box(
            modifier = Modifier
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
                        TabItem(
                            tab = tab,
                            selected = current == tab,
                            dark = dark,
                            badge = if (tab == AppTab.INBOX) inboxBadge else 0,
                            onClick = { onSelect(tab) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TabItem(tab: AppTab, selected: Boolean, dark: Boolean, badge: Int, onClick: () -> Unit) {
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
        // No clip here: the Inbox count sits over the icon's corner.
        modifier = Modifier
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Box {
            Canvas(Modifier.size(24.dp)) { drawTabIcon(tab, tint) }
            // `.tb-n`: the gold count on Inbox.
            if (badge > 0) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 10.dp, y = (-6).dp)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(colors.eliteGold)
                ) {
                    Text(badge.toString(), style = GWPPATheme.typography.archivoBold11, color = colors.inkBlack)
                }
            }
        }
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
        // `.tb-plus i`: the small gold "+" on the globe.
        Text(
            "+",
            style = GWPPATheme.typography.archivoBold13,
            color = colors.inkBlack,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.TopEnd).offset(x = (-4).dp, y = 4.dp).size(16.dp).clip(CircleShape).background(colors.eliteGold)
        )
    }
}

/** Line icons drawn on a 24-unit grid, like the prototype's inline SVGs. */
private fun DrawScope.drawTabIcon(tab: AppTab, color: Color) {
    val u = size.width / 24f
    val stroke = Stroke(width = 1.8f * u, cap = StrokeCap.Round, join = StrokeJoin.Round)
    fun p(x: Float, y: Float) = Offset(x * u, y * u)
    when (tab) {
        AppTab.HOME -> {
            val house = Path().apply {
                moveTo(4f * u, 11.5f * u); lineTo(12f * u, 5f * u); lineTo(20f * u, 11.5f * u); lineTo(20f * u, 20f * u)
                lineTo(14.5f * u, 20f * u); lineTo(14.5f * u, 15f * u); lineTo(9.5f * u, 15f * u); lineTo(9.5f * u, 20f * u)
                lineTo(4f * u, 20f * u); close()
            }
            drawPath(house, color, style = stroke)
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
        AppTab.INBOX -> {
            // A tray: the box and its dipped opening.
            drawRoundRect(color, p(3.5f, 5f), Size(17f * u, 14f * u), androidx.compose.ui.geometry.CornerRadius(2.5f * u), style = stroke)
            val tray = Path().apply {
                moveTo(3.5f * u, 13f * u); lineTo(8.5f * u, 13f * u); lineTo(10f * u, 15.5f * u)
                lineTo(14f * u, 15.5f * u); lineTo(15.5f * u, 13f * u); lineTo(20.5f * u, 13f * u)
            }
            drawPath(tray, color, style = stroke)
        }
        AppTab.PROFILE -> {
            drawCircle(color, radius = 3.8f * u, center = p(12f, 8.5f), style = stroke)
            val shoulders = Path().apply {
                moveTo(4.5f * u, 20f * u); cubicTo(5f * u, 15.5f * u, 8f * u, 13.5f * u, 12f * u, 13.5f * u)
                cubicTo(16f * u, 13.5f * u, 19f * u, 15.5f * u, 19.5f * u, 20f * u)
            }
            drawPath(shoulders, color, style = stroke)
        }
    }
}
