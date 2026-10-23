package com.global_water_polo.play_abroad.di

import org.koin.core.module.Module

/**
 * Platform specific dependencies: the Ktor engine (OkHttp on Android, Darwin on iOS)
 * and the DataStore file location. See PlatformModule.android.kt / PlatformModule.ios.kt.
 */

expect val platformModule: Module
