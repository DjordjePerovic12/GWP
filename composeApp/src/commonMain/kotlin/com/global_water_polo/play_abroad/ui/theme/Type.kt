package com.global_water_polo.play_abroad.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp

/**
  TODO: Placeholder typography on the system font. Replace with the Figma fonts:
  font files go in composeResources/font as <font>_<weight>.ttf and are loaded in composition, e.g.

  val dmSansSemiBold: FontFamily
      @Composable get() = FontFamily(Font(Res.font.dm_sans_semibold))

  Style names are <fontCamel><Weight><size> (dmSansSemiBold16), never headlineLarge/bodyMedium.
 **/

val defaultRegular: FontFamily
    @Composable get() = FontFamily.Default

@Immutable
data class GWPPATypography(
    val defaultRegular12: TextStyle,
    val defaultRegular14: TextStyle,
    val defaultRegular16: TextStyle,
)

val LocalGWPPATypography = staticCompositionLocalOf {
    GWPPATypography(
        defaultRegular12 = TextStyle.Default,
        defaultRegular14 = TextStyle.Default,
        defaultRegular16 = TextStyle.Default,
    )
}

val gwppaTypography: GWPPATypography
    @Composable get() = GWPPATypography(
        defaultRegular12 = TextStyle(
            fontFamily = defaultRegular,
            fontSize = 12.sp,
        ),
        defaultRegular14 = TextStyle(
            fontFamily = defaultRegular,
            fontSize = 14.sp,
        ),
        defaultRegular16 = TextStyle(
            fontFamily = defaultRegular,
            fontSize = 16.sp,
        ),
    )
