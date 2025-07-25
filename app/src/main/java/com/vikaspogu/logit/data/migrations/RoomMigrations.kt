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

val MIGRATION_4_5 = object : Migration(4,5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
                CREATE TABLE IF NOT EXISTS regional_type (
                    id INTEGER PRIMARY KEY NOT NULL,
                    name TEXT NOT NULL UNIQUE
                )
                """.trimIndent()
        )
        db.execSQL("CREATE UNIQUE INDEX index_regional_type_name ON regional_type(name)")
        db.execSQL(
            """
          CREATE TABLE entries_new (
            id             INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            quantity       INTEGER NOT NULL,
            notes          TEXT    NOT NULL,
            asa            INTEGER NOT NULL,
            clinical       TEXT    NOT NULL,
            cvc            TEXT    NOT NULL,
            age            INTEGER NOT NULL,
            gender         TEXT    NOT NULL,
            attending_id   INTEGER NOT NULL,
            entry_date     INTEGER NOT NULL,
            type_id        INTEGER NOT NULL,
            regional_id    INTEGER,
            FOREIGN KEY(type_id)        REFERENCES types(id)           ON DELETE NO ACTION ON UPDATE NO ACTION,
            FOREIGN KEY(attending_id)   REFERENCES attending(id)       ON DELETE NO ACTION ON UPDATE NO ACTION,
            FOREIGN KEY(regional_id)   REFERENCES regional_type(id)    ON DELETE SET NULL ON UPDATE NO ACTION
          );
        """.trimIndent()
        )

        db.execSQL(
            """
          INSERT INTO entries_new (
            id, quantity, notes, age, gender,
            attending_id, entry_date, type_id
          )
          SELECT
            id, quantity, notes, age, gender,
            attending_id, entry_date, type_id
          FROM entries;
        """.trimIndent()
        )

        db.execSQL("DROP TABLE entries;")
        db.execSQL("ALTER TABLE entries_new RENAME TO entries;")

        db.execSQL("CREATE INDEX index_entries_type_id       ON entries(type_id);")
        db.execSQL("CREATE INDEX index_entries_attending_id  ON entries(attending_id);")
        db.execSQL("CREATE INDEX index_entries_regional_id   ON entries(regional_id);")
    }
}