package com.example.circlechord.ui

import com.example.circlechord.domain.model.ChordResult
import com.example.circlechord.domain.model.CircleChordHistory
import com.example.circlechord.domain.model.LengthUnit

data class CircleChordUiState(
    val radiusInput: String = "10",
    val angleInput: String = "60",
    val radiusUnit: LengthUnit = LengthUnit.CENTIMETER,
    val resultUnit: LengthUnit = LengthUnit.CENTIMETER,
    val result: ChordResult? = null,
    val history: List<CircleChordHistory> = emptyList(),
    val errorMessage: String? = null,
    val isCalculating: Boolean = false,
    val saveFeedbackMessage: String? = null
)
