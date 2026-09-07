# TAWI for Android

Android client for **TAWI 2.0** — an AI-powered app that suppresses background noise, sharpens
confusable phonemes (e.g. "p" vs "b"), and transcribes speech live, including Kenyan-accented
English and Swahili. Built for people with Auditory Processing Disorder, and for anyone who
struggles to isolate speech in a noisy environment. It works with ordinary wired earphones — no
special hardware.

This repository is independent. It shares no code and no git history with `TAWI-IOS` or
`TAWI-backend`; the apps coordinate with the backend only through its HTTP API contract.

**Current state: Phase 1 skeleton.** The app builds, launches, and shows a Compose placeholder
screen. Audio capture, transcription, auth, and the role-aware navigation shell arrive in later
phases.

## Stack

| Concern        | Choice                                                        |
|----------------|---------------------------------------------------------------|
| Language       | Kotlin                                                        |
| UI             | Jetpack Compose (Material 3), Compose BOM `2026.02.01`        |
| Build          | Gradle 9.6.0 (wrapper), AGP 9.4.0                             |
| SDK            | `compileSdk`/`targetSdk` 37, `minSdk` 24                      |
| JDK            | 25 (auto-provisioned by the Gradle daemon toolchain)          |
| Application id | `dev.mabeya.tawi`                                             |
| Tests          | JUnit 4 (JVM), AndroidX Test + Compose UI test (instrumented) |

## Prerequisites

- **Android Studio** (Narwhal or newer — the project uses AGP 9 and SDK 37) or a standalone
  Android SDK.
- **Android SDK packages** — `platforms;android-37.0` and `build-tools;37.0.0`. Android Studio
  offers to install these on first open; from the command line use `sdkmanager`.
- **No local JDK setup needed.** `gradle/gradle-daemon-jvm.properties` pins the daemon to JDK 25
  and Gradle downloads it automatically on first build.
- `local.properties` is generated for you by Android Studio. Building from the command line
  without Android Studio requires either `ANDROID_HOME` exported or a `local.properties`
  containing `sdk.dir=/path/to/Android/sdk`. It is intentionally git-ignored.

## Build and run

From a fresh clone:

```bash
./gradlew assembleDebug                 # build the debug APK
./gradlew installDebug                  # install onto a connected device/emulator
```

Or open the folder in Android Studio and press Run. The launcher activity is
`dev.mabeya.tawi.MainActivity`, which renders `PlaceholderScreen`.

To launch it manually on a running emulator:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n dev.mabeya.tawi/.MainActivity
```

## Tests

**JVM unit tests** — no device needed, and what CI runs:

```bash
./gradlew testDebugUnitTest
```

**Instrumented tests** — Phase 1 item 1.8, requires a booted emulator or attached device:

```bash
./gradlew connectedDebugAndroidTest
```

`PlaceholderScreenTest` covers the Phase 1 requirement directly: it asserts `MainActivity` reaches
`RESUMED` without crashing and that the placeholder screen's nodes and string resources are
actually rendered. Reports land in `app/build/reports/androidTests/connected/`.

> Instrumented tests run **locally**, not in CI — a hosted emulator adds minutes per run for a
> single placeholder screen. CI still compiles and packages the instrumented APK
> (`assembleDebugAndroidTest`), so the on-device suite cannot silently rot.

**Lint:**

```bash
./gradlew lint                          # report at app/build/reports/lint-results-debug.html
```

## Continuous integration

`.github/workflows/ci.yml` runs on every push and pull request: JDK 25 + Android SDK 37 setup,
then `lint`, `testDebugUnitTest`, `assembleDebug`, and `assembleDebugAndroidTest`. Lint and test
reports are uploaded as build artifacts.

Note for anyone editing CI: AGP 9 uses minor-versioned SDK platform ids, so the package is
`platforms;android-37.0` — `platforms;android-37` does not resolve.

## Layout

```text
app/src/main/java/dev/mabeya/tawi/
  MainActivity.kt              # launcher activity, hosts the Compose tree
  ui/PlaceholderScreen.kt      # Phase 1 placeholder screen + shared test tags
  ui/theme/                    # Material 3 theme, colors, typography
app/src/test/                  # JVM unit tests
app/src/androidTest/           # instrumented Compose UI tests
gradle/libs.versions.toml      # version catalog — dependency versions live here
```

## Project docs

The PRD (`TAWI-Redesign-PRD.md`) and the phased plan (`TAWI-Development-Phases.md`) live in the
local parent working folder alongside the three repos. They are deliberately not committed here.
