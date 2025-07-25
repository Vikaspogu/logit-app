package com.vikaspogu.logit.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(private val dataStore: DataStore<Preferences>) {

    val isDarkTheme: Flow<Boolean> = dataStore.data.catch {
        if (it is IOException) {
            Log.e(TAG, "Error reading preferences.", it)
            emit(emptyPreferences())
        } else {
            throw it
        }
    }.map { preferences -> preferences[IS_DARK_THEME] ?: false }

    val isResidentView: Flow<Boolean> = dataStore.data.catch {
        if (it is IOException) {
            Log.e(TAG, "Error reading preferences.", it)
            emit(emptyPreferences())
        } else {
            throw it
        }
    }.map { preferences -> preferences[IS_RESIDENT_VIEW] ?: false }

    val username: Flow<String> = dataStore.data.map { preferences ->
        preferences[USERNAME] ?: ""
    }

    val selectedDays: Flow<Set<String>> = dataStore.data.map { preferences ->
        preferences[REMINDER_DAYS] ?: emptySet()
    }

    val selectedTime: Flow<Long> = dataStore.data.catch {
        if (it is IOException) {
            Log.e(TAG, "Error reading preferences.", it)
            emit(emptyPreferences())
        } else {
            throw it
        }
    }.map { preferences -> preferences[REMINDER_TIME] ?: 0L }

    private companion object {
        val USERNAME = stringPreferencesKey("username")
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
        val IS_RESIDENT_VIEW = booleanPreferencesKey("is_resident_view")
        val REMINDER_DAYS = stringSetPreferencesKey("reminder_days")
        val REMINDER_TIME = longPreferencesKey("reminder_time")
    }

    suspend fun saveView(isResidentView: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_RESIDENT_VIEW] = isResidentView
        }
    }

    suspend fun saveThemePreferences(isDarkTheme: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_DARK_THEME] = isDarkTheme
        }
    }

    suspend fun saveReminderDays(days: Set<String>) {
        dataStore.edit { preferences ->
            preferences[REMINDER_DAYS] = days
        }
    }

    suspend fun saveReminderTime(reminderTime: Long) {
        dataStore.edit { preferences ->
            preferences[REMINDER_TIME] = reminderTime
        }
    }

    suspend fun saveUsername(username: String) {
        dataStore.edit { preferences ->
            preferences[USERNAME] = username
        }
    }
}