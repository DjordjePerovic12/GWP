package com.global_water_polo.play_abroad

import android.app.Application
import android.os.StrictMode
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import com.global_water_polo.play_abroad.di.initKoin

/**
This is the Application class for the project.
 * - Starts Koin with the Android context (the Koin equivalent of @HiltAndroidApp).
 * - Turns on StrictMode in debug builds.
 **/

class BaseApp : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites()
                    .detectNetwork().penaltyLog().build()
            )

            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().penaltyLog()
                    .penaltyDeath().build()
            )
        }

        initKoin {
            androidLogger()
            androidContext(this@BaseApp)
        }
    }
}
