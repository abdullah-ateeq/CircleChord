package com.example.circlechord.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.circlechord.data.model.AppSettings
import com.example.circlechord.data.model.AppTheme
import com.example.circlechord.domain.model.LengthUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.RoundingMode

class SettingsRepository(
    private val prefs: SharedPreferences
) {
    constructor(context: Context) : this(
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    )

    private val _settingsFlow = MutableStateFlow(loadSettings())
    val settingsFlow: StateFlow<AppSettings> = _settingsFlow.asStateFlow()

    private fun loadSettings(): AppSettings {
        val themeStr = prefs.getString(KEY_THEME, AppTheme.SYSTEM.name) ?: AppTheme.SYSTEM.name
        val theme = try { AppTheme.valueOf(themeStr) } catch (_: Exception) { AppTheme.SYSTEM }

        val dynamicColor = prefs.getBoolean(KEY_DYNAMIC_COLOR, true)
        val hapticFeedback = prefs.getBoolean(KEY_HAPTIC, true)
        val animations = prefs.getBoolean(KEY_ANIMATIONS, true)
        val decimalPlaces = prefs.getInt(KEY_DECIMAL_PLACES, 6)

        val roundingStr = prefs.getString(KEY_ROUNDING_MODE, RoundingMode.HALF_UP.name) ?: RoundingMode.HALF_UP.name
        val roundingMode = try { RoundingMode.valueOf(roundingStr) } catch (_: Exception) { RoundingMode.HALF_UP }

        val showFormula = prefs.getBoolean(KEY_SHOW_FORMULA, true)
        val showCalculationSteps = prefs.getBoolean(KEY_SHOW_STEPS, true)
        val showDiagram = prefs.getBoolean(KEY_SHOW_DIAGRAM, true)
        val showMoreDetails = prefs.getBoolean(KEY_SHOW_MORE_DETAILS, false)
        val confirmDelete = prefs.getBoolean(KEY_CONFIRM_DELETE, true)

        val lastRadius = prefs.getString(KEY_LAST_RADIUS, "10") ?: "10"
        val lastAngle = prefs.getString(KEY_LAST_ANGLE, "60") ?: "60"

        val radiusUnitStr = prefs.getString(KEY_RADIUS_UNIT, LengthUnit.CENTIMETER.name) ?: LengthUnit.CENTIMETER.name
        val radiusUnit = try { LengthUnit.valueOf(radiusUnitStr) } catch (_: Exception) { LengthUnit.CENTIMETER }

        val resultUnitStr = prefs.getString(KEY_RESULT_UNIT, LengthUnit.CENTIMETER.name) ?: LengthUnit.CENTIMETER.name
        val resultUnit = try { LengthUnit.valueOf(resultUnitStr) } catch (_: Exception) { LengthUnit.CENTIMETER }

        return AppSettings(
            theme = theme,
            dynamicColor = dynamicColor,
            hapticFeedback = hapticFeedback,
            animations = animations,
            decimalPlaces = decimalPlaces,
            roundingMode = roundingMode,
            showFormula = showFormula,
            showCalculationSteps = showCalculationSteps,
            showDiagram = showDiagram,
            showMoreDetails = showMoreDetails,
            confirmDelete = confirmDelete,
            lastRadius = lastRadius,
            lastAngle = lastAngle,
            radiusUnit = radiusUnit,
            resultUnit = resultUnit
        )
    }

    fun updateTheme(theme: AppTheme) {
        prefs.edit().putString(KEY_THEME, theme.name).apply()
        _settingsFlow.value = _settingsFlow.value.copy(theme = theme)
    }

    fun updateDynamicColor(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DYNAMIC_COLOR, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(dynamicColor = enabled)
    }

    fun updateHapticFeedback(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HAPTIC, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(hapticFeedback = enabled)
    }

    fun updateAnimations(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_ANIMATIONS, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(animations = enabled)
    }

    fun updateDecimalPlaces(places: Int) {
        prefs.edit().putInt(KEY_DECIMAL_PLACES, places).apply()
        _settingsFlow.value = _settingsFlow.value.copy(decimalPlaces = places)
    }

    fun updateRoundingMode(mode: RoundingMode) {
        prefs.edit().putString(KEY_ROUNDING_MODE, mode.name).apply()
        _settingsFlow.value = _settingsFlow.value.copy(roundingMode = mode)
    }

    fun updateShowFormula(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_FORMULA, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(showFormula = enabled)
    }

    fun updateShowCalculationSteps(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_STEPS, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(showCalculationSteps = enabled)
    }

    fun updateShowDiagram(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_DIAGRAM, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(showDiagram = enabled)
    }

    fun updateShowMoreDetails(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_MORE_DETAILS, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(showMoreDetails = enabled)
    }

    fun updateConfirmDelete(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_CONFIRM_DELETE, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(confirmDelete = enabled)
    }

    fun saveLastInputs(radius: String, angle: String, radiusUnit: LengthUnit, resultUnit: LengthUnit) {
        prefs.edit()
            .putString(KEY_LAST_RADIUS, radius)
            .putString(KEY_LAST_ANGLE, angle)
            .putString(KEY_RADIUS_UNIT, radiusUnit.name)
            .putString(KEY_RESULT_UNIT, resultUnit.name)
            .apply()
        _settingsFlow.value = _settingsFlow.value.copy(
            lastRadius = radius,
            lastAngle = angle,
            radiusUnit = radiusUnit,
            resultUnit = resultUnit
        )
    }

    fun resetSettings() {
        val defaults = AppSettings()
        prefs.edit()
            .putString(KEY_THEME, defaults.theme.name)
            .putBoolean(KEY_DYNAMIC_COLOR, defaults.dynamicColor)
            .putBoolean(KEY_HAPTIC, defaults.hapticFeedback)
            .putBoolean(KEY_ANIMATIONS, defaults.animations)
            .putInt(KEY_DECIMAL_PLACES, defaults.decimalPlaces)
            .putString(KEY_ROUNDING_MODE, defaults.roundingMode.name)
            .putBoolean(KEY_SHOW_FORMULA, defaults.showFormula)
            .putBoolean(KEY_SHOW_STEPS, defaults.showCalculationSteps)
            .putBoolean(KEY_SHOW_DIAGRAM, defaults.showDiagram)
            .putBoolean(KEY_SHOW_MORE_DETAILS, defaults.showMoreDetails)
            .putBoolean(KEY_CONFIRM_DELETE, defaults.confirmDelete)
            .putString(KEY_LAST_RADIUS, defaults.lastRadius)
            .putString(KEY_LAST_ANGLE, defaults.lastAngle)
            .putString(KEY_RADIUS_UNIT, defaults.radiusUnit.name)
            .putString(KEY_RESULT_UNIT, defaults.resultUnit.name)
            .apply()
        _settingsFlow.value = defaults
    }

    companion object {
        private const val PREFS_NAME = "circle_chord_settings"
        private const val KEY_THEME = "theme"
        private const val KEY_DYNAMIC_COLOR = "dynamic_color"
        private const val KEY_HAPTIC = "haptic_feedback"
        private const val KEY_ANIMATIONS = "animations"
        private const val KEY_DECIMAL_PLACES = "decimal_places"
        private const val KEY_ROUNDING_MODE = "rounding_mode"
        private const val KEY_SHOW_FORMULA = "show_formula"
        private const val KEY_SHOW_STEPS = "show_steps"
        private const val KEY_SHOW_DIAGRAM = "show_diagram"
        private const val KEY_SHOW_MORE_DETAILS = "show_more_details"
        private const val KEY_CONFIRM_DELETE = "confirm_delete"
        private const val KEY_LAST_RADIUS = "last_radius"
        private const val KEY_LAST_ANGLE = "last_angle"
        private const val KEY_RADIUS_UNIT = "radius_unit"
        private const val KEY_RESULT_UNIT = "result_unit"
    }
}
