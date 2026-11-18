package id.santosa.racingtools.calc

internal fun Double.isPositiveFinite(): Boolean = isFinite() && this > 0.0
