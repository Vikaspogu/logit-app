package com.vikaspogu.logit.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vikaspogu.logit.data.dao.AttendingDao
import com.vikaspogu.logit.data.dao.EntryDao
import com.vikaspogu.logit.data.dao.RegionalTypeDao
import com.vikaspogu.logit.data.dao.TypeDao
import com.vikaspogu.logit.data.model.Attending
import com.vikaspogu.logit.data.model.Entry
import com.vikaspogu.logit.data.model.RegionalType
import com.vikaspogu.logit.data.model.Type

@Database(entities = [Entry::class, Type::class,Attending::class, RegionalType::class], version = 6, exportSchema = false)
abstract class LogItDatabase : RoomDatabase() {

    abstract fun entryDao(): EntryDao
    abstract fun typeDao(): TypeDao
    abstract fun attendingDao(): AttendingDao
    abstract fun regionalTypeDao(): RegionalTypeDao

    companion object {
        const val DATABASE_NAME = "logit_database"
    }

}
