package id.santosa.racingtools.data

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class YamahaSportDataTest {
    private val dir = File("src/main/assets/specs")

    private fun load(): Catalog {
        val files = JSONObject(File(dir, "index.json").readText()).getJSONArray("files")
        val parts = List(files.length()) { CatalogParser.parse(File(dir, files.getString(it)).readText()) }
        return CatalogParser.merge(parts)
    }

    private fun generation(catalog: Catalog, id: String): Generation =
        catalog.generations.first { it.id == id }

    private fun engineOf(catalog: Catalog, generationId: String): Engine {
        val record = generation(catalog, generationId)
        return catalog.engine(record.engineId)!!
    }

    @Test
    fun everyYamahaSportFamilyHasRecords() {
        val catalog = load()
        val expected = mapOf(
            "R15" to 2,
            "MT-15" to 1,
            "XSR155" to 1,
            "WR155R" to 1,
            "R25" to 2,
            "MT-25" to 1,
            "Vixion" to 2,
            "Byson" to 2,
            "Scorpio Z" to 1
        )
        expected.forEach { (family, count) ->
            assertEquals(family, count, catalog.generationsFor("Yamaha", family).size)
        }
    }

    @Test
    fun r15EngineIsSharedAcrossFamilies() {
        val catalog = load()
        val ids = listOf(
            "yamaha-r15-2017",
            "yamaha-r15-connected",
            "yamaha-mt-15",
            "yamaha-xsr-155",
            "yamaha-vixion-r"
        )
        assertEquals(1, ids.map { generation(catalog, it).engineId }.distinct().size)
        val engine = engineOf(catalog, "yamaha-r15-connected")
        assertEquals(58.0, engine.boreMm.value!!, 1e-9)
        assertEquals(58.7, engine.strokeMm.value!!, 1e-9)
        assertEquals(11.6, engine.compressionRatio.value!!, 1e-9)
        assertEquals(19.3, engine.powerPs.value!!, 1e-9)
        assertEquals(14.7, engine.torqueNm.value!!, 1e-9)
    }

    @Test
    fun wr155rKeepsGeometryButLowerOutput() {
        val catalog = load()
        val r15 = engineOf(catalog, "yamaha-r15-connected")
        val wr = engineOf(catalog, "yamaha-wr-155r")
        assertEquals(r15.boreMm.value, wr.boreMm.value)
        assertEquals(r15.strokeMm.value, wr.strokeMm.value)
        assertEquals(r15.compressionRatio.value, wr.compressionRatio.value)
        assertEquals(16.7, wr.powerPs.value!!, 1e-9)
        assertTrue(wr.powerPs.value!! < r15.powerPs.value!!)
        assertEquals(6500.0, wr.torqueRpm.value!!, 1e-9)
    }

    @Test
    fun vixionBodiesDifferInEngineAndWheelbase() {
        val catalog = load()
        val plain = generation(catalog, "yamaha-vixion-150")
        val r = generation(catalog, "yamaha-vixion-r")
        assertTrue(plain.engineId != r.engineId)
        assertEquals(1295.0, plain.wheelbaseMm.value!!, 1e-9)
        assertEquals(1320.0, r.wheelbaseMm.value!!, 1e-9)
        val engine = engineOf(catalog, "yamaha-vixion-150")
        assertEquals(57.0, engine.boreMm.value!!, 1e-9)
        assertEquals(149.8, engine.displacementCc.value!!, 1e-9)
        assertEquals(Verification.CALCULATED, engine.powerPs.status)
        assertEquals("Manual, 5-speed", catalog.transmission(plain.transmissionId)!!.kind.value)
        assertEquals("Manual, 6-speed", catalog.transmission(r.transmissionId)!!.kind.value)
    }

    @Test
    fun r25AndMt25ShareTheTwinEngine() {
        val catalog = load()
        val r25 = generation(catalog, "yamaha-r25-2014")
        val mt25 = generation(catalog, "yamaha-mt-25")
        assertEquals(r25.engineId, mt25.engineId)
        val engine = catalog.engine(r25.engineId)!!
        assertEquals(2.0, engine.cylinders.value!!, 1e-9)
        assertEquals(60.0, engine.boreMm.value!!, 1e-9)
        assertEquals(44.1, engine.strokeMm.value!!, 1e-9)
        assertEquals(36.0, engine.powerPs.value!!, 1e-9)
        assertTrue(mt25.kerbWeightKg.value!! < r25.kerbWeightKg.value!!)
    }

    @Test
    fun euro5R25ConvertsPowerFromKilowatts() {
        val catalog = load()
        val old = engineOf(catalog, "yamaha-r25-2014")
        val update = engineOf(catalog, "yamaha-r25-euro5")
        assertEquals(old.boreMm.value, update.boreMm.value)
        assertEquals(old.strokeMm.value, update.strokeMm.value)
        assertEquals(Verification.CALCULATED, update.powerPs.status)
        assertEquals(22.5, update.torqueNm.value!!, 1e-9)
        assertTrue(update.torqueNm.value!! < old.torqueNm.value!!)
    }

    @Test
    fun bysonBodiesUseDifferentAirCooledEngines() {
        val catalog = load()
        val carb = engineOf(catalog, "yamaha-byson-2010")
        val injected = engineOf(catalog, "yamaha-byson-fi")
        assertEquals("Air-cooled", carb.cooling.value)
        assertEquals("Air-cooled", injected.cooling.value)
        assertEquals(153.0, carb.displacementCc.value!!, 1e-9)
        assertEquals(149.0, injected.displacementCc.value!!, 1e-9)
        assertTrue(injected.powerPs.value!! > carb.powerPs.value!!)
        assertNull(generation(catalog, "yamaha-byson-2010").transmissionId)
        assertEquals(
            "Manual, 5-speed",
            catalog.transmission(generation(catalog, "yamaha-byson-fi").transmissionId)!!.kind.value
        )
    }

    @Test
    fun sportTransmissionsKeepRatiosUnknown() {
        val catalog = load()
        val gearbox = catalog.transmission("yamaha-155-6speed")!!
        assertEquals("Manual, 6-speed", gearbox.kind.value)
        assertTrue(gearbox.gearRatios.isEmpty())
        assertEquals(Verification.UNKNOWN, gearbox.finalReduction.status)
    }

    @Test
    fun unsourcedValuesStayUnknown() {
        val catalog = load()
        val euro5 = generation(catalog, "yamaha-r25-euro5")
        assertEquals(Verification.UNKNOWN, euro5.yearFrom.status)
        assertEquals(Verification.UNKNOWN, euro5.frontTyre.status)
        assertNull(euro5.transmissionId)
        assertNull(generation(catalog, "yamaha-mt-25").transmissionId)
        assertEquals(Verification.UNKNOWN, generation(catalog, "yamaha-scorpio-z").yearFrom.status)
        assertEquals(Verification.UNKNOWN, generation(catalog, "yamaha-byson-fi").frontTyre.status)
        assertEquals(Verification.UNKNOWN, engineOf(catalog, "yamaha-scorpio-z").fuelSystem.status)
        assertEquals(Verification.UNKNOWN, engineOf(catalog, "yamaha-r25-euro5").fuelSystem.status)
    }
}
