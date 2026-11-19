package id.santosa.racingtools.data

import id.santosa.racingtools.calc.EngineMath
import java.util.Locale
import kotlin.math.abs
import kotlin.math.floor

object CatalogValidator {
    private const val DISPLACEMENT_TOLERANCE = 0.02

    fun validate(catalog: Catalog): List<String> {
        val errors = mutableListOf<String>()
        val sourceIds = catalog.sources.map { it.id }.toSet()

        duplicates("source", catalog.sources.map { it.id }, errors)
        duplicates("engine", catalog.engines.map { it.id }, errors)
        duplicates("transmission", catalog.transmissions.map { it.id }, errors)
        duplicates("generation", catalog.generations.map { it.id }, errors)

        catalog.engines.forEach { checkEngine(it, sourceIds, errors) }
        catalog.transmissions.forEach { checkTransmission(it, sourceIds, errors) }
        catalog.generations.forEach { checkGeneration(it, catalog, sourceIds, errors) }
        return errors
    }

    private fun checkEngine(e: Engine, sourceIds: Set<String>, errors: MutableList<String>) {
        val at = "engine " + e.id
        number(at, "cylinders", e.cylinders, 1.0, 12.0, sourceIds, errors, whole = true)
        number(at, "boreMm", e.boreMm, 20.0, 200.0, sourceIds, errors)
        number(at, "strokeMm", e.strokeMm, 20.0, 200.0, sourceIds, errors)
        number(at, "displacementCc", e.displacementCc, 20.0, 2500.0, sourceIds, errors)
        number(at, "compressionRatio", e.compressionRatio, 5.0, 20.0, sourceIds, errors)
        number(at, "powerPs", e.powerPs, 0.5, 400.0, sourceIds, errors)
        number(at, "powerRpm", e.powerRpm, 500.0, 20000.0, sourceIds, errors)
        number(at, "torqueNm", e.torqueNm, 0.5, 400.0, sourceIds, errors)
        number(at, "torqueRpm", e.torqueRpm, 500.0, 20000.0, sourceIds, errors)
        text(at, "cooling", e.cooling, sourceIds, errors)
        text(at, "fuelSystem", e.fuelSystem, sourceIds, errors)

        val bore = e.boreMm.value
        val stroke = e.strokeMm.value
        val cylinders = e.cylinders.value
        val stated = e.displacementCc.value
        if (bore != null && stroke != null && cylinders != null && stated != null) {
            val calculated = EngineMath.displacementCc(bore, stroke, cylinders.toInt())
            if (calculated != null && abs(calculated - stated) / stated > DISPLACEMENT_TOLERANCE) {
                errors.add(
                    at + ": displacement " + stated + " cc does not match bore and stroke (" +
                        String.format(Locale.US, "%.1f", calculated) + " cc)"
                )
            }
        }
    }

    private fun checkTransmission(t: Transmission, sourceIds: Set<String>, errors: MutableList<String>) {
        val at = "transmission " + t.id
        number(at, "cvtLowRatio", t.cvtLowRatio, 0.1, 20.0, sourceIds, errors)
        number(at, "cvtHighRatio", t.cvtHighRatio, 0.1, 20.0, sourceIds, errors)
        number(at, "primaryRatio", t.primaryRatio, 0.1, 20.0, sourceIds, errors)
        number(at, "finalReduction", t.finalReduction, 0.1, 20.0, sourceIds, errors)
        number(at, "frontSprocketTeeth", t.frontSprocketTeeth, 5.0, 100.0, sourceIds, errors, whole = true)
        number(at, "rearSprocketTeeth", t.rearSprocketTeeth, 5.0, 100.0, sourceIds, errors, whole = true)
        t.gearRatios.forEachIndexed { index, ratio ->
            number(at, "gearRatios[" + index + "]", ratio, 0.1, 20.0, sourceIds, errors)
        }
        text(at, "kind", t.kind, sourceIds, errors)
    }

    private fun checkGeneration(g: Generation, catalog: Catalog, sourceIds: Set<String>, errors: MutableList<String>) {
        val at = "generation " + g.id
        if (g.brand.isBlank() || g.family.isBlank() || g.label.isBlank()) {
            errors.add(at + ": brand, family and label are required")
        }
        number(at, "yearFrom", g.yearFrom, 1900.0, 2100.0, sourceIds, errors, whole = true)
        number(at, "yearTo", g.yearTo, 1900.0, 2100.0, sourceIds, errors, whole = true)
        number(at, "wheelbaseMm", g.wheelbaseMm, 800.0, 2500.0, sourceIds, errors)
        number(at, "kerbWeightKg", g.kerbWeightKg, 30.0, 500.0, sourceIds, errors)
        number(at, "seatHeightMm", g.seatHeightMm, 400.0, 1200.0, sourceIds, errors)
        text(at, "frontTyre", g.frontTyre, sourceIds, errors)
        text(at, "rearTyre", g.rearTyre, sourceIds, errors)

        if (g.engineId != null && catalog.engine(g.engineId) == null) {
            errors.add(at + ": unknown engine " + g.engineId)
        }
        if (g.transmissionId != null && catalog.transmission(g.transmissionId) == null) {
            errors.add(at + ": unknown transmission " + g.transmissionId)
        }

        val from = g.yearFrom.value
        val to = g.yearTo.value
        if (from != null && to != null && from > to) {
            errors.add(at + ": yearFrom is after yearTo")
        }
        if (g.inProduction && to != null) {
            errors.add(at + ": inProduction is set but yearTo is given")
        }
    }

    private fun duplicates(kind: String, ids: List<String>, errors: MutableList<String>) {
        ids.groupingBy { it }.eachCount()
            .filter { it.value > 1 }
            .keys
            .forEach { errors.add("duplicate " + kind + " id " + it) }
    }

    private fun number(
        at: String,
        name: String,
        field: SpecNumber,
        min: Double,
        max: Double,
        sourceIds: Set<String>,
        errors: MutableList<String>,
        whole: Boolean = false
    ) {
        val label = at + " " + name
        provenance(label, field.value != null, field.status, field.sourceId, sourceIds, errors)
        val value = field.value ?: return
        if (!value.isFinite() || value < min || value > max) {
            errors.add(label + " out of range: " + value)
        } else if (whole && value != floor(value)) {
            errors.add(label + " must be a whole number: " + value)
        }
    }

    private fun text(
        at: String,
        name: String,
        field: SpecText,
        sourceIds: Set<String>,
        errors: MutableList<String>
    ) {
        val hasValue = !field.value.isNullOrBlank()
        provenance(at + " " + name, hasValue, field.status, field.sourceId, sourceIds, errors)
    }

    private fun provenance(
        label: String,
        hasValue: Boolean,
        status: Verification,
        sourceId: String?,
        sourceIds: Set<String>,
        errors: MutableList<String>
    ) {
        if (hasValue && status == Verification.UNKNOWN) {
            errors.add(label + ": value without a verification status")
        }
        if (!hasValue && status != Verification.UNKNOWN) {
            errors.add(label + ": status given without a value")
        }
        if (hasValue && status.needsSource && sourceId == null) {
            errors.add(label + ": status '" + status.key + "' requires a source")
        }
        if (sourceId != null && sourceId !in sourceIds) {
            errors.add(label + ": unknown source " + sourceId)
        }
    }
}
