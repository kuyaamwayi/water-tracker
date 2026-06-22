package com.example.watertracker

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.temporal.ChronoUnit

val Context.waterDataStore: DataStore<Preferences> by preferencesDataStore(name = "water_settings")

object WaterPrefs {
    val VOLUME_ML = intPreferencesKey("volume_ml")
    val STREAK = intPreferencesKey("streak")
    val LAST_LOG_DATE = stringPreferencesKey("last_log_date")
}

// FIX: Daily goal constant in one place — used by both repo and widget
const val DAILY_GOAL_ML = 2000

class WaterRepository(private val context: Context) {

    val volumeFlow: Flow<Int> = context.waterDataStore.data.map { it[WaterPrefs.VOLUME_ML] ?: 0 }
    val streakFlow: Flow<Int> = context.waterDataStore.data.map { it[WaterPrefs.STREAK] ?: 0 }

    suspend fun addBottle() {
        context.waterDataStore.edit { prefs ->
            val today = LocalDate.now().toString()
            val lastLog = prefs[WaterPrefs.LAST_LOG_DATE] ?: today

            if (lastLog != today) {
                handleDayRollover(prefs, lastLog, today)
            }

            // FIX: removed the 2000 cap — allow logging beyond goal so streak is earned correctly
            val newVolume = (prefs[WaterPrefs.VOLUME_ML] ?: 0) + 500 // 500ml per tap is more realistic
            prefs[WaterPrefs.VOLUME_ML] = newVolume
            prefs[WaterPrefs.LAST_LOG_DATE] = today
        }
    }

    suspend fun performMidnightReset() {
        context.waterDataStore.edit { prefs ->
            val today = LocalDate.now().toString()
            val lastLog = prefs[WaterPrefs.LAST_LOG_DATE] ?: today
            if (lastLog != today) {
                handleDayRollover(prefs, lastLog, today)
            }
        }
    }

    private fun handleDayRollover(
        prefs: MutablePreferences,
        lastLogString: String,
        todayString: String
    ) {
        val lastLog = LocalDate.parse(lastLogString)
        val today = LocalDate.parse(todayString)
        val daysPassed = ChronoUnit.DAYS.between(lastLog, today)

        if (daysPassed >= 1) {
            val currentVolume = prefs[WaterPrefs.VOLUME_ML] ?: 0
            val currentStreak = prefs[WaterPrefs.STREAK] ?: 0

            // FIX: streak threshold now correctly matches the daily goal (2000ml, not 1000ml)
            prefs[WaterPrefs.STREAK] = if (currentVolume >= DAILY_GOAL_ML) currentStreak + 1 else 0
            prefs[WaterPrefs.VOLUME_ML] = 0
        }
    }
}
