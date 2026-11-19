# Specification data format

Data files live in `app/src/main/assets/specs/`. `index.json` lists the data files the app loads:

```json
{ "formatVersion": 1, "files": ["honda.json"] }
```

Each data file has four lists: `sources`, `engines`, `transmissions` and `generations`.

## Fields and provenance

Every number or text value carries its own status and source:

```json
"boreMm": { "v": 47.0, "s": "secondary", "src": "autofun-beat-2026-04" }
```

- `v`: the value. Leave the field out, or use `null`, when the value is not known.
- `s`: one of `factory`, `technical`, `secondary`, `measured`, `calculated`, `unknown`.
- `src`: id of an entry in `sources`. Required for `factory`, `technical` and `secondary`.

## Records

- **source:** `id`, `title`, `publisher`, `url`, `published`, `accessed`, `type`.
- **engine:** bore, stroke, displacement, compression ratio, power and torque with rpm, cylinders, cooling, fuel system.
- **transmission:** type, CVT low and high ratio, primary ratio, final reduction, sprocket teeth, gear ratios.
- **generation:** brand, family, category, label, production years, `inProduction`, `engineId`, `transmissionId`, wheelbase, kerb weight, seat height, tyres, notes.

Engines and transmissions are stored once and linked from every generation that uses them.

## Rules

- A model name is not a generation. Add one record for each generation, body or engine version.
- Unknown values stay unknown. Do not copy a value from another generation.
- A value taken from a news article or a price list is `secondary`, even when it repeats the maker's figures.
- Add `factory` only for a manufacturer owner manual, parts catalogue or official specification page.
- A value worked out from other fields is `calculated`.

## Checks

`CatalogValidator` runs in the unit tests for every file in `assets/specs`. It rejects:

- values without a status, or statuses without a value
- missing or unknown sources
- duplicate ids and broken engine or transmission links
- numbers outside plausible ranges
- a displacement that is more than 2% away from the bore, stroke and cylinder count
- year ranges that are the wrong way round

Run it with `gradle test`.
