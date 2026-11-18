package id.santosa.racingtools.calc

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import kotlin.math.PI

class ClearanceMathTest {
    private val delta = 1e-9

    @Test
    fun buretteVolumeIsEndMinusStart() {
        assertEquals(12.0, ClearanceMath.buretteVolumeCc(0.0, 12.0)!!, delta)
        assertEquals(12.0, ClearanceMath.buretteVolumeCc(5.5, 17.5)!!, delta)
    }

    @Test
    fun buretteVolumeRejectsInvalidReadings() {
        assertNull(ClearanceMath.buretteVolumeCc(12.0, 12.0))
        assertNull(ClearanceMath.buretteVolumeCc(12.0, 5.0))
        assertNull(ClearanceMath.buretteVolumeCc(-1.0, 5.0))
        assertNull(ClearanceMath.buretteVolumeCc(0.0, 0.0))
        assertNull(ClearanceMath.buretteVolumeCc(Double.NaN, 5.0))
        assertNull(ClearanceMath.buretteVolumeCc(0.0, Double.POSITIVE_INFINITY))
    }

    @Test
    fun discVolumeMatchesCylinderFormula() {
        assertEquals(2.5 * PI, ClearanceMath.discVolumeCc(100.0, 1.0)!!, delta)
        assertEquals(0.0, ClearanceMath.discVolumeCc(100.0, 0.0)!!, delta)
    }

    @Test
    fun discVolumeScalesWithHeight() {
        val one = ClearanceMath.discVolumeCc(52.4, 0.5)!!
        val two = ClearanceMath.discVolumeCc(52.4, 1.0)!!
        assertEquals(one * 2.0, two, delta)
    }

    @Test
    fun discVolumeRejectsInvalidInput() {
        assertNull(ClearanceMath.discVolumeCc(0.0, 1.0))
        assertNull(ClearanceMath.discVolumeCc(-52.4, 1.0))
        assertNull(ClearanceMath.discVolumeCc(52.4, -0.1))
        assertNull(ClearanceMath.discVolumeCc(Double.NaN, 1.0))
        assertNull(ClearanceMath.discVolumeCc(52.4, Double.POSITIVE_INFINITY))
    }

    @Test
    fun totalAddsAllContributions() {
        assertEquals(13.5, ClearanceMath.totalCc(12.0, 1.0, 0.5, 0.0)!!, delta)
    }

    @Test
    fun domedPistonReducesTotal() {
        assertEquals(11.5, ClearanceMath.totalCc(12.0, 1.0, 0.5, -2.0)!!, delta)
    }

    @Test
    fun dishedPistonIncreasesTotal() {
        assertEquals(15.5, ClearanceMath.totalCc(12.0, 1.0, 0.5, 2.0)!!, delta)
    }

    @Test
    fun totalRejectsInvalidInput() {
        assertNull(ClearanceMath.totalCc(0.0, 1.0, 0.5, 0.0))
        assertNull(ClearanceMath.totalCc(12.0, -1.0, 0.5, 0.0))
        assertNull(ClearanceMath.totalCc(12.0, 1.0, -0.5, 0.0))
        assertNull(ClearanceMath.totalCc(12.0, 1.0, 0.5, Double.NaN))
        assertNull(ClearanceMath.totalCc(1.0, 0.0, 0.0, -2.0))
    }

    @Test
    fun clearanceFeedsCompressionRatio() {
        val gasket = ClearanceMath.discVolumeCc(52.4, 0.5)!!
        val total = ClearanceMath.totalCc(12.0, gasket, 0.0, 0.0)!!
        val swept = EngineMath.displacementCc(52.4, 57.9, 1)!!
        val ratio = EngineMath.staticCompressionRatio(swept, total)!!
        assertEquals((swept + total) / total, ratio, delta)
    }
}
