# Calculation Formula Notes

## Displacement

For a cylindrical bore and stroke:

V_cc = (pi / 4) * bore_mm^2 * stroke_mm * cylinder_count / 1000

## Bore-up and stroke-up

The new displacement uses the same formula with the new bore and stroke.

change % = (new - old) / old * 100

## Static compression ratio

CR = (Vs + Vc) / Vc

Vs is swept volume. Vc is total clearance volume. Vc must include chamber, gasket, deck and piston crown volume contributions with a consistent sign convention.

## Clearance volume

Vc = chamber + gasket + deck + crown

- chamber = burette end reading - burette start reading
- gasket = (pi / 4) * bore_mm^2 * compressed gasket thickness_mm / 1000
- deck = (pi / 4) * bore_mm^2 * piston below deck_mm / 1000
- crown is positive for a dished piston and negative for a domed piston

The gasket is taken at the cylinder bore size. All values are per cylinder.

## Squish clearance

squish = piston below deck + compressed gasket thickness

A piston above the deck is entered as a negative value. A result of zero or less is not accepted.

## Gear ratio

For a simple gear pair:

ratio = driven tooth count / driving tooth count

For sprockets:

final reduction = rear sprocket tooth count / front sprocket tooth count

The overall reduction depends on the actual transmission architecture and must be assembled from correctly defined stages.

## Road speed

tyre circumference = pi * (rim_in * 25.4 + 2 * width_mm * aspect_% / 100)

overall reduction = primary * gear * (rear teeth / front teeth)

wheel rpm = engine rpm / overall reduction

speed (km/h) = wheel rpm * circumference_m * 60 / 1000

The result ignores slip and tyre growth, and it does not apply to CVT drives.

## Mean piston speed

Mean piston speed (m/s) = 2 * stroke_m * RPM / 60

All displayed values should retain full precision internally and be rounded only for presentation.
