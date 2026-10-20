package com.example.circlechord.data.model

import com.example.circlechord.domain.model.LengthUnit
import java.math.RoundingMode

enum class AppTheme(val displayName: String) {
    SYSTEM("System Default"),
    LIGHT("Light"),
    DARK("Dark")
}

data class AppSettings(
    val theme: AppTheme = AppTheme.SYSTEM,
    val dynamicColor: Boolean = true,
    val hapticFeedback: Boolean = true,
    val animations: Boolean = true,
    val decimalPlaces: Int = 6,
    val roundingMode: RoundingMode = RoundingMode.HALF_UP,
    val showFormula: Boolean = true,
    val showCalculationSteps: Boolean = true,
    val showDiagram: Boolean = true,
    val showMoreDetails: Boolean = false,
    val confirmDelete: Boolean = true,
    val lastRadius: String = "10",
    val lastAngle: String = "60",
    val radiusUnit: LengthUnit = LengthUnit.CENTIMETER,
    val resultUnit: LengthUnit = LengthUnit.CENTIMETER
)
