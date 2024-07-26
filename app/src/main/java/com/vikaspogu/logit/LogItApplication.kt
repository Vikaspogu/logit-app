package com.vikaspogu.logit

import android.app.Application
import com.vikaspogu.logit.data.LogItDatabase
import com.vikaspogu.logit.data.repository.AttendingRepository
import com.vikaspogu.logit.data.repository.EntryRepository
import com.vikaspogu.logit.data.repository.OfflineAttendingRepository
import com.vikaspogu.logit.data.repository.OfflineEntryRepository
import com.vikaspogu.logit.data.repository.OfflineTypeRepository
import com.vikaspogu.logit.data.repository.TypeRepository

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
}
