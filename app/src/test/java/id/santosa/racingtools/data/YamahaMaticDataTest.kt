package id.santosa.racingtools.data

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class YamahaMaticDataTest {
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
    fun everyYamahaMaticFamilyHasRecords() {
        val catalog = load()
        val expected = mapOf(
            "Aerox" to 1,
            "NMAX" to 2,
            "Lexi" to 1,
            "Fazzio" to 1,
            "Grand Filano" to 2,
            "Gear" to 2,
            "FreeGo" to 1
        )
        expected.forEach { (family, count) ->
            assertEquals(family, count, catalog.generationsFor("Yamaha", family).size)
        }
    }

    @Test
    fun maxi155ShareGeometryButNotTorque() {
        val catalog = load()
        val aerox = engineOf(catalog, "yamaha-aerox-155")
        val lexi = engineOf(catalog, "yamaha-lexi-lx-155")
        val nmax = engineOf(catalog, "yamaha-nmax-155-2021")
        val neo = engineOf(catalog, "yamaha-nmax-155-neo")
        listOf(lexi, nmax, neo).forEach {
            assertEquals(aerox.boreMm.value, it.boreMm.value)
            assertEquals(aerox.strokeMm.value, it.strokeMm.value)
            assertEquals(aerox.compressionRatio.value, it.compressionRatio.value)
            assertEquals(15.3, it.powerPs.value!!, 1e-9)
        }
        assertEquals(13.9, nmax.torqueNm.value!!, 1e-9)
        assertEquals(14.2, neo.torqueNm.value!!, 1e-9)
        assertEquals(14.2, lexi.torqueNm.value!!, 1e-9)
    }

    @Test
    fun hybrid125EnginesShareGeometry() {
        val catalog = load()
        val fazzio = engineOf(catalog, "yamaha-fazzio-hybrid")
        val filano = engineOf(catalog, "yamaha-grand-filano-2023")
        assertEquals(52.4, fazzio.boreMm.value!!, 1e-9)
        assertEquals(fazzio.boreMm.value, filano.boreMm.value)
        assertEquals(fazzio.strokeMm.value, filano.strokeMm.value)
        assertEquals(11.0, filano.compressionRatio.value!!, 1e-9)
        assertTrue(fazzio.powerPs.value!! > filano.powerPs.value!!)
    }

    @Test
    fun gearAndFreeGoShareOneLowerCompressionEngine() {
        val catalog = load()
        val gear = catalog.generations.first { it.id == "yamaha-gear-125" }
        val freego = catalog.generations.first { it.id == "yamaha-freego-125" }
        assertEquals(gear.engineId, freego.engineId)
        val engine = catalog.engine(gear.engineId)!!
        assertEquals(9.5, engine.compressionRatio.value!!, 1e-9)
        assertEquals(9.5, engine.powerPs.value!!, 1e-9)
    }

    @Test
    fun grandFilanoKeepsBothGenerations() {
        val catalog = load()
        val records = catalog.generationsFor("Yamaha", "Grand Filano")
        assertEquals(setOf(2015.0, 2023.0), records.map { it.yearFrom.value!! }.toSet())
        assertEquals(1280.0, records.first { it.id == "yamaha-grand-filano-2023" }.wheelbaseMm.value!!, 1e-9)
    }

    @Test
    fun unsourcedValuesStayUnknown() {
        val catalog = load()
        val ultima = engineOf(catalog, "yamaha-gear-ultima-125")
        assertEquals(Verification.UNKNOWN, ultima.powerPs.status)
        assertEquals(Verification.UNKNOWN, ultima.boreMm.status)
        assertEquals(Verification.UNKNOWN, catalog.generations.first { it.id == "yamaha-fazzio-hybrid" }.wheelbaseMm.status)
        assertEquals(Verification.UNKNOWN, catalog.generations.first { it.id == "yamaha-aerox-155" }.frontTyre.status)
        assertNull(catalog.generations.first { it.id == "yamaha-grand-filano-2023" }.transmissionId)
    }

    @Test
    fun yamahaTransmissionsKeepRatiosUnknown() {
        val catalog = load()
        val cvt = catalog.transmission("yamaha-lexi-lx-155-cvt")!!
        assertEquals("Automatic CVT", cvt.kind.value)
        assertEquals(Verification.UNKNOWN, cvt.cvtLowRatio.status)
        assertEquals(Verification.UNKNOWN, cvt.finalReduction.status)
    }
}
