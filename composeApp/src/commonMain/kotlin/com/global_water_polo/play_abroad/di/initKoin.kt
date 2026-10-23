package com.global_water_polo.play_abroad.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

/**
 * Starts Koin. Android calls it from BaseApp, iOS from iOSApp.swift (InitKoinKt.doInitKoin(config: nil)).
 */

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            platformModule,
            appModule,
            repositoryModule,
            abstractionModule,
            stringProviderModule,
            viewModelModule
        )
    }
}
