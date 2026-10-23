package com.global_water_polo.play_abroad.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun GWPPATheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalGWPPAColors provides gwppaColors,
        LocalGWPPATypography provides gwppaTypography,
        content = content
    )
}

object GWPPATheme {
    val colors: GWPPAColors
        @Composable get() = LocalGWPPAColors.current
    val typography: GWPPATypography
        @Composable get() = LocalGWPPATypography.current
}
