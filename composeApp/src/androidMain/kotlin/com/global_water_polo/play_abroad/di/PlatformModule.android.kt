package com.global_water_polo.play_abroad.di

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module
import com.global_water_polo.play_abroad.core.utils.Constants.DATASTORE_NAME
import com.global_water_polo.play_abroad.core.utils.createDataStore

actual val platformModule: Module = module {
    single<HttpClientEngine> { OkHttp.create() }
    single {
        createDataStore {
            androidContext().filesDir.resolve(DATASTORE_NAME).absolutePath
        }
    }
}
