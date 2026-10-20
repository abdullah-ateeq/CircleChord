package com.example.circlechord.domain.model

import java.math.BigDecimal

data class ChordResult(
    val radius: BigDecimal,
    val centralAngleDegrees: BigDecimal,
    val chordLength: BigDecimal,
    val diameter: BigDecimal,
    val arcLength: BigDecimal,
    val sectorArea: BigDecimal,
    val sagitta: BigDecimal,
    val formula: String,
    val calculationSteps: List<String>,
    val calculatedAt: Long = System.currentTimeMillis()
)

data class ChordCalculationDetails(
    val formula: String,
    val calculationSteps: List<String>
)
