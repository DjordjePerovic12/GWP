package com.global_water_polo.play_abroad.core.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * The prototype's sounds, rendered from its Web Audio synthesis into composeResources/files: a ratchet tooth
 * while winding, the swoosh when the spring lets go, the splash on picking a role, the clock's tick, and the
 * boom for the big moments.
 */
enum class Sfx(val file: String) {
    TICK("files/tick.wav"),
    RATCHET("files/ratchet.wav"),
    SWOOSH("files/swoosh.wav"),
    SPLASH("files/splash.wav"),
    BOOM("files/boom.wav")
}

/** Plays short sound effects; quiet until its sounds have loaded. */
fun interface SoundPlayer {
    fun play(sfx: Sfx)
}

/** The app's sound player, provided by App; silent in previews. */
val LocalSound = staticCompositionLocalOf { SoundPlayer { } }

/** Loads every [Sfx] once and keeps the platform player for the app's lifetime. */
@Composable
expect fun rememberSoundPlayer(): SoundPlayer
