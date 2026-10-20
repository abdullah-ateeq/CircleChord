package com.example.circlechord.domain

import com.example.circlechord.domain.model.CircleChordError
import java.math.BigDecimal

object CircleChordParser {

    private const val MAX_DIGITS = 500

    fun parseNumber(input: String): Result<BigDecimal> {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return Result.failure(ValidationException(CircleChordError.EmptyInput))
        }

        // Count digit characters
        val digitCount = trimmed.count { it.isDigit() }
        if (digitCount > MAX_DIGITS) {
            return Result.failure(ValidationException(CircleChordError.InputTooLong))
        }

        // Handle leading or trailing decimal point
        val sanitized = when {
            trimmed.startsWith(".") -> "0$trimmed"
            trimmed.startsWith("-.") -> "-0${trimmed.substring(2)}"
            trimmed.startsWith("+.") -> "0${trimmed.substring(2)}"
            trimmed.endsWith(".") -> trimmed.dropLast(1)
            else -> trimmed
        }

        return try {
            val bd = BigDecimal(sanitized)
            Result.success(bd)
        } catch (_: NumberFormatException) {
            Result.failure(ValidationException(CircleChordError.InvalidNumber))
        }
    }
}

class ValidationException(val error: CircleChordError) : IllegalArgumentException(error.toUserMessage())
