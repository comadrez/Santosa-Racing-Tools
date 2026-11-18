# Project Rules

Working rules for Santosa Racing Tools. Follow these on every change.

## Repo

- Name: Santosa-Racing-Tools
- Link: https://github.com/comadrez/Santosa-Racing-Tools.git
- Branch: main
- App ID: id.santosa.racingtools

## Local setup (Termux)

- Repo directory: `~/SRT`
- Zip directory: `~/storage/downloads/`

## Delivering changes

1. Ask for confirmation before creating or editing files.
2. Deliver changes as a zip containing only the changed files, with their real repo paths.
3. Every zip gets a unique name with a date and version, so Android never shows a stale file.
   Format: `SRT-<topic>-<yyyymmdd>-v<number>.zip`
   Example: `SRT-workflow-fix-20261119-v2.zip`

## Termux command

Template (replace the zip name, commit message and paths):

```bash
cd ~/SRT && unzip -o ~/storage/downloads/<zip-name>.zip && git add <changed-paths> && git commit -m "<message>" && git push
```

Example:

```bash
cd ~/SRT && unzip -o ~/storage/downloads/SRT-workflow-fix-20261119-v2.zip && git add .github/workflows && git commit -m "Fix Android CI: locate sdkmanager via ANDROID_HOME" && git push
```

Check state before or after pushing:

```bash
cd ~/SRT && git status -s ; git log -1 --stat
```

## CI

- Workflows are in `.github/workflows/`.
- Android Unit Tests only runs on push when `app/**`, `*.gradle.kts` or `gradle.properties` change.
  A change to workflow files alone does not trigger it. Start it from the Actions tab with Run workflow.
- Do not use the re-run button after a fix. It reuses the old commit. Start a new run.
- The runner already has the Android SDK. Do not add `android-actions/setup-android`.
- Stack: JDK 17, Gradle 8.9, AGP 8.7.3, Kotlin 2.0.21, compileSdk 35.

## Code and content style

The app must read like a normal hand-written project. Nothing in it should point to AI.

- No mention of AI, Claude, ChatGPT, assistants, "generated", or "auto-generated" anywhere:
  code, comments, strings, README, docs, commit messages, file names.
- No tutorial-style comments that explain obvious lines. Comment only what is not clear from the code.
- No decorative banners, emoji, or "Note:" / "Here we..." style comments.
- UI text stays short and plain: no marketing phrases, no filler like "seamless" or "powerful".
- Keep names, spacing and structure consistent with the existing files.
- Commit messages are short and plain, one line.
