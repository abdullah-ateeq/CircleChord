package com.example.circlechord

import com.example.circlechord.domain.CircleChordParser
import com.example.circlechord.domain.CircleChordValidator
import com.example.circlechord.domain.model.CircleChordError
import com.example.circlechord.domain.ValidationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class CircleChordValidationTest {

    // Test 11: Zero angle rejection
    @Test
    fun test11_zeroAngleRejection() {
        val res = CircleChordValidator.validateAngle("0")
        assertTrue(res.isFailure)
        val ex = res.exceptionOrNull() as ValidationException
        assertEquals(CircleChordError.InvalidAngle, ex.error)
    }

    // Test 12: Negative angle rejection
    @Test
    fun test12_negativeAngleRejection() {
        val res = CircleChordValidator.validateAngle("-45")
        assertTrue(res.isFailure)
        val ex = res.exceptionOrNull() as ValidationException
        assertEquals(CircleChordError.InvalidAngle, ex.error)
    }

    // Test 13: Angle > 360° rejection
    @Test
    fun test13_angleGreaterThan360Rejection() {
        val res = CircleChordValidator.validateAngle("360.1")
        assertTrue(res.isFailure)
        val ex = res.exceptionOrNull() as ValidationException
        assertEquals(CircleChordError.InvalidAngle, ex.error)
    }

    // Test 14: Zero radius rejection
    @Test
    fun test14_zeroRadiusRejection() {
        val res = CircleChordValidator.validateRadius("0")
        assertTrue(res.isFailure)
        val ex = res.exceptionOrNull() as ValidationException
        assertEquals(CircleChordError.NonPositiveRadius, ex.error)
    }

    // Test 15: Negative radius rejection
    @Test
    fun test15_negativeRadiusRejection() {
        val res = CircleChordValidator.validateRadius("-5")
        assertTrue(res.isFailure)
        val ex = res.exceptionOrNull() as ValidationException
        assertEquals(CircleChordError.NonPositiveRadius, ex.error)
    }

    // Test 18: Leading decimal (e.g. ".5" -> 0.5)
    @Test
    fun test18_leadingDecimal() {
        val res = CircleChordParser.parseNumber(".5")
        assertTrue(res.isSuccess)
        assertEquals(BigDecimal("0.5"), res.getOrThrow())
    }

    // Test 19: Trailing decimal (e.g. "10." -> 10)
    @Test
    fun test19_trailingDecimal() {
        val res = CircleChordParser.parseNumber("10.")
        assertTrue(res.isSuccess)
        assertEquals(0, BigDecimal("10").compareTo(res.getOrThrow()))
    }

    // Test 20: Scientific notation ("1E+5" -> 100000, "1.5E-2" -> 0.015)
    // Critical Test: Radius = 1E+5 cm, Angle = 60° -> Chord = 100000 cm
    @Test
    fun test20_scientificNotation() {
        val res1 = CircleChordParser.parseNumber("1E+5")
        assertTrue(res1.isSuccess)
        assertEquals(0, BigDecimal("100000").compareTo(res1.getOrThrow()))

        val res2 = CircleChordParser.parseNumber("1.5E-2")
        assertTrue(res2.isSuccess)
        assertEquals(0, BigDecimal("0.015").compareTo(res2.getOrThrow()))
    }

    // Test 56: Invalid input
    @Test
    fun test56_invalidInput() {
        val invalidInputs = listOf("abc", "12..5", "1.2.3", "--")
        for (input in invalidInputs) {
            val res = CircleChordParser.parseNumber(input)
            assertTrue("Expected failure for '$input'", res.isFailure)
            val ex = res.exceptionOrNull() as ValidationException
            assertEquals(CircleChordError.InvalidNumber, ex.error)
        }
    }

    // Test 57: Empty input
    @Test
    fun test57_emptyInput() {
        val res = CircleChordParser.parseNumber("   ")
        assertTrue(res.isFailure)
        val ex = res.exceptionOrNull() as ValidationException
        assertEquals(CircleChordError.EmptyInput, ex.error)
    }

    // Test 58: Maximum input length (> 500 digits)
    @Test
    fun test58_maximumInputLength() {
        val longString = "1".repeat(501)
        val res = CircleChordParser.parseNumber(longString)
        assertTrue(res.isFailure)
        val ex = res.exceptionOrNull() as ValidationException
        assertEquals(CircleChordError.InputTooLong, ex.error)
    }
}
