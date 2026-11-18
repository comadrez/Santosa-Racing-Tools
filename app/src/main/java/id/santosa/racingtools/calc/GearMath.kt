package id.santosa.racingtools.calc

import kotlin.math.PI

object GearMath {
    fun ratio(drivingTeeth: Double, drivenTeeth: Double): Double? {
        if (!drivingTeeth.isPositiveFinite() || !drivenTeeth.isPositiveFinite()) return null
        return drivenTeeth / drivingTeeth
    }

    fun tyreCircumferenceMm(widthMm: Double, aspectPercent: Double, rimInch: Double): Double? {
        if (!widthMm.isPositiveFinite() || !aspectPercent.isPositiveFinite() || !rimInch.isPositiveFinite()) return null
        val diameterMm = rimInch * 25.4 + 2.0 * widthMm * aspectPercent / 100.0
        return PI * diameterMm
    }

    fun roadSpeedKmh(
        rpm: Double,
        primaryRatio: Double,
        gearRatio: Double,
        frontTeeth: Double,
        rearTeeth: Double,
        wheelCircumferenceMm: Double
    ): Double? {
        if (!rpm.isPositiveFinite() || !primaryRatio.isPositiveFinite() || !gearRatio.isPositiveFinite()) return null
        if (!frontTeeth.isPositiveFinite() || !rearTeeth.isPositiveFinite()) return null
        if (!wheelCircumferenceMm.isPositiveFinite()) return null
        val overall = primaryRatio * gearRatio * (rearTeeth / frontTeeth)
        val wheelRpm = rpm / overall
        return wheelRpm * (wheelCircumferenceMm / 1000.0) * 60.0 / 1000.0
    }
}
