package com.example.circlechord.domain

import com.example.circlechord.domain.model.ChordCalculationDetails
import com.example.circlechord.domain.model.ChordResult
import com.example.circlechord.domain.model.LengthUnit
import com.example.circlechord.math.BigDecimalMath
import com.example.circlechord.math.MathConstants
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

object ChordCalculator {

    private const val FORMULA_TEXT = "c = 2r sin(θ/2)"

    fun calculateChord(
        radius: BigDecimal,
        angleDegrees: BigDecimal,
        mathContext: MathContext = MathConstants.DEFAULT_MATH_CONTEXT
    ): BigDecimal {
        val workingContext = MathContext(mathContext.precision + 15, RoundingMode.HALF_UP)
        val halfAngle = angleDegrees.divide(MathConstants.TWO, workingContext)
        val halfAngleRadians = BigDecimalMath.toRadians(halfAngle, workingContext)
        val sine = BigDecimalMath.sin(halfAngleRadians, workingContext)
        val twoR = MathConstants.TWO.multiply(radius, workingContext)
        return twoR.multiply(sine, workingContext).round(mathContext)
    }

    fun calculateDiameter(
        radius: BigDecimal,
        mathContext: MathContext = MathConstants.DEFAULT_MATH_CONTEXT
    ): BigDecimal {
        return MathConstants.TWO.multiply(radius, mathContext)
    }

    fun calculateArcLength(
        radius: BigDecimal,
        angleDegrees: BigDecimal,
        mathContext: MathContext = MathConstants.DEFAULT_MATH_CONTEXT
    ): BigDecimal {
        val workingContext = MathContext(mathContext.precision + 15, RoundingMode.HALF_UP)
        val angleRadians = BigDecimalMath.toRadians(angleDegrees, workingContext)
        return radius.multiply(angleRadians, workingContext).round(mathContext)
    }

    fun calculateSectorArea(
        radius: BigDecimal,
        angleDegrees: BigDecimal,
        mathContext: MathContext = MathConstants.DEFAULT_MATH_CONTEXT
    ): BigDecimal {
        val workingContext = MathContext(mathContext.precision + 15, RoundingMode.HALF_UP)
        val angleRadians = BigDecimalMath.toRadians(angleDegrees, workingContext)
        val rSquared = radius.multiply(radius, workingContext)
        val halfRSquared = rSquared.multiply(MathConstants.HALF, workingContext)
        return halfRSquared.multiply(angleRadians, workingContext).round(mathContext)
    }

    fun calculateSagitta(
        radius: BigDecimal,
        angleDegrees: BigDecimal,
        mathContext: MathContext = MathConstants.DEFAULT_MATH_CONTEXT
    ): BigDecimal {
        val workingContext = MathContext(mathContext.precision + 15, RoundingMode.HALF_UP)
        val halfAngle = angleDegrees.divide(MathConstants.TWO, workingContext)
        val halfAngleRadians = BigDecimalMath.toRadians(halfAngle, workingContext)
        val cosine = BigDecimalMath.cos(halfAngleRadians, workingContext)
        val rCos = radius.multiply(cosine, workingContext)
        return radius.subtract(rCos, workingContext).round(mathContext)
    }

    fun calculateFull(
        radius: BigDecimal,
        radiusUnit: LengthUnit,
        angleDegrees: BigDecimal,
        resultUnit: LengthUnit,
        mathContext: MathContext = MathConstants.DEFAULT_MATH_CONTEXT
    ): ChordResult {
        val workingContext = MathContext(mathContext.precision + 20, RoundingMode.HALF_UP)

        // Convert radius to meters for authoritative internal standard
        val radiusMeters = LengthUnitConverter.convertLength(
            value = radius,
            from = radiusUnit,
            to = LengthUnit.METER,
            mathContext = workingContext
        )

        // Calculate authoritative values in meters / square meters
        val chordMeters = calculateChord(radiusMeters, angleDegrees, workingContext)
        val diameterMeters = calculateDiameter(radiusMeters, workingContext)
        val arcLengthMeters = calculateArcLength(radiusMeters, angleDegrees, workingContext)
        val sectorAreaSqMeters = calculateSectorArea(radiusMeters, angleDegrees, workingContext)
        val sagittaMeters = calculateSagitta(radiusMeters, angleDegrees, workingContext)

        // Convert back to target result units
        val chordInResultUnit = LengthUnitConverter.convertLength(
            value = chordMeters,
            from = LengthUnit.METER,
            to = resultUnit,
            mathContext = mathContext
        )
        val diameterInResultUnit = LengthUnitConverter.convertLength(
            value = diameterMeters,
            from = LengthUnit.METER,
            to = resultUnit,
            mathContext = mathContext
        )
        val arcLengthInResultUnit = LengthUnitConverter.convertLength(
            value = arcLengthMeters,
            from = LengthUnit.METER,
            to = resultUnit,
            mathContext = mathContext
        )
        val sectorAreaInResultUnit = LengthUnitConverter.convertArea(
            value = sectorAreaSqMeters,
            from = LengthUnit.METER,
            to = resultUnit,
            mathContext = mathContext
        )
        val sagittaInResultUnit = LengthUnitConverter.convertLength(
            value = sagittaMeters,
            from = LengthUnit.METER,
            to = resultUnit,
            mathContext = mathContext
        )

        // Also radius converted to result unit for steps consistency
        val radiusInResultUnit = LengthUnitConverter.convertLength(
            value = radius,
            from = radiusUnit,
            to = resultUnit,
            mathContext = mathContext
        )

        val steps = generateCalculationSteps(
            radius = radius,
            radiusUnit = radiusUnit,
            radiusInResultUnit = radiusInResultUnit,
            angleDegrees = angleDegrees,
            chordInResultUnit = chordInResultUnit,
            resultUnit = resultUnit
        )

        return ChordResult(
            radius = radius,
            centralAngleDegrees = angleDegrees,
            chordLength = chordInResultUnit,
            diameter = diameterInResultUnit,
            arcLength = arcLengthInResultUnit,
            sectorArea = sectorAreaInResultUnit,
            sagitta = sagittaInResultUnit,
            formula = FORMULA_TEXT,
            calculationSteps = steps.calculationSteps,
            calculatedAt = System.currentTimeMillis()
        )
    }

    fun generateCalculationSteps(
        radius: BigDecimal,
        radiusUnit: LengthUnit,
        radiusInResultUnit: BigDecimal,
        angleDegrees: BigDecimal,
        chordInResultUnit: BigDecimal,
        resultUnit: LengthUnit
    ): ChordCalculationDetails {
        val steps = mutableListOf<String>()

        steps.add("Chord = 2r sin(θ/2)")

        if (radiusUnit != resultUnit) {
            steps.add("Convert radius: r = ${formatNumberForStep(radius)} ${radiusUnit.symbol} = ${formatNumberForStep(radiusInResultUnit)} ${resultUnit.symbol}")
        }

        val halfAngle = angleDegrees.divide(MathConstants.TWO, MathConstants.DEFAULT_MATH_CONTEXT)
        steps.add("Chord = 2 × ${formatNumberForStep(radiusInResultUnit)} × sin(${formatNumberForStep(angleDegrees)}°/2)")

        val twoR = MathConstants.TWO.multiply(radiusInResultUnit, MathConstants.DEFAULT_MATH_CONTEXT)
        steps.add("Chord = ${formatNumberForStep(twoR)} × sin(${formatNumberForStep(halfAngle)}°)")

        steps.add("Chord = ${formatNumberForStep(chordInResultUnit)} ${resultUnit.symbol}")

        if (angleDegrees.compareTo(BigDecimal("180")) == 0) {
            steps.add("Note: A 180° chord is a diameter.")
        } else if (angleDegrees.compareTo(BigDecimal("360")) == 0) {
            steps.add("Note: At 360°, the two endpoints meet and the chord length is 0.")
        }

        return ChordCalculationDetails(
            formula = FORMULA_TEXT,
            calculationSteps = steps
        )
    }

    private fun formatNumberForStep(number: BigDecimal): String {
        val rounded = number.setScale(6, RoundingMode.HALF_UP).stripTrailingZeros()
        return rounded.toPlainString()
    }
}
