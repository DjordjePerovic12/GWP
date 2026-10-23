# GWPPA

Kotlin Multiplatform + Compose Multiplatform app (Android and iOS) created from the Amplitudo KMP template.

## Requirements

- Android Studio (current stable) with its bundled JBR
- Xcode with an iOS Simulator runtime (macOS only)

## Run

**Android:** open the project in Android Studio and run the `androidApp` configuration, or `./gradlew :androidApp:installDebug`.

**iOS:** open `iosApp/iosApp.xcodeproj` in Xcode and run on a simulator. For a real device set `TEAM_ID` in `iosApp/Configuration/Config.xcconfig`.

## Configuration

| What | Where |
|---|---|
| Backend base URL | `BASE_URL` in `gradle.properties` (or `local.properties` per machine) |
| Refresh token route | `Constants.REFRESH_TOKEN_ROUTE` |
| Android app id / iOS bundle id | `androidApp/build.gradle.kts`, `iosApp/Configuration/Config.xcconfig` |
| Versions | `gradle/libs.versions.toml` |

## Structure

All shared code is in `composeApp/src/commonMain/kotlin/` with the same packages as the Amplitudo Android base: `presentation/<feature>/` (Screen, ScreenContent, ViewModel, State, Event), `data/`, `domain/`, `di/`, `core/navigation/`, `ui/theme/`. `presentation/example` and `presentation/auth/login` are reference features; delete them when the real app starts.
