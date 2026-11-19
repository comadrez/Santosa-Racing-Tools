# Santosa Racing Tools - Project Blueprint

## Status

Done:

- Engine calculators: displacement, bore-up and stroke-up, compression ratio, clearance volume, squish, piston speed
- Gear ratio and road speed
- Unit tests for the calculation code
- Specification database, part 1: data format, loader, validator, tests and the catalogue screen, with Honda BeAT as sample data
- Specification database, part 2: Honda matic, current models (Vario 125, Vario 150, Vario 160, Stylo 160, Scoopy, Genio, PCX 160, ADV 160, Forza 250) in `honda-matic.json`
- Specification database, part 2b: Honda matic, older and discontinued models (Vario 110 and 125, Scoopy, BeAT, PCX 125 and 150, ADV 150, Spacy) in `honda-matic-older.json`
- Specification database, part 3b: Honda underbone and cub, older models (Astrea Supra, Supra X 100, Supra Fit, Supra X 125 carburettor, Revo 100, Absolute Revo 110, Kirana 125) in `honda-underbone-older.json`
- Specification database, part 3: Honda underbone and cub, current models (Supra X 125 FI, Supra GTR 150, Revo FI, Blade 125 FI, Sonic 150R) plus Supra X 125 Helm In and Blade 110, in `honda-underbone.json`
- Specification database, part 4: Honda sport, trail and naked models (CBR150R, CB150R Streetfire, CB150X, CBR250RR, CRF150L, CB150 Verza) in `honda-sport.json`

Next:

- Wheelie check calculator for bore-up CVT scooters (not built yet)
- Specification database, part 5 onward

Not started:

- Workshop articles
- Saved builds and comparison
- Language selection

## Specification database

Goal: base specifications for every brand sold in Indonesia, including old and discontinued models, so the calculators can start from accurate standard values.

Format and rules are in `SPEC_DATA_FORMAT.md`. The catalogue policy is in `MOTORCYCLE_CATALOG_DATA_POLICY.md`.

Size estimate (my estimate, not a sourced count):

- about 1,000 to 1,400 model-generation records
- about 300 to 450 unique engines, because many models share an engine

Parts, tick each one when its data is in the repository:

- [x] 1. Data format and app code
- [x] 2. Honda matic, current models
- [x] 2b. Honda matic, older and discontinued models (earlier Vario 110 and 125, Spacy, Scoopy, PCX 125 and 150, ADV 150, older BeAT). Vision and Lead are not added: no source found that shows them sold in Indonesia. Many older fields stay unknown where sources disagree or give no figure.
- [x] 3. Honda underbone and cub, current models
- [x] 3b. Honda underbone and cub, older and discontinued models (Supra X 100 and 125 carburettor, Supra Fit, Supra X 125 R, Absolute Revo, Revo 100, Kirana 125; Blade 110 was already in part 3). Karisma, Win 100 and Supra X 110 are not added: no usable source found yet. Many older fields stay unknown where sources disagree or give no figure.
- [x] 4. Honda sport, trail and big bikes (CBR150R, CB150R Streetfire in two bodies, CB150X, CBR250RR first generation and the 2023 SP, CRF150L, CB150 Verza). CRF250 Rally, Tiger, MegaPro, GL Pro and GL Max, the older CB150R and CBR150R generations and the big bikes are not added: no usable source collected yet. The CBR250RR 2023 Standard type has no record of its own. Wheelbase, tyres, start years and gear ratios stay unknown where sources are missing or disagree.
- [ ] 5. Yamaha matic
- [ ] 6. Yamaha underbone and cub
- [ ] 7. Yamaha sport and others
- [ ] 8. Suzuki, part 1
- [ ] 9. Suzuki, part 2
- [ ] 10. Kawasaki, part 1
- [ ] 11. Kawasaki, part 2
- [ ] 12. TVS, Bajaj and older Asian brands
- [ ] 13. European and US brands, part 1
- [ ] 14. European and US brands, part 2
- [ ] 15. Chinese, Malaysian and Taiwanese brands
- [ ] 16. Electric brands
- [ ] 17. Verification pass against manuals and official pages
- [ ] 18. Calculator hookup: choose a model and fill in the calculators and the wheelie check

Working rules:

- Do the current models first, then work back to discontinued ones.
- Models from before about 2000 often have no official online source. Mark those fields `secondary` or leave them unknown.
- Manufacturers do not publish clearance volume. A baseline can be calculated as `Vc = Vs / (CR - 1)` and is stored as `calculated`. The published compression ratio is rounded, so the result is approximate.
- Workshop measurements can replace a baseline value and are stored as `measured`.
- Brands already confirmed in sources: Honda, Yamaha, Suzuki, Kawasaki, TVS, BMW Motorrad, Harley-Davidson, Italjet, Peugeot, Royal Enfield, Scomadi, Triumph, Benelli, Keeway, Benda, Aprilia, Moto Guzzi, Vespa, Piaggio, QJMotor, CFMoto, WMoto, Morbidelli, and the electric brands Alva, Polytron, United, Yadea, Gesits, Selis, Davigo, Smev, Greentech, Viar, Dibao, VinFast. KTM, Husqvarna and Kymco appear in a 2019 list. Ducati, SYM and Bajaj are not confirmed yet.

## Planned modules

1. Motorcycle database covering Indonesian-market motorcycles, including discontinued models.
2. Engine calculators: displacement, bore-up, stroke-up, burette, static/dynamic compression, squish.
3. Gearbox: primary, individual gears, final drive, RPM/speed, CVT.
4. Rules-based modification recommendations with compatibility states.
5. Workshop tutorials in English and Bahasa Indonesia.
6. Saved builds and project comparison.
7. Offline-first database with optional versioned updates.

## Data integrity

Every factory specification must carry provenance and a verification state. Unknown values remain unknown. Recommendations must not infer physical compatibility from displacement alone.
