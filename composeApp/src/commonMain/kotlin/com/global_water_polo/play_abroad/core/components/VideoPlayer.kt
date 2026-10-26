package com.global_water_polo.play_abroad.core.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.vinceglb.filekit.PlatformFile

/**
 * Plays a picked video with the platform's own player and controls: Media3 ExoPlayer on Android,
 * AVPlayer on iOS. Paused until the user taps play; released when it leaves the screen.
 */
@Composable
expect fun VideoPlayer(file: PlatformFile, modifier: Modifier = Modifier)
