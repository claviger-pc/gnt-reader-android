# Working on GNT Reader

## Project layout

- This is a Kotlin Android app using Jetpack Compose and Gradle. The application module is `app`; reusable code is split between `core/*` and `feature/*` modules listed in `settings.gradle.kts`.
- The main reader/navigation integration lives in `app/src/main/java/com/mattrobertson/greek/reader/presentation/MainScreen.kt`.
- Reading plan UI, plan definitions, and reminder scheduling live in `feature/plans/src/main/java/com/mattrobertson/greek/reader/plans/`. Settings integration is in `feature/settings-ui` and the app presentation layer.
- Word taps are routed by `ChapterText`/`ComposeReader` callbacks to the concordance bottom sheet. If a new reader surface is added, make sure it forwards the word-selection callback rather than supplying a no-op.
- Keep module dependencies intentional. Add modules in `settings.gradle.kts` and use the generated type-safe project accessors where appropriate.

## Build and test

- Use JDK 17 and an installed Android SDK. On macOS, set `JAVA_HOME` and `ANDROID_HOME`/`ANDROID_SDK_ROOT` if Gradle cannot find them automatically.
- Build the app with `./gradlew assembleDebug` or `./gradlew assembleRelease`.
- Run plan unit tests with `./gradlew :feature:plans:testDebugUnitTest`; run the broader JVM unit-test suite with `./gradlew testDebugUnitTest`.
- Release builds enable both R8 minification and resource shrinking. A successful release build may still produce `app/build/outputs/apk/release/app-release-unsigned.apk`; this fork has no configured production release keystore. Do not treat a debug-key-signed APK as a production-signed update.
- When a device is available, verify install and launch with ADB, then exercise the changed screen/interaction itself. A successful compile or launch alone does not prove behavior such as notifications or concordance taps.

## App-specific behavior and pitfalls

- App version code and name are currently set directly in `app/build.gradle.kts`. Increment both for a new app release. Update the README's versioned release notes when user-facing behavior changes.
- Reading plans persist progress and reminder preferences in app shared preferences. Reminder behavior depends on notification permission on Android 13+ and exact-alarm access on Android 12+; account for denied access and do not assume exact delivery is available.
- Optimized release builds can expose reflection/serialization issues that debug builds hide. Preserve required generated protobuf fields as configured in `app/proguard-rules.pro` and verify the minified release build after changing serialization or reflection-sensitive code.
- The v8 APK grew partly because of bundled Greek font choices and a larger packaged database. Compare APK contents and build variants before attributing size changes to debug artifacts.

## Git and publishing

- Match commit author identity to the authenticated GitHub account before committing. For this fork, use `claviger-pc` and its GitHub no-reply email; check `git config user.name`, `git config user.email`, and `gh auth status` first.
- Do not rewrite or force-push published branch history or release tags unless the repository owner explicitly requests it. Prefer `--force-with-lease` for an explicitly authorized history correction.
- The GitHub Actions workflow in `.github/workflows/main.yml` builds and uploads an APK artifact; it does not itself publish a GitHub Release. Creating a release or publishing to a store is an external action and requires explicit authorization.
- Preserve unrelated worktree files. In particular, do not add local `.DS_Store`, build output, `local.properties`, signing keys, or Play Store credentials to commits.
