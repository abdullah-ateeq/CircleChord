package com.example.circlechord

import com.example.circlechord.domain.CircleChordFormatter
import com.example.circlechord.domain.model.LengthUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.math.RoundingMode

class CircleChordFormattingTest {

    // Test 35: Chord result formatting
    @Test
    fun test35_chordResultFormatting() {
        val value = BigDecimal("10.5")
        val formatted = CircleChordFormatter.formatLength(value, LengthUnit.CENTIMETER, 6, RoundingMode.HALF_UP)
        assertEquals("10.5 cm", formatted)
    }

    // Test 36: Trailing-zero removal (10.000000 -> "10", 7.07106800 -> "7.071068")
    @Test
    fun test36_trailingZeroRemoval() {
        val v1 = BigDecimal("10.000000")
        assertEquals("10 cm", CircleChordFormatter.formatLength(v1, LengthUnit.CENTIMETER, 6, RoundingMode.HALF_UP))

        val v2 = BigDecimal("0.125000")
        assertEquals("0.125 cm", CircleChordFormatter.formatLength(v2, LengthUnit.CENTIMETER, 6, RoundingMode.HALF_UP))
    }

    // Test 37: HALF_UP
    // 2.55 with 1 decimal -> 2.6
    @Test
    fun test37_halfUp() {
        val v = BigDecimal("2.55")
        assertEquals("2.6", CircleChordFormatter.formatNumber(v, 1, RoundingMode.HALF_UP))
    }

    // Test 38: HALF_DOWN
    // 2.55 with 1 decimal -> 2.5
    @Test
    fun test38_halfDown() {
        val v = BigDecimal("2.55")
        assertEquals("2.5", CircleChordFormatter.formatNumber(v, 1, RoundingMode.HALF_DOWN))
    }

    // Test 39: HALF_EVEN
    // 2.55 -> 2.6 (to nearest even)
    // 2.45 -> 2.4 (to nearest even)
    @Test
    fun test39_halfEven() {
        val v1 = BigDecimal("2.55")
        val v2 = BigDecimal("2.45")
        assertEquals("2.6", CircleChordFormatter.formatNumber(v1, 1, RoundingMode.HALF_EVEN))
        assertEquals("2.4", CircleChordFormatter.formatNumber(v2, 1, RoundingMode.HALF_EVEN))
    }

    // Test 40: UP
    // 2.51 with 1 decimal -> 2.6
    @Test
    fun test40_up() {
        val v = BigDecimal("2.51")
        assertEquals("2.6", CircleChordFormatter.formatNumber(v, 1, RoundingMode.UP))
    }

    // Test 41: DOWN
    // 2.59 with 1 decimal -> 2.5
    @Test
    fun test41_down() {
        val v = BigDecimal("2.59")
        assertEquals("2.5", CircleChordFormatter.formatNumber(v, 1, RoundingMode.DOWN))
    }

    // Test 42: CEILING
    // -2.59 -> -2.5
    // 2.51 -> 2.6
    @Test
    fun test42_ceiling() {
        val vPositive = BigDecimal("2.51")
        val vNegative = BigDecimal("-2.59")
        assertEquals("2.6", CircleChordFormatter.formatNumber(vPositive, 1, RoundingMode.CEILING))
        assertEquals("-2.5", CircleChordFormatter.formatNumber(vNegative, 1, RoundingMode.CEILING))
    }

    // Test 43: FLOOR
    // 2.59 -> 2.5
    // -2.51 -> -2.6
    @Test
    fun test43_floor() {
        val vPositive = BigDecimal("2.59")
        val vNegative = BigDecimal("-2.51")
        assertEquals("2.5", CircleChordFormatter.formatNumber(vPositive, 1, RoundingMode.FLOOR))
        assertEquals("-2.6", CircleChordFormatter.formatNumber(vNegative, 1, RoundingMode.FLOOR))
    }

    // Test copy and share text formatting
    @Test
    fun test_copyAndShareFormatting() {
        val copyText = CircleChordFormatter.formatCopyFull(
            radius = "10",
            radiusUnit = LengthUnit.CENTIMETER,
            angle = "60",
            chordResult = "10 cm"
        )
        assertTrue(copyText.contains("Radius: 10 cm"))
        assertTrue(copyText.contains("Central Angle: 60°"))
        assertTrue(copyText.contains("Chord Length: 10 cm"))

        val shareText = CircleChordFormatter.formatShareText(
            radius = "10",
            radiusUnit = LengthUnit.CENTIMETER,
            angle = "60",
            chordResult = "10 cm"
        )
        assertTrue(shareText.contains("Radius: 10 cm"))
        assertTrue(shareText.contains("Central Angle: 60°"))
        assertTrue(shareText.contains("Chord Length: 10 cm"))
    }
}
