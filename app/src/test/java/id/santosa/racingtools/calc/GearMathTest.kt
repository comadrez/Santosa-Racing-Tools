package id.santosa.racingtools.calc

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

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
}
