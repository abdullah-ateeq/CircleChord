package com.example.circlechord.domain

import com.example.circlechord.domain.model.LengthUnit
import com.example.circlechord.math.MathConstants
import java.math.BigDecimal
import java.math.MathContext

object LengthUnitConverter {

    fun convertLength(
        value: BigDecimal,
        from: LengthUnit,
        to: LengthUnit,
        mathContext: MathContext = MathConstants.DEFAULT_MATH_CONTEXT
    ): BigDecimal {
        if (from == to) return value
        val inMeters = value.multiply(from.metersFactor, mathContext)
        return inMeters.divide(to.metersFactor, mathContext)
    }

    fun convertArea(
        value: BigDecimal,
        from: LengthUnit,
        to: LengthUnit,
        mathContext: MathContext = MathConstants.DEFAULT_MATH_CONTEXT
    ): BigDecimal {
        if (from == to) return value
        val fromFactorSquared = from.metersFactor.multiply(from.metersFactor, mathContext)
        val toFactorSquared = to.metersFactor.multiply(to.metersFactor, mathContext)
        val inSquareMeters = value.multiply(fromFactorSquared, mathContext)
        return inSquareMeters.divide(toFactorSquared, mathContext)
    }
}
