package id.santosa.racingtools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.santosa.racingtools.data.Catalog
import id.santosa.racingtools.data.Generation
import id.santosa.racingtools.data.SpecFormat
import id.santosa.racingtools.data.Verification

@Composable
internal fun SpecDetail(generation: Generation, catalog: Catalog) {
    val engine = catalog.engine(generation.engineId)
    val transmission = catalog.transmission(generation.transmissionId)
    val sourceIds = SpecFormat.sourceIds(generation, engine, transmission)

    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(generation.brand + " " + generation.family, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
            Text(generation.label)
            SpecRow("Production", SpecFormat.yearRange(generation), generation.yearFrom.status)

            if (engine != null) {
                SpecHeading("Engine")
                SpecRow("Bore", SpecFormat.number(engine.boreMm, 1, "mm"), engine.boreMm.status)
                SpecRow("Stroke", SpecFormat.number(engine.strokeMm, 1, "mm"), engine.strokeMm.status)
                SpecRow("Displacement", SpecFormat.number(engine.displacementCc, 1, "cc"), engine.displacementCc.status)
                SpecRow("Compression ratio", SpecFormat.compression(engine.compressionRatio), engine.compressionRatio.status)
                SpecRow("Power", SpecFormat.atRpm(engine.powerPs, engine.powerRpm, 1, "PS"), engine.powerPs.status)
                SpecRow("Torque", SpecFormat.atRpm(engine.torqueNm, engine.torqueRpm, 1, "Nm"), engine.torqueNm.status)
                SpecRow("Cooling", SpecFormat.text(engine.cooling), engine.cooling.status)
                SpecRow("Fuel system", SpecFormat.text(engine.fuelSystem), engine.fuelSystem.status)
            }

            if (transmission != null) {
                SpecHeading("Transmission")
                SpecRow("Type", SpecFormat.text(transmission.kind), transmission.kind.status)
                SpecRow("CVT low ratio", SpecFormat.number(transmission.cvtLowRatio, 3), transmission.cvtLowRatio.status)
                SpecRow("CVT high ratio", SpecFormat.number(transmission.cvtHighRatio, 3), transmission.cvtHighRatio.status)
                SpecRow("Primary ratio", SpecFormat.number(transmission.primaryRatio, 3), transmission.primaryRatio.status)
                SpecRow("Final reduction", SpecFormat.number(transmission.finalReduction, 3), transmission.finalReduction.status)
                SpecRow("Front sprocket", SpecFormat.number(transmission.frontSprocketTeeth, 0, "teeth"), transmission.frontSprocketTeeth.status)
                SpecRow("Rear sprocket", SpecFormat.number(transmission.rearSprocketTeeth, 0, "teeth"), transmission.rearSprocketTeeth.status)
                transmission.gearRatios.forEachIndexed { index, ratio ->
                    SpecRow("Gear " + (index + 1), SpecFormat.number(ratio, 3), ratio.status)
                }
            }

            SpecHeading("Chassis")
            SpecRow("Wheelbase", SpecFormat.number(generation.wheelbaseMm, 0, "mm"), generation.wheelbaseMm.status)
            SpecRow("Kerb weight", SpecFormat.number(generation.kerbWeightKg, 0, "kg"), generation.kerbWeightKg.status)
            SpecRow("Seat height", SpecFormat.number(generation.seatHeightMm, 0, "mm"), generation.seatHeightMm.status)
            SpecRow("Front tyre", SpecFormat.text(generation.frontTyre), generation.frontTyre.status)
            SpecRow("Rear tyre", SpecFormat.text(generation.rearTyre), generation.rearTyre.status)

            if (generation.notes != null) {
                Text(generation.notes, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (sourceIds.isNotEmpty()) {
                SpecHeading("Sources")
                sourceIds.forEach { id ->
                    val source = catalog.source(id)
                    if (source != null) {
                        Text(
                            source.title + " - " + source.publisher,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SpecHeading(text: String) {
    Text(text, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
}

@Composable
private fun SpecRow(label: String, value: String, status: Verification) {
    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontWeight = FontWeight.Medium)
        }
        if (status != Verification.UNKNOWN) {
            Text(status.label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
