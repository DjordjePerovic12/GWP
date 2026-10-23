package com.global_water_polo.play_abroad.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import com.global_water_polo.play_abroad.resources.Res
import com.global_water_polo.play_abroad.resources.archivo_bold
import com.global_water_polo.play_abroad.resources.archivo_expanded_bold
import com.global_water_polo.play_abroad.resources.archivo_expanded_extrabold
import com.global_water_polo.play_abroad.resources.archivo_expanded_semibold
import com.global_water_polo.play_abroad.resources.archivo_regular
import com.global_water_polo.play_abroad.resources.archivo_semibold

// Archivo, as in the prototype. Expanded = the prototype's font-stretch 122% (wide) headings.
val archivoRegular: FontFamily
    @Composable get() = FontFamily(Font(Res.font.archivo_regular))
val archivoSemiBold: FontFamily
    @Composable get() = FontFamily(Font(Res.font.archivo_semibold))
val archivoBold: FontFamily
    @Composable get() = FontFamily(Font(Res.font.archivo_bold))
val archivoExpandedSemiBold: FontFamily
    @Composable get() = FontFamily(Font(Res.font.archivo_expanded_semibold))
val archivoExpandedBold: FontFamily
    @Composable get() = FontFamily(Font(Res.font.archivo_expanded_bold))
val archivoExpandedExtraBold: FontFamily
    @Composable get() = FontFamily(Font(Res.font.archivo_expanded_extrabold))

@Immutable
data class GWPPATypography(
    val archivoRegular13: TextStyle,
    val archivoRegular14: TextStyle,
    val archivoRegular15: TextStyle,
    val archivoRegular16: TextStyle,
    val archivoSemiBold11: TextStyle,
    val archivoSemiBold12: TextStyle,
    val archivoSemiBold13: TextStyle,
    val archivoSemiBold14: TextStyle,
    val archivoSemiBold15: TextStyle,
    val archivoSemiBold18: TextStyle,
    val archivoBold11: TextStyle,
    val archivoBold12: TextStyle,
    val archivoBold13: TextStyle,
    val archivoBold16: TextStyle,
    val archivoBold17: TextStyle,
    val archivoExpandedSemiBold21: TextStyle,
    val archivoExpandedBold12: TextStyle,
    val archivoExpandedBold16: TextStyle,
    val archivoExpandedBold17: TextStyle,
    val archivoExpandedBold19: TextStyle,
    val archivoExpandedBold21: TextStyle,
    val archivoExpandedBold27: TextStyle,
    val archivoExpandedBold32: TextStyle,
    val archivoExpandedBold37: TextStyle,
    val archivoExpandedBold38: TextStyle,
    val archivoExpandedExtraBold13: TextStyle,
    val archivoExpandedExtraBold23: TextStyle,
    val archivoExpandedExtraBold24: TextStyle,
    val archivoExpandedExtraBold30: TextStyle,
    val archivoExpandedExtraBold32: TextStyle,
    val archivoExpandedExtraBold50: TextStyle,
    val archivoExpandedExtraBold51: TextStyle,
    val archivoExpandedExtraBold104: TextStyle,
)

val LocalGWPPATypography = staticCompositionLocalOf {
    GWPPATypography(
        archivoRegular13 = TextStyle.Default,
        archivoRegular14 = TextStyle.Default,
        archivoRegular15 = TextStyle.Default,
        archivoRegular16 = TextStyle.Default,
        archivoSemiBold11 = TextStyle.Default,
        archivoSemiBold12 = TextStyle.Default,
        archivoSemiBold13 = TextStyle.Default,
        archivoSemiBold14 = TextStyle.Default,
        archivoSemiBold15 = TextStyle.Default,
        archivoSemiBold18 = TextStyle.Default,
        archivoBold11 = TextStyle.Default,
        archivoBold12 = TextStyle.Default,
        archivoBold13 = TextStyle.Default,
        archivoBold16 = TextStyle.Default,
        archivoBold17 = TextStyle.Default,
        archivoExpandedSemiBold21 = TextStyle.Default,
        archivoExpandedBold12 = TextStyle.Default,
        archivoExpandedBold16 = TextStyle.Default,
        archivoExpandedBold17 = TextStyle.Default,
        archivoExpandedBold19 = TextStyle.Default,
        archivoExpandedBold21 = TextStyle.Default,
        archivoExpandedBold27 = TextStyle.Default,
        archivoExpandedBold32 = TextStyle.Default,
        archivoExpandedBold37 = TextStyle.Default,
        archivoExpandedBold38 = TextStyle.Default,
        archivoExpandedExtraBold13 = TextStyle.Default,
        archivoExpandedExtraBold23 = TextStyle.Default,
        archivoExpandedExtraBold24 = TextStyle.Default,
        archivoExpandedExtraBold30 = TextStyle.Default,
        archivoExpandedExtraBold32 = TextStyle.Default,
        archivoExpandedExtraBold50 = TextStyle.Default,
        archivoExpandedExtraBold51 = TextStyle.Default,
        archivoExpandedExtraBold104 = TextStyle.Default,
    )
}

val gwppaTypography: GWPPATypography
    @Composable get() = GWPPATypography(
        archivoRegular13 = TextStyle(
            fontFamily = archivoRegular,
            fontSize = 13.sp,
        ),
        archivoRegular14 = TextStyle(
            fontFamily = archivoRegular,
            fontSize = 14.sp,
        ),
        archivoRegular15 = TextStyle(
            fontFamily = archivoRegular,
            fontSize = 15.sp,
        ),
        archivoRegular16 = TextStyle(
            fontFamily = archivoRegular,
            fontSize = 16.sp,
        ),
        archivoSemiBold11 = TextStyle(
            fontFamily = archivoSemiBold,
            fontSize = 11.sp,
        ),
        archivoSemiBold12 = TextStyle(
            fontFamily = archivoSemiBold,
            fontSize = 12.sp,
        ),
        archivoSemiBold13 = TextStyle(
            fontFamily = archivoSemiBold,
            fontSize = 13.sp,
        ),
        archivoSemiBold14 = TextStyle(
            fontFamily = archivoSemiBold,
            fontSize = 14.sp,
        ),
        archivoSemiBold15 = TextStyle(
            fontFamily = archivoSemiBold,
            fontSize = 15.sp,
        ),
        archivoSemiBold18 = TextStyle(
            fontFamily = archivoSemiBold,
            fontSize = 18.sp,
        ),
        archivoBold11 = TextStyle(
            fontFamily = archivoBold,
            fontSize = 11.sp,
        ),
        archivoBold12 = TextStyle(
            fontFamily = archivoBold,
            fontSize = 12.sp,
        ),
        archivoBold13 = TextStyle(
            fontFamily = archivoBold,
            fontSize = 13.sp,
        ),
        archivoBold16 = TextStyle(
            fontFamily = archivoBold,
            fontSize = 16.sp,
        ),
        archivoBold17 = TextStyle(
            fontFamily = archivoBold,
            fontSize = 17.sp,
        ),
        archivoExpandedSemiBold21 = TextStyle(
            fontFamily = archivoExpandedSemiBold,
            fontSize = 21.sp,
            letterSpacing = 0.24.em,
        ),
        archivoExpandedBold12 = TextStyle(
            fontFamily = archivoExpandedBold,
            fontSize = 12.sp,
        ),
        archivoExpandedBold16 = TextStyle(
            fontFamily = archivoExpandedBold,
            fontSize = 16.sp,
        ),
        archivoExpandedBold17 = TextStyle(
            fontFamily = archivoExpandedBold,
            fontSize = 17.sp,
        ),
        archivoExpandedBold19 = TextStyle(
            fontFamily = archivoExpandedBold,
            fontSize = 19.sp,
        ),
        archivoExpandedBold21 = TextStyle(
            fontFamily = archivoExpandedBold,
            fontSize = 21.sp,
        ),
        archivoExpandedBold27 = TextStyle(
            fontFamily = archivoExpandedBold,
            fontSize = 27.sp,
            letterSpacing = (-0.02).em,
        ),
        archivoExpandedBold32 = TextStyle(
            fontFamily = archivoExpandedBold,
            fontSize = 32.sp,
            lineHeight = 1.02.em,
            letterSpacing = (-0.025).em,
        ),
        archivoExpandedBold37 = TextStyle(
            fontFamily = archivoExpandedBold,
            fontSize = 37.sp,
        ),
        archivoExpandedBold38 = TextStyle(
            fontFamily = archivoExpandedBold,
            fontSize = 38.sp,
            lineHeight = 1.02.em,
            letterSpacing = (-0.025).em,
        ),
        archivoExpandedExtraBold13 = TextStyle(
            fontFamily = archivoExpandedExtraBold,
            fontSize = 13.sp,
        ),
        archivoExpandedExtraBold23 = TextStyle(
            fontFamily = archivoExpandedExtraBold,
            fontSize = 23.sp,
        ),
        archivoExpandedExtraBold24 = TextStyle(
            fontFamily = archivoExpandedExtraBold,
            fontSize = 24.sp,
            letterSpacing = (-0.02).em,
        ),
        archivoExpandedExtraBold30 = TextStyle(
            fontFamily = archivoExpandedExtraBold,
            fontSize = 30.sp,
            letterSpacing = (-0.02).em,
        ),
        archivoExpandedExtraBold32 = TextStyle(
            fontFamily = archivoExpandedExtraBold,
            fontSize = 32.sp,
            letterSpacing = (-0.03).em,
        ),
        archivoExpandedExtraBold50 = TextStyle(
            fontFamily = archivoExpandedExtraBold,
            fontSize = 50.sp,
            lineHeight = 0.86.em, letterSpacing = (-0.03).em,
        ),
        archivoExpandedExtraBold51 = TextStyle(
            fontFamily = archivoExpandedExtraBold,
            fontSize = 51.sp,
            lineHeight = 0.95.em, letterSpacing = (-0.035).em,
        ),
        archivoExpandedExtraBold104 = TextStyle(
            fontFamily = archivoExpandedExtraBold,
            fontSize = 104.sp,
            lineHeight = 1.em, letterSpacing = (-0.04).em,
        ),
    )
