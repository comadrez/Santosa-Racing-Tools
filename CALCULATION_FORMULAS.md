# Calculation Formula Notes

## Displacement

For a cylindrical bore and stroke:

V_cc = (pi / 4) * bore_mm^2 * stroke_mm * cylinder_count / 1000

## Static compression ratio

CR = (Vs + Vc) / Vc

Vs is swept volume. Vc is total clearance volume. Vc must include chamber, gasket, deck and piston crown volume contributions with a consistent sign convention.

## Gear ratio

For a simple gear pair:

ratio = driven tooth count / driving tooth count

For sprockets:

final reduction = rear sprocket tooth count / front sprocket tooth count

The overall reduction depends on the actual transmission architecture and must be assembled from correctly defined stages.

## Mean piston speed

Mean piston speed (m/s) = 2 * stroke_m * RPM / 60

All displayed values should retain full precision internally and be rounded only for presentation.
