package com.global_water_polo.play_abroad.core.components

import android.media.AudioAttributes
import android.media.SoundPool
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import com.global_water_polo.play_abroad.resources.Res

/** SoundPool needs files, so each sound is copied out of the resources into the cache once. */
@Composable
actual fun rememberSoundPlayer(): SoundPlayer {
    val context = LocalContext.current
    val pool = remember {
        SoundPool.Builder()
            .setMaxStreams(6)
            .setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build())
            .build()
    }
    val ids = remember { HashMap<Sfx, Int>() }
    LaunchedEffect(pool) {
        Sfx.entries.forEach { sfx ->
            val file = withContext(Dispatchers.IO) {
                File(context.cacheDir, "sfx_${sfx.name.lowercase()}.wav").apply { writeBytes(Res.readBytes(sfx.file)) }
            }
            ids[sfx] = pool.load(file.absolutePath, 1)
        }
    }
    DisposableEffect(pool) {
        onDispose { pool.release() }
    }
    return remember(pool) { SoundPlayer { sfx -> ids[sfx]?.let { pool.play(it, 1f, 1f, 1, 0, 1f) } } }
}
