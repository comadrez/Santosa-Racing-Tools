package id.santosa.racingtools.data

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class HondaSportDataTest {
    private val dir = File("src/main/assets/specs")

    private fun load(): Catalog {
        val files = JSONObject(File(dir, "index.json").readText()).getJSONArray("files")
        val parts = List(files.length()) { CatalogParser.parse(File(dir, files.getString(it)).readText()) }
        return CatalogParser.merge(parts)
    }

    private fun engineOf(catalog: Catalog, generationId: String): Engine {
        val record = catalog.generations.first { it.id == generationId }
        return catalog.engine(record.engineId)!!
    }

    @Test
    fun everySportFamilyHasRecords() {
        val catalog = load()
        val expected = mapOf(
            "CBR150R" to 1,
            "CB150R Streetfire" to 2,
            "CB150X" to 1,
            "CBR250RR" to 2,
            "CRF150L" to 1,
            "Verza" to 1
        )
        expected.forEach { (family, count) ->
            assertEquals(family, count, catalog.generationsFor("Honda", family).size)
        }
    }

    @Test
    fun liquidCooled150ShareGeometryButNotOutput() {
        val catalog = load()
        val cbr = engineOf(catalog, "honda-cbr150r")
        val streetfire = engineOf(catalog, "honda-cb150r-streetfire-2021")
        val adventure = engineOf(catalog, "honda-cb150x")
        listOf(streetfire, adventure).forEach {
            assertEquals(cbr.boreMm.value, it.boreMm.value)
            assertEquals(cbr.strokeMm.value, it.strokeMm.value)
            assertEquals(cbr.compressionRatio.value, it.compressionRatio.value)
        }
        assertEquals(17.1, cbr.powerPs.value!!, 1e-9)
        assertEquals(16.9, streetfire.powerPs.value!!, 1e-9)
        assertEquals(15.6, adventure.powerPs.value!!, 1e-9)
    }

    @Test
    fun streetfireBodiesShareOneEngine() {
        val catalog = load()
        val records = catalog.generationsFor("Honda", "CB150R Streetfire")
        assertTrue(records.all { it.engineId == "honda-cb150r" })
        assertEquals(1293.0, records.first { it.id == "honda-cb150r-streetfire-prev" }.wheelbaseMm.value!!, 1e-9)
        assertEquals(1298.0, records.first { it.id == "honda-cb150r-streetfire-2021" }.wheelbaseMm.value!!, 1e-9)
    }

    @Test
    fun airCooled150UseLowerCompression() {
        val catalog = load()
        val trail = engineOf(catalog, "honda-crf150l")
        val naked = engineOf(catalog, "honda-cb150-verza")
        assertEquals(9.5, trail.compressionRatio.value!!, 1e-9)
        assertEquals(trail.compressionRatio.value, naked.compressionRatio.value)
        assertEquals(12.91, trail.powerPs.value!!, 1e-9)
        assertEquals(13.04, naked.powerPs.value!!, 1e-9)
        assertEquals("Air-cooled", naked.cooling.value)
    }

    @Test
    fun cbr250rrTwinsDifferInCompressionAndOutput() {
        val catalog = load()
        val first = engineOf(catalog, "honda-cbr250rr-2016")
        val update = engineOf(catalog, "honda-cbr250rr-sp-2023")
        assertEquals(2.0, first.cylinders.value!!, 1e-9)
        assertEquals(first.boreMm.value, update.boreMm.value)
        assertEquals(first.strokeMm.value, update.strokeMm.value)
        assertEquals(11.5, first.compressionRatio.value!!, 1e-9)
        assertEquals(12.5, update.compressionRatio.value!!, 1e-9)
        assertTrue(update.powerPs.value!! > first.powerPs.value!!)
    }

    @Test
    fun unsourcedValuesStayUnknown() {
        val catalog = load()
        val cbr = catalog.generations.first { it.id == "honda-cbr150r" }
        assertEquals(Verification.UNKNOWN, cbr.wheelbaseMm.status)
        assertEquals(Verification.UNKNOWN, cbr.kerbWeightKg.status)
        val previous = catalog.generations.first { it.id == "honda-cb150r-streetfire-prev" }
        assertEquals(Verification.UNKNOWN, previous.yearFrom.status)
        assertEquals(Verification.UNKNOWN, previous.frontTyre.status)
        assertEquals(Verification.UNKNOWN, engineOf(catalog, "honda-cbr250rr-sp-2023").cooling.status)
        assertEquals(Verification.UNKNOWN, engineOf(catalog, "honda-cb150r").cylinders.status)
    }

    @Test
    fun sportTransmissionsKeepRatiosUnknown() {
        val catalog = load()
        val gearbox = catalog.transmission("honda-crf150l-5speed")!!
        assertEquals("Manual, 5-speed", gearbox.kind.value)
        assertTrue(gearbox.gearRatios.isEmpty())
        assertEquals(Verification.UNKNOWN, gearbox.finalReduction.status)
    }
}
