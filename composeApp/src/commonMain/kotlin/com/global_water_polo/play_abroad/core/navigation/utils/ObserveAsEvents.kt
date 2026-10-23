package com.global_water_polo.play_abroad.core.navigation.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
  A Composable function that safely observes Flow events in a lifecycle-aware manner.

  This function helps handle events from a Flow while respecting the lifecycle,
  preventing memory leaks and ensuring events are only processed when the app is active.

  @param flow The Flow to observe
  @param key1 Optional key to trigger recomposition (like a viewModel or other state)
  @param key2 Additional optional key to trigger recomposition
  @param onEvent Callback function that handles each event emitted by the Flow
 **/

@Composable
fun <T> ObserveAsEvents(
    flow: Flow<T>,
    key1: Any? = null,
    key2: Any? = null,
    onEvent: (T) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(
        lifecycleOwner.lifecycle,
        key1,
        key2
    ) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                flow.collect(onEvent)
            }
        }
    }
}
