package com.global_water_polo.play_abroad.core.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.key

/**
  Runtime app language switching, the multiplatform replacement for
  LocaleContextWrapper + attachBaseContext on Android.

  MainViewModel reads the preferred language tag from DataStore and App() wraps everything in
  AppEnvironment(languageTag). Changing the tag re-creates the UI in the new language, no restart needed.

  TODO: Delete this file, the actual implementations and AppEnvironment usage if the app doesn't have multilang functionality
 **/

expect object LocalAppLocale {
    val current: String @Composable get

    @Composable
    infix fun provides(value: String?): ProvidedValue<*>
}

@Composable
fun AppEnvironment(
    languageTag: String?,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalAppLocale provides languageTag
    ) {
        key(languageTag) {
            content()
        }
    }
}
