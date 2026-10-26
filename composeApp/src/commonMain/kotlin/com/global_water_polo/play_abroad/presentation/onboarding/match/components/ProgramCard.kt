package com.global_water_polo.play_abroad.presentation.onboarding.match.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import com.global_water_polo.play_abroad.domain.model.Program
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

val Program.title: StringResource
    get() = when (this) {
        Program.EXPLORE -> Res.string.prog_explore
        Program.FALL -> Res.string.prog_fall
        Program.SPRING -> Res.string.prog_spring
        Program.SUMMER -> Res.string.prog_summer
        Program.GAP_YEAR -> Res.string.prog_gapyear
    }

val Program.duration: StringResource
    get() = when (this) {
        Program.EXPLORE -> Res.string.prog_explore_when
        Program.FALL -> Res.string.prog_fall_when
        Program.SPRING -> Res.string.prog_spring_when
        Program.SUMMER -> Res.string.prog_summer_when
        Program.GAP_YEAR -> Res.string.prog_gapyear_when
    }

val Program.text: StringResource
    get() = when (this) {
        Program.EXPLORE -> Res.string.prog_explore_text
        Program.FALL -> Res.string.prog_fall_text
        Program.SPRING -> Res.string.prog_spring_text
        Program.SUMMER -> Res.string.prog_summer_text
        Program.GAP_YEAR -> Res.string.prog_gapyear_text
    }

// Same photo per program as the prototype (the file names are from its earlier program list).
val Program.image: DrawableResource
    get() = when (this) {
        Program.EXPLORE -> Res.drawable.p_explore
        Program.FALL -> Res.drawable.p_immersion
        Program.SPRING -> Res.drawable.p_residency
        Program.SUMMER -> Res.drawable.p_semester
        Program.GAP_YEAR -> Res.drawable.p_gapyear
    }

/** The prototype's `.mcard`: photo with a tag (and a lock when Elite is needed), name, when, one paragraph. */
@Composable
fun ProgramCard(
    program: Program,
    name: String,
    tag: String,
    lockLabel: String?,
    whenLabel: String,
    body: String,
    modifier: Modifier = Modifier
) {
    val colors = GWPPATheme.colors
    val shape = RoundedCornerShape(22.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.colorWhite)
            .border(if (program.flagship) 2.dp else 1.dp, if (program.flagship) colors.ballYellow else colors.lineGrey, shape)
    ) {
        Box(Modifier.fillMaxWidth().height(170.dp)) {
            Image(
                painter = painterResource(program.image),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(170.dp)
            )
            Pill(tag, Modifier.align(Alignment.TopStart).padding(12.dp))
            if (lockLabel != null) Pill(lockLabel, Modifier.align(Alignment.TopEnd).padding(12.dp), gold = true)
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(16.dp)) {
            Text(text = name, style = GWPPATheme.typography.archivoExpandedBold21, color = colors.inkBlack)
            Text(text = whenLabel, style = GWPPATheme.typography.archivoBold13, color = colors.chlorineBlue, modifier = Modifier.padding(top = 2.dp))
            Text(text = body, style = GWPPATheme.typography.archivoRegular15, color = colors.slateGrey, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
private fun Pill(text: String, modifier: Modifier = Modifier, gold: Boolean = false) {
    val colors = GWPPATheme.colors
    Text(
        text = text,
        style = GWPPATheme.typography.archivoBold12,
        color = if (gold) colors.nightNavy else colors.colorWhite,
        modifier = modifier
            .clip(CircleShape)
            .background(if (gold) colors.eliteGold else colors.nightNavy.copy(alpha = 0.85f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}
