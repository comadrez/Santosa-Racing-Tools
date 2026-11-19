package id.santosa.racingtools.data

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class HondaMaticDataTest {
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
    fun everyMaticFamilyHasRecords() {
        val catalog = load()
        val expected = mapOf(
            "BeAT" to 6,
            "Vario" to 8,
            "Stylo 160" to 1,
            "Scoopy" to 5,
            "Genio" to 1,
            "PCX" to 4,
            "ADV" to 2,
            "Forza" to 2,
            "Spacy" to 2
        )
        expected.forEach { (family, count) ->
            assertEquals(family, count, catalog.generationsFor("Honda", family).size)
        }
    }

    @Test
    fun vario160Figures() {
        val engine = engineOf(load(), "honda-vario-160")
        assertEquals(60.0, engine.boreMm.value!!, 1e-9)
        assertEquals(55.5, engine.strokeMm.value!!, 1e-9)
        assertEquals(12.0, engine.compressionRatio.value!!, 1e-9)
        assertEquals(15.4, engine.powerPs.value!!, 1e-9)
        assertEquals(7000.0, engine.torqueRpm.value!!, 1e-9)
    }

    @Test
    fun pcxAndAdvOutputDiffersFromVario160() {
        val catalog = load()
        val vario = engineOf(catalog, "honda-vario-160")
        val pcx = engineOf(catalog, "honda-pcx-160")
        val adv = engineOf(catalog, "honda-adv-160")
        assertEquals(vario.boreMm.value, pcx.boreMm.value)
        assertEquals(16.0, pcx.powerPs.value!!, 1e-9)
        assertEquals(pcx.powerPs.value, adv.powerPs.value)
        assertEquals(14.7, adv.torqueNm.value!!, 1e-9)
        assertTrue(pcx.powerPs.value!! > vario.powerPs.value!!)
    }

    @Test
    fun forzaKeepsBothEngineVersions() {
        val catalog = load()
        val early = engineOf(catalog, "honda-forza-250-gen4")
        val update = engineOf(catalog, "honda-forza-250-2021")
        assertEquals(68.0, early.boreMm.value!!, 1e-9)
        assertEquals(67.0, update.boreMm.value!!, 1e-9)
        assertEquals(70.7, update.strokeMm.value!!, 1e-9)
    }

    @Test
    fun unsourcedValuesStayUnknown() {
        val catalog = load()
        assertEquals(Verification.UNKNOWN, engineOf(catalog, "honda-pcx-160").cooling.status)
        assertNull(catalog.generations.first { it.id == "honda-pcx-160" }.transmissionId)
        assertEquals(Verification.UNKNOWN, engineOf(catalog, "honda-scoopy-early-esp").compressionRatio.status)
        assertEquals(Verification.UNKNOWN, catalog.generations.first { it.id == "honda-genio" }.kerbWeightKg.status)
    }

    @Test
    fun cylinderCountWorkedOutFromDisplacementIsMarkedCalculated() {
        val engine = engineOf(load(), "honda-adv-160")
        assertEquals(Verification.CALCULATED, engine.cylinders.status)
        assertNull(engine.cylinders.sourceId)
    }
}
