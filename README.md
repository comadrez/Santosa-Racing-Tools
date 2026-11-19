# Santosa Racing Tools

Android app for motorcycle engine calculations, technical references and build planning.

## Calculators

- Displacement, bore-up and stroke-up
- Static compression ratio
- Clearance volume from burette readings, gasket, deck and piston crown
- Squish clearance
- Mean piston speed
- Gear ratio and road speed from RPM

The Motorcycles tab shows sourced specifications. It has Honda matic models so far (BeAT, Vario, Stylo, Scoopy, Genio, PCX, ADV, Forza). The full database is built in parts, see `docs/PROJECT_BLUEPRINT.md`. Workshop articles and saved builds are planned.

## Project layout

- `app/src/main/java/id/santosa/racingtools/calc/` - calculation code, no UI
- `app/src/main/java/id/santosa/racingtools/MainActivity.kt` - screens
- `app/src/main/java/id/santosa/racingtools/data/` - specification data model, loader and validator
- `app/src/main/assets/specs/` - specification data files
- `app/src/test/` - unit tests for the calculation code and the data
- `docs/` - formulas, database plan, development notes

## Build locally

Requirements: JDK 17, Android SDK Platform 35, Gradle 8.9.

```bash
gradle test
gradle :app:assembleDebug
```

The debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

## GitHub Actions

Both workflows are in `.github/workflows/`.

- **Android Unit Tests** runs the unit tests. It starts on a push that changes `app/**`, the Gradle files or `gradle.properties`, and it can be started by hand.
- **Android Build** builds an APK and is started by hand. Choose `debug` or `release`.

A debug build deletes the older debug artifacts before it uploads the new one, so only the latest debug APK is kept.

## Release signing

A signed release needs these repository secrets:

- `ANDROID_SIGNING_KEY_BASE64`: base64-encoded JKS keystore
- `ANDROID_SIGNING_STORE_PASSWORD`
- `ANDROID_SIGNING_KEY_ALIAS`
- `ANDROID_SIGNING_KEY_PASSWORD`

Then run Android Build with `release`. Keep the keystore and passwords private. If the keystore is lost, updates to an app signed with it are no longer possible.

## Notes on the results

All results are theoretical. Check dimensions, clearances and limits by measuring the parts before machining or assembly.

The compression calculators work per cylinder. The road speed calculator does not cover CVT drives. No motorcycle data is included until it can be added with a source and a verification status.

## Package

`id.santosa.racingtools`

Changing the application ID after publishing breaks updates for existing installs.
