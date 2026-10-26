package com.global_water_polo.play_abroad.core.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitViewController
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.pause
import platform.AVKit.AVPlayerViewController

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun VideoPlayer(file: PlatformFile, modifier: Modifier) {
    // PHPicker hands back a security-scoped copy; keep access open while the player reads it.
    val player = remember(file) {
        file.nsUrl.startAccessingSecurityScopedResource()
        AVPlayer(uRL = file.nsUrl)
    }
    val controller = remember(player) { AVPlayerViewController().apply { this.player = player } }
    DisposableEffect(player) {
        onDispose {
            player.pause()
            file.nsUrl.stopAccessingSecurityScopedResource()
        }
    }
    UIKitViewController(factory = { controller }, modifier = modifier)
}
