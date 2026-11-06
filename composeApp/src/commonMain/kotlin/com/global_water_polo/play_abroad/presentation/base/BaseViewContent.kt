package com.global_water_polo.play_abroad.presentation.base

import com.global_water_polo.play_abroad.core.components.FullScreenLoader
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

/**
    A base wrapper composable for structuring screen content.

    This function provides a common layout structure for screens, handling:
    - System bar padding adjustments (optional).
    - Displaying a top app bar and floating action button (FAB).
    - Injecting content dynamically based on the [viewState].
    - Showing a loading indicator when [viewState.isLoading] is `true`.

    It ensures that all screen content is properly padded and structured within a `Scaffold` layout.

    @param modifier The [Modifier] to be applied to the layout.
    @param viewState The current state of the screen, containing UI-related states such as loading.
    @param applySystemBarsPadding If `true`, applies padding for system bars (default: `true`).
    @param topBar A composable lambda function for defining the top app bar (default: empty).
    @param containerColor Scaffold background; transparent by default so the app-wide pool stage shows through.
    @param fab A composable lambda function for defining the floating action button (FAB) (default: empty).
    @param content The main content of the screen, provided as a lambda function with access to [viewState].

    Check ExampleScreen for example usage
 **/

@Composable
fun BaseContentView(
    modifier: Modifier = Modifier,
    viewState: BaseViewState,
    applySystemBarsPadding: Boolean = true,
    topBar: @Composable () -> Unit = {},
    fab: @Composable () -> Unit = {},
    containerColor: Color = Color.Transparent,
    content: @Composable (BaseViewState) -> Unit
) {

    val customModifier = remember(modifier, applySystemBarsPadding) {
        if (applySystemBarsPadding) {
            modifier
                .systemBarsPadding()
                .imePadding()
                .fillMaxSize()
        } else {
            modifier
                .imePadding()
                .fillMaxSize()
        }
    }

    Scaffold(
        modifier = customModifier,
        topBar = topBar,
        floatingActionButton = fab,
        // Without system bar padding the screen handles insets itself (e.g. under the globe badge).
        contentWindowInsets = if (applySystemBarsPadding) ScaffoldDefaults.contentWindowInsets else WindowInsets(0),
        containerColor = containerColor
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            content(viewState)
            if (viewState.isLoading) {
                FullScreenLoader()
            }
        }
    }
}
