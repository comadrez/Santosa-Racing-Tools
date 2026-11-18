package id.santosa.racingtools.calc

import kotlin.math.PI

object EngineMath {
    const val MAX_CYLINDERS = 12

    fun displacementCc(boreMm: Double, strokeMm: Double, cylinders: Int): Double? {
        if (!boreMm.isPositiveFinite() || !strokeMm.isPositiveFinite()) return null
        if (cylinders !in 1..MAX_CYLINDERS) return null
        return PI / 4.0 * boreMm * boreMm * strokeMm * cylinders / 1000.0
    }

    fun staticCompressionRatio(sweptCc: Double, clearanceCc: Double): Double? {
        if (!sweptCc.isPositiveFinite() || !clearanceCc.isPositiveFinite()) return null
        return (sweptCc + clearanceCc) / clearanceCc
    }

    fun meanPistonSpeedMs(strokeMm: Double, rpm: Double): Double? {
        if (!strokeMm.isPositiveFinite() || !rpm.isPositiveFinite()) return null
        val strokeM = strokeMm / 1000.0
        return 2.0 * strokeM * rpm / 60.0
    }
}
