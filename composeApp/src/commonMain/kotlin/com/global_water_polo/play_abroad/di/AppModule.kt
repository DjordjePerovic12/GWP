package com.global_water_polo.play_abroad.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import com.global_water_polo.play_abroad.data.remote.HttpClientFactory
import com.global_water_polo.play_abroad.data.remote.services.ApiService

/**
 * Koin module providing singleton network-related dependencies.
 * Configures and provides the HttpClient and ApiService instances.
 * The HttpClientEngine (OkHttp / Darwin) comes from platformModule.
 */

val appModule = module {
    single { HttpClientFactory.create(engine = get(), dataStoreRepository = get()) }
    singleOf(::ApiService)
}
