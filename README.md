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

## Roadmap

Only the work owned by **this** repo is listed. Item numbers refer to `TAWI-Development-Phases.md`.
Each phase is a separate commit here, prefixed with its phase number, and a phase is not done until
its exit gate is met on this platform — exit gates are evaluated per platform, so Android can pass
while iOS is still failing.

- [x] **Phase 1 — Repo scaffolding & CI baseline** (1.3, 1.5–1.6, 1.8, 1.10)
      Compose placeholder screen, instrumented launch test, CI, this README.

- [ ] **Phases 2 and 3 — no Android work.** Accounts/roles and the server-side AI pipeline are
      backend-only. This repo stays untouched; it just gains an API contract to build against.

- [ ] **Phase 4 — Audio capture + backend connection** *(needs a reachable `/process-audio`)*
  - 4.2 Wired-earphone audio capture via the `AudioRecord` API, send to backend, render the
    returned transcript
  - 4.4 Auth: login screen and token storage against Phase 2 backend accounts
  - 4.5 Role-aware navigation shell (Listener view vs. Caregiver/Teacher placeholder view)
  - 4.6–4.8 Tests: 5 seconds of speech with wired earphones produces an on-screen transcript;
    token survives app restart and expiry forces re-login; device matrix confirming wired-earphone
    input is prioritized over the built-in mic on 2–3 representative devices
  - *Exit gate 4.9: a real conversation into wired earphones produces an on-screen transcript
    end-to-end against the live backend*

- [ ] **Phase 5 — Real-time mode**
  - 5.3 Stream audio and consume incremental transcript chunks
  - 5.4 Live caption UI updating as chunks arrive, with phoneme-clarity highlighting
  - 5.5 Waveform visual feedback component
  - 5.6–5.8 Tests: spoken-word-to-caption latency (~1s target, measured end-to-end across all three
    repos); 10-minute continuous conversation with no dropped connection or memory leak (profiled);
    manual check that highlighted words match the corrected audio

- [ ] **Phase 6 — Offline / on-device mode** *(flagged as the hardest problem in the PRD)*
  - 6.2 Spike: evaluate on-device options (quantized Whisper variants, mobile noise suppression)
    on mid-range hardware; document accuracy/latency trade-offs vs. server-side
  - 6.3 Lightweight on-device pipeline: basic noise suppression + short-buffer transcription
  - 6.4 Local session queuing
  - 6.6 Offline/online and sync-status UI indicator
  - 6.7–6.9 Tests: WER and latency on a minimum-spec **and** a mid/high-spec device; airplane-mode
    capture then correct sync; force-kill mid-session with no data loss on relaunch

- [ ] **Phase 7 — Accessibility & consent hardening** *(baseline, not an add-on)*
  - 7.2 TalkBack labels across all screens
  - 7.3 High-contrast mode and dyslexia-friendly font toggle
  - 7.4 Tap-target audit against Android accessibility guidelines
  - 7.5 COPPA/GDPR-style consent flow at signup for minors; data retention settings screen
  - 7.7, 7.9 Tests: Android Accessibility Scanner clean on all core screens; manual
    screen-reader-only walkthrough of record → transcript → history

- [ ] **Phase 8 — Caregiver/therapist dashboards & sharing**
  - 8.2 Caregiver/Teacher dashboard: session history, progress indicators
  - 8.3 Adult-user therapist-sharing flow
  - 8.5 Test: dashboard reflects a session recorded on the Listener's device within an acceptable
    sync delay

- [ ] **Phase 9 — Async mode & gamification**
  - 9.3 Async record → process → review flow, including edit/correct-transcript
  - 9.5 Gamification (streaks, badges, progress), feature-flagged so it can be hidden for adults
  - 9.6, 9.8 Tests: async result appears in the review UI with correct transcript/audio; streak
    counter increments/resets correctly across day boundaries and timezones

- [ ] **Phase 10 — Monetization & entitlements**
  - 10.3 Google Play Billing, paywall/upsell touchpoints, restore-purchase flow
  - 10.6 Test: purchase, renewal, cancellation and restore all update entitlement state in sandbox
    billing (server-side enforcement lives in the backend — never trust the client)

- [ ] **Phase 11 — Beta hardening & launch readiness**
  - 11.2 Full end-to-end regression pass with the other two repos
  - 11.3 Performance/battery profiling during extended real-time use
  - 11.4 Privacy-respecting crash reporting and analytics, consistent with Phase 7 consent settings
  - 11.5 Beta feedback loop: in-app FAQ + feedback form
  - 11.6–11.8 Tests: regression suite green independently; battery drain over a 30–60 minute
    real-time session within budget; pilot dry run with 5–10 users

## Project docs

The PRD (`TAWI-Redesign-PRD.md`) and the phased plan (`TAWI-Development-Phases.md`) live in the
local parent working folder alongside the three repos. They are deliberately not committed here.
