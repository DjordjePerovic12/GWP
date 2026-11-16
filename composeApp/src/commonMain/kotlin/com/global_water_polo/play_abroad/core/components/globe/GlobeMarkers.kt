package com.global_water_polo.play_abroad.core.components.globe

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** A country seen from far away: one numbered circle at the middle of its cities. */
data class GlobeCluster(val country: String, val latitude: Float, val longitude: Float, val count: Int)

/** A city pin; [active] is the picked city, drawn bigger with a ring. */
data class GlobeCity(val id: String, val latitude: Float, val longitude: Float, val active: Boolean = false)

/** A club badge: one club's monogram, or "3 CLUBS" for a group of nearby clubs. [index] is the group's. */
data class GlobeBadge(val index: Int, val latitude: Float, val longitude: Float, val monogram: String, val count: Int)

/** A city name label for close zoom, with its club count line ("3 clubs"). */
data class GlobeLabel(val cityId: String, val latitude: Float, val longitude: Float, val name: String, val note: String)

/** Everything Globe+ marks on the ball. Which layer shows depends on the zoom (the prototype's pinScale). */
data class GlobeMarkers(
    val clusters: List<GlobeCluster>,
    val cities: List<GlobeCity>,
    val badges: List<GlobeBadge>,
    val labels: List<GlobeLabel>
)

/** What a tap on the ball landed on. */
sealed interface GlobeHit {
    data class Country(val country: String) : GlobeHit
    data class City(val cityId: String) : GlobeHit
    data class Group(val index: Int) : GlobeHit
}

/** Zoom thresholds: city pins from [NEAR], club badges from [DEEP], city labels from [CLOSE]; clusters below [NEAR]. */
const val ZOOM_NEAR = 1.45f
const val ZOOM_DEEP = 2.2f
const val ZOOM_CLOSE = 4.2f

private val Navy = Color(0xFF0B1628)
private val Ball = Color(0xFFDFAE3B)
private val Gold = Color(0xFFA8864E)

/**
 * Draws the layer for [zoom] and returns where each tappable marker landed, in this canvas's pixels.
 * [project] turns a latitude and longitude into a point on the ball, or null on the far side.
 */
fun DrawScope.drawGlobeMarkers(
    markers: GlobeMarkers,
    zoom: Float,
    project: (Float, Float) -> Offset?,
    measurer: TextMeasurer
): List<Pair<Offset, GlobeHit>> {
    val hits = ArrayList<Pair<Offset, GlobeHit>>()
    val near = zoom >= ZOOM_NEAR
    val deep = zoom >= ZOOM_DEEP
    val close = zoom >= ZOOM_CLOSE
    val bold = TextStyle(fontSize = 13.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold)

    if (!near) markers.clusters.forEach { c ->
        val p = project(c.latitude, c.longitude) ?: return@forEach
        val r = 15.dp.toPx()
        drawCircle(Navy.copy(alpha = 0.92f), radius = r, center = p)
        drawCircle(Ball, radius = r, center = p, style = Stroke(width = 2.5.dp.toPx()))
        drawCentered(measurer, c.count.toString(), bold.copy(color = Color.White), p)
        hits += p to GlobeHit.Country(c.country)
    }

    if (near) markers.cities.forEach { city ->
        val p = project(city.latitude, city.longitude) ?: return@forEach
        val dot = (if (city.active) 6f else if (deep) 2.6f else 4f).dp.toPx()
        if (city.active) drawCircle(Color.White.copy(alpha = 0.85f), radius = dot * 2.2f, center = p, style = Stroke(width = 2.dp.toPx()))
        drawCircle(Navy, radius = dot + 1.5.dp.toPx(), center = p)
        drawCircle(Ball, radius = dot, center = p)
        hits += p to GlobeHit.City(city.id)
    }

    // Labels that would overlap one already drawn are skipped; zooming further in brings them back.
    val placed = ArrayList<androidx.compose.ui.geometry.Rect>()
    if (close) markers.labels.forEach { label ->
        val p = project(label.latitude, label.longitude) ?: return@forEach
        val name = measurer.measure(label.name, bold.copy(color = Color.White))
        val note = if (label.note.isEmpty()) null else measurer.measure(label.note, bold.copy(color = Ball, fontSize = 11.sp))
        val gap = 6.dp.toPx()
        val w = name.size.width + (note?.let { gap + it.size.width } ?: 0f) + 20.dp.toPx()
        val h = 26.dp.toPx()
        val topLeft = Offset(p.x - w / 2f, p.y + 10.dp.toPx())
        val box = androidx.compose.ui.geometry.Rect(topLeft, Size(w, h)).inflate(3.dp.toPx())
        if (placed.any { it.overlaps(box) }) return@forEach
        placed += box
        drawRoundRect(Navy.copy(alpha = 0.86f), topLeft, Size(w, h), CornerRadius(h / 2f))
        val textY = topLeft.y + (h - name.size.height) / 2f
        drawText(name, topLeft = Offset(topLeft.x + 10.dp.toPx(), textY))
        note?.let { drawText(it, topLeft = Offset(topLeft.x + 10.dp.toPx() + name.size.width + gap, topLeft.y + (h - it.size.height) / 2f)) }
        hits += Offset(p.x, topLeft.y + h / 2f) to GlobeHit.City(label.cityId)
    }

    if (deep) markers.badges.forEach { badge ->
        val pin = project(badge.latitude, badge.longitude) ?: return@forEach
        val p = Offset(pin.x, pin.y - 22.dp.toPx())
        val multi = badge.count > 1
        val r = 17.dp.toPx()
        drawCircle(Color.Black.copy(alpha = 0.25f), radius = r, center = Offset(p.x, p.y + 2.dp.toPx()))
        drawCircle(if (multi) Ball else Navy, radius = r, center = p)
        drawCircle(if (multi) Navy else Gold, radius = r, center = p, style = Stroke(width = 2.dp.toPx()))
        if (multi) {
            drawCentered(measurer, badge.count.toString(), bold.copy(color = Navy, fontSize = 14.sp), Offset(p.x, p.y - 4.dp.toPx()))
            drawCentered(measurer, "CLUBS", bold.copy(color = Navy, fontSize = 7.sp), Offset(p.x, p.y + 8.dp.toPx()))
        } else {
            drawCentered(measurer, badge.monogram, bold.copy(color = Ball, fontSize = 12.sp), p)
        }
        hits += p to GlobeHit.Group(badge.index)
    }
    return hits
}

private fun DrawScope.drawCentered(measurer: TextMeasurer, text: String, style: TextStyle, center: Offset) {
    val layout = measurer.measure(text, style)
    drawText(layout, topLeft = Offset(center.x - layout.size.width / 2f, center.y - layout.size.height / 2f))
}
