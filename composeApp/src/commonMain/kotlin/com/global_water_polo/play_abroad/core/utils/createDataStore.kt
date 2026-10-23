package com.global_water_polo.play_abroad.core.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

/**
  Creates the app's preferences DataStore. Each platform passes the absolute file path
  (see di/PlatformModule.android.kt and di/PlatformModule.ios.kt).
 **/

fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { producePath().toPath() }
    )
