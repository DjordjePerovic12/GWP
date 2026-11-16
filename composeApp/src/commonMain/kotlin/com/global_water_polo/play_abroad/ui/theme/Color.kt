package com.global_water_polo.play_abroad.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Colors from the GWP prototype, v29 palette (navy, gold, ivory): gwp-app-v29.netlify.app
val inkBlack = Color(0xFF0F2834) // --ink (v29 petrol)
val slateGrey = Color(0xFF4B5A62) // --ink-2
val mistGrey = Color(0xFF6A747A) // --ink-3
val lineGrey = Color(0xFFE1D9CA) // --line
val boneWhite = Color(0xFFF2EDE3) // --bone
val colorWhite = Color(0xFFFBF8F2) // --paper
val chlorineBlue = Color(0xFF0F2834) // --chlorine (v29: petrol, no more blue)
val chlorineBlueSoft = Color(0xFFF3E8CC) // --chlorine-soft
val ballYellow = Color(0xFFDFAE3B) // --ball (v29 gold)
val ballYellowSoft = Color(0xFFF6EAC8) // --ball-soft
val liveRed = Color(0xFFE5402C) // --live
val nightNavy = Color(0xFF04101E) // clock takeover background
val poolTeal = Color(0xFF0D8F7A) // feed card gradient
val graphiteGrey = Color(0xFF3B3F46) // feed card gradient
val eliteGold = Color(0xFFDDB45C) // --gold3: GWP Elite and gold accents on dark
val antiqueGold = Color(0xFFB08A3E) // --gold: kickers on light cards
val sandBeige = Color(0xFFEBE5D8) // --chlorine-soft in the athlete app: segment bars, the % pill, photo stripes
val sandLight = Color(0xFFEFEADF) // the lighter stripe of an empty photo card

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
    val antiqueGold: Color,
    val sandBeige: Color,
    val sandLight: Color,
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
        antiqueGold = Color.Unspecified,
        sandBeige = Color.Unspecified,
        sandLight = Color.Unspecified,
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
    antiqueGold = antiqueGold,
    sandBeige = sandBeige,
    sandLight = sandLight,
)

/**
 * The athlete app's palette (the prototype's `.device.app` overrides on top of the v29 palette): a softer
 * beige and a lighter gold. App.kt switches to it on the app's tabs; onboarding keeps [gwppaColors].
 */
val appColors = gwppaColors.copy(
    chlorineBlueSoft = sandBeige, // --chlorine-soft in the app
    ballYellow = eliteGold, // --ball in the app (#DDB45C)
    ballYellowSoft = Color(0xFFF3ECDD)
)
