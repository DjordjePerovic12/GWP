package com.global_water_polo.play_abroad.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

//TODO: Placeholder palette. Replace with the Figma colors (see the amplitudo-kmp-compose theme reference)
val colorBlack = Color(0xFF000000)
val colorWhite = Color(0xFFFFFFFF)
val errorRed = Color(0xFFC13515)

@Immutable
data class GWPPAColors(
    val colorBlack: Color,
    val colorWhite: Color,
    val errorRed: Color,
)

val LocalGWPPAColors = staticCompositionLocalOf {
    GWPPAColors(
        colorBlack = Color.Unspecified,
        colorWhite = Color.Unspecified,
        errorRed = Color.Unspecified,
    )
}

val gwppaColors = GWPPAColors(
    colorBlack = colorBlack,
    colorWhite = colorWhite,
    errorRed = errorRed,
)
