package com.example.circlechord.domain.model

import java.util.UUID

data class CircleChordHistory(
    val id: String = UUID.randomUUID().toString(),
    val radius: String,
    val radiusUnit: LengthUnit,
    val angle: String,
    val resultUnit: LengthUnit,
    val chordResult: String,
    val savedAt: Long = System.currentTimeMillis()
)
