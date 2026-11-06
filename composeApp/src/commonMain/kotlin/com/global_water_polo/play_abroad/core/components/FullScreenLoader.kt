package com.global_water_polo.play_abroad.core.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieComposition
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.DotLottie
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import com.global_water_polo.play_abroad.resources.Res

/**
 * The app's loader: the GWP shot loop (composeResources/files/gwp_loader_light.lottie, made in Blender)
 * centred on white, covering the whole screen and swallowing taps while something loads. BaseContentView
 * shows it for any screen whose state is loading.
 */
@Composable
fun FullScreenLoader(modifier: Modifier = Modifier) {
    val composition by produceState(LoaderAnimation.ready) { value = LoaderAnimation.get() }
    // 2.5x the 4 s loop: the ball is released and reaches the viewer in about half a second, so even a fast
    // call shows the shot land.
    val progress by animateLottieCompositionAsState(composition, iterations = Compottie.IterateForever, speed = LOADER_SPEED)
    val statusOverride = LocalStatusBarOverride.current
    DisposableEffect(Unit) {
        LoaderVisibility.count++
        statusOverride.value = false
        onDispose {
            LoaderVisibility.count--
            statusOverride.value = null
        }
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .pointerInput(Unit) { detectTapGestures { } }
    ) {
        Image(
            painter = rememberLottiePainter(composition = composition, progress = { progress }),
            contentDescription = null,
            modifier = Modifier.size(240.dp)
        )
    }
}

private const val LOADER_SPEED = 2.5f

/**
 * The loader's animation, parsed once off the main thread and kept: it is a large frame-by-frame vector loop,
 * so App preloads it at launch and even a fast call shows it straight away.
 */
object LoaderAnimation {
    private val mutex = Mutex()
    var ready: LottieComposition? = null
        private set

    suspend fun get(): LottieComposition? = mutex.withLock {
        ready ?: withContext(Dispatchers.Default) {
            runCatching { LottieCompositionSpec.DotLottie(Res.readBytes("files/gwp_loader_light.lottie")).load() }.getOrNull()
        }.also { ready = it }
    }
}

/**
 * Whether a full-screen loader is up. Native views drawn over Compose (the iOS 3D ball) hide while it is, so
 * nothing shows through the loader.
 */
object LoaderVisibility {
    var count by mutableIntStateOf(0)
    val showing: Boolean get() = count > 0
}
