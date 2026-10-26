package com.global_water_polo.play_abroad.presentation.app.globe.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.ButtonStyle
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

private data class Room(val image: DrawableResource, val title: StringResource, val sub: StringResource)

private val ROOMS = listOf(
    Room(Res.drawable.apt_1, Res.string.tour_living, Res.string.tour_living_sub),
    Room(Res.drawable.apt_2, Res.string.tour_bed, Res.string.tour_bed_sub),
    Room(Res.drawable.apt_3, Res.string.tour_kitchen, Res.string.tour_kitchen_sub)
)

private val AMENITIES = listOf(
    Res.string.tour_am_wifi,
    Res.string.tour_am_washer,
    Res.string.tour_am_ac,
    Res.string.tour_am_desk,
    Res.string.tour_am_linens,
    Res.string.tour_am_balcony
)

/**
 * The full-screen apartment tour (the prototype's aptTour): swipe up through the living space, bedroom
 * and kitchen, then a sample floor plan and amenities. Concept images, said so on screen.
 */
@Composable
fun ApartmentTour(
    cityName: String,
    onRequestClick: () -> Unit,
    onClose: () -> Unit
) {
    val colors = GWPPATheme.colors
    val pager = rememberPagerState { ROOMS.size + 1 }
    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(Modifier.fillMaxSize().background(colors.nightNavy)) {
            VerticalPager(state = pager, modifier = Modifier.fillMaxSize()) { page ->
                if (page < ROOMS.size) RoomPage(ROOMS[page]) else InfoPage(cityName)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars).padding(16.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.45f)).clickable(onClick = onClose)
                ) {
                    Text("✕", style = GWPPATheme.typography.archivoBold16, color = colors.colorWhite)
                }
                Text(
                    stringResource(Res.string.tour_concept),
                    style = GWPPATheme.typography.archivoSemiBold12,
                    color = colors.colorWhite,
                    modifier = Modifier.clip(CircleShape).background(Color.Black.copy(alpha = 0.45f)).padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(start = 20.dp, end = 20.dp, bottom = 16.dp)
            ) {
                PrimaryButton(text = stringResource(Res.string.tour_request), onClick = onRequestClick, style = ButtonStyle.BALL)
                Text(
                    stringResource(Res.string.tour_fine),
                    style = GWPPATheme.typography.archivoRegular13,
                    color = colors.colorWhite.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun RoomPage(room: Room) {
    val colors = GWPPATheme.colors
    Box(Modifier.fillMaxSize()) {
        Image(painterResource(room.image), null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(0.45f to Color.Transparent, 1f to colors.nightNavy.copy(alpha = 0.95f))))
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.align(Alignment.BottomStart).windowInsetsPadding(WindowInsets.navigationBars).padding(start = 22.dp, end = 22.dp, bottom = 130.dp)
        ) {
            Text(stringResource(Res.string.tour_walk).uppercase(), style = GWPPATheme.typography.archivoBold12, color = colors.eliteGold)
            Text(stringResource(room.title), style = GWPPATheme.typography.archivoExpandedExtraBold30, color = colors.colorWhite)
            Text(stringResource(room.sub), style = GWPPATheme.typography.archivoRegular16, color = colors.colorWhite.copy(alpha = 0.85f))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun InfoPage(cityName: String) {
    val colors = GWPPATheme.colors
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(start = 22.dp, end = 22.dp, top = 72.dp)
    ) {
        Text(stringResource(Res.string.tour_plan).uppercase(), style = GWPPATheme.typography.archivoBold12, color = colors.eliteGold)
        Text(stringResource(Res.string.tour_sample), style = GWPPATheme.typography.archivoExpandedBold21, color = colors.colorWhite)
        FloorPlan()
        Text(stringResource(Res.string.tour_amenities).uppercase(), style = GWPPATheme.typography.archivoBold12, color = colors.eliteGold, modifier = Modifier.padding(top = 6.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            AMENITIES.forEach {
                Text(
                    stringResource(it),
                    style = GWPPATheme.typography.archivoSemiBold13,
                    color = colors.colorWhite,
                    modifier = Modifier.clip(CircleShape).border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape).padding(horizontal = 12.dp, vertical = 7.dp)
                )
            }
        }
        Text(stringResource(Res.string.tour_distance).uppercase(), style = GWPPATheme.typography.archivoBold12, color = colors.eliteGold, modifier = Modifier.padding(top = 6.dp))
        Text(stringResource(Res.string.tour_distance_sub, cityName), style = GWPPATheme.typography.archivoRegular15, color = colors.colorWhite.copy(alpha = 0.8f))
    }
}

/** The prototype's sample plan: living and bedroom on top, kitchen and bath below, doors left open. */
@Composable
private fun FloorPlan() {
    val colors = GWPPATheme.colors
    val line = Color.White.copy(alpha = 0.7f)
    Box(Modifier.fillMaxWidth().aspectRatio(300f / 190f)) {
        Canvas(Modifier.fillMaxSize()) {
            val k = size.width / 300f
            fun p(x: Float, y: Float) = Offset(x * k, y * k)
            val stroke = 2.dp.toPx()
            drawRoundRect(line, topLeft = p(6f, 6f), size = Size(288f * k, 178f * k), cornerRadius = CornerRadius(6f * k), style = Stroke(stroke))
            drawLine(line, p(150f, 6f), p(150f, 106f), stroke)
            drawLine(line, p(150f, 130f), p(150f, 184f), stroke)
            drawLine(line, p(6f, 110f), p(116f, 110f), stroke)
            drawLine(line, p(190f, 110f), p(294f, 110f), stroke)
        }
        val label = GWPPATheme.typography.archivoSemiBold13
        Box(Modifier.fillMaxSize()) {
            Text(stringResource(Res.string.tour_plan_living), style = label, color = colors.colorWhite, modifier = Modifier.align(Alignment.TopStart).padding(start = 22.dp, top = 26.dp))
            Text(stringResource(Res.string.tour_plan_bed), style = label, color = colors.colorWhite, modifier = Modifier.align(Alignment.TopEnd).padding(end = 22.dp, top = 26.dp))
            Text(stringResource(Res.string.tour_plan_kitchen), style = label, color = colors.colorWhite, modifier = Modifier.align(Alignment.BottomStart).padding(start = 22.dp, bottom = 20.dp))
            Text(stringResource(Res.string.tour_plan_bath), style = label, color = colors.colorWhite, modifier = Modifier.align(Alignment.BottomEnd).padding(end = 22.dp, bottom = 20.dp))
        }
    }
}
