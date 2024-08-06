package com.vikaspogu.logit

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.vikaspogu.logit.data.repository.UserPreferencesRepository
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LogItApplication : Application() {

    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var appContext: Context
    
    override fun onCreate() {
        super.onCreate()
        appContext = this
        userPreferencesRepository = UserPreferencesRepository(dataStore = dataStore)
    }
}

private const val THEME_PREFERENCE_NAME = "theme_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = THEME_PREFERENCE_NAME
)