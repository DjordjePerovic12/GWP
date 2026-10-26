package com.global_water_polo.play_abroad.core.components

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import io.github.vinceglb.filekit.AndroidFile
import io.github.vinceglb.filekit.PlatformFile

@Composable
actual fun VideoPlayer(file: PlatformFile, modifier: Modifier) {
    val context = LocalContext.current
    val player = remember(file) {
        val uri = when (val f = file.androidFile) {
            is AndroidFile.UriWrapper -> f.uri
            is AndroidFile.FileWrapper -> Uri.fromFile(f.file)
        }
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
        }
    }
    DisposableEffect(player) {
        onDispose { player.release() }
    }
    AndroidView(
        factory = { PlayerView(it).apply { this.player = player } },
        update = { it.player = player },
        modifier = modifier
    )
}
