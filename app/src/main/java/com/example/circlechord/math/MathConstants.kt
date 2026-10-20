package com.example.circlechord.math

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

object MathConstants {
    val PI = BigDecimal(
        "3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170679"
    )

    val TWO = BigDecimal("2")
    val ONE_EIGHTY = BigDecimal("180")
    val HALF = BigDecimal("0.5")

    val DEFAULT_MATH_CONTEXT = MathContext(50, RoundingMode.HALF_UP)
}
