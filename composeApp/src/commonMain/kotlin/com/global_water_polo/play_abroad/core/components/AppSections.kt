package com.global_water_polo.play_abroad.core.components

import androidx.compose.foundation.Image
import com.global_water_polo.play_abroad.resources.allDrawableResources
import org.jetbrains.compose.resources.DrawableResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Brush
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.resources.Res
import com.global_water_polo.play_abroad.resources.brand_long
import com.global_water_polo.play_abroad.resources.brand_short
import com.global_water_polo.play_abroad.resources.gwp_logo
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** Logo + GLOBAL WATER POLO, the left side of the app screens' bar; [short] says GWP where a chip shares the row. */
@Composable
fun BrandBar(modifier: Modifier = Modifier, short: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = modifier) {
        Image(painter = painterResource(Res.drawable.gwp_logo), contentDescription = null, modifier = Modifier.size(26.dp))
        Text(text = stringResource(if (short) Res.string.brand_short else Res.string.brand_long), style = GWPPATheme.typography.archivoExpandedBold12, color = GWPPATheme.colors.inkBlack)
    }
}

/** Section heading with an optional grey note on the right (`.h2`). */
@Composable
fun SectionTitle(title: String, modifier: Modifier = Modifier, note: String? = null) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
        modifier = modifier.fillMaxWidth().padding(top = 24.dp, bottom = 10.dp)
    ) {
        Text(text = title, style = GWPPATheme.typography.archivoExpandedBold17, color = GWPPATheme.colors.inkBlack)
        if (note != null) Text(text = note, style = GWPPATheme.typography.archivoSemiBold13, color = GWPPATheme.colors.mistGrey)
    }
}

/** Premium offer row (`.prem button`); [hero] = the dark one on top. */
@Composable
fun OfferCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badge: String? = null,
    hero: Boolean = false
) {
    val colors = GWPPATheme.colors
    val shape = RoundedCornerShape(16.dp)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (hero) colors.inkBlack else colors.colorWhite)
            .border(1.dp, if (hero) colors.inkBlack else colors.lineGrey, shape)
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = title, style = GWPPATheme.typography.archivoBold16, color = if (hero) colors.colorWhite else colors.inkBlack)
            Text(text = subtitle, style = GWPPATheme.typography.archivoRegular13, color = if (hero) Color.White.copy(alpha = 0.7f) else colors.slateGrey)
        }
        if (badge != null) {
            Text(
                text = badge,
                style = GWPPATheme.typography.archivoBold11,
                color = colors.inkBlack,
                modifier = Modifier
                    .background(if (hero) colors.ballYellow else colors.ballYellowSoft, CircleShape)
                    .padding(horizontal = 9.dp, vertical = 4.dp)
            )
        }
    }
}

/** A drawable by its file name ("c_kotor", "pool_3"), for images chosen from data; null when there is none. */
fun drawableNamed(name: String): DrawableResource? = Res.allDrawableResources[name]

/** City photo for a Globe+ city id ("herceg-novi" -> c_herceg_novi). */
fun cityImage(cityId: String): DrawableResource? = drawableNamed("c_" + cityId.replace('-', '_'))

/**
 * A photo with a dark gradient and text over it: the prototype's `.hero3` / `.pd-hero`.
 * [kicker] is the small quoted label ("MY PATHWAY"), [title] the big line.
 */
@Composable
fun PhotoHero(
    image: DrawableResource?,
    title: String,
    modifier: Modifier = Modifier,
    kicker: String? = null,
    sub: String? = null,
    height: Dp = 300.dp,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    val colors = GWPPATheme.colors
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = height)
            .clip(RoundedCornerShape(22.dp))
            .background(colors.nightNavy)
    ) {
        if (image != null) {
            Image(painter = painterResource(image), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.matchParentSize())
        }
        Box(
            Modifier
                .matchParentSize()
                .background(Brush.verticalGradient(0f to colors.nightNavy.copy(alpha = 0.1f), 0.45f to colors.nightNavy.copy(alpha = 0.55f), 1f to colors.nightNavy.copy(alpha = 0.94f)))
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.align(Alignment.BottomStart).padding(20.dp)
        ) {
            kicker?.let { Text(it, style = GWPPATheme.typography.archivoBold12, color = colors.eliteGold) }
            Text(title.uppercase(), style = GWPPATheme.typography.archivoExpandedExtraBold24, color = colors.colorWhite)
            sub?.let { Text(it, style = GWPPATheme.typography.archivoRegular14, color = colors.colorWhite.copy(alpha = 0.8f)) }
            content()
        }
    }
}

/** The app's bottom sheet (the prototype's `.sheet`): bone background, scrolling content, padded. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSheet(
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = GWPPATheme.colors.boneWhite
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, bottom = 32.dp),
            content = content
        )
    }
}

/** A heading inside a sheet (`.pd h4`). */
@Composable
fun SheetHeading(text: String) {
    Text(text, style = GWPPATheme.typography.archivoExpandedBold16, color = GWPPATheme.colors.inkBlack, modifier = Modifier.padding(top = 10.dp))
}

/** A plain bulleted list (`.pd-ul`). */
@Composable
fun BulletList(items: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        items.forEach { item ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("•", style = GWPPATheme.typography.archivoBold16, color = GWPPATheme.colors.chlorineBlue)
                Text(item, style = GWPPATheme.typography.archivoRegular15, color = GWPPATheme.colors.inkBlack)
            }
        }
    }
}

/** A label and value row inside a white panel (`.irow`). */
@Composable
fun InfoRow(label: String, value: String, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(vertical = 12.dp)
    ) {
        Text(label, style = GWPPATheme.typography.archivoRegular15, color = GWPPATheme.colors.slateGrey, modifier = Modifier.weight(1f))
        Text(value, style = GWPPATheme.typography.archivoBold13, color = GWPPATheme.colors.inkBlack)
        if (onClick != null) Text("  ›", style = GWPPATheme.typography.archivoBold16, color = GWPPATheme.colors.mistGrey)
    }
}

/** A white rounded panel holding [InfoRow]s. */
@Composable
fun InfoPanel(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(18.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(GWPPATheme.colors.colorWhite)
            .border(1.dp, GWPPATheme.colors.lineGrey, shape)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        content = content
    )
}
