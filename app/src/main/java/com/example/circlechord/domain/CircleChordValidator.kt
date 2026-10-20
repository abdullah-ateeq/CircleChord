package com.example.circlechord.domain

import com.example.circlechord.domain.model.CircleChordError
import java.math.BigDecimal

object CircleChordValidator {

    private val ZERO = BigDecimal.ZERO
    private val THREE_SIXTY = BigDecimal("360")

    fun validateRadius(input: String): Result<BigDecimal> {
        val parseResult = CircleChordParser.parseNumber(input)
        if (parseResult.isFailure) {
            return parseResult
        }
        val value = parseResult.getOrThrow()
        return validateRadiusValue(value)
    }

    fun validateRadiusValue(value: BigDecimal): Result<BigDecimal> {
        if (value.compareTo(ZERO) <= 0) {
            return Result.failure(ValidationException(CircleChordError.NonPositiveRadius))
        }
        return Result.success(value)
    }

    fun validateAngle(input: String): Result<BigDecimal> {
        val parseResult = CircleChordParser.parseNumber(input)
        if (parseResult.isFailure) {
            return parseResult
        }
        val value = parseResult.getOrThrow()
        return validateAngleValue(value)
    }

    fun validateAngleValue(value: BigDecimal): Result<BigDecimal> {
        if (value.compareTo(ZERO) <= 0 || value.compareTo(THREE_SIXTY) > 0) {
            return Result.failure(ValidationException(CircleChordError.InvalidAngle))
        }
        return Result.success(value)
    }
}
