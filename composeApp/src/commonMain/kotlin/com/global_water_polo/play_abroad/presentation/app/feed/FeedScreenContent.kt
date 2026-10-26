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

/** Feed tab (the prototype's feedTab): For you on a dark stage, then the Library and Saved. */
@Composable
fun FeedScreenContent(
    modifier: Modifier = Modifier,
    state: FeedState,
    onEvent: (FeedEvent) -> Unit
) {
    val colors = GWPPATheme.colors
    val dark = state.tab == FeedTab.FOR_YOU
    Column(modifier = modifier.fillMaxSize().background(if (dark) colors.nightNavy else colors.boneWhite)) {
        BoxWithConstraints(Modifier.weight(1f).fillMaxWidth()) {
            // One reel fills what is visible under the status bar and the For you / Library / Saved row.
            val statusTop = with(LocalDensity.current) { WindowInsets.statusBars.getTop(this).toDp() }
            val reelHeight = maxHeight - statusTop - 64.dp
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .verticalScroll(rememberScrollState())
                    .padding(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 20.dp)
            ) {
                Tabs(current = state.tab, dark = dark, onClick = { onEvent(FeedEvent.OnFeedTabClick(it)) })
                when (state.tab) {
                    FeedTab.FOR_YOU -> FeedState.STORIES.forEachIndexed { i, story ->
                        ReelCard(
                            story = story,
                            locked = state.storyLocked(story),
                            liked = "r$i" in state.profile.likes,
                            saved = "r$i" in state.profile.saved,
                            showNote = i == 0,
                            height = reelHeight,
                            onLikeClick = { onEvent(FeedEvent.OnLikeClick(i)) },
                            onSaveClick = { onEvent(FeedEvent.OnSaveStoryClick(i)) },
                            onActionClick = { onEvent(FeedEvent.OnStoryActionClick(i)) }
                        )
                    }
                    FeedTab.LIBRARY -> Library(state, onEvent)
                    FeedTab.SAVED -> Saved(state, onEvent)
                }
            }
        }
        AppTabBar(current = AppTab.FEED, onSelect = { onEvent(FeedEvent.OnTabClick(it)) }, dark = dark)
    }
}

@Composable
private fun Tabs(current: FeedTab, dark: Boolean, onClick: (FeedTab) -> Unit) {
    val colors = GWPPATheme.colors
    Row(horizontalArrangement = Arrangement.spacedBy(20.dp), modifier = Modifier.padding(horizontal = 6.dp)) {
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
                style = GWPPATheme.typography.archivoExpandedBold17,
                color = when {
                    dark && on -> colors.colorWhite
                    dark -> colors.colorWhite.copy(alpha = 0.45f)
                    on -> colors.inkBlack
                    else -> colors.mistGrey
                },
                modifier = Modifier.clickable { onClick(tab) }.padding(vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun Library(state: FeedState, onEvent: (FeedEvent) -> Unit) {
    val colors = GWPPATheme.colors
    Text(stringResource(Res.string.feed_lib_title), style = GWPPATheme.typography.archivoExpandedBold27, color = colors.inkBlack, modifier = Modifier.padding(horizontal = 6.dp))
    Text(
        stringResource(
            when {
                state.membership.elite -> Res.string.feed_lib_elite
                state.membership.plan != null -> Res.string.feed_lib_next
                else -> Res.string.feed_lib_none
            }
        ),
        style = GWPPATheme.typography.archivoRegular15,
        color = colors.slateGrey,
        modifier = Modifier.padding(horizontal = 6.dp)
    )
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
    SmallText(text = stringResource(Res.string.feed_lib_fine), modifier = Modifier.padding(horizontal = 6.dp))
}

@Composable
private fun CourseTile(course: Course, locked: Boolean, saved: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = GWPPATheme.colors
    Box(modifier = modifier.aspectRatio(0.78f).clip(RoundedCornerShape(18.dp)).background(colors.nightNavy).clickable(onClick = onClick)) {
        drawableNamed(course.image)?.let {
            Image(painterResource(it), null, contentScale = ContentScale.Crop, modifier = Modifier.matchParentSize().alpha(if (locked) 0.55f else 1f))
        }
        Box(Modifier.matchParentSize().background(Brush.verticalGradient(0.35f to Color.Transparent, 1f to colors.nightNavy.copy(alpha = 0.92f))))
        Column(Modifier.align(Alignment.BottomStart).padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(course.category.uppercase(), style = GWPPATheme.typography.archivoBold11, color = colors.eliteGold)
            Text(course.title, style = GWPPATheme.typography.archivoExpandedBold16, color = colors.colorWhite)
            Text(
                if (course.free) stringResource(Res.string.feed_lessons_free, course.lessons.size) else stringResource(Res.string.feed_lessons, course.lessons.size),
                style = GWPPATheme.typography.archivoRegular13,
                color = colors.colorWhite.copy(alpha = 0.7f)
            )
        }
        if (locked) Text("🔒", style = GWPPATheme.typography.archivoExpandedBold21, modifier = Modifier.align(Alignment.Center))
        if (saved) Text("★", style = GWPPATheme.typography.archivoExpandedBold21, color = colors.ballYellow, modifier = Modifier.align(Alignment.TopEnd).padding(10.dp))
    }
}

@Composable
private fun Saved(state: FeedState, onEvent: (FeedEvent) -> Unit) {
    val colors = GWPPATheme.colors
    val stories = FeedState.STORIES.indices.filter { "r$it" in state.profile.saved }
    val courses = FeedState.COURSES.indices.filter { "c$it" in state.profile.saved }
    Text(stringResource(Res.string.feed_saved_title), style = GWPPATheme.typography.archivoExpandedBold27, color = colors.inkBlack, modifier = Modifier.padding(horizontal = 6.dp))
    if (stories.isEmpty() && courses.isEmpty()) {
        Text(stringResource(Res.string.feed_saved_empty), style = GWPPATheme.typography.archivoRegular15, color = colors.slateGrey, modifier = Modifier.padding(horizontal = 6.dp))
        PrimaryButton(text = stringResource(Res.string.feed_browse), onClick = { onEvent(FeedEvent.OnFeedTabClick(FeedTab.FOR_YOU)) })
        return
    }
    if (stories.isNotEmpty()) {
        Text(stringResource(Res.string.feed_saved_stories), style = GWPPATheme.typography.archivoBold13, color = colors.slateGrey, modifier = Modifier.padding(horizontal = 6.dp))
        stories.forEach { i ->
            val story = FeedState.STORIES[i]
            SavedRow(story.image, story.title, story.category, onClick = { onEvent(FeedEvent.OnFeedTabClick(FeedTab.FOR_YOU)) })
        }
    }
    if (courses.isNotEmpty()) {
        Text(stringResource(Res.string.feed_saved_courses), style = GWPPATheme.typography.archivoBold13, color = colors.slateGrey, modifier = Modifier.padding(horizontal = 6.dp))
        courses.forEach { i ->
            val course = FeedState.COURSES[i]
            SavedRow(course.image, course.title, stringResource(Res.string.feed_lessons, course.lessons.size), onClick = { onEvent(FeedEvent.OnCourseClick(i)) })
        }
    }
}

@Composable
private fun SavedRow(image: String, title: String, sub: String, onClick: () -> Unit) {
    val colors = GWPPATheme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(colors.colorWhite)
            .border(1.dp, colors.lineGrey, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        drawableNamed(image)?.let {
            Image(painterResource(it), null, contentScale = ContentScale.Crop, modifier = Modifier.size(width = 52.dp, height = 68.dp).clip(RoundedCornerShape(10.dp)))
        }
        Column {
            Text(title, style = GWPPATheme.typography.archivoBold16, color = colors.inkBlack)
            Text(sub, style = GWPPATheme.typography.archivoRegular13, color = colors.slateGrey)
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
