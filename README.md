# Santosa Racing Tools

Native Android application foundation for motorcycle engine calculations, technical references and modification project planning.

## Current foundation

- Kotlin + Jetpack Compose
- Dark workshop-oriented UI
- Manual GitHub Actions APK build
- Initial displacement calculator
- Static compression ratio calculator
- Basic tooth-count ratio calculator
- Motorcycle database and workshop module placeholders

## Build locally

Requirements: JDK 17, Android SDK Platform 35, Gradle 8.9.

```bash
gradle :app:assembleDebug
```

APK output:

`app/build/outputs/apk/debug/app-debug.apk`

## Build using GitHub Actions

1. Push this repository to GitHub.
2. Open **Actions**.
3. Select **Android Build**.
4. Select **Run workflow**.
5. Choose `debug` for a development APK.
6. Download the artifact after the workflow succeeds.

## Release signing

For a signed release, configure these repository secrets:

- `ANDROID_SIGNING_KEY_BASE64`: base64-encoded JKS keystore
- `ANDROID_SIGNING_STORE_PASSWORD`
- `ANDROID_SIGNING_KEY_ALIAS`
- `ANDROID_SIGNING_KEY_PASSWORD`

Then run Android Build with `release`.

Keep the keystore and passwords private. Losing the keystore can prevent updates to an app distributed under the same signing identity.

## Important engineering notes

The initial calculators are foundation implementations, not a complete engine engineering suite. The compression calculator expects total clearance volume as an input. The gear ratio screen is a basic tooth-count ratio tool, not a validated motorcycle gearbox database or road-speed model.

No motorcycle specification catalogue is represented as complete or verified in this initial scaffold. Model data must be added with source and verification status.

## Package identity

`id.santosa.racingtools`

Changing the application ID after publishing may affect app update continuity.
