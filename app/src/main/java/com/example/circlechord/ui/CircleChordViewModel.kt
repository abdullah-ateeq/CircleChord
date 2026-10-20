package com.example.circlechord.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.circlechord.data.model.AppSettings
import com.example.circlechord.data.model.AppTheme
import com.example.circlechord.data.repository.HistoryRepository
import com.example.circlechord.data.repository.SettingsRepository
import com.example.circlechord.domain.ChordCalculator
import com.example.circlechord.domain.CircleChordFormatter
import com.example.circlechord.domain.CircleChordValidator
import com.example.circlechord.domain.model.CircleChordHistory
import com.example.circlechord.domain.model.LengthUnit
import com.example.circlechord.math.MathConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

class CircleChordViewModel(
    application: Application,
    private val settingsRepository: SettingsRepository = SettingsRepository(application),
    private val historyRepository: HistoryRepository = HistoryRepository(application)
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(
        CircleChordUiState(
            radiusInput = settingsRepository.settingsFlow.value.lastRadius,
            angleInput = settingsRepository.settingsFlow.value.lastAngle,
            radiusUnit = settingsRepository.settingsFlow.value.radiusUnit,
            resultUnit = settingsRepository.settingsFlow.value.resultUnit,
            history = historyRepository.historyFlow.value
        )
    )
    val uiState: StateFlow<CircleChordUiState> = _uiState.asStateFlow()

    val settings: StateFlow<AppSettings> = settingsRepository.settingsFlow

    init {
        // Collect history updates
        viewModelScope.launch {
            historyRepository.historyFlow.collect { historyList ->
                _uiState.update { it.copy(history = historyList) }
            }
        }

        // Run initial authoritative calculation
        recalculate(
            radiusStr = _uiState.value.radiusInput,
            angleStr = _uiState.value.angleInput,
            radiusUnit = _uiState.value.radiusUnit,
            resultUnit = _uiState.value.resultUnit
        )
    }

    fun onRadiusChanged(newRadius: String) {
        _uiState.update { it.copy(radiusInput = newRadius) }
        recalculate(
            radiusStr = newRadius,
            angleStr = _uiState.value.angleInput,
            radiusUnit = _uiState.value.radiusUnit,
            resultUnit = _uiState.value.resultUnit
        )
    }

    fun onAngleChanged(newAngle: String) {
        _uiState.update { it.copy(angleInput = newAngle) }
        recalculate(
            radiusStr = _uiState.value.radiusInput,
            angleStr = newAngle,
            radiusUnit = _uiState.value.radiusUnit,
            resultUnit = _uiState.value.resultUnit
        )
    }

    fun onRadiusUnitChanged(newUnit: LengthUnit) {
        _uiState.update { it.copy(radiusUnit = newUnit) }
        recalculate(
            radiusStr = _uiState.value.radiusInput,
            angleStr = _uiState.value.angleInput,
            radiusUnit = newUnit,
            resultUnit = _uiState.value.resultUnit
        )
    }

    fun onResultUnitChanged(newUnit: LengthUnit) {
        _uiState.update { it.copy(resultUnit = newUnit) }
        recalculate(
            radiusStr = _uiState.value.radiusInput,
            angleStr = _uiState.value.angleInput,
            radiusUnit = _uiState.value.radiusUnit,
            resultUnit = newUnit
        )
    }

    private fun recalculate(
        radiusStr: String,
        angleStr: String,
        radiusUnit: LengthUnit,
        resultUnit: LengthUnit
    ) {
        val radiusResult = CircleChordValidator.validateRadius(radiusStr)
        if (radiusResult.isFailure) {
            val error = radiusResult.exceptionOrNull()?.message ?: "Radius must be greater than zero."
            _uiState.update { it.copy(result = null, errorMessage = error) }
            return
        }

        val angleResult = CircleChordValidator.validateAngle(angleStr)
        if (angleResult.isFailure) {
            val error = angleResult.exceptionOrNull()?.message ?: "Angle must be greater than 0° and no greater than 360°."
            _uiState.update { it.copy(result = null, errorMessage = error) }
            return
        }

        val radius = radiusResult.getOrThrow()
        val angle = angleResult.getOrThrow()

        try {
            val chordResult = ChordCalculator.calculateFull(
                radius = radius,
                radiusUnit = radiusUnit,
                angleDegrees = angle,
                resultUnit = resultUnit,
                mathContext = MathConstants.DEFAULT_MATH_CONTEXT
            )
            _uiState.update {
                it.copy(
                    result = chordResult,
                    errorMessage = null
                )
            }
            // Persist valid inputs
            settingsRepository.saveLastInputs(
                radius = radiusStr,
                angle = angleStr,
                radiusUnit = radiusUnit,
                resultUnit = resultUnit
            )
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    result = null,
                    errorMessage = e.message ?: "Calculation error"
                )
            }
        }
    }

    fun saveCurrentCalculation(): Boolean {
        val state = _uiState.value
        val result = state.result ?: return false

        val formattedChord = CircleChordFormatter.formatLength(
            value = result.chordLength,
            unit = state.resultUnit,
            decimalPlaces = settings.value.decimalPlaces,
            roundingMode = settings.value.roundingMode
        )

        val saved = historyRepository.saveCalculation(
            radius = state.radiusInput,
            radiusUnit = state.radiusUnit,
            angle = state.angleInput,
            resultUnit = state.resultUnit,
            chordResult = formattedChord
        )

        _uiState.update {
            it.copy(
                saveFeedbackMessage = if (saved) "Calculation saved to history" else "Already saved in history"
            )
        }
        return saved
    }

    fun clearSaveFeedback() {
        _uiState.update { it.copy(saveFeedbackMessage = null) }
    }

    fun restoreHistory(item: CircleChordHistory) {
        _uiState.update {
            it.copy(
                radiusInput = item.radius,
                angleInput = item.angle,
                radiusUnit = item.radiusUnit,
                resultUnit = item.resultUnit
            )
        }
        recalculate(
            radiusStr = item.radius,
            angleStr = item.angle,
            radiusUnit = item.radiusUnit,
            resultUnit = item.resultUnit
        )
    }

    fun deleteHistoryItem(id: String) {
        historyRepository.deleteItem(id)
    }

    fun clearHistory() {
        historyRepository.clearHistory()
    }

    fun resetToDefaults() {
        val defaultRadius = "10"
        val defaultAngle = "60"
        val defaultUnit = LengthUnit.CENTIMETER

        _uiState.update {
            it.copy(
                radiusInput = defaultRadius,
                angleInput = defaultAngle,
                radiusUnit = defaultUnit,
                resultUnit = defaultUnit
            )
        }
        recalculate(
            radiusStr = defaultRadius,
            angleStr = defaultAngle,
            radiusUnit = defaultUnit,
            resultUnit = defaultUnit
        )
    }

    // Settings actions
    fun updateTheme(theme: AppTheme) = settingsRepository.updateTheme(theme)
    fun updateDynamicColor(enabled: Boolean) = settingsRepository.updateDynamicColor(enabled)
    fun updateHapticFeedback(enabled: Boolean) = settingsRepository.updateHapticFeedback(enabled)
    fun updateAnimations(enabled: Boolean) = settingsRepository.updateAnimations(enabled)
    fun updateDecimalPlaces(places: Int) = settingsRepository.updateDecimalPlaces(places)
    fun updateRoundingMode(mode: RoundingMode) = settingsRepository.updateRoundingMode(mode)
    fun updateShowFormula(enabled: Boolean) = settingsRepository.updateShowFormula(enabled)
    fun updateShowCalculationSteps(enabled: Boolean) = settingsRepository.updateShowCalculationSteps(enabled)
    fun updateShowDiagram(enabled: Boolean) = settingsRepository.updateShowDiagram(enabled)
    fun updateShowMoreDetails(enabled: Boolean) = settingsRepository.updateShowMoreDetails(enabled)
    fun updateConfirmDelete(enabled: Boolean) = settingsRepository.updateConfirmDelete(enabled)
    fun resetSettings() = settingsRepository.resetSettings()

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CircleChordViewModel(application) as T
                }
            }
    }
}
