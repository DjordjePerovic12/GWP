package com.global_water_polo.play_abroad.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Colors from the GWP spin onboarding prototype (friendly-melomakarona-953fa1.netlify.app)
val inkBlack = Color(0xFF0B1117) // --ink
val slateGrey = Color(0xFF56606A) // --ink-2
val mistGrey = Color(0xFF8B939A) // --ink-3
val lineGrey = Color(0xFFDCDDD6) // --line
val boneWhite = Color(0xFFF2F2EE) // --bone
val colorWhite = Color(0xFFFFFFFF) // --paper
val chlorineBlue = Color(0xFF1546FF) // --chlorine
val chlorineBlueSoft = Color(0xFFE6EBFF) // --chlorine-soft
val ballYellow = Color(0xFFF5C400) // --ball
val ballYellowSoft = Color(0xFFFFF5CC) // --ball-soft
val liveRed = Color(0xFFE5402C) // --live
val nightNavy = Color(0xFF04101E) // clock takeover background
val poolTeal = Color(0xFF0D8F7A) // feed card gradient
val graphiteGrey = Color(0xFF3B3F46) // feed card gradient
val eliteGold = Color(0xFFC9A96A) // GWP Elite membership

@Immutable
data class GWPPAColors(
    val inkBlack: Color,
    val slateGrey: Color,
    val mistGrey: Color,
    val lineGrey: Color,
    val boneWhite: Color,
    val colorWhite: Color,
    val chlorineBlue: Color,
    val chlorineBlueSoft: Color,
    val ballYellow: Color,
    val ballYellowSoft: Color,
    val liveRed: Color,
    val nightNavy: Color,
    val poolTeal: Color,
    val graphiteGrey: Color,
    val eliteGold: Color,
)

val LocalGWPPAColors = staticCompositionLocalOf {
    GWPPAColors(
        inkBlack = Color.Unspecified,
        slateGrey = Color.Unspecified,
        mistGrey = Color.Unspecified,
        lineGrey = Color.Unspecified,
        boneWhite = Color.Unspecified,
        colorWhite = Color.Unspecified,
        chlorineBlue = Color.Unspecified,
        chlorineBlueSoft = Color.Unspecified,
        ballYellow = Color.Unspecified,
        ballYellowSoft = Color.Unspecified,
        liveRed = Color.Unspecified,
        nightNavy = Color.Unspecified,
        poolTeal = Color.Unspecified,
        graphiteGrey = Color.Unspecified,
        eliteGold = Color.Unspecified,
    )
}

val gwppaColors = GWPPAColors(
    inkBlack = inkBlack,
    slateGrey = slateGrey,
    mistGrey = mistGrey,
    lineGrey = lineGrey,
    boneWhite = boneWhite,
    colorWhite = colorWhite,
    chlorineBlue = chlorineBlue,
    chlorineBlueSoft = chlorineBlueSoft,
    ballYellow = ballYellow,
    ballYellowSoft = ballYellowSoft,
    liveRed = liveRed,
    nightNavy = nightNavy,
    poolTeal = poolTeal,
    graphiteGrey = graphiteGrey,
    eliteGold = eliteGold,
)
