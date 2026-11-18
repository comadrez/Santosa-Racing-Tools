package id.santosa.racingtools.calc

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI

class GearMathTest {
    private val delta = 1e-9

    @Test
    fun ratioIsDrivenOverDriving() {
        assertEquals(3.0, GearMath.ratio(14.0, 42.0)!!, delta)
        assertEquals(2.75, GearMath.ratio(16.0, 44.0)!!, delta)
    }

    @Test
    fun ratioBelowOneIsAnOverdrive() {
        assertEquals(0.5, GearMath.ratio(40.0, 20.0)!!, delta)
    }

    @Test
    fun equalToothCountsGiveOne() {
        assertEquals(1.0, GearMath.ratio(18.0, 18.0)!!, delta)
    }

    @Test
    fun finalDriveUsesRearOverFront() {
        assertEquals(3.2142857142857144, GearMath.ratio(14.0, 45.0)!!, delta)
    }

    @Test
    fun ratioRejectsInvalidToothCounts() {
        assertNull(GearMath.ratio(0.0, 42.0))
        assertNull(GearMath.ratio(14.0, 0.0))
        assertNull(GearMath.ratio(-14.0, 42.0))
        assertNull(GearMath.ratio(14.0, -42.0))
        assertNull(GearMath.ratio(Double.NaN, 42.0))
        assertNull(GearMath.ratio(14.0, Double.POSITIVE_INFINITY))
    }

    @Test
    fun tyreCircumferenceFromSidewallSize() {
        assertEquals(1808.9290499370027, GearMath.tyreCircumferenceMm(80.0, 90.0, 17.0)!!, delta)
    }

    @Test
    fun tyreCircumferenceGrowsWithRim() {
        val small = GearMath.tyreCircumferenceMm(80.0, 90.0, 17.0)!!
        val large = GearMath.tyreCircumferenceMm(80.0, 90.0, 18.0)!!
        assertEquals(25.4 * PI, large - small, delta)
    }

    @Test
    fun tyreCircumferenceRejectsInvalidInput() {
        assertNull(GearMath.tyreCircumferenceMm(0.0, 90.0, 17.0))
        assertNull(GearMath.tyreCircumferenceMm(80.0, 0.0, 17.0))
        assertNull(GearMath.tyreCircumferenceMm(80.0, 90.0, 0.0))
        assertNull(GearMath.tyreCircumferenceMm(80.0, 90.0, -17.0))
        assertNull(GearMath.tyreCircumferenceMm(Double.NaN, 90.0, 17.0))
    }

    @Test
    fun roadSpeedWithUnitRatios() {
        assertEquals(60.0, GearMath.roadSpeedKmh(1000.0, 1.0, 1.0, 1.0, 1.0, 1000.0)!!, delta)
    }

    @Test
    fun roadSpeedForTypicalSetup() {
        val circumference = GearMath.tyreCircumferenceMm(80.0, 90.0, 17.0)!!
        val speed = GearMath.roadSpeedKmh(8000.0, 3.0, 1.0, 14.0, 42.0, circumference)
        assertEquals(96.47621599664015, speed!!, delta)
    }

    @Test
    fun roadSpeedDoublesWithRpm() {
        val low = GearMath.roadSpeedKmh(4000.0, 3.0, 1.5, 14.0, 42.0, 1800.0)!!
        val high = GearMath.roadSpeedKmh(8000.0, 3.0, 1.5, 14.0, 42.0, 1800.0)!!
        assertEquals(low * 2.0, high, delta)
    }

    @Test
    fun roadSpeedFallsWithLowerGearing() {
        val tall = GearMath.roadSpeedKmh(8000.0, 3.0, 1.0, 14.0, 40.0, 1800.0)!!
        val short = GearMath.roadSpeedKmh(8000.0, 3.0, 1.0, 14.0, 45.0, 1800.0)!!
        assertTrue(tall > short)
    }

    @Test
    fun roadSpeedRejectsInvalidInput() {
        assertNull(GearMath.roadSpeedKmh(0.0, 3.0, 1.0, 14.0, 42.0, 1800.0))
        assertNull(GearMath.roadSpeedKmh(8000.0, 0.0, 1.0, 14.0, 42.0, 1800.0))
        assertNull(GearMath.roadSpeedKmh(8000.0, 3.0, 0.0, 14.0, 42.0, 1800.0))
        assertNull(GearMath.roadSpeedKmh(8000.0, 3.0, 1.0, 0.0, 42.0, 1800.0))
        assertNull(GearMath.roadSpeedKmh(8000.0, 3.0, 1.0, 14.0, 0.0, 1800.0))
        assertNull(GearMath.roadSpeedKmh(8000.0, 3.0, 1.0, 14.0, 42.0, 0.0))
        assertNull(GearMath.roadSpeedKmh(Double.NaN, 3.0, 1.0, 14.0, 42.0, 1800.0))
    }
}
