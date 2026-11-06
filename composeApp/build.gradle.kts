import com.codingfeline.buildkonfig.compiler.FieldSpec
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.buildKonfig)
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    android {
        namespace = "com.global_water_polo.play_abroad.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
        androidResources {
            enable = true
        }
        withHostTest {}
    }

    sourceSets {
        commonMain.dependencies {
            // Lottie: the full-screen loader plays a .lottie (dotLottie) file.
            implementation(libs.compottie)
            implementation(libs.compottie.dot)
            implementation(libs.bundles.compose)
            implementation(libs.bundles.lifecycle)
            implementation(libs.androidx.navigation.compose)

            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.bundles.koin)

            implementation(libs.bundles.ktor)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)

            implementation(libs.androidx.datastore.preferences)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
            implementation(libs.kermit)
            implementation(libs.filekit.dialogs.compose)
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.androidx.media3.exoplayer)
            implementation(libs.androidx.media3.ui)
            // 3D: the GWP ball model on the opener (Filament renders the glTF).
            implementation(libs.filament.android)
            implementation(libs.filament.gltfio)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
            implementation(libs.ktor.client.mock)
        }
    }
}

compose.resources {
    packageOfResClass = "com.global_water_polo.play_abroad.resources"
    publicResClass = false
}

buildkonfig {
    packageName = "com.global_water_polo.play_abroad"

    val localProperties = Properties().apply {
        rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { load(it) }
    }
    val baseUrl = localProperties.getProperty("BASE_URL") ?: providers.gradleProperty("BASE_URL").get()
    // Quick start on the intro (long-press the logo): sample profiles for testing. Set false for client builds.
    val demoShortcuts = (localProperties.getProperty("DEMO_SHORTCUTS") ?: providers.gradleProperty("DEMO_SHORTCUTS").getOrElse("false")).toBoolean()

    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, "BASE_URL", baseUrl)
        buildConfigField(FieldSpec.Type.BOOLEAN, "DEMO_SHORTCUTS", demoShortcuts.toString())
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.ui.tooling)
}
