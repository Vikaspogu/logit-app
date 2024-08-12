package com.vikaspogu.logit.data.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
                CREATE TABLE new_types (
                    id INTEGER PRIMARY KEY NOT NULL,
                    type TEXT NOT NULL UNIQUE
                )
                """.trimIndent()
        )
        db.execSQL(
            """
                INSERT INTO new_types (id, type)
                SELECT id, type FROM types
                """.trimIndent()
        )
        db.execSQL("DROP TABLE types")
        db.execSQL("ALTER TABLE new_types RENAME TO types")
        db.execSQL("CREATE UNIQUE INDEX index_types_type ON types(type)")

        db.execSQL(
            """
                CREATE TABLE new_attending (
                    id INTEGER PRIMARY KEY NOT NULL,
                    name TEXT NOT NULL UNIQUE
                )
                """.trimIndent()
        )
        db.execSQL(
            """
                INSERT INTO new_attending (id, name)
                SELECT id, name FROM attending
                """.trimIndent()
        )
        db.execSQL("DROP TABLE attending")
        db.execSQL("ALTER TABLE new_attending RENAME TO attending")
        db.execSQL("CREATE UNIQUE INDEX index_attending_name ON attending(name)")
    }
}