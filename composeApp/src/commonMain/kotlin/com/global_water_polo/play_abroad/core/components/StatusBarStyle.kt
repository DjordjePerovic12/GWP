package com.global_water_polo.play_abroad.core.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Lets a screen with its own dark top (Feed's For you) ask for light status bar icons: true or false while it
 * is shown, null to follow the globe again. App reads it.
 */
val LocalStatusBarOverride = staticCompositionLocalOf<MutableState<Boolean?>> { mutableStateOf(null) }

/** White status bar icons over dark screens ([lightIcons]), dark ones over light screens. */
@Composable
expect fun StatusBarStyle(lightIcons: Boolean)
