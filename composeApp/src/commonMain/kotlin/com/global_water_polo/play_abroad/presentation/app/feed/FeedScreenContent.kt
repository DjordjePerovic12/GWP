package com.global_water_polo.play_abroad.presentation.app.feed

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.global_water_polo.play_abroad.core.components.LocalStatusBarOverride
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.global_water_polo.play_abroad.presentation.app.feed.components.BookmarkIcon
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import com.global_water_polo.play_abroad.core.components.AppTab
import com.global_water_polo.play_abroad.core.components.AppTabBar
import com.global_water_polo.play_abroad.core.components.PrimaryButton
import com.global_water_polo.play_abroad.core.components.SmallText
import com.global_water_polo.play_abroad.core.components.drawableNamed
import com.global_water_polo.play_abroad.presentation.app.feed.components.ReelCard
import com.global_water_polo.play_abroad.resources.*
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/**
 * Feed tab (the prototype's feedTab): navy on every tab. For you pages through full-screen stories with the
 * For you / Library / Saved pill floating over them; Library and Saved scroll under the same pill.
 */
@Composable
fun FeedScreenContent(
    modifier: Modifier = Modifier,
    state: FeedState,
    onEvent: (FeedEvent) -> Unit
) {
    val colors = GWPPATheme.colors
    // White status bar icons on every tab: the feed is dark throughout.
    val statusOverride = LocalStatusBarOverride.current
    DisposableEffect(Unit) {
        statusOverride.value = true
        onDispose { statusOverride.value = null }
    }
    val statusTop = with(LocalDensity.current) { WindowInsets.statusBars.getTop(this).toDp() }
    Column(modifier = modifier.fillMaxSize().background(colors.nightNavy)) {
        Box(Modifier.weight(1f).fillMaxWidth()) {
            when (state.tab) {
                FeedTab.FOR_YOU -> {
                    val pager = rememberPagerState { FeedState.STORIES.size }
                    VerticalPager(state = pager, modifier = Modifier.fillMaxSize()) { i ->
                        val story = FeedState.STORIES[i]
                        ReelCard(
                            story = story,
                            locked = state.storyLocked(story),
                            liked = "r$i" in state.profile.likes,
                            saved = "r$i" in state.profile.saved,
                            onLikeClick = { onEvent(FeedEvent.OnLikeClick(i)) },
                            onSaveClick = { onEvent(FeedEvent.OnSaveStoryClick(i)) },
                            onActionClick = { onEvent(FeedEvent.OnStoryActionClick(i)) }
                        )
                    }
                    // `.rl-note`, on the first story only.
                    if (pager.currentPage == 0) {
                        Text(
                            stringResource(Res.string.feed_sample_note),
                            style = GWPPATheme.typography.archivoRegular13.copy(fontSize = 11.sp),
                            color = colors.colorWhite.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(top = statusTop + 52.dp, start = 20.dp, end = 20.dp)
                        )
                    }
                }
                FeedTab.LIBRARY, FeedTab.SAVED -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(start = 20.dp, end = 20.dp, top = statusTop + 62.dp, bottom = 24.dp)
                ) {
                    if (state.tab == FeedTab.LIBRARY) Library(state, onEvent) else Saved(state, onEvent)
                }
            }
            Tabs(
                current = state.tab,
                onClick = { onEvent(FeedEvent.OnFeedTabClick(it)) },
                modifier = Modifier.align(Alignment.TopCenter).padding(top = statusTop + 6.dp)
            )
        }
        AppTabBar(current = AppTab.FEED, onSelect = { onEvent(FeedEvent.OnTabClick(it)) }, dark = true)
    }
}

/** `.f3-seg`: a dark see-through pill, the open tab in ivory. */
@Composable
private fun Tabs(current: FeedTab, onClick: (FeedTab) -> Unit, modifier: Modifier = Modifier) {
    val colors = GWPPATheme.colors
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier
            .clip(CircleShape)
            .background(colors.nightNavy.copy(alpha = 0.5f))
            .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
            .padding(3.dp)
    ) {
        FeedTab.entries.forEach { tab ->
            val on = tab == current
            Text(
                stringResource(
                    when (tab) {
                        FeedTab.FOR_YOU -> Res.string.feed_for_you
                        FeedTab.LIBRARY -> Res.string.feed_library
                        FeedTab.SAVED -> Res.string.feed_saved
                    }
                ),
                style = GWPPATheme.typography.archivoBold13,
                color = if (on) colors.inkBlack else colors.colorWhite.copy(alpha = 0.7f),
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (on) colors.colorWhite else Color.Transparent)
                    .clickable { onClick(tab) }
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            )
        }
    }
}

/** `.f3-h` and `.f3-s`: the big capitals heading and its line. */
@Composable
private fun FeedHeading(title: String, sub: String) {
    val colors = GWPPATheme.colors
    Text(
        title.uppercase(),
        style = GWPPATheme.typography.archivoExpandedExtraBold32.copy(fontSize = 37.sp, lineHeight = 0.92.em, letterSpacing = (-0.01).em),
        color = colors.colorWhite
    )
    Text(sub, style = GWPPATheme.typography.archivoRegular15, color = colors.colorWhite.copy(alpha = 0.72f), modifier = Modifier.padding(top = 10.dp))
}

@Composable
private fun Library(state: FeedState, onEvent: (FeedEvent) -> Unit) {
    val colors = GWPPATheme.colors
    FeedHeading(
        title = stringResource(Res.string.feed_lib_title),
        sub = stringResource(
            when {
                state.membership.elite -> Res.string.feed_lib_elite
                state.membership.plan != null -> Res.string.feed_lib_next
                else -> Res.string.feed_lib_none
            }
        )
    )
    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 20.dp)) {
        FeedState.COURSES.chunked(2).forEachIndexed { row, pair ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                pair.forEachIndexed { col, course ->
                    val index = row * 2 + col
                    CourseTile(
                        course = course,
                        locked = state.courseLocked(course),
                        saved = "c$index" in state.profile.saved,
                        onClick = { onEvent(FeedEvent.OnCourseClick(index)) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
    Text(
        stringResource(Res.string.feed_lib_fine),
        style = GWPPATheme.typography.archivoRegular13.copy(fontSize = 12.sp),
        color = colors.colorWhite.copy(alpha = 0.5f),
        modifier = Modifier.padding(top = 14.dp)
    )
}

/** `.lb-c`: a 3:4 photo tile, darkened at the bottom under gold category, name and lessons. */
@Composable
private fun CourseTile(course: Course, locked: Boolean, saved: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = GWPPATheme.colors
    Box(modifier = modifier.aspectRatio(0.75f).clip(RoundedCornerShape(18.dp)).background(colors.nightNavy).clickable(onClick = onClick)) {
        drawableNamed(course.image)?.let {
            Image(painterResource(it), null, contentScale = ContentScale.Crop, modifier = Modifier.matchParentSize())
        }
        Box(Modifier.matchParentSize().background(Brush.verticalGradient(0.35f to Color.Transparent, 1f to colors.nightNavy.copy(alpha = 0.9f))))
        Column(Modifier.align(Alignment.BottomStart).padding(12.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                course.category.uppercase(),
                style = GWPPATheme.typography.archivoBold11.copy(fontWeight = FontWeight.ExtraBold, fontSize = 10.sp, letterSpacing = 0.14.em),
                color = colors.eliteGold
            )
            Text(course.title, style = GWPPATheme.typography.archivoExpandedBold16.copy(lineHeight = 1.08.em), color = colors.colorWhite)
            Text(
                if (course.free) stringResource(Res.string.feed_lessons_free, course.lessons.size) else stringResource(Res.string.feed_lessons, course.lessons.size),
                style = GWPPATheme.typography.archivoRegular13.copy(fontSize = 12.sp),
                color = colors.colorWhite.copy(alpha = 0.65f)
            )
        }
        // `.lb-chain`: locked courses are dimmed under a gold lock.
        if (locked) {
            Box(Modifier.matchParentSize().background(colors.nightNavy.copy(alpha = 0.42f)), contentAlignment = Alignment.Center) {
                Text("🔒", style = GWPPATheme.typography.archivoExpandedBold27)
            }
        }
        if (saved) BookmarkIcon(colors.eliteGold, Modifier.align(Alignment.TopEnd).padding(10.dp).size(20.dp))
    }
}

@Composable
private fun Saved(state: FeedState, onEvent: (FeedEvent) -> Unit) {
    val colors = GWPPATheme.colors
    val stories = FeedState.STORIES.indices.filter { "r$it" in state.profile.saved }
    val courses = FeedState.COURSES.indices.filter { "c$it" in state.profile.saved }
    if (stories.isEmpty() && courses.isEmpty()) {
        FeedHeading(title = stringResource(Res.string.feed_saved_title), sub = stringResource(Res.string.feed_saved_empty))
        // `.cta3`: an ivory pill with spaced capitals.
        Text(
            stringResource(Res.string.feed_browse).uppercase(),
            style = GWPPATheme.typography.archivoExpandedBold16.copy(fontSize = 13.sp, letterSpacing = 0.05.em),
            color = colors.inkBlack,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 18.dp)
                .fillMaxWidth()
                .height(54.dp)
                .clip(CircleShape)
                .background(colors.colorWhite)
                .clickable { onEvent(FeedEvent.OnFeedTabClick(FeedTab.FOR_YOU)) }
                .wrapContentHeight(Alignment.CenterVertically)
        )
        return
    }
    Text(
        stringResource(Res.string.feed_saved_title).uppercase(),
        style = GWPPATheme.typography.archivoExpandedExtraBold32.copy(fontSize = 37.sp, lineHeight = 0.92.em),
        color = colors.colorWhite
    )
    if (stories.isNotEmpty()) {
        SavedKicker(stringResource(Res.string.feed_saved_stories))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            stories.forEach { i ->
                val story = FeedState.STORIES[i]
                SavedRow(story.image, story.title, story.category, onClick = { onEvent(FeedEvent.OnFeedTabClick(FeedTab.FOR_YOU)) })
            }
        }
    }
    if (courses.isNotEmpty()) {
        SavedKicker(stringResource(Res.string.feed_saved_courses))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            courses.forEach { i ->
                val course = FeedState.COURSES[i]
                SavedRow(course.image, course.title, stringResource(Res.string.feed_lessons, course.lessons.size), onClick = { onEvent(FeedEvent.OnCourseClick(i)) })
            }
        }
    }
}

/** `.f3-k`: a gold spaced-capitals label over a saved group. */
@Composable
private fun SavedKicker(text: String) {
    Text(
        text.uppercase(),
        style = GWPPATheme.typography.archivoBold11.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 0.16.em),
        color = GWPPATheme.colors.eliteGold,
        modifier = Modifier.padding(top = 22.dp, bottom = 10.dp)
    )
}

/** `.sv-r`: a faint see-through row with the picture, title and line. */
@Composable
private fun SavedRow(image: String, title: String, sub: String, onClick: () -> Unit) {
    val colors = GWPPATheme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.04f))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        drawableNamed(image)?.let {
            Image(painterResource(it), null, contentScale = ContentScale.Crop, modifier = Modifier.size(width = 52.dp, height = 68.dp).clip(RoundedCornerShape(10.dp)))
        }
        Column {
            Text(title, style = GWPPATheme.typography.archivoBold16, color = colors.colorWhite)
            Text(sub, style = GWPPATheme.typography.archivoRegular13, color = colors.colorWhite.copy(alpha = 0.6f))
        }
    }
}

@Preview
@Composable
private fun FeedScreenContentPreview() {
    GWPPATheme {
        FeedScreenContent(state = FeedState(tab = FeedTab.LIBRARY), onEvent = {})
    }
}
