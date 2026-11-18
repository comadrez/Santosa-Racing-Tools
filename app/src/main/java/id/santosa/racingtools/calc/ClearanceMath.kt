package id.santosa.racingtools.calc

import kotlin.math.PI

object ClearanceMath {
    fun buretteVolumeCc(startCc: Double, endCc: Double): Double? {
        if (!startCc.isNonNegativeFinite() || !endCc.isPositiveFinite()) return null
        return if (endCc > startCc) endCc - startCc else null
    }

    fun discVolumeCc(boreMm: Double, heightMm: Double): Double? {
        if (!boreMm.isPositiveFinite() || !heightMm.isNonNegativeFinite()) return null
        return PI / 4.0 * boreMm * boreMm * heightMm / 1000.0
    }

    fun totalCc(chamberCc: Double, gasketCc: Double, deckCc: Double, crownCc: Double): Double? {
        if (!chamberCc.isPositiveFinite()) return null
        if (!gasketCc.isNonNegativeFinite() || !deckCc.isNonNegativeFinite()) return null
        if (!crownCc.isFinite()) return null
        val total = chamberCc + gasketCc + deckCc + crownCc
        return if (total > 0.0) total else null
    }
}
