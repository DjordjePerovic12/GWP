package com.global_water_polo.play_abroad.core.components

import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import io.github.vinceglb.filekit.AndroidFile
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.delay
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

private val SPEEDS = listOf(0.5f, 1f, 1.5f, 2f)

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
    var fullscreen by remember { mutableStateOf(false) }

    // The inline view lets go of the player while it plays full screen.
    PlayerSurface(player = player, attached = !fullscreen, onFullscreen = { fullscreen = true }, modifier = modifier)
    if (fullscreen) {
        Dialog(
            onDismissRequest = { fullscreen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
        ) {
            PlayerSurface(
                player = player,
                attached = true,
                onFullscreen = { fullscreen = false },
                modifier = Modifier.fillMaxSize().background(Color.Black)
            )
        }
    }
}

/**
 * The video with the prototype's slim browser-style controls instead of Media3's: a dark fade at the bottom,
 * play / pause, the time, mute, full screen and more (playback speed), with the track underneath. Shown
 * while paused; while playing they fade after a moment and come back on a tap.
 */
@Composable
private fun PlayerSurface(player: ExoPlayer, attached: Boolean, onFullscreen: () -> Unit, modifier: Modifier) {
    var playing by remember { mutableStateOf(player.isPlaying) }
    var muted by remember { mutableStateOf(player.volume == 0f) }
    var position by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }
    var shown by remember { mutableStateOf(true) }
    var touched by remember { mutableStateOf(0) }
    var menu by remember { mutableStateOf(false) }
    var speed by remember { mutableFloatStateOf(player.playbackParameters.speed) }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                playing = isPlaying
            }
        }
        player.addListener(listener)
        onDispose { player.removeListener(listener) }
    }
    LaunchedEffect(player) {
        while (true) {
            position = player.currentPosition.coerceAtLeast(0L)
            duration = player.duration.takeIf { it > 0 } ?: 0L
            delay(200)
        }
    }
    LaunchedEffect(playing, touched, menu) {
        if (!playing) shown = true
        else if (!menu) {
            delay(2500)
            shown = false
        }
    }

    Box(modifier) {
        AndroidView(
            factory = {
                PlayerView(it).apply {
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    setShutterBackgroundColor(android.graphics.Color.BLACK)
                }
            },
            update = { it.player = if (attached) player else null },
            modifier = Modifier.matchParentSize()
        )
        Box(
            Modifier.matchParentSize().pointerInput(Unit) {
                detectTapGestures {
                    shown = !shown
                    touched++
                }
            }
        )
        if (shown) {
            Column(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(0f to Color.Transparent, 1f to Color.Black.copy(alpha = 0.75f)))
                    .padding(start = 14.dp, end = 10.dp, top = 28.dp, bottom = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = {
                        if (player.playbackState == Player.STATE_ENDED) player.seekTo(0)
                        if (playing) player.pause() else player.play()
                        touched++
                    }) { if (playing) pauseIcon() else playIcon() }
                    Text(
                        "${clock(position)} / ${clock(duration)}",
                        style = GWPPATheme.typography.archivoSemiBold13.copy(fontSize = 13.sp),
                        color = Color.White
                    )
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = {
                        muted = !muted
                        player.volume = if (muted) 0f else 1f
                        touched++
                    }) { speakerIcon(muted) }
                    IconButton(onClick = onFullscreen) { fullscreenIcon() }
                    Box {
                        IconButton(onClick = { menu = true }) { moreIcon() }
                        DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                            SPEEDS.forEach { s ->
                                DropdownMenuItem(
                                    text = { Text((if (s == speed) "✓  " else "     ") + "${s.toString().removeSuffix(".0")}×") },
                                    onClick = {
                                        speed = s
                                        player.playbackParameters = PlaybackParameters(s)
                                        menu = false
                                    }
                                )
                            }
                        }
                    }
                }
                Track(
                    fraction = if (duration > 0) position.toFloat() / duration else 0f,
                    onSeek = { f ->
                        if (duration > 0) player.seekTo((f * duration).toLong())
                        position = (f * duration).toLong()
                        touched++
                    },
                    modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 6.dp)
                )
            }
        }
    }
}

/** The seek track: a thin rounded line, played part in white; tap or drag along it to seek. */
@Composable
private fun Track(fraction: Float, onSeek: (Float) -> Unit, modifier: Modifier) {
    BoxWithConstraints(
        modifier
            .fillMaxWidth()
            .height(20.dp)
            .pointerInput(Unit) {
                detectTapGestures { onSeek((it.x / size.width).coerceIn(0f, 1f)) }
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, _ -> onSeek((change.position.x / size.width).coerceIn(0f, 1f)) }
            },
        contentAlignment = Alignment.Center
    ) {
        Box(Modifier.fillMaxWidth().height(4.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.35f))) {
            Box(Modifier.fillMaxWidth(fraction.coerceIn(0f, 1f)).height(4.dp).clip(CircleShape).background(Color.White))
        }
    }
}

@Composable
private fun IconButton(onClick: () -> Unit, icon: DrawScope.() -> Unit) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(36.dp).clip(CircleShape).clickable(onClick = onClick)) {
        Canvas(Modifier.size(20.dp)) { icon() }
    }
}

private fun clock(ms: Long): String {
    val s = ms / 1000
    return "${s / 60}:${(s % 60).toString().padStart(2, '0')}"
}

private fun DrawScope.playIcon() {
    val w = size.width
    drawPath(Path().apply { moveTo(w * 0.22f, w * 0.12f); lineTo(w * 0.86f, w * 0.5f); lineTo(w * 0.22f, w * 0.88f); close() }, Color.White)
}

private fun DrawScope.pauseIcon() {
    val w = size.width
    drawRect(Color.White, Offset(w * 0.22f, w * 0.14f), androidx.compose.ui.geometry.Size(w * 0.18f, w * 0.72f))
    drawRect(Color.White, Offset(w * 0.6f, w * 0.14f), androidx.compose.ui.geometry.Size(w * 0.18f, w * 0.72f))
}

private fun DrawScope.speakerIcon(muted: Boolean) {
    val w = size.width
    drawPath(
        Path().apply {
            moveTo(w * 0.1f, w * 0.38f); lineTo(w * 0.3f, w * 0.38f); lineTo(w * 0.52f, w * 0.16f)
            lineTo(w * 0.52f, w * 0.84f); lineTo(w * 0.3f, w * 0.62f); lineTo(w * 0.1f, w * 0.62f); close()
        },
        Color.White
    )
    val stroke = Stroke(width = w * 0.08f, cap = StrokeCap.Round)
    if (muted) {
        drawLine(Color.White, Offset(w * 0.66f, w * 0.36f), Offset(w * 0.92f, w * 0.64f), stroke.width, StrokeCap.Round)
        drawLine(Color.White, Offset(w * 0.92f, w * 0.36f), Offset(w * 0.66f, w * 0.64f), stroke.width, StrokeCap.Round)
    } else {
        drawArc(Color.White, -45f, 90f, false, Offset(w * 0.42f, w * 0.3f), androidx.compose.ui.geometry.Size(w * 0.4f, w * 0.4f), style = stroke)
        drawArc(Color.White, -45f, 90f, false, Offset(w * 0.34f, w * 0.14f), androidx.compose.ui.geometry.Size(w * 0.72f, w * 0.72f), style = stroke)
    }
}

private fun DrawScope.fullscreenIcon() {
    val w = size.width
    val s = w * 0.09f
    val a = w * 0.14f
    val b = w * 0.86f
    val l = w * 0.24f
    fun corner(x: Float, y: Float, dx: Float, dy: Float) {
        drawLine(Color.White, Offset(x, y), Offset(x + dx * l, y), s, StrokeCap.Square)
        drawLine(Color.White, Offset(x, y), Offset(x, y + dy * l), s, StrokeCap.Square)
    }
    corner(a, a, 1f, 1f)
    corner(b, a, -1f, 1f)
    corner(a, b, 1f, -1f)
    corner(b, b, -1f, -1f)
}

private fun DrawScope.moreIcon() {
    val w = size.width
    listOf(0.2f, 0.5f, 0.8f).forEach { drawCircle(Color.White, w * 0.08f, Offset(w * 0.5f, w * it)) }
}
