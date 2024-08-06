package com.vikaspogu.logit.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(private val dataStore: DataStore<Preferences>) {

    val isDarkTheme: Flow<Boolean> =
        dataStore.data.catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences -> preferences[IS_DARK_THEME] ?: false }

    private companion object {
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
    }

    suspend fun saveThemePreferences(isDarkTheme: Boolean){
        dataStore.edit {
            preferences -> preferences[IS_DARK_THEME] = isDarkTheme
        }
    }
}