package id.santosa.racingtools.data

import org.json.JSONObject

object CatalogParser {
    const val FORMAT_VERSION = 1

    fun parse(json: String): Catalog {
        val root = JSONObject(json)
        val version = root.optInt("formatVersion", -1)
        require(version == FORMAT_VERSION) { "Unsupported formatVersion: " + version }
        return Catalog(
            sources = root.objects("sources").map { parseSource(it) },
            engines = root.objects("engines").map { parseEngine(it) },
            transmissions = root.objects("transmissions").map { parseTransmission(it) },
            generations = root.objects("generations").map { parseGeneration(it) }
        )
    }

    fun merge(parts: List<Catalog>): Catalog = Catalog(
        sources = parts.flatMap { it.sources },
        engines = parts.flatMap { it.engines },
        transmissions = parts.flatMap { it.transmissions },
        generations = parts.flatMap { it.generations }
    )

    private fun parseSource(o: JSONObject) = Source(
        id = o.getString("id"),
        title = o.getString("title"),
        publisher = o.getString("publisher"),
        url = o.stringOrNull("url"),
        published = o.stringOrNull("published"),
        accessed = o.getString("accessed"),
        type = o.getString("type")
    )

    private fun parseEngine(o: JSONObject) = Engine(
        id = o.getString("id"),
        name = o.getString("name"),
        cooling = o.text("cooling"),
        fuelSystem = o.text("fuelSystem"),
        cylinders = o.number("cylinders"),
        boreMm = o.number("boreMm"),
        strokeMm = o.number("strokeMm"),
        displacementCc = o.number("displacementCc"),
        compressionRatio = o.number("compressionRatio"),
        powerPs = o.number("powerPs"),
        powerRpm = o.number("powerRpm"),
        torqueNm = o.number("torqueNm"),
        torqueRpm = o.number("torqueRpm")
    )

    private fun parseTransmission(o: JSONObject): Transmission {
        val ratios = o.optJSONArray("gearRatios")
        val gears = if (ratios == null) {
            emptyList<SpecNumber>()
        } else {
            List(ratios.length()) { numberOf(ratios.getJSONObject(it), "gearRatios") }
        }
        return Transmission(
            id = o.getString("id"),
            kind = o.text("kind"),
            cvtLowRatio = o.number("cvtLowRatio"),
            cvtHighRatio = o.number("cvtHighRatio"),
            primaryRatio = o.number("primaryRatio"),
            finalReduction = o.number("finalReduction"),
            frontSprocketTeeth = o.number("frontSprocketTeeth"),
            rearSprocketTeeth = o.number("rearSprocketTeeth"),
            gearRatios = gears
        )
    }

    private fun parseGeneration(o: JSONObject) = Generation(
        id = o.getString("id"),
        brand = o.getString("brand"),
        family = o.getString("family"),
        category = o.getString("category"),
        label = o.getString("label"),
        yearFrom = o.number("yearFrom"),
        yearTo = o.number("yearTo"),
        inProduction = o.optBoolean("inProduction", false),
        engineId = o.stringOrNull("engineId"),
        transmissionId = o.stringOrNull("transmissionId"),
        wheelbaseMm = o.number("wheelbaseMm"),
        kerbWeightKg = o.number("kerbWeightKg"),
        seatHeightMm = o.number("seatHeightMm"),
        frontTyre = o.text("frontTyre"),
        rearTyre = o.text("rearTyre"),
        notes = o.stringOrNull("notes")
    )

    private fun JSONObject.objects(key: String): List<JSONObject> {
        val array = optJSONArray(key) ?: return emptyList()
        return List(array.length()) { array.getJSONObject(it) }
    }

    private fun JSONObject.stringOrNull(key: String): String? =
        if (has(key) && !isNull(key)) getString(key) else null

    private fun JSONObject.number(key: String): SpecNumber = numberOf(optJSONObject(key), key)

    private fun numberOf(field: JSONObject?, key: String): SpecNumber {
        if (field == null) return SpecNumber.UNKNOWN
        val value = if (field.has("v") && !field.isNull("v")) field.getDouble("v") else null
        return SpecNumber(value, statusOf(field, key), field.stringOrNull("src"))
    }

    private fun JSONObject.text(key: String): SpecText {
        val field = optJSONObject(key) ?: return SpecText.UNKNOWN
        return SpecText(field.stringOrNull("v"), statusOf(field, key), field.stringOrNull("src"))
    }

    private fun statusOf(field: JSONObject, key: String): Verification {
        val raw = field.stringOrNull("s") ?: "unknown"
        return Verification.fromKey(raw)
            ?: throw IllegalArgumentException("Unknown status '" + raw + "' in field " + key)
    }
}
