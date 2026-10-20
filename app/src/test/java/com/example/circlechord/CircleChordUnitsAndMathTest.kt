package com.example.circlechord

import com.example.circlechord.domain.ChordCalculator
import com.example.circlechord.domain.CircleChordFormatter
import com.example.circlechord.domain.LengthUnitConverter
import com.example.circlechord.domain.model.LengthUnit
import com.example.circlechord.math.BigDecimalMath
import com.example.circlechord.math.MathConstants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

class CircleChordUnitsAndMathTest {

    // Test 23: Millimeter conversion (1 mm = 0.001 m)
    @Test
    fun test23_millimeterConversion() {
        val converted = LengthUnitConverter.convertLength(
            BigDecimal("1000"),
            LengthUnit.MILLIMETER,
            LengthUnit.METER
        )
        assertEquals("1", CircleChordFormatter.formatNumber(converted, 6, RoundingMode.HALF_UP))
    }

    // Test 24: Centimeter conversion (1 cm = 0.01 m)
    @Test
    fun test24_centimeterConversion() {
        val converted = LengthUnitConverter.convertLength(
            BigDecimal("100"),
            LengthUnit.CENTIMETER,
            LengthUnit.METER
        )
        assertEquals("1", CircleChordFormatter.formatNumber(converted, 6, RoundingMode.HALF_UP))
    }

    // Test 25: Meter conversion (1 m = 1 m)
    @Test
    fun test25_meterConversion() {
        val converted = LengthUnitConverter.convertLength(
            BigDecimal("5.5"),
            LengthUnit.METER,
            LengthUnit.METER
        )
        assertEquals("5.5", CircleChordFormatter.formatNumber(converted, 6, RoundingMode.HALF_UP))
    }

    // Test 26: Kilometer conversion (1 km = 1000 m)
    @Test
    fun test26_kilometerConversion() {
        val converted = LengthUnitConverter.convertLength(
            BigDecimal("2.5"),
            LengthUnit.KILOMETER,
            LengthUnit.METER
        )
        assertEquals("2500", CircleChordFormatter.formatNumber(converted, 6, RoundingMode.HALF_UP))
    }

    // Test 27: Inch conversion (1 in = 0.0254 m)
    @Test
    fun test27_inchConversion() {
        val converted = LengthUnitConverter.convertLength(
            BigDecimal("10"),
            LengthUnit.INCH,
            LengthUnit.METER
        )
        assertEquals("0.254", CircleChordFormatter.formatNumber(converted, 6, RoundingMode.HALF_UP))
    }

    // Test 28: Foot conversion (1 ft = 0.3048 m)
    @Test
    fun test28_footConversion() {
        val converted = LengthUnitConverter.convertLength(
            BigDecimal("10"),
            LengthUnit.FOOT,
            LengthUnit.METER
        )
        assertEquals("3.048", CircleChordFormatter.formatNumber(converted, 6, RoundingMode.HALF_UP))
    }

    // Test 29: Yard conversion (1 yd = 0.9144 m)
    @Test
    fun test29_yardConversion() {
        val converted = LengthUnitConverter.convertLength(
            BigDecimal("10"),
            LengthUnit.YARD,
            LengthUnit.METER
        )
        assertEquals("9.144", CircleChordFormatter.formatNumber(converted, 6, RoundingMode.HALF_UP))
    }

    // Test 30: Mixed input/output units
    // Critical Test: Radius = 1 m, Angle = 60°, Result unit = cm -> Expected: 100 cm
    @Test
    fun test30_mixedInputOutputUnits() {
        val result = ChordCalculator.calculateFull(
            radius = BigDecimal("1"),
            radiusUnit = LengthUnit.METER,
            angleDegrees = BigDecimal("60"),
            resultUnit = LengthUnit.CENTIMETER
        )
        val formatted = CircleChordFormatter.formatNumber(result.chordLength, 6, RoundingMode.HALF_UP)
        assertEquals("100", formatted)
    }

    // Critical Test: Inch (Radius = 10 in, Angle = 60° -> Expected: 10 in)
    @Test
    fun test_inchCriticalTest() {
        val result = ChordCalculator.calculateFull(
            radius = BigDecimal("10"),
            radiusUnit = LengthUnit.INCH,
            angleDegrees = BigDecimal("60"),
            resultUnit = LengthUnit.INCH
        )
        val formatted = CircleChordFormatter.formatNumber(result.chordLength, 6, RoundingMode.HALF_UP)
        assertEquals("10", formatted)
    }

    // Test 31: High-precision π (at least 50 digits)
    @Test
    fun test31_highPrecisionPi() {
        val piStr = MathConstants.PI.toString()
        assertTrue("Pi string must have at least 50 digits after decimal", piStr.length >= 52)
        assertTrue(piStr.startsWith("3.1415926535897932384626433832795028841971693993751"))
    }

    // Test 32: High-precision sine (sin(30°) = 0.5, sin(90°) = 1, sin(180°) = 0)
    @Test
    fun test32_highPrecisionSine() {
        val rad30 = BigDecimalMath.toRadians(BigDecimal("30"))
        val sin30 = BigDecimalMath.sin(rad30)
        assertEquals("0.5", CircleChordFormatter.formatNumber(sin30, 6, RoundingMode.HALF_UP))

        val rad90 = BigDecimalMath.toRadians(BigDecimal("90"))
        val sin90 = BigDecimalMath.sin(rad90)
        assertEquals("1", CircleChordFormatter.formatNumber(sin90, 6, RoundingMode.HALF_UP))

        val rad180 = BigDecimalMath.toRadians(BigDecimal("180"))
        val sin180 = BigDecimalMath.sin(rad180)
        assertEquals("0", CircleChordFormatter.formatNumber(sin180, 6, RoundingMode.HALF_UP))
    }

    // Test 33: High-precision cosine (cos(0) = 1, cos(60°) = 0.5, cos(90°) = 0)
    @Test
    fun test33_highPrecisionCosine() {
        val rad0 = BigDecimal.ZERO
        val cos0 = BigDecimalMath.cos(rad0)
        assertEquals("1", CircleChordFormatter.formatNumber(cos0, 6, RoundingMode.HALF_UP))

        val rad60 = BigDecimalMath.toRadians(BigDecimal("60"))
        val cos60 = BigDecimalMath.cos(rad60)
        assertEquals("0.5", CircleChordFormatter.formatNumber(cos60, 6, RoundingMode.HALF_UP))

        val rad90 = BigDecimalMath.toRadians(BigDecimal("90"))
        val cos90 = BigDecimalMath.cos(rad90)
        assertEquals("0", CircleChordFormatter.formatNumber(cos90, 6, RoundingMode.HALF_UP))
    }

    // Test 34: Degrees-to-radians (180° -> π radians)
    @Test
    fun test34_degreesToRadians() {
        val rad = BigDecimalMath.toRadians(BigDecimal("180"))
        val diff = rad.subtract(MathConstants.PI).abs()
        assertTrue(diff < BigDecimal.ONE.movePointLeft(40))
    }

    // Critical Test: BigDecimal arithmetic with high-precision sqrt
    @Test
    fun test_bigDecimalSqrt() {
        val value = BigDecimal("2")
        val sqrt2 = BigDecimalMath.sqrt(value)
        val formatted = CircleChordFormatter.formatNumber(sqrt2, 10, RoundingMode.HALF_UP)
        assertEquals("1.4142135624", formatted)
    }

    // Critical Test: No binary floating-point artifacts (e.g. 0.1 + 0.2 != 0.30000000000000004 in double)
    @Test
    fun test_noBinaryFloatingPointArtifacts() {
        val radius = BigDecimal("0.1")
        val angle = BigDecimal("60")
        val chord = ChordCalculator.calculateChord(radius, angle)
        assertEquals("0.1", CircleChordFormatter.formatNumber(chord, 6, RoundingMode.HALF_UP))
    }
}
