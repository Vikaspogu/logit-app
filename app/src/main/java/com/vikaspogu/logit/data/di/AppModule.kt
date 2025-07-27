package com.vikaspogu.logit.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.vikaspogu.logit.data.LogItDatabase
import com.vikaspogu.logit.data.dao.AttendingDao
import com.vikaspogu.logit.data.dao.EntryDao
import com.vikaspogu.logit.data.dao.RegionalTypeDao
import com.vikaspogu.logit.data.dao.TypeDao
import com.vikaspogu.logit.data.migrations.MIGRATION_3_4
import com.vikaspogu.logit.data.migrations.MIGRATION_4_5
import com.vikaspogu.logit.data.migrations.MIGRATION_5_6
import com.vikaspogu.logit.data.repository.AttendingRepository
import com.vikaspogu.logit.data.repository.EntryRepository
import com.vikaspogu.logit.data.repository.OfflineAttendingRepository
import com.vikaspogu.logit.data.repository.OfflineEntryRepository
import com.vikaspogu.logit.data.repository.OfflineRegionalTypeRepository
import com.vikaspogu.logit.data.repository.OfflineTypeRepository
import com.vikaspogu.logit.data.repository.RegionalTypeRepository
import com.vikaspogu.logit.data.repository.TypeRepository
import com.vikaspogu.logit.data.repository.WorkerManagerReminderRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

private const val USER_PREFERENCES = "user_preferences"

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideLogItDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, LogItDatabase::class.java, LogItDatabase.DATABASE_NAME)
            .createFromAsset("database/logit_database.db")
            .addMigrations(MIGRATION_3_4)
            .addMigrations(MIGRATION_4_5)
            .addMigrations(MIGRATION_5_6)
            .allowMainThreadQueries()
            .build()

    @Singleton
    @Provides
    fun providesEntryDao(database: LogItDatabase): EntryDao = database.entryDao()

    @Singleton
    @Provides
    fun providesAttendingDao(database: LogItDatabase): AttendingDao = database.attendingDao()

    @Singleton
    @Provides
    fun providesTypeDao(database: LogItDatabase): TypeDao = database.typeDao()

    @Singleton
    @Provides
    fun providesRegionalTypeDao(database: LogItDatabase): RegionalTypeDao = database.regionalTypeDao()

    @Singleton
    @Provides
    fun provideEntryRepository(entryDao: EntryDao): EntryRepository = OfflineEntryRepository(entryDao)

    @Singleton
    @Provides
    fun provideAttendingRepository(attendingDao: AttendingDao): AttendingRepository = OfflineAttendingRepository(attendingDao)

    @Singleton
    @Provides
    fun provideTypeRepository(typeDao: TypeDao): TypeRepository = OfflineTypeRepository(typeDao)

    @Singleton
    @Provides
    fun provideRegionalTypeRepository(regionalTypeDao: RegionalTypeDao): RegionalTypeRepository = OfflineRegionalTypeRepository(regionalTypeDao)

    @Singleton
    @Provides
    fun provideWorkerManagerReminderRepository(@ApplicationContext context: Context): WorkerManagerReminderRepository = WorkerManagerReminderRepository(context)

    @Singleton
    @Provides
    fun providePreferencesDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            migrations = listOf(SharedPreferencesMigration(appContext,USER_PREFERENCES)),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { appContext.preferencesDataStoreFile(USER_PREFERENCES) }
        )
    }
}