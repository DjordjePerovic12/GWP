package com.global_water_polo.play_abroad.core.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryAmbient
import platform.AVFAudio.setActive
import platform.Foundation.NSData
import platform.Foundation.dataWithBytes
import com.global_water_polo.play_abroad.resources.Res

/**
 * One AVAudioPlayer per sound. The session is Ambient: the sounds mix with the user's music and follow the
 * ring/silent switch, like the prototype on iPhone.
 */
@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberSoundPlayer(): SoundPlayer {
    val players = remember { HashMap<Sfx, AVAudioPlayer>() }
    LaunchedEffect(Unit) {
        AVAudioSession.sharedInstance().setCategory(AVAudioSessionCategoryAmbient, null)
        AVAudioSession.sharedInstance().setActive(true, null)
        Sfx.entries.forEach { sfx ->
            val bytes = Res.readBytes(sfx.file)
            val data = bytes.usePinned { NSData.dataWithBytes(it.addressOf(0), bytes.size.toULong()) }
            AVAudioPlayer(data = data, error = null).also { it.prepareToPlay() }.let { players[sfx] = it }
        }
    }
    return remember {
        SoundPlayer { sfx ->
            players[sfx]?.let {
                it.currentTime = 0.0
                it.play()
            }
        }
    }
}
