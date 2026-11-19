package id.santosa.racingtools.data

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class SeedDataTest {
    private val dir = File("src/main/assets/specs")

    private fun listedFiles(): List<String> {
        val files = JSONObject(File(dir, "index.json").readText()).getJSONArray("files")
        return List(files.length()) { files.getString(it) }
    }

    private fun load(): Catalog =
        CatalogParser.merge(listedFiles().map { CatalogParser.parse(File(dir, it).readText()) })

    @Test
    fun seedDataPassesValidation() {
        val errors = CatalogValidator.validate(load())
        assertTrue(errors.joinToString("\n"), errors.isEmpty())
    }

    @Test
    fun indexListsEveryDataFile() {
        val onDisk = dir.listFiles { file -> file.extension == "json" && file.name != "index.json" }!!
            .map { it.name }
            .sorted()
        assertEquals(onDisk, listedFiles().sorted())
    }

    @Test
    fun beatRecordsShareOneSourcedEngine() {
        val catalog = load()
        val records = catalog.generationsFor("Honda", "BeAT")
        assertEquals(2, records.size)
        val engine = catalog.engine(records.first().engineId)!!
        assertEquals(47.0, engine.boreMm.value!!, 1e-9)
        assertEquals(63.1, engine.strokeMm.value!!, 1e-9)
        assertEquals(10.0, engine.compressionRatio.value!!, 1e-9)
        assertTrue(records.all { it.engineId == records.first().engineId })
    }

    @Test
    fun unlistedValuesStayUnknown() {
        val catalog = load()
        val transmission = catalog.transmission("honda-beat-vmatic")!!
        assertEquals(Verification.UNKNOWN, transmission.cvtLowRatio.status)
        assertEquals(Verification.UNKNOWN, transmission.finalReduction.status)
        val record = catalog.generationsFor("Honda", "BeAT").first()
        assertEquals(Verification.UNKNOWN, record.kerbWeightKg.status)
    }
}
