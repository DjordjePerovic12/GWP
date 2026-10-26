package com.global_water_polo.play_abroad

import androidx.compose.ui.uikit.OnFocusBehavior
import androidx.compose.ui.window.ComposeUIViewController

// The keyboard is handled through WindowInsets.ime (screens pin their Continue above it),
// so iOS must not also slide the whole view up when a field gets focus.
fun MainViewController() = ComposeUIViewController(configure = { onFocusBehavior = OnFocusBehavior.DoNothing }) { App() }
