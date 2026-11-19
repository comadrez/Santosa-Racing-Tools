package id.santosa.racingtools.data

import java.util.Locale

object SpecFormat {
    const val UNKNOWN_TEXT = "Unknown"

    fun number(field: SpecNumber, decimals: Int = 1, unit: String = ""): String {
        val value = field.value ?: return UNKNOWN_TEXT
        val text = String.format(Locale.US, "%." + decimals + "f", value)
        return if (unit.isBlank()) text else text + " " + unit
    }

    fun compression(field: SpecNumber): String {
        val value = field.value ?: return UNKNOWN_TEXT
        return String.format(Locale.US, "%.1f : 1", value)
    }

    fun atRpm(value: SpecNumber, rpm: SpecNumber, decimals: Int, unit: String): String {
        val main = number(value, decimals, unit)
        val speed = rpm.value ?: return main
        if (value.value == null) return main
        return main + " @ " + String.format(Locale.US, "%,d", speed.toInt()) + " rpm"
    }

    fun text(field: SpecText): String = field.value?.takeIf { it.isNotBlank() } ?: UNKNOWN_TEXT

    fun yearRange(generation: Generation): String {
        val from = generation.yearFrom.value?.toInt() ?: return UNKNOWN_TEXT
        val to = generation.yearTo.value?.toInt()
        return when {
            to != null -> from.toString() + " - " + to
            generation.inProduction -> from.toString() + " - present"
            else -> from.toString() + " - end year unknown"
        }
    }

    fun yearOptions(generation: Generation, currentYear: Int): List<String> {
        val from = generation.yearFrom.value?.toInt() ?: return emptyList()
        val end = generation.yearTo.value?.toInt() ?: if (generation.inProduction) currentYear else from
        if (end < from) return listOf(from.toString())
        return (from..end).map { it.toString() }
    }

    fun sourceIds(generation: Generation, engine: Engine?, transmission: Transmission?): List<String> {
        val ids = linkedSetOf<String>()
        val numbers = generation.numberFields +
            (engine?.numberFields ?: emptyList()) +
            (transmission?.numberFields ?: emptyList())
        val texts = generation.textFields +
            (engine?.textFields ?: emptyList()) +
            (transmission?.textFields ?: emptyList())
        numbers.forEach { field -> field.sourceId?.let { ids.add(it) } }
        texts.forEach { field -> field.sourceId?.let { ids.add(it) } }
        return ids.toList()
    }
}
