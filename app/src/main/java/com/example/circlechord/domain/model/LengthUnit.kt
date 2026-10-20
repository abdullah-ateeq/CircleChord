package com.example.circlechord.domain.model

import java.math.BigDecimal

enum class LengthUnit(
    val symbol: String,
    val displayName: String,
    val metersFactor: BigDecimal
) {
    MILLIMETER("mm", "Millimeter", BigDecimal("0.001")),
    CENTIMETER("cm", "Centimeter", BigDecimal("0.01")),
    METER("m", "Meter", BigDecimal("1")),
    KILOMETER("km", "Kilometer", BigDecimal("1000")),
    INCH("in", "Inch", BigDecimal("0.0254")),
    FOOT("ft", "Foot", BigDecimal("0.3048")),
    YARD("yd", "Yard", BigDecimal("0.9144"));

    val areaSymbol: String
        get() = "${symbol}²"

    companion object {
        fun search(query: String): List<LengthUnit> {
            val q = query.trim().lowercase()
            if (q.isEmpty()) return entries.toList()
            return entries.filter { unit ->
                unit.symbol.lowercase().contains(q) ||
                unit.displayName.lowercase().contains(q) ||
                when (unit) {
                    MILLIMETER -> "millimeters".contains(q) || "milli".contains(q)
                    CENTIMETER -> "centimeters".contains(q) || "centi".contains(q)
                    METER -> "meters".contains(q) || "metre".contains(q) || "metres".contains(q)
                    KILOMETER -> "kilometers".contains(q) || "kilometre".contains(q) || "kilo".contains(q)
                    INCH -> "inches".contains(q) || "in".contains(q)
                    FOOT -> "feet".contains(q) || "foot".contains(q) || "ft".contains(q)
                    YARD -> "yards".contains(q) || "yard".contains(q) || "yd".contains(q)
                }
            }
        }

        fun fromSymbol(symbol: String): LengthUnit {
            return entries.firstOrNull { it.symbol.equals(symbol, ignoreCase = true) }
                ?: CENTIMETER
        }
    }
}
