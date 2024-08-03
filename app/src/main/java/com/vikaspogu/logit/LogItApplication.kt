package com.vikaspogu.logit

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.vikaspogu.logit.data.LogItDatabase
import com.vikaspogu.logit.data.repository.AttendingRepository
import com.vikaspogu.logit.data.repository.EntryRepository
import com.vikaspogu.logit.data.repository.OfflineAttendingRepository
import com.vikaspogu.logit.data.repository.OfflineEntryRepository
import com.vikaspogu.logit.data.repository.OfflineTypeRepository
import com.vikaspogu.logit.data.repository.TypeRepository
import com.vikaspogu.logit.data.repository.UserPreferencesRepository

class LogItApplication : Application() {
    val entryRepository: EntryRepository by lazy {
        OfflineEntryRepository(
            LogItDatabase.getDatabase(
                this
            ).entryDao()
        )
    }
    val typeRepository: TypeRepository by lazy {
        OfflineTypeRepository(
            LogItDatabase.getDatabase(
                this
            ).typeDao()
        )
    }
    val attendingRepository: AttendingRepository by lazy {
        OfflineAttendingRepository(
            LogItDatabase.getDatabase(this).attendingDao()
        )
    }

    lateinit var userPreferencesRepository: UserPreferencesRepository
    
    override fun onCreate() {
        super.onCreate()
        userPreferencesRepository = UserPreferencesRepository(dataStore = dataStore)
    }
}


private const val THEME_PREFERENCE_NAME = "theme_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = THEME_PREFERENCE_NAME
)