package id.santosa.racingtools.data

enum class Verification(val key: String, val label: String) {
    FACTORY("factory", "Factory verified"),
    TECHNICAL("technical", "Technical verified"),
    SECONDARY("secondary", "Secondary source"),
    MEASURED("measured", "Workshop measured"),
    CALCULATED("calculated", "Calculated"),
    UNKNOWN("unknown", "Unknown");

    val needsSource: Boolean
        get() = this == FACTORY || this == TECHNICAL || this == SECONDARY

    companion object {
        fun fromKey(key: String?): Verification? = values().firstOrNull { it.key == key }
    }
}

data class SpecNumber(
    val value: Double?,
    val status: Verification,
    val sourceId: String?
) {
    companion object {
        val UNKNOWN = SpecNumber(null, Verification.UNKNOWN, null)
    }
}

data class SpecText(
    val value: String?,
    val status: Verification,
    val sourceId: String?
) {
    companion object {
        val UNKNOWN = SpecText(null, Verification.UNKNOWN, null)
    }
}

data class Source(
    val id: String,
    val title: String,
    val publisher: String,
    val url: String?,
    val published: String?,
    val accessed: String,
    val type: String
)

data class Engine(
    val id: String,
    val name: String,
    val cooling: SpecText = SpecText.UNKNOWN,
    val fuelSystem: SpecText = SpecText.UNKNOWN,
    val cylinders: SpecNumber = SpecNumber.UNKNOWN,
    val boreMm: SpecNumber = SpecNumber.UNKNOWN,
    val strokeMm: SpecNumber = SpecNumber.UNKNOWN,
    val displacementCc: SpecNumber = SpecNumber.UNKNOWN,
    val compressionRatio: SpecNumber = SpecNumber.UNKNOWN,
    val powerPs: SpecNumber = SpecNumber.UNKNOWN,
    val powerRpm: SpecNumber = SpecNumber.UNKNOWN,
    val torqueNm: SpecNumber = SpecNumber.UNKNOWN,
    val torqueRpm: SpecNumber = SpecNumber.UNKNOWN
) {
    val numberFields: List<SpecNumber>
        get() = listOf(cylinders, boreMm, strokeMm, displacementCc, compressionRatio, powerPs, powerRpm, torqueNm, torqueRpm)

    val textFields: List<SpecText>
        get() = listOf(cooling, fuelSystem)
}

data class Transmission(
    val id: String,
    val kind: SpecText = SpecText.UNKNOWN,
    val cvtLowRatio: SpecNumber = SpecNumber.UNKNOWN,
    val cvtHighRatio: SpecNumber = SpecNumber.UNKNOWN,
    val primaryRatio: SpecNumber = SpecNumber.UNKNOWN,
    val finalReduction: SpecNumber = SpecNumber.UNKNOWN,
    val frontSprocketTeeth: SpecNumber = SpecNumber.UNKNOWN,
    val rearSprocketTeeth: SpecNumber = SpecNumber.UNKNOWN,
    val gearRatios: List<SpecNumber> = emptyList()
) {
    val numberFields: List<SpecNumber>
        get() = listOf(cvtLowRatio, cvtHighRatio, primaryRatio, finalReduction, frontSprocketTeeth, rearSprocketTeeth) + gearRatios

    val textFields: List<SpecText>
        get() = listOf(kind)
}

data class Generation(
    val id: String,
    val brand: String,
    val family: String,
    val category: String,
    val label: String,
    val yearFrom: SpecNumber = SpecNumber.UNKNOWN,
    val yearTo: SpecNumber = SpecNumber.UNKNOWN,
    val inProduction: Boolean = false,
    val engineId: String? = null,
    val transmissionId: String? = null,
    val wheelbaseMm: SpecNumber = SpecNumber.UNKNOWN,
    val kerbWeightKg: SpecNumber = SpecNumber.UNKNOWN,
    val seatHeightMm: SpecNumber = SpecNumber.UNKNOWN,
    val frontTyre: SpecText = SpecText.UNKNOWN,
    val rearTyre: SpecText = SpecText.UNKNOWN,
    val notes: String? = null
) {
    val numberFields: List<SpecNumber>
        get() = listOf(yearFrom, yearTo, wheelbaseMm, kerbWeightKg, seatHeightMm)

    val textFields: List<SpecText>
        get() = listOf(frontTyre, rearTyre)
}

data class Catalog(
    val sources: List<Source>,
    val engines: List<Engine>,
    val transmissions: List<Transmission>,
    val generations: List<Generation>
) {
    fun source(id: String?): Source? = sources.firstOrNull { it.id == id }

    fun engine(id: String?): Engine? = engines.firstOrNull { it.id == id }

    fun transmission(id: String?): Transmission? = transmissions.firstOrNull { it.id == id }

    fun generationsFor(brand: String, family: String): List<Generation> =
        generations.filter { it.brand.equals(brand, ignoreCase = true) && it.family.equals(family, ignoreCase = true) }

    companion object {
        val EMPTY = Catalog(emptyList(), emptyList(), emptyList(), emptyList())
    }
}
