package com.global_water_polo.play_abroad.di

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import com.global_water_polo.play_abroad.core.utils.Constants.DATASTORE_NAME
import com.global_water_polo.play_abroad.core.utils.createDataStore

@OptIn(ExperimentalForeignApi::class)
actual val platformModule: Module = module {
    single<HttpClientEngine> { Darwin.create() }
    single {
        createDataStore {
            val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = false,
                error = null
            )
            requireNotNull(documentDirectory).path + "/$DATASTORE_NAME"
        }
    }
}
