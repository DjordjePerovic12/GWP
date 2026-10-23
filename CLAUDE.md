# GWPPA

Kotlin Multiplatform / Compose Multiplatform app for Android and iOS, built on the Amplitudo KMP template.

Conventions: the `amplitudo-kmp-compose` skill (plugin `amplitudo-kmp-compose@amplitudo`). It loads automatically when you work on features; follow it for file names, packages, ViewModels, navigation, networking and theme. List new file names and wait for approval before creating them.

- All app code: `composeApp/src/commonMain/kotlin/`. Platform code only through `expect`/`actual` in `androidMain` / `iosMain`.
- Android entry: `androidApp/` (BaseApp, MainActivity). iOS entry: `iosApp/` (Xcode project).
- Strings: `composeApp/src/commonMain/composeResources/values/strings.xml`.
- Base URL: `BASE_URL` in `gradle.properties` (override per machine in `local.properties`).

## Build and test

```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew :composeApp:allTests                               # unit tests, Android host + iOS simulator
./gradlew :androidApp:assembleDebug                          # Android APK
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64    # iOS framework
```

A change in `commonMain` is done only when both platforms build. iOS app: open `iosApp/iosApp.xcodeproj` in Xcode, or `xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16 Pro' build`.

## QA

QA board link: `.claude/qa-board.md` (created by `/amplitudo-qa:qa-board-setup`). QA sessions: `claude --agent amplitudo-qa:qa-engineer`. One board for both platforms; a bug in `commonMain` is `platform: all`.
