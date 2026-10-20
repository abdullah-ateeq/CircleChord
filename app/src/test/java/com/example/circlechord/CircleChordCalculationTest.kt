package com.example.circlechord

import com.example.circlechord.domain.ChordCalculator
import com.example.circlechord.domain.CircleChordFormatter
import com.example.circlechord.domain.model.LengthUnit
import com.example.circlechord.math.MathConstants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.math.RoundingMode

class CircleChordCalculationTest {

    // Test 1: Basic chord calculation
    @Test
    fun test01_basicChordCalculation() {
        val radius = BigDecimal("10")
        val angle = BigDecimal("60")
        val chord = ChordCalculator.calculateChord(radius, angle)
        val formatted = CircleChordFormatter.formatNumber(chord, 6, RoundingMode.HALF_UP)
        assertEquals("10", formatted)
    }

    // Test 2: 30° angle
    // Critical Test: Radius = 10 cm, Angle = 30° -> Chord ≈ 5.1763809... cm (at 6 decimals: 5.176381 cm)
    @Test
    fun test02_angle30Degrees() {
        val radius = BigDecimal("10")
        val angle = BigDecimal("30")
        val chord = ChordCalculator.calculateChord(radius, angle)
        val formatted = CircleChordFormatter.formatNumber(chord, 6, RoundingMode.HALF_UP)
        assertEquals("5.176381", formatted)
    }

    // Test 3: 60° angle
    // Critical Test: Radius = 10 cm, Angle = 60° -> Chord = 10 cm
    @Test
    fun test03_angle60Degrees() {
        val radius = BigDecimal("10")
        val angle = BigDecimal("60")
        val chord = ChordCalculator.calculateChord(radius, angle)
        val formatted = CircleChordFormatter.formatNumber(chord, 6, RoundingMode.HALF_UP)
        assertEquals("10", formatted)
    }

    // Test 4: 90° angle
    // Critical Test: Radius = 5 cm, Angle = 90° -> Chord ≈ 7.071067812... cm (at 6 decimals: 7.071068 cm)
    @Test
    fun test04_angle90Degrees() {
        val radius = BigDecimal("5")
        val angle = BigDecimal("90")
        val chord = ChordCalculator.calculateChord(radius, angle)
        val formatted = CircleChordFormatter.formatNumber(chord, 6, RoundingMode.HALF_UP)
        assertEquals("7.071068", formatted)
    }

    // Test 5: 120° angle: c = 2r sin(60°) = 2 * 10 * (sqrt(3)/2) = 10 * 1.7320508... = 17.320508
    @Test
    fun test05_angle120Degrees() {
        val radius = BigDecimal("10")
        val angle = BigDecimal("120")
        val chord = ChordCalculator.calculateChord(radius, angle)
        val formatted = CircleChordFormatter.formatNumber(chord, 6, RoundingMode.HALF_UP)
        assertEquals("17.320508", formatted)
    }

    // Test 6: 180° angle
    // Critical Test: Radius = 5 cm, Angle = 180° -> Chord = 10 cm (diameter)
    @Test
    fun test06_angle180Degrees() {
        val radius = BigDecimal("5")
        val angle = BigDecimal("180")
        val chord = ChordCalculator.calculateChord(radius, angle)
        val formatted = CircleChordFormatter.formatNumber(chord, 6, RoundingMode.HALF_UP)
        assertEquals("10", formatted)
    }

    // Test 7: 270° angle
    // Critical Test: Radius = 10 cm, Angle = 270° -> Chord = 2 * 10 * sin(135°) ≈ 14.1421356... (at 6 decimals: 14.142136 cm)
    @Test
    fun test07_angle270Degrees() {
        val radius = BigDecimal("10")
        val angle = BigDecimal("270")
        val chord = ChordCalculator.calculateChord(radius, angle)
        val formatted = CircleChordFormatter.formatNumber(chord, 6, RoundingMode.HALF_UP)
        assertEquals("14.142136", formatted)
    }

    // Test 8: 360° angle
    // Critical Test: Radius = 5 cm, Angle = 360° -> Chord = 0 cm
    @Test
    fun test08_angle360Degrees() {
        val radius = BigDecimal("5")
        val angle = BigDecimal("360")
        val chord = ChordCalculator.calculateChord(radius, angle)
        val formatted = CircleChordFormatter.formatNumber(chord, 6, RoundingMode.HALF_UP)
        assertEquals("0", formatted)
    }

    // Test 9: Very small angle (e.g. 0.001°)
    // Critical Test: Radius = 1000 cm, Angle = 0.001°
    // c = 2 * 1000 * sin(0.0005° * pi/180) ≈ 2000 * 8.72664625997e-6 ≈ 0.017453 cm
    @Test
    fun test09_verySmallAngle() {
        val radius = BigDecimal("1000")
        val angle = BigDecimal("0.001")
        val chord = ChordCalculator.calculateChord(radius, angle)
        val formatted = CircleChordFormatter.formatNumber(chord, 6, RoundingMode.HALF_UP)
        assertEquals("0.017453", formatted)
    }

    // Test 10: Large angle (e.g. 359.999°)
    @Test
    fun test10_largeAngle() {
        val radius = BigDecimal("10")
        val angle = BigDecimal("359.999")
        val chord = ChordCalculator.calculateChord(radius, angle)
        assertTrue(chord > BigDecimal.ZERO)
        assertTrue(chord < BigDecimal("0.001"))
    }

    // Test 16: Decimal radius
    @Test
    fun test16_decimalRadius() {
        val radius = BigDecimal("12.5")
        val angle = BigDecimal("60")
        val chord = ChordCalculator.calculateChord(radius, angle)
        val formatted = CircleChordFormatter.formatNumber(chord, 6, RoundingMode.HALF_UP)
        assertEquals("12.5", formatted)
    }

    // Test 17: Decimal angle
    @Test
    fun test17_decimalAngle() {
        val radius = BigDecimal("10")
        val angle = BigDecimal("45.5")
        val chord = ChordCalculator.calculateChord(radius, angle)
        val formatted = CircleChordFormatter.formatNumber(chord, 6, RoundingMode.HALF_UP)
        assertEquals("7.734219", formatted)
    }

    // Test 21: Very small radius
    @Test
    fun test21_verySmallRadius() {
        val radius = BigDecimal("0.000000001")
        val angle = BigDecimal("60")
        val chord = ChordCalculator.calculateChord(radius, angle)
        val formatted = CircleChordFormatter.formatNumber(chord, 12, RoundingMode.HALF_UP)
        assertEquals("0.000000001", formatted)
    }

    // Test 22: Very large radius
    // Critical Test: Radius = 999999999999999999999999 cm, Angle = 60°
    @Test
    fun test22_veryLargeRadius() {
        val radius = BigDecimal("999999999999999999999999")
        val angle = BigDecimal("60")
        val chord = ChordCalculator.calculateChord(radius, angle)
        val formatted = CircleChordFormatter.formatNumber(chord, 0, RoundingMode.HALF_UP)
        assertEquals("999999999999999999999999", formatted)
    }

    // Test 44: Arc length: Arc Length = rθ
    // For r = 10, θ = 60°: Arc Length = 10 * (pi / 3) ≈ 10.471976
    @Test
    fun test44_arcLength() {
        val radius = BigDecimal("10")
        val angle = BigDecimal("60")
        val arcLength = ChordCalculator.calculateArcLength(radius, angle)
        val formatted = CircleChordFormatter.formatNumber(arcLength, 6, RoundingMode.HALF_UP)
        assertEquals("10.471976", formatted)
    }

    // Test 45: Sector area: 1/2 r² θ
    // For r = 10, θ = 60°: Area = 0.5 * 100 * (pi / 3) = 50 * pi / 3 ≈ 52.359878
    @Test
    fun test45_sectorArea() {
        val radius = BigDecimal("10")
        val angle = BigDecimal("60")
        val area = ChordCalculator.calculateSectorArea(radius, angle)
        val formatted = CircleChordFormatter.formatNumber(area, 6, RoundingMode.HALF_UP)
        assertEquals("52.359878", formatted)
    }

    // Test 46: Sagitta: s = r - r cos(θ / 2)
    // For r = 10, θ = 60°: s = 10 - 10 * cos(30°) = 10 - 10 * (sqrt(3)/2) ≈ 10 - 8.660254 = 1.339746
    @Test
    fun test46_sagitta() {
        val radius = BigDecimal("10")
        val angle = BigDecimal("60")
        val sagitta = ChordCalculator.calculateSagitta(radius, angle)
        val formatted = CircleChordFormatter.formatNumber(sagitta, 6, RoundingMode.HALF_UP)
        assertEquals("1.339746", formatted)
    }

    // Test 47: Diameter: 2r
    @Test
    fun test47_diameter() {
        val radius = BigDecimal("10")
        val diameter = ChordCalculator.calculateDiameter(radius)
        assertEquals("20", CircleChordFormatter.formatNumber(diameter, 6, RoundingMode.HALF_UP))
    }
}
