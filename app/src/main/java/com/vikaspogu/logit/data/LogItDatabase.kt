package com.vikaspogu.logit.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.vikaspogu.logit.data.dao.EntryDao
import com.vikaspogu.logit.data.dao.TypeDao
import com.vikaspogu.logit.data.model.Entry
import com.vikaspogu.logit.data.model.Type

@Database(entities = [Entry::class, Type::class], version = 2, exportSchema = false)
abstract class LogItDatabase : RoomDatabase() {

    abstract fun entryDao(): EntryDao
    abstract fun typeDao(): TypeDao

    companion object {

        @Volatile
        private var Instance: LogItDatabase? = null

        fun getDatabase(context: Context): LogItDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, LogItDatabase::class.java, "logit_database")
                    .createFromAsset("database/logit_database.db")
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {

    }
}
