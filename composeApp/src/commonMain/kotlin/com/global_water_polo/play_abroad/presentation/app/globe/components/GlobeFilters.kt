package com.global_water_polo.play_abroad.presentation.app.globe.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.domain.model.WaterPoloMap
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/**
 * Globe+ filters (the prototype's g-filt): a country dropdown, then Men's and Women's toggles. They narrow
 * the city and club lists together with the search; picking a country also flies the globe there.
 */
@Composable
fun GlobeFilters(
    country: String,
    sex: String,
    onCountryClick: (String) -> Unit,
    onSexClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GWPPATheme.colors
    var open by remember { mutableStateOf(false) }
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Box(Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .clip(CircleShape)
                    .background(colors.nightNavy.copy(alpha = 0.55f))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                    .clickable { open = true }
                    .padding(horizontal = 14.dp)
            ) {
                Text(
                    country.ifEmpty { stringResource(Res.string.globe_all_countries) },
                    style = GWPPATheme.typography.archivoBold13,
                    color = colors.colorWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Chevron(colors.colorWhite)
            }
            DropdownMenu(expanded = open, onDismissRequest = { open = false }, containerColor = colors.colorWhite) {
                (listOf("") + WaterPoloMap.countries).forEach { c ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                c.ifEmpty { stringResource(Res.string.globe_all_countries) },
                                style = if (c == country) GWPPATheme.typography.archivoBold16 else GWPPATheme.typography.archivoRegular16,
                                color = colors.inkBlack
                            )
                        },
                        onClick = {
                            open = false
                            onCountryClick(c)
                        }
                    )
                }
            }
        }
        listOf("men" to Res.string.globe_mens, "women" to Res.string.globe_womens).forEach { (key, label) ->
            val on = sex == key
            Text(
                stringResource(label),
                style = GWPPATheme.typography.archivoBold13,
                color = if (on) colors.inkBlack else colors.colorWhite,
                modifier = Modifier
                    .height(36.dp)
                    .clip(CircleShape)
                    .background(if (on) colors.colorWhite else colors.nightNavy.copy(alpha = 0.55f))
                    .border(1.dp, Color.White.copy(alpha = if (on) 0f else 0.2f), CircleShape)
                    .clickable { onSexClick(key) }
                    .wrapContentHeight(Alignment.CenterVertically)
                    .padding(horizontal = 14.dp)
            )
        }
    }
}

/**
 * The search over the globe (`.g-in`): a see-through field with a magnifier and the placeholder inside. It
 * keeps its own text state and reports changes, like FormField (iOS drops keystrokes otherwise).
 */
@Composable
fun GlobeSearch(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val colors = GWPPATheme.colors
    val focusManager = LocalFocusManager.current
    val field = rememberTextFieldState(query)
    val sent = remember { ArrayDeque<String>() }
    val report by rememberUpdatedState(onQueryChange)
    LaunchedEffect(field) {
        snapshotFlow { field.text.toString() }.collect { text ->
            if (text == query && sent.isEmpty()) return@collect
            sent.addLast(text)
            if (sent.size > 64) sent.removeFirst()
            report(text)
        }
    }
    LaunchedEffect(query) {
        val i = sent.indexOf(query)
        if (i >= 0) repeat(i + 1) { sent.removeFirst() }
        else if (query != field.text.toString()) field.setTextAndPlaceCursorAtEnd(query)
    }
    BasicTextField(
        state = field,
        lineLimits = TextFieldLineLimits.SingleLine,
        textStyle = GWPPATheme.typography.archivoRegular16.copy(color = colors.colorWhite),
        cursorBrush = SolidColor(colors.colorWhite),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        onKeyboardAction = { focusManager.clearFocus() },
        modifier = modifier.fillMaxWidth(),
        decorator = { inner ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.14f))
                    .padding(horizontal = 14.dp)
            ) {
                Canvas(Modifier.size(18.dp)) {
                    val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                    val r = size.width * 0.32f
                    val c = Offset(size.width * 0.42f, size.height * 0.42f)
                    drawCircle(Color.White.copy(alpha = 0.7f), r, c, style = stroke)
                    drawLine(Color.White.copy(alpha = 0.7f), Offset(c.x + r * 0.72f, c.y + r * 0.72f), Offset(size.width * 0.92f, size.height * 0.92f), stroke.width, StrokeCap.Round)
                }
                Box(Modifier.weight(1f)) {
                    if (field.text.isEmpty()) {
                        Text(stringResource(Res.string.globe_search), style = GWPPATheme.typography.archivoRegular16, color = Color.White.copy(alpha = 0.55f))
                    }
                    inner()
                }
            }
        }
    )
}

/** The dropdown's chevron (`.g-sel::after`). */
@Composable
private fun Chevron(color: Color) {
    Canvas(Modifier.size(10.dp)) {
        val stroke = 2.dp.toPx()
        drawLine(color, Offset(size.width * 0.1f, size.height * 0.3f), Offset(size.width * 0.5f, size.height * 0.7f), stroke, StrokeCap.Round)
        drawLine(color, Offset(size.width * 0.5f, size.height * 0.7f), Offset(size.width * 0.9f, size.height * 0.3f), stroke, StrokeCap.Round)
    }
}

/** The zoom buttons on the right of the globe (`.g-zoom`). */
@Composable
fun GlobeZoomButtons(onZoomIn: () -> Unit, onZoomOut: () -> Unit, modifier: Modifier = Modifier) {
    val colors = GWPPATheme.colors
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = modifier) {
        listOf("+" to onZoomIn, "−" to onZoomOut).forEach { (label, onClick) ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.14f))
                    .clickable(onClick = onClick)
            ) {
                Text(label, style = GWPPATheme.typography.archivoExpandedBold21, color = colors.colorWhite)
            }
        }
    }
}
