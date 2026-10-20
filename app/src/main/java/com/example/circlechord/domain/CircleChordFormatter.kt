package com.example.circlechord.domain

import com.example.circlechord.domain.model.ChordResult
import com.example.circlechord.domain.model.LengthUnit
import java.math.BigDecimal
import java.math.RoundingMode

object CircleChordFormatter {

    fun formatNumber(
        value: BigDecimal,
        decimalPlaces: Int = 6,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): String {
        val rounded = value.setScale(decimalPlaces, roundingMode)
        val stripped = rounded.stripTrailingZeros()
        // If stripped has negative scale (e.g. 100), toPlainString handles it properly
        return stripped.toPlainString()
    }

    fun formatLength(
        value: BigDecimal,
        unit: LengthUnit,
        decimalPlaces: Int = 6,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): String {
        val formattedNumber = formatNumber(value, decimalPlaces, roundingMode)
        return "$formattedNumber ${unit.symbol}"
    }

    fun formatArea(
        value: BigDecimal,
        unit: LengthUnit,
        decimalPlaces: Int = 6,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): String {
        val formattedNumber = formatNumber(value, decimalPlaces, roundingMode)
        return "$formattedNumber ${unit.areaSymbol}"
    }

    fun formatAngle(
        value: BigDecimal,
        decimalPlaces: Int = 6,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): String {
        val formattedNumber = formatNumber(value, decimalPlaces, roundingMode)
        return "$formattedNumber°"
    }

    fun formatCalculation(
        result: ChordResult,
        unit: LengthUnit,
        decimalPlaces: Int = 6,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): String {
        val chord = formatLength(result.chordLength, unit, decimalPlaces, roundingMode)
        val angle = formatAngle(result.centralAngleDegrees, decimalPlaces, roundingMode)
        val radius = formatNumber(result.radius, decimalPlaces, roundingMode)
        return "Radius: $radius, Angle: $angle, Chord: $chord"
    }

    fun formatCopyFull(
        radius: String,
        radiusUnit: LengthUnit,
        angle: String,
        chordResult: String
    ): String {
        return buildString {
            appendLine("CircleChord")
            appendLine()
            appendLine("Radius: $radius ${radiusUnit.symbol}")
            appendLine("Central Angle: $angle°")
            appendLine()
            append("Chord Length: $chordResult")
        }
    }

    fun formatShareText(
        radius: String,
        radiusUnit: LengthUnit,
        angle: String,
        chordResult: String
    ): String {
        return buildString {
            appendLine("CircleChord")
            appendLine()
            appendLine("Radius: $radius ${radiusUnit.symbol}")
            appendLine("Central Angle: $angle°")
            append("Chord Length: $chordResult")
        }
    }
}
