package id.santosa.racingtools.data

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class YamahaUnderboneDataTest {
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
        val expected = mapOf("MX King" to 2, "Jupiter" to 2, "Vega" to 1)
        expected.forEach { (family, count) ->
            assertEquals(family, count, catalog.generationsFor("Yamaha", family).size)
        }
    }

    @Test
    fun mxKingAndJupiterMx150ShareOneEngine() {
        val catalog = load()
        val jupiter = catalog.generations.first { it.id == "yamaha-jupiter-mx-150" }
        val mxKing = catalog.generations.first { it.id == "yamaha-mx-king-150-2015" }
        assertEquals(mxKing.engineId, jupiter.engineId)
        val engine = catalog.engine(jupiter.engineId)!!
        assertEquals(57.0, engine.boreMm.value!!, 1e-9)
        assertEquals(58.7, engine.strokeMm.value!!, 1e-9)
        assertEquals(10.4, engine.compressionRatio.value!!, 1e-9)
        assertEquals(15.3, engine.powerPs.value!!, 1e-9)
    }

    @Test
    fun mxKingBodiesDifferInSeatAndWeight() {
        val catalog = load()
        val old = catalog.generations.first { it.id == "yamaha-mx-king-150-2015" }
        val current = catalog.generations.first { it.id == "yamaha-mx-king-150" }
        assertEquals(780.0, old.seatHeightMm.value!!, 1e-9)
        assertEquals(795.0, current.seatHeightMm.value!!, 1e-9)
        assertTrue(current.kerbWeightKg.value!! > old.kerbWeightKg.value!!)
        assertEquals(Verification.UNKNOWN, current.yearFrom.status)
    }

    @Test
    fun jupiterZ1AndVegaForceShareGeometry() {
        val catalog = load()
        val z1 = engineOf(catalog, "yamaha-jupiter-z1")
        val vega = engineOf(catalog, "yamaha-vega-force")
        assertEquals(50.0, z1.boreMm.value!!, 1e-9)
        assertEquals(z1.boreMm.value, vega.boreMm.value)
        assertEquals(z1.strokeMm.value, vega.strokeMm.value)
        assertEquals(z1.compressionRatio.value, vega.compressionRatio.value)
        assertTrue(z1.powerPs.value!! > vega.powerPs.value!!)
        assertEquals("Air-cooled", vega.cooling.value)
    }

    @Test
    fun vegaForceHasFourSpeedGearbox() {
        val catalog = load()
        val record = catalog.generations.first { it.id == "yamaha-vega-force" }
        val gearbox = catalog.transmission(record.transmissionId)!!
        assertEquals("Manual, 4-speed", gearbox.kind.value)
        assertTrue(gearbox.gearRatios.isEmpty())
        assertEquals(Verification.UNKNOWN, gearbox.finalReduction.status)
    }

    @Test
    fun unsourcedValuesStayUnknown() {
        val catalog = load()
        val z1 = catalog.generations.first { it.id == "yamaha-jupiter-z1" }
        assertEquals(Verification.UNKNOWN, z1.frontTyre.status)
        assertEquals(Verification.UNKNOWN, z1.yearFrom.status)
        assertNull(z1.transmissionId)
        val jupiter = catalog.generations.first { it.id == "yamaha-jupiter-mx-150" }
        assertEquals(Verification.UNKNOWN, jupiter.rearTyre.status)
    }
}
