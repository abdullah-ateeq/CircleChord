package com.example.circlechord.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.circlechord.domain.model.CircleChordHistory
import com.example.circlechord.domain.model.LengthUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HistoryRepository(
    private val prefs: SharedPreferences
) {
    constructor(context: Context) : this(
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    )

    private val _historyFlow = MutableStateFlow<List<CircleChordHistory>>(emptyList())
    val historyFlow: StateFlow<List<CircleChordHistory>> = _historyFlow.asStateFlow()

    init {
        _historyFlow.value = loadHistory()
    }

    private fun loadHistory(): List<CircleChordHistory> {
        val raw = prefs.getString(KEY_HISTORY, null) ?: return emptyList()
        if (raw.isBlank()) return emptyList()
        return try {
            raw.lines()
                .filter { it.isNotBlank() }
                .mapNotNull { line ->
                    val parts = line.split(DELIMITER)
                    if (parts.size >= 7) {
                        CircleChordHistory(
                            id = unescape(parts[0]),
                            radius = unescape(parts[1]),
                            radiusUnit = LengthUnit.valueOf(unescape(parts[2])),
                            angle = unescape(parts[3]),
                            resultUnit = LengthUnit.valueOf(unescape(parts[4])),
                            chordResult = unescape(parts[5]),
                            savedAt = parts[6].toLongOrNull() ?: System.currentTimeMillis()
                        )
                    } else null
                }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun persist(list: List<CircleChordHistory>) {
        val encoded = list.joinToString("\n") { item ->
            listOf(
                escape(item.id),
                escape(item.radius),
                escape(item.radiusUnit.name),
                escape(item.angle),
                escape(item.resultUnit.name),
                escape(item.chordResult),
                item.savedAt.toString()
            ).joinToString(DELIMITER)
        }
        prefs.edit().putString(KEY_HISTORY, encoded).apply()
        _historyFlow.value = list
    }

    /**
     * Saves a calculation.
     * Duplicate protection: If the most recent entry has the exact same radius, radiusUnit, angle, and resultUnit,
     * do not add a duplicate.
     * Limit: Maximum 10 items. When adding the 11th, the oldest is removed.
     * Returns true if saved, false if skipped due to consecutive duplicate.
     */
    fun saveCalculation(
        radius: String,
        radiusUnit: LengthUnit,
        angle: String,
        resultUnit: LengthUnit,
        chordResult: String
    ): Boolean {
        val current = _historyFlow.value
        if (current.isNotEmpty()) {
            val first = current.first()
            if (first.radius == radius &&
                first.radiusUnit == radiusUnit &&
                first.angle == angle &&
                first.resultUnit == resultUnit
            ) {
                return false
            }
        }

        val newItem = CircleChordHistory(
            radius = radius,
            radiusUnit = radiusUnit,
            angle = angle,
            resultUnit = resultUnit,
            chordResult = chordResult,
            savedAt = System.currentTimeMillis()
        )

        val updated = mutableListOf(newItem)
        updated.addAll(current)
        val trimmed = if (updated.size > MAX_HISTORY) updated.take(MAX_HISTORY) else updated

        persist(trimmed)
        return true
    }

    fun deleteItem(id: String) {
        val filtered = _historyFlow.value.filterNot { it.id == id }
        persist(filtered)
    }

    fun clearHistory() {
        persist(emptyList())
    }

    private fun escape(value: String): String {
        return value.replace("\\", "\\\\")
            .replace(DELIMITER, "\\$DELIMITER")
            .replace("\n", "\\n")
    }

    private fun unescape(value: String): String {
        return value.replace("\\n", "\n")
            .replace("\\$DELIMITER", DELIMITER)
            .replace("\\\\", "\\")
    }

    companion object {
        private const val PREFS_NAME = "circle_chord_history_prefs"
        private const val KEY_HISTORY = "history_entries_v2"
        private const val DELIMITER = ":::"
        const val MAX_HISTORY = 10
    }
}
