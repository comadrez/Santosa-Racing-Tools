package id.santosa.racingtools.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CatalogParserTest {
    private val sample = """
        {
          "formatVersion": 1,
          "sources": [
            {"id": "s1", "title": "Spec sheet", "publisher": "Maker", "url": "https://example.com/a", "accessed": "2026-11-19", "type": "secondary"}
          ],
          "engines": [
            {"id": "e1", "name": "Test engine",
             "cylinders": {"v": 1, "s": "secondary", "src": "s1"},
             "boreMm": {"v": 50.0, "s": "secondary", "src": "s1"},
             "strokeMm": {"v": 55.0, "s": "secondary", "src": "s1"},
             "displacementCc": {"v": 108.0, "s": "secondary", "src": "s1"},
             "cooling": {"v": "Air-cooled", "s": "secondary", "src": "s1"}}
          ],
          "transmissions": [
            {"id": "t1", "gearRatios": [{"v": 2.5, "s": "technical", "src": "s1"}, {"v": null, "s": "unknown"}]}
          ],
          "generations": [
            {"id": "g1", "brand": "Brand", "family": "Family", "category": "Scooter / Matic", "label": "1st generation",
             "yearFrom": {"v": 2020, "s": "secondary", "src": "s1"}, "inProduction": true,
             "engineId": "e1", "transmissionId": "t1"}
          ]
        }
    """.trimIndent()

    @Test
    fun parsesSourceAndEngineFields() {
        val catalog = CatalogParser.parse(sample)
        assertEquals("https://example.com/a", catalog.source("s1")!!.url)
        val engine = catalog.engine("e1")!!
        assertEquals(50.0, engine.boreMm.value!!, 1e-9)
        assertEquals(Verification.SECONDARY, engine.boreMm.status)
        assertEquals("s1", engine.boreMm.sourceId)
        assertEquals("Air-cooled", engine.cooling.value)
    }

    @Test
    fun missingFieldsStayUnknown() {
        val engine = CatalogParser.parse(sample).engine("e1")!!
        assertNull(engine.compressionRatio.value)
        assertEquals(Verification.UNKNOWN, engine.compressionRatio.status)
        assertNull(engine.fuelSystem.value)
    }

    @Test
    fun parsesGearRatioList() {
        val transmission = CatalogParser.parse(sample).transmission("t1")!!
        assertEquals(2, transmission.gearRatios.size)
        assertEquals(2.5, transmission.gearRatios[0].value!!, 1e-9)
        assertEquals(Verification.TECHNICAL, transmission.gearRatios[0].status)
        assertNull(transmission.gearRatios[1].value)
    }

    @Test
    fun parsesGeneration() {
        val generation = CatalogParser.parse(sample).generations.single()
        assertEquals("e1", generation.engineId)
        assertTrue(generation.inProduction)
        assertEquals(2020.0, generation.yearFrom.value!!, 1e-9)
        assertNull(generation.wheelbaseMm.value)
    }

    @Test
    fun generationLookupIgnoresCase() {
        val catalog = CatalogParser.parse(sample)
        assertEquals(1, catalog.generationsFor("brand", "FAMILY").size)
        assertEquals(0, catalog.generationsFor("brand", "Other").size)
    }

    @Test
    fun mergeJoinsAllParts() {
        val catalog = CatalogParser.parse(sample)
        val merged = CatalogParser.merge(listOf(catalog, Catalog.EMPTY, catalog))
        assertEquals(2, merged.generations.size)
        assertEquals(2, merged.sources.size)
    }

    @Test(expected = IllegalArgumentException::class)
    fun unknownStatusIsRejected() {
        CatalogParser.parse(sample.replace("technical", "verified"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun wrongFormatVersionIsRejected() {
        CatalogParser.parse(sample.replace("\"formatVersion\": 1", "\"formatVersion\": 2"))
    }
}
