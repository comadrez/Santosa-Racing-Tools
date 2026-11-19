package id.santosa.racingtools.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CatalogValidatorTest {
    private fun source(id: String = "s1") =
        Source(id, "Title", "Publisher", null, null, "2026-11-19", "secondary")

    private fun num(value: Double?, status: Verification = Verification.SECONDARY, src: String? = "s1") =
        if (value == null) SpecNumber.UNKNOWN else SpecNumber(value, status, src)

    private fun engine(
        id: String = "e1",
        bore: SpecNumber = num(47.0),
        stroke: SpecNumber = num(63.1),
        cylinders: SpecNumber = num(1.0),
        displacement: SpecNumber = num(109.5),
        compression: SpecNumber = num(10.0)
    ) = Engine(
        id = id,
        name = "Engine",
        boreMm = bore,
        strokeMm = stroke,
        cylinders = cylinders,
        displacementCc = displacement,
        compressionRatio = compression
    )

    private fun generation(
        id: String = "g1",
        engineId: String? = "e1",
        transmissionId: String? = null,
        yearFrom: SpecNumber = num(2020.0),
        yearTo: SpecNumber = SpecNumber.UNKNOWN,
        inProduction: Boolean = true
    ) = Generation(
        id = id,
        brand = "Brand",
        family = "Family",
        category = "Scooter / Matic",
        label = "Label",
        yearFrom = yearFrom,
        yearTo = yearTo,
        inProduction = inProduction,
        engineId = engineId,
        transmissionId = transmissionId
    )

    private fun catalog(
        engines: List<Engine> = listOf(engine()),
        generations: List<Generation> = listOf(generation()),
        sources: List<Source> = listOf(source())
    ) = Catalog(sources, engines, emptyList(), generations)

    private fun errors(catalog: Catalog) = CatalogValidator.validate(catalog)

    @Test
    fun validCatalogHasNoErrors() {
        assertEquals(emptyList<String>(), errors(catalog()))
    }

    @Test
    fun secondaryValueNeedsSource() {
        val bad = engine(bore = SpecNumber(47.0, Verification.SECONDARY, null))
        assertTrue(errors(catalog(engines = listOf(bad))).any { it.contains("boreMm") && it.contains("requires a source") })
    }

    @Test
    fun valueNeedsStatus() {
        val bad = engine(bore = SpecNumber(47.0, Verification.UNKNOWN, null))
        assertTrue(errors(catalog(engines = listOf(bad))).any { it.contains("boreMm") && it.contains("without a verification status") })
    }

    @Test
    fun statusNeedsValue() {
        val bad = engine(bore = SpecNumber(null, Verification.SECONDARY, "s1"))
        assertTrue(errors(catalog(engines = listOf(bad))).any { it.contains("boreMm") && it.contains("without a value") })
    }

    @Test
    fun unknownSourceIsReported() {
        val bad = engine(bore = num(47.0, src = "missing"))
        assertTrue(errors(catalog(engines = listOf(bad))).any { it.contains("unknown source missing") })
    }

    @Test
    fun calculatedValueNeedsNoSource() {
        val ok = engine(compression = SpecNumber(10.0, Verification.CALCULATED, null))
        assertEquals(emptyList<String>(), errors(catalog(engines = listOf(ok))))
    }

    @Test
    fun outOfRangeBoreIsReported() {
        val bad = engine(bore = num(500.0), displacement = SpecNumber.UNKNOWN)
        assertTrue(errors(catalog(engines = listOf(bad))).any { it.contains("boreMm out of range") })
    }

    @Test
    fun cylinderCountMustBeWhole() {
        val bad = engine(cylinders = num(1.5), displacement = SpecNumber.UNKNOWN)
        assertTrue(errors(catalog(engines = listOf(bad))).any { it.contains("cylinders must be a whole number") })
    }

    @Test
    fun displacementMustMatchBoreAndStroke() {
        val bad = engine(displacement = num(125.0))
        assertTrue(errors(catalog(engines = listOf(bad))).any { it.contains("does not match bore and stroke") })
    }

    @Test
    fun roundedDisplacementIsAccepted() {
        val ok = engine(displacement = num(109.5))
        assertEquals(emptyList<String>(), errors(catalog(engines = listOf(ok))))
    }

    @Test
    fun duplicateIdsAreReported() {
        val result = errors(catalog(generations = listOf(generation("g1"), generation("g1"))))
        assertTrue(result.any { it.contains("duplicate generation id g1") })
    }

    @Test
    fun unknownEngineReferenceIsReported() {
        val result = errors(catalog(generations = listOf(generation(engineId = "nope"))))
        assertTrue(result.any { it.contains("unknown engine nope") })
    }

    @Test
    fun unknownTransmissionReferenceIsReported() {
        val result = errors(catalog(generations = listOf(generation(transmissionId = "nope"))))
        assertTrue(result.any { it.contains("unknown transmission nope") })
    }

    @Test
    fun yearOrderIsChecked() {
        val bad = generation(yearFrom = num(2020.0), yearTo = num(2010.0), inProduction = false)
        assertTrue(errors(catalog(generations = listOf(bad))).any { it.contains("yearFrom is after yearTo") })
    }

    @Test
    fun inProductionCannotHaveEndYear() {
        val bad = generation(yearFrom = num(2020.0), yearTo = num(2024.0), inProduction = true)
        assertTrue(errors(catalog(generations = listOf(bad))).any { it.contains("inProduction") })
    }
}
