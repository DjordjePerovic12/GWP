package com.global_water_polo.play_abroad.presentation.app.feed.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.AppSheet
import com.global_water_polo.play_abroad.core.components.InfoPanel
import com.global_water_polo.play_abroad.core.components.PhotoHero
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.core.components.TextLink
import com.global_water_polo.play_abroad.core.components.drawableNamed
import com.global_water_polo.play_abroad.presentation.app.feed.FeedState
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/** A Library folder (v29's courseSheet): its lessons; save it, buy it (Next) or open it with a plan. */
@Composable
fun CourseSheet(
    index: Int,
    state: FeedState,
    onCtaClick: () -> Unit,
    onOpenAllClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = GWPPATheme.colors
    val course = FeedState.COURSES[index]
    val locked = state.courseLocked(course)
    val saved = "c$index" in state.profile.saved
    var buyTapped by remember { mutableStateOf(false) }
    AppSheet(onDismiss = onDismiss) {
        PhotoHero(
            image = drawableNamed(course.image),
            kicker = "\"${course.category.uppercase()}\"",
            title = course.title,
            sub = stringResource(Res.string.feed_lessons, course.lessons.size) +
                (if (course.free) ". " + stringResource(Res.string.course_free) else if (locked) ". " + stringResource(Res.string.course_included) else ""),
            height = 220.dp
        )
        InfoPanel {
            course.lessons.forEachIndexed { i, lesson ->
                if (i > 0) HorizontalDivider(color = colors.lineGrey)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                    Text((i + 1).toString().padStart(2, '0'), style = GWPPATheme.typography.archivoBold12, color = colors.mistGrey, modifier = Modifier.width(30.dp))
                    Text(lesson, style = GWPPATheme.typography.archivoSemiBold15, color = colors.inkBlack, modifier = Modifier.weight(1f))
                    if (locked) Text("🔒", style = GWPPATheme.typography.archivoRegular13)
                }
            }
        }
        PrimaryButton(
            text = stringResource(
                when {
                    locked && state.membership.plan != null -> Res.string.course_buy
                    locked -> Res.string.course_start_trial
                    saved -> Res.string.course_saved
                    else -> Res.string.course_save
                }
            ),
            onClick = {
                if (locked && state.membership.plan != null) buyTapped = true
                onCtaClick()
            },
            modifier = Modifier.padding(top = 12.dp)
        )
        // Prototype: buying a single folder isn't set up yet; say so right under the button.
        if (buyTapped) SmallText(text = stringResource(Res.string.course_buy_toast), center = true, modifier = Modifier.fillMaxWidth())
        // Next members can buy a single folder, or open all of them with Elite.
        if (locked && state.membership.plan != null) {
            TextLink(text = stringResource(Res.string.course_open_all), onClick = onOpenAllClick, modifier = Modifier.align(Alignment.CenterHorizontally))
            SmallText(text = stringResource(Res.string.course_prices))
        }
        SmallText(text = stringResource(Res.string.course_fine))
    }
}
