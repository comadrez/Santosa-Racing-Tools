package id.santosa.racingtools.data

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class HondaUnderboneDataTest {
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
    fun everyUnderboneFamilyHasRecords() {
        val catalog = load()
        val expected = mapOf("Supra" to 7, "Revo" to 3, "Kirana" to 1, "Blade" to 2, "Sonic 150R" to 1)
        expected.forEach { (family, count) ->
            assertEquals(family, count, catalog.generationsFor("Honda", family).size)
        }
    }

    @Test
    fun supraAndBladeShare125Geometry() {
        val catalog = load()
        val supra = engineOf(catalog, "honda-supra-x-125-fi")
        val blade = engineOf(catalog, "honda-blade-125-fi")
        assertEquals(52.4, supra.boreMm.value!!, 1e-9)
        assertEquals(supra.boreMm.value, blade.boreMm.value)
        assertEquals(supra.strokeMm.value, blade.strokeMm.value)
        assertEquals(9.3, blade.compressionRatio.value!!, 1e-9)
    }

    @Test
    fun gtrAndSonicShare150GeometryButNotOutput() {
        val catalog = load()
        val gtr = engineOf(catalog, "honda-supra-gtr-150")
        val sonic = engineOf(catalog, "honda-sonic-150r")
        assertEquals(57.3, gtr.boreMm.value!!, 1e-9)
        assertEquals(gtr.strokeMm.value, sonic.strokeMm.value)
        assertEquals(16.3, gtr.powerPs.value!!, 1e-9)
        assertEquals(16.0, sonic.powerPs.value!!, 1e-9)
        assertTrue(gtr.torqueNm.value!! > sonic.torqueNm.value!!)
    }

    @Test
    fun revoFiFigures() {
        val engine = engineOf(load(), "honda-revo-fi")
        assertEquals(50.0, engine.boreMm.value!!, 1e-9)
        assertEquals(55.6, engine.strokeMm.value!!, 1e-9)
        assertEquals(9.3, engine.compressionRatio.value!!, 1e-9)
        assertEquals(6000.0, engine.torqueRpm.value!!, 1e-9)
    }

    @Test
    fun unsourcedValuesStayUnknown() {
        val catalog = load()
        val helmIn = engineOf(catalog, "honda-supra-x-125-helmin")
        assertEquals(Verification.UNKNOWN, helmIn.compressionRatio.status)
        assertEquals(Verification.UNKNOWN, helmIn.torqueRpm.status)
        val blade110 = engineOf(catalog, "honda-blade-110")
        assertEquals(Verification.UNKNOWN, blade110.powerPs.status)
        assertNull(catalog.generations.first { it.id == "honda-blade-110" }.transmissionId)
        assertEquals(Verification.UNKNOWN, catalog.generations.first { it.id == "honda-supra-x-125-fi" }.kerbWeightKg.status)
    }

    @Test
    fun underboneTransmissionsKeepRatiosUnknown() {
        val catalog = load()
        val gearbox = catalog.transmission("honda-sonic150r-6speed")!!
        assertEquals("Manual, 6-speed", gearbox.kind.value)
        assertTrue(gearbox.gearRatios.isEmpty())
        assertEquals(Verification.UNKNOWN, gearbox.finalReduction.status)
    }
}
