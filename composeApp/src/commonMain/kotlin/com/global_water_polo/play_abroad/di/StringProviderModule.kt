package com.global_water_polo.play_abroad.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import com.global_water_polo.play_abroad.core.utils.StringProvider
import com.global_water_polo.play_abroad.core.utils.StringProviderImpl

/**
 * Module dedicated to string resource management and localization.
 * Keeps string-related dependencies isolated and maintainable.
 */

val stringProviderModule = module {
    singleOf(::StringProviderImpl) bind StringProvider::class
}
