package com.global_water_polo.play_abroad.core.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.global_water_polo.play_abroad.core.components.globe.LocalGlobeTopInset

/**
 * The prototype's `.screen`: content starts under the globe badge, a bar on top, the question and
 * its inputs, and the call to action pinned to the bottom (scrolling when the keyboard is up).
 */
@Composable
fun OnboardingLayout(
    modifier: Modifier = Modifier,
    barStart: @Composable () -> Unit = {},
    barEnd: @Composable () -> Unit = {},
    showBar: Boolean = true,
    bottom: @Composable ColumnScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    BoxWithConstraints(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.ime)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .heightIn(min = maxHeight)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(top = LocalGlobeTopInset.current, start = 24.dp, end = 24.dp, bottom = 28.dp)
        ) {
            Column {
                if (showBar) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 40.dp)
                            .padding(bottom = 14.dp)
                    ) {
                        barStart()
                        barEnd()
                    }
                }
                content()
            }
            Column(modifier = Modifier.padding(top = 16.dp)) {
                bottom()
            }
        }
    }
}

/** Back button + the flow's progress bar, the left side of the onboarding bar. */
@Composable
fun BarStart(
    onBackClick: () -> Unit,
    progress: Float? = null
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        BackButton(onClick = onBackClick)
        if (progress != null) {
            Spacer(Modifier.padding(start = 14.dp))
            FlowBar(progress = progress)
        }
    }
}
