package id.santosa.racingtools.calc

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI

class EngineMathTest {
    private val delta = 1e-9

    @Test
    fun displacementOfSquareEngine() {
        val cc = EngineMath.displacementCc(100.0, 100.0, 1)
        assertEquals(250.0 * PI, cc!!, delta)
    }

    @Test
    fun displacementOfSmallSingle() {
        val cc = EngineMath.displacementCc(52.4, 57.9, 1)
        assertEquals(124.86221045943726, cc!!, delta)
    }

    @Test
    fun displacementOfFourCylinder() {
        val cc = EngineMath.displacementCc(67.0, 42.5, 4)
        assertEquals(599.3609004334947, cc!!, delta)
    }

    @Test
    fun displacementScalesWithCylinderCount() {
        val one = EngineMath.displacementCc(60.0, 50.0, 1)!!
        val three = EngineMath.displacementCc(60.0, 50.0, 3)!!
        assertEquals(one * 3.0, three, delta)
    }

    @Test
    fun displacementAcceptsCylinderLimits() {
        assertNotNull(EngineMath.displacementCc(50.0, 50.0, 1))
        assertNotNull(EngineMath.displacementCc(50.0, 50.0, EngineMath.MAX_CYLINDERS))
    }

    @Test
    fun displacementRejectsCylinderCountOutOfRange() {
        assertNull(EngineMath.displacementCc(50.0, 50.0, 0))
        assertNull(EngineMath.displacementCc(50.0, 50.0, -1))
        assertNull(EngineMath.displacementCc(50.0, 50.0, EngineMath.MAX_CYLINDERS + 1))
    }

    @Test
    fun displacementRejectsInvalidDimensions() {
        assertNull(EngineMath.displacementCc(0.0, 50.0, 1))
        assertNull(EngineMath.displacementCc(50.0, 0.0, 1))
        assertNull(EngineMath.displacementCc(-50.0, 50.0, 1))
        assertNull(EngineMath.displacementCc(50.0, -50.0, 1))
        assertNull(EngineMath.displacementCc(Double.NaN, 50.0, 1))
        assertNull(EngineMath.displacementCc(50.0, Double.POSITIVE_INFINITY, 1))
    }

    @Test
    fun compressionRatioMatchesFormula() {
        val ratio = EngineMath.staticCompressionRatio(124.0, 12.0)
        assertEquals(34.0 / 3.0, ratio!!, delta)
    }

    @Test
    fun compressionRatioRejectsZeroSweptVolume() {
        assertNull(EngineMath.staticCompressionRatio(0.0, 12.0))
    }

    @Test
    fun compressionRatioFallsAsClearanceGrows() {
        val small = EngineMath.staticCompressionRatio(100.0, 10.0)!!
        val large = EngineMath.staticCompressionRatio(100.0, 20.0)!!
        assertEquals(11.0, small, delta)
        assertEquals(6.0, large, delta)
    }

    @Test
    fun compressionRatioRejectsInvalidVolumes() {
        assertNull(EngineMath.staticCompressionRatio(124.0, 0.0))
        assertNull(EngineMath.staticCompressionRatio(-124.0, 12.0))
        assertNull(EngineMath.staticCompressionRatio(124.0, -12.0))
        assertNull(EngineMath.staticCompressionRatio(Double.NaN, 12.0))
        assertNull(EngineMath.staticCompressionRatio(124.0, Double.POSITIVE_INFINITY))
    }

    @Test
    fun pistonSpeedMatchesFormula() {
        assertEquals(10.0, EngineMath.meanPistonSpeedMs(50.0, 6000.0)!!, delta)
        assertEquals(15.44, EngineMath.meanPistonSpeedMs(57.9, 8000.0)!!, delta)
    }

    @Test
    fun pistonSpeedDoublesWithRpm() {
        val low = EngineMath.meanPistonSpeedMs(55.0, 4000.0)!!
        val high = EngineMath.meanPistonSpeedMs(55.0, 8000.0)!!
        assertEquals(low * 2.0, high, delta)
    }

    @Test
    fun pistonSpeedRejectsInvalidInput() {
        assertNull(EngineMath.meanPistonSpeedMs(0.0, 8000.0))
        assertNull(EngineMath.meanPistonSpeedMs(55.0, 0.0))
        assertNull(EngineMath.meanPistonSpeedMs(-55.0, 8000.0))
        assertNull(EngineMath.meanPistonSpeedMs(55.0, -8000.0))
        assertNull(EngineMath.meanPistonSpeedMs(Double.NaN, 8000.0))
        assertNull(EngineMath.meanPistonSpeedMs(55.0, Double.POSITIVE_INFINITY))
    }

    @Test
    fun percentChangeUpAndDown() {
        assertEquals(10.0, EngineMath.percentChange(100.0, 110.0)!!, delta)
        assertEquals(-10.0, EngineMath.percentChange(100.0, 90.0)!!, delta)
        assertEquals(0.0, EngineMath.percentChange(100.0, 100.0)!!, delta)
    }

    @Test
    fun percentChangeRejectsInvalidInput() {
        assertNull(EngineMath.percentChange(0.0, 100.0))
        assertNull(EngineMath.percentChange(100.0, 0.0))
        assertNull(EngineMath.percentChange(-100.0, 100.0))
        assertNull(EngineMath.percentChange(Double.NaN, 100.0))
    }

    @Test
    fun boreUpRaisesDisplacement() {
        val before = EngineMath.displacementCc(52.4, 57.9, 1)!!
        val after = EngineMath.displacementCc(54.0, 57.9, 1)!!
        assertEquals(132.60379847463693, after, delta)
        assertTrue(after > before)
    }

    @Test
    fun squishClearanceAddsDeckAndGasket() {
        assertEquals(0.8, EngineMath.squishClearanceMm(0.3, 0.5)!!, delta)
        assertEquals(0.5, EngineMath.squishClearanceMm(0.0, 0.5)!!, delta)
    }

    @Test
    fun squishClearanceAllowsPistonAboveDeck() {
        assertEquals(0.8, EngineMath.squishClearanceMm(-0.2, 1.0)!!, delta)
    }

    @Test
    fun squishClearanceRejectsContactAndBadInput() {
        assertNull(EngineMath.squishClearanceMm(-1.0, 0.5))
        assertNull(EngineMath.squishClearanceMm(0.0, 0.0))
        assertNull(EngineMath.squishClearanceMm(0.3, -0.1))
        assertNull(EngineMath.squishClearanceMm(Double.NaN, 0.5))
        assertNull(EngineMath.squishClearanceMm(0.3, Double.POSITIVE_INFINITY))
    }
}
