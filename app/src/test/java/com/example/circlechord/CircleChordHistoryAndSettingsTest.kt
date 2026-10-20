package com.example.circlechord

import com.example.circlechord.data.model.AppTheme
import com.example.circlechord.data.repository.HistoryRepository
import com.example.circlechord.data.repository.SettingsRepository
import com.example.circlechord.domain.model.LengthUnit
import com.example.circlechord.testutil.FakeSharedPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.RoundingMode

class CircleChordHistoryAndSettingsTest {

    // Test 48: History save
    // Critical Test: Save Radius = 10 cm, Angle = 60° -> one history entry
    @Test
    fun test48_historySave() {
        val fakePrefs = FakeSharedPreferences()
        val repository = HistoryRepository(fakePrefs)

        val saved = repository.saveCalculation(
            radius = "10",
            radiusUnit = LengthUnit.CENTIMETER,
            angle = "60",
            resultUnit = LengthUnit.CENTIMETER,
            chordResult = "10 cm"
        )

        assertTrue(saved)
        assertEquals(1, repository.historyFlow.value.size)
        val entry = repository.historyFlow.value.first()
        assertEquals("10", entry.radius)
        assertEquals(LengthUnit.CENTIMETER, entry.radiusUnit)
        assertEquals("60", entry.angle)
        assertEquals(LengthUnit.CENTIMETER, entry.resultUnit)
        assertEquals("10 cm", entry.chordResult)
    }

    // Test 49: History restore
    @Test
    fun test49_historyRestore() {
        val fakePrefs = FakeSharedPreferences()
        val repository = HistoryRepository(fakePrefs)

        repository.saveCalculation(
            radius = "5",
            radiusUnit = LengthUnit.METER,
            angle = "90",
            resultUnit = LengthUnit.CENTIMETER,
            chordResult = "707.106781 cm"
        )

        val entry = repository.historyFlow.value.first()
        assertEquals("5", entry.radius)
        assertEquals(LengthUnit.METER, entry.radiusUnit)
        assertEquals("90", entry.angle)
        assertEquals(LengthUnit.CENTIMETER, entry.resultUnit)
    }

    // Test 50: History limit (save 10 calculations -> 10 entries; save 11th -> oldest removed)
    @Test
    fun test50_historyLimit() {
        val fakePrefs = FakeSharedPreferences()
        val repository = HistoryRepository(fakePrefs)

        for (i in 1..10) {
            repository.saveCalculation(
                radius = "$i",
                radiusUnit = LengthUnit.CENTIMETER,
                angle = "${i * 10}",
                resultUnit = LengthUnit.CENTIMETER,
                chordResult = "$i cm"
            )
        }
        assertEquals(10, repository.historyFlow.value.size)
        assertEquals("1", repository.historyFlow.value.last().radius)

        // Save 11th calculation
        repository.saveCalculation(
            radius = "11",
            radiusUnit = LengthUnit.CENTIMETER,
            angle = "110",
            resultUnit = LengthUnit.CENTIMETER,
            chordResult = "11 cm"
        )

        assertEquals(10, repository.historyFlow.value.size)
        assertEquals("11", repository.historyFlow.value.first().radius)
        // Oldest item "1" has been removed
        assertFalse(repository.historyFlow.value.any { it.radius == "1" })
        assertEquals("2", repository.historyFlow.value.last().radius)
    }

    // Test 51: History delete individual item
    @Test
    fun test51_historyDelete() {
        val fakePrefs = FakeSharedPreferences()
        val repository = HistoryRepository(fakePrefs)

        repository.saveCalculation("10", LengthUnit.CENTIMETER, "60", LengthUnit.CENTIMETER, "10 cm")
        repository.saveCalculation("20", LengthUnit.CENTIMETER, "90", LengthUnit.CENTIMETER, "28.28 cm")
        assertEquals(2, repository.historyFlow.value.size)

        val firstId = repository.historyFlow.value.first().id
        repository.deleteItem(firstId)

        assertEquals(1, repository.historyFlow.value.size)
        assertEquals("10", repository.historyFlow.value.first().radius)
    }

    // Test 52: History clear
    @Test
    fun test52_historyClear() {
        val fakePrefs = FakeSharedPreferences()
        val repository = HistoryRepository(fakePrefs)

        repository.saveCalculation("10", LengthUnit.CENTIMETER, "60", LengthUnit.CENTIMETER, "10 cm")
        repository.saveCalculation("20", LengthUnit.CENTIMETER, "90", LengthUnit.CENTIMETER, "28.28 cm")
        assertEquals(2, repository.historyFlow.value.size)

        repository.clearHistory()
        assertEquals(0, repository.historyFlow.value.size)
    }

    // Test 53: Duplicate history protection
    // Critical Test: Save the same exact calculation twice consecutively -> no duplicate
    @Test
    fun test53_duplicateHistoryProtection() {
        val fakePrefs = FakeSharedPreferences()
        val repository = HistoryRepository(fakePrefs)

        val firstSave = repository.saveCalculation(
            radius = "10",
            radiusUnit = LengthUnit.CENTIMETER,
            angle = "60",
            resultUnit = LengthUnit.CENTIMETER,
            chordResult = "10 cm"
        )
        assertTrue(firstSave)
        assertEquals(1, repository.historyFlow.value.size)

        val secondSave = repository.saveCalculation(
            radius = "10",
            radiusUnit = LengthUnit.CENTIMETER,
            angle = "60",
            resultUnit = LengthUnit.CENTIMETER,
            chordResult = "10 cm"
        )
        assertFalse(secondSave)
        assertEquals(1, repository.historyFlow.value.size)
    }

    // Test 54: Settings persistence
    // Critical Test: Dark mode, decimal places = 4 persist across repository reload
    @Test
    fun test54_settingsPersistence() {
        val fakePrefs = FakeSharedPreferences()
        val repo1 = SettingsRepository(fakePrefs)

        repo1.updateTheme(AppTheme.DARK)
        repo1.updateDecimalPlaces(4)
        repo1.updateRoundingMode(RoundingMode.HALF_EVEN)
        repo1.updateShowFormula(false)

        // Reload repository with the same preferences
        val repo2 = SettingsRepository(fakePrefs)
        val loaded = repo2.settingsFlow.value

        assertEquals(AppTheme.DARK, loaded.theme)
        assertEquals(4, loaded.decimalPlaces)
        assertEquals(RoundingMode.HALF_EVEN, loaded.roundingMode)
        assertFalse(loaded.showFormula)
    }

    // Test 55: Reset settings
    @Test
    fun test55_resetSettings() {
        val fakePrefs = FakeSharedPreferences()
        val settingsRepo = SettingsRepository(fakePrefs)
        val historyRepo = HistoryRepository(fakePrefs)

        // Modify settings and add history
        settingsRepo.updateTheme(AppTheme.DARK)
        settingsRepo.updateDecimalPlaces(2)
        historyRepo.saveCalculation("15", LengthUnit.CENTIMETER, "45", LengthUnit.CENTIMETER, "11.48 cm")

        // Reset settings
        settingsRepo.resetSettings()

        val resetSettings = settingsRepo.settingsFlow.value
        assertEquals(AppTheme.SYSTEM, resetSettings.theme)
        assertEquals(6, resetSettings.decimalPlaces)
        assertEquals("10", resetSettings.lastRadius)
        assertEquals("60", resetSettings.lastAngle)
        assertEquals(LengthUnit.CENTIMETER, resetSettings.radiusUnit)
        assertEquals(LengthUnit.CENTIMETER, resetSettings.resultUnit)

        // Verify history is still intact!
        assertEquals(1, historyRepo.historyFlow.value.size)
    }
}
