# Motorcycle catalogue data policy

## Scope
The intended register covers motorcycles documented as present in Indonesia:
- official Indonesian-market models;
- discontinued and historical models;
- grey-market and parallel imports;
- documented privately imported models where evidence exists.

The initial Kotlin list is only a starter index of manufacturers and model families. It is not exhaustive and must not be presented as such.

## Selection hierarchy
1. Manufacturer
2. Motorcycle category/type
3. Model or series
4. Exact model/type code and variant
5. Production year
6. Generation derived from sourced year applicability

Every later selector must be filtered by the earlier selections. Search is available within each dropdown. Users must not manually type model codes into the factory catalogue.

## Do not conflate identifiers
Store these as separate fields:
- manufacturer family/model prefix;
- exact model/type designation;
- manufacturer commercial model name;
- engine code;
- frame/VIN prefix;
- parts-catalogue or fiche identifier.

A prefix such as `5TL` must not be treated as an exact variant code unless the source explicitly defines it that way.

## Evidence and verification
Every model/variant/year mapping needs a source record and a status:
- factory verified;
- technical verified;
- secondary source;
- workshop measured;
- estimated/calculated;
- unknown.

Unknown values remain null/unknown. Do not infer a production year, generation, engine code, bore, stroke, compression ratio, transmission ratio or other technical specification from a model-family name.

## Next data-development stage
For each model family, add exact variants and year applicability from manufacturer owner manuals, official parts catalogues, homologation/type-approval records, official importer archives, and reputable technical references. Record the URL/document title, publisher, publication date where available, page/section, access date, and which fields it supports.
