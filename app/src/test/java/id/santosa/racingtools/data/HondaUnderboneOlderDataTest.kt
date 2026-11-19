package id.santosa.racingtools.data

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class HondaUnderboneOlderDataTest {
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
        val errors = CatalogValidator.validate(CatalogParser.parse(File(dir, "honda-underbone-older.json").readText()))
        assertTrue(errors.joinToString("\n"), errors.isEmpty())
        val listed = JSONObject(File(dir, "index.json").readText()).getJSONArray("files")
        assertTrue(List(listed.length()) { listed.getString(it) }.contains("honda-underbone-older.json"))
    }

    @Test
    fun supraFitFigures() {
        val engine = engineOf(load(), "honda-supra-fit-100")
        assertEquals(50.0, engine.boreMm.value!!, 1e-9)
        assertEquals(49.5, engine.strokeMm.value!!, 1e-9)
        assertEquals(9.0, engine.compressionRatio.value!!, 1e-9)
        assertEquals(7.3, engine.powerPs.value!!, 1e-9)
    }

    @Test
    fun firstRevoSharesTheSupraFitEngine() {
        val catalog = load()
        val revo = catalog.generations.first { it.id == "honda-revo-100" }
        val fit = catalog.generations.first { it.id == "honda-supra-fit-100" }
        assertEquals(fit.engineId, revo.engineId)
    }

    @Test
    fun absoluteRevoFigures() {
        val engine = engineOf(load(), "honda-revo-absolute-110")
        assertEquals(50.0, engine.boreMm.value!!, 1e-9)
        assertEquals(55.6, engine.strokeMm.value!!, 1e-9)
        assertEquals(9.0, engine.compressionRatio.value!!, 1e-9)
        assertEquals(8.46, engine.powerPs.value!!, 1e-9)
    }

    @Test
    fun carburettorSupra125AndKiranaShareGeometry() {
        val catalog = load()
        val supra = engineOf(catalog, "honda-supra-x-125-carb")
        val kirana = engineOf(catalog, "honda-kirana-125")
        assertEquals(supra.boreMm.value, kirana.boreMm.value)
        assertEquals(supra.strokeMm.value, kirana.strokeMm.value)
        assertEquals(supra.compressionRatio.value, kirana.compressionRatio.value)
    }

    @Test
    fun conflictingOrMissingValuesStayUnknown() {
        val catalog = load()
        assertEquals(Verification.UNKNOWN, engineOf(catalog, "honda-supra-x-125-carb").torqueNm.status)
        assertEquals(Verification.UNKNOWN, engineOf(catalog, "honda-supra-x-100").compressionRatio.status)
        assertNull(catalog.generations.first { it.id == "honda-supra-x-100" }.transmissionId)
        assertEquals(Verification.UNKNOWN, catalog.generations.first { it.id == "honda-revo-100" }.yearTo.status)
    }
}
