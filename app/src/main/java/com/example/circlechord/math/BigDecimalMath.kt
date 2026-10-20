package com.example.circlechord.math

import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.math.RoundingMode

object BigDecimalMath {

    fun toRadians(degrees: BigDecimal, mathContext: MathContext = MathConstants.DEFAULT_MATH_CONTEXT): BigDecimal {
        val workingContext = MathContext(mathContext.precision + 15, RoundingMode.HALF_UP)
        return degrees.multiply(MathConstants.PI, workingContext)
            .divide(MathConstants.ONE_EIGHTY, workingContext)
            .round(mathContext)
    }

    fun toDegrees(radians: BigDecimal, mathContext: MathContext = MathConstants.DEFAULT_MATH_CONTEXT): BigDecimal {
        val workingContext = MathContext(mathContext.precision + 15, RoundingMode.HALF_UP)
        return radians.multiply(MathConstants.ONE_EIGHTY, workingContext)
            .divide(MathConstants.PI, workingContext)
            .round(mathContext)
    }

    fun sin(radians: BigDecimal, mathContext: MathContext = MathConstants.DEFAULT_MATH_CONTEXT): BigDecimal {
        val workingContext = MathContext(mathContext.precision + 20, RoundingMode.HALF_UP)
        val pi = MathConstants.PI
        val twoPi = MathConstants.TWO.multiply(pi, workingContext)
        val piOverTwo = pi.divide(MathConstants.TWO, workingContext)

        // Range reduction to [0, 2π)
        var x = radians.remainder(twoPi, workingContext)
        if (x.signum() < 0) {
            x = x.add(twoPi, workingContext)
        }

        // Quick check for 0 or very close
        if (x.signum() == 0) {
            return BigDecimal.ZERO.setScale(mathContext.precision, RoundingMode.HALF_UP).stripTrailingZeros()
        }

        // Symmetry reduction
        val sign: Int
        val y: BigDecimal

        when {
            x <= piOverTwo -> {
                sign = 1
                y = x
            }
            x <= pi -> {
                sign = 1
                y = pi.subtract(x, workingContext)
            }
            x <= pi.multiply(BigDecimal("1.5"), workingContext) -> {
                sign = -1
                y = x.subtract(pi, workingContext)
            }
            else -> {
                sign = -1
                y = twoPi.subtract(x, workingContext)
            }
        }

        if (y.signum() == 0) {
            return BigDecimal.ZERO
        }

        // Maclaurin series for sin(y): y - y^3/3! + y^5/5! - ...
        var term = y
        var sum = y
        val ySquared = y.multiply(y, workingContext)
        val epsilon = BigDecimal.ONE.movePointLeft(mathContext.precision + 12)

        var n = 1
        while (term.abs() > epsilon && n < 200) {
            val denom = BigDecimal((2 * n) * (2 * n + 1))
            term = term.multiply(ySquared, workingContext)
                .divide(denom, workingContext)
                .negate()
            sum = sum.add(term, workingContext)
            n++
        }

        val result = if (sign < 0) sum.negate() else sum
        return result.round(mathContext)
    }

    fun cos(radians: BigDecimal, mathContext: MathContext = MathConstants.DEFAULT_MATH_CONTEXT): BigDecimal {
        val workingContext = MathContext(mathContext.precision + 20, RoundingMode.HALF_UP)
        val pi = MathConstants.PI
        val twoPi = MathConstants.TWO.multiply(pi, workingContext)
        val piOverTwo = pi.divide(MathConstants.TWO, workingContext)

        // Range reduction to [0, 2π)
        var x = radians.remainder(twoPi, workingContext)
        if (x.signum() < 0) {
            x = x.add(twoPi, workingContext)
        }

        if (x.signum() == 0) {
            return BigDecimal.ONE.round(mathContext)
        }

        val sign: Int
        val y: BigDecimal

        when {
            x <= piOverTwo -> {
                sign = 1
                y = x
            }
            x <= pi -> {
                sign = -1
                y = pi.subtract(x, workingContext)
            }
            x <= pi.multiply(BigDecimal("1.5"), workingContext) -> {
                sign = -1
                y = x.subtract(pi, workingContext)
            }
            else -> {
                sign = 1
                y = twoPi.subtract(x, workingContext)
            }
        }

        if (y.signum() == 0) {
            val res = if (sign < 0) BigDecimal.ONE.negate() else BigDecimal.ONE
            return res.round(mathContext)
        }

        // Maclaurin series for cos(y): 1 - y^2/2! + y^4/4! - ...
        var term = BigDecimal.ONE
        var sum = BigDecimal.ONE
        val ySquared = y.multiply(y, workingContext)
        val epsilon = BigDecimal.ONE.movePointLeft(mathContext.precision + 12)

        var n = 1
        while (term.abs() > epsilon && n < 200) {
            val denom = BigDecimal((2 * n - 1) * (2 * n))
            term = term.multiply(ySquared, workingContext)
                .divide(denom, workingContext)
                .negate()
            sum = sum.add(term, workingContext)
            n++
        }

        val result = if (sign < 0) sum.negate() else sum
        return result.round(mathContext)
    }

    fun sqrt(value: BigDecimal, mathContext: MathContext = MathConstants.DEFAULT_MATH_CONTEXT): BigDecimal {
        val cmp = value.signum()
        if (cmp < 0) {
            throw IllegalArgumentException("Cannot calculate square root of negative number: $value")
        }
        if (cmp == 0) {
            return BigDecimal.ZERO
        }
        if (value.compareTo(BigDecimal.ONE) == 0) {
            return BigDecimal.ONE.round(mathContext)
        }

        val workingContext = MathContext(mathContext.precision + 20, RoundingMode.HALF_UP)
        
        // Initial estimate using Double or scale estimation without loss of determinism
        val initialDouble = try {
            val d = value.toDouble()
            if (d.isFinite() && d > 0) java.lang.Math.sqrt(d) else 1.0
        } catch (_: Exception) {
            1.0
        }
        
        var x = BigDecimal(initialDouble.toString())
        if (x.signum() <= 0) {
            x = BigDecimal.ONE
        }

        val two = MathConstants.TWO
        val epsilon = BigDecimal.ONE.movePointLeft(mathContext.precision + 12)

        // Newton-Raphson: x_{k+1} = 0.5 * (x_k + value / x_k)
        var diff: BigDecimal
        var iterations = 0
        do {
            val nextX = x.add(value.divide(x, workingContext), workingContext).divide(two, workingContext)
            diff = nextX.subtract(x, workingContext).abs()
            x = nextX
            iterations++
        } while (diff > epsilon && iterations < 100)

        return x.round(mathContext)
    }
}
