package id.santosa.racingtools.calc

object GearMath {
    fun ratio(drivingTeeth: Double, drivenTeeth: Double): Double? {
        if (!drivingTeeth.isPositiveFinite() || !drivenTeeth.isPositiveFinite()) return null
        return drivenTeeth / drivingTeeth
    }
}
