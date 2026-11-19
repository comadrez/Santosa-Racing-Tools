package id.santosa.racingtools.data

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class HondaMaticOlderDataTest {
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
    fun olderFileIsListedAndValid() {
        val errors = CatalogValidator.validate(CatalogParser.parse(File(dir, "honda-matic-older.json").readText()))
        assertTrue(errors.joinToString("\n"), errors.isEmpty())
        val listed = JSONObject(File(dir, "index.json").readText()).getJSONArray("files")
        assertTrue(List(listed.length()) { listed.getString(it) }.contains("honda-matic-older.json"))
    }

    @Test
    fun carburettorVarioFigures() {
        val engine = engineOf(load(), "honda-vario-gen1-110")
        assertEquals(50.0, engine.boreMm.value!!, 1e-9)
        assertEquals(55.0, engine.strokeMm.value!!, 1e-9)
        assertEquals(10.7, engine.compressionRatio.value!!, 1e-9)
        assertEquals(8.99, engine.powerPs.value!!, 1e-9)
    }

    @Test
    fun techno125SharesBoreAndStrokeWithPcx125() {
        val catalog = load()
        val vario = engineOf(catalog, "honda-vario-techno-125-pgmfi")
        val pcx = engineOf(catalog, "honda-pcx-125")
        assertEquals(vario.boreMm.value, pcx.boreMm.value)
        assertEquals(vario.strokeMm.value, pcx.strokeMm.value)
    }

    @Test
    fun adv150AndPcx150ShareBoreStrokeAndCompression() {
        val catalog = load()
        val adv = engineOf(catalog, "honda-adv-150")
        val pcx = engineOf(catalog, "honda-pcx-150")
        assertEquals(pcx.boreMm.value, adv.boreMm.value)
        assertEquals(pcx.strokeMm.value, adv.strokeMm.value)
        assertEquals(pcx.compressionRatio.value, adv.compressionRatio.value)
        assertTrue(adv.torqueNm.value!! > pcx.torqueNm.value!!)
    }

    @Test
    fun beatGenerationsKeepTheirOwnEngines() {
        val catalog = load()
        assertEquals(9.2, engineOf(catalog, "honda-beat-gen1-carb").compressionRatio.value!!, 1e-9)
        assertEquals(55.1, engineOf(catalog, "honda-beat-gen3-esp").strokeMm.value!!, 1e-9)
        assertEquals(63.1, engineOf(catalog, "honda-beat-2020-esaf").strokeMm.value!!, 1e-9)
    }

    @Test
    fun conflictingOrMissingValuesStayUnknown() {
        val catalog = load()
        assertEquals(Verification.UNKNOWN, engineOf(catalog, "honda-pcx-150-cbu").powerPs.status)
        assertEquals(Verification.UNKNOWN, engineOf(catalog, "honda-vario-110-esp").boreMm.status)
        assertNull(catalog.generations.first { it.id == "honda-pcx-125" }.transmissionId)
        assertEquals(Verification.UNKNOWN, catalog.generations.first { it.id == "honda-vario-110-fi-air" }.yearFrom.status)
    }

    @Test
    fun spacyHasBothVersions() {
        val catalog = load()
        assertEquals(2, catalog.generationsFor("Honda", "Spacy").size)
        assertEquals(8.67, engineOf(catalog, "honda-spacy-helmin-pgmfi").powerPs.value!!, 1e-9)
    }
}
