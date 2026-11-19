# Development Guide

- Calculation code lives in `app/src/main/java/id/santosa/racingtools/calc/`. It must not depend on Android or Compose.
- Every calculation function returns `null` for invalid input. The screens show a message instead of a number.
- Every formula has unit tests in `app/src/test/`, including invalid and boundary values.
- Run the tests with `gradle test`.
- Public functions take the units shown on screen (mm, cc, rpm). Conversions happen inside the function.
- Keep unknown technical specifications null instead of guessing values.
- Keep full precision internally and round only when showing a value.
- Put UI strings in Android resources once the localization work starts.
- Keep user projects local and keep them when the database is updated.
- Show recommendations as rule-based output with the evidence and the uncertainty.
- Specification data lives in `app/src/main/assets/specs/`. The format is described in `docs/SPEC_DATA_FORMAT.md`.
- Add each new data file to `assets/specs/index.json`. The unit tests check that the index lists every file and that every file passes `CatalogValidator`.
