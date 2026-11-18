# Model & brand catalogue update (draft)

## Files
- `app/src/main/java/id/santosa/racingtools/MotorcycleCatalogScreen.kt` — initial brand/model-family directory and cascading searchable dropdown UI.
- `docs/MOTORCYCLE_CATALOG_DATA_POLICY.md` — coverage and data-integrity rules.

## Integration
In `MainActivity.kt`, replace the existing `MotorcyclesScreen()` implementation with:

```kotlin
@Composable
private fun MotorcyclesScreen() {
    ScreenColumn {
        Eyebrow("Specification library")
        SectionTitle("Motorcycles")
        MotorcycleCatalogScreen()
    }
}
```

No extra imports are required because the new file is in the same Kotlin package.

## Important data limitation
This is a first-pass model-family index, not a complete Indonesian motorcycle register and not a factory-specification database. It deliberately does not invent exact model/type codes, production-year ranges, generations, engine codes, or dimensions. Those dropdowns currently show an explicit pending-verification value. Expand the exact variants only after collecting model-specific sources.
