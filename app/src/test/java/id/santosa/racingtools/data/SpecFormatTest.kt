package id.santosa.racingtools.data

import org.junit.Assert.assertEquals
import org.junit.Test

class SpecFormatTest {
    private fun num(value: Double?) =
        if (value == null) SpecNumber.UNKNOWN else SpecNumber(value, Verification.SECONDARY, "s1")

    private fun generation(from: Double?, to: Double?, inProduction: Boolean) =
        Generation(
            id = "g",
            brand = "B",
            family = "F",
            category = "C",
            label = "L",
            yearFrom = num(from),
            yearTo = num(to),
            inProduction = inProduction
        )

    @Test
    fun numberUsesRequestedDecimalsAndUnit() {
        assertEquals("47.0 mm", SpecFormat.number(num(47.0), 1, "mm"))
        assertEquals("1255 mm", SpecFormat.number(num(1255.0), 0, "mm"))
        assertEquals("2.500", SpecFormat.number(num(2.5), 3))
    }

    @Test
    fun unknownNumbersAreShownAsUnknown() {
        assertEquals("Unknown", SpecFormat.number(SpecNumber.UNKNOWN, 1, "mm"))
        assertEquals("Unknown", SpecFormat.compression(SpecNumber.UNKNOWN))
        assertEquals("Unknown", SpecFormat.text(SpecText.UNKNOWN))
    }

    @Test
    fun compressionIsShownAsRatio() {
        assertEquals("10.0 : 1", SpecFormat.compression(num(10.0)))
    }

    @Test
    fun powerShowsRpmWhenKnown() {
        assertEquals("9.0 PS @ 7,500 rpm", SpecFormat.atRpm(num(9.0), num(7500.0), 1, "PS"))
        assertEquals("9.0 PS", SpecFormat.atRpm(num(9.0), SpecNumber.UNKNOWN, 1, "PS"))
        assertEquals("Unknown", SpecFormat.atRpm(SpecNumber.UNKNOWN, num(7500.0), 1, "PS"))
    }

    @Test
    fun yearRangeVariants() {
        assertEquals("2024 - present", SpecFormat.yearRange(generation(2024.0, null, true)))
        assertEquals("1998 - 2005", SpecFormat.yearRange(generation(1998.0, 2005.0, false)))
        assertEquals("1990 - end year unknown", SpecFormat.yearRange(generation(1990.0, null, false)))
        assertEquals("Unknown", SpecFormat.yearRange(generation(null, null, false)))
    }

    @Test
    fun yearOptionsFollowProductionYears() {
        assertEquals(listOf("2023", "2024", "2025"), SpecFormat.yearOptions(generation(2023.0, null, true), 2025))
        assertEquals(listOf("1998", "1999", "2000"), SpecFormat.yearOptions(generation(1998.0, 2000.0, false), 2025))
        assertEquals(listOf("1990"), SpecFormat.yearOptions(generation(1990.0, null, false), 2025))
        assertEquals(emptyList<String>(), SpecFormat.yearOptions(generation(null, null, false), 2025))
    }

    @Test
    fun sourceIdsAreCollectedOnce() {
        val engine = Engine(id = "e", name = "E", boreMm = num(47.0), strokeMm = num(63.1))
        val generation = generation(2024.0, null, true)
        assertEquals(listOf("s1"), SpecFormat.sourceIds(generation, engine, null))
    }
}
