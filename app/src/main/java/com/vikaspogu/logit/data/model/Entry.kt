package com.vikaspogu.logit.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "entries",
    foreignKeys = [
        ForeignKey(
            entity = Type::class,
            parentColumns = ["id"],
            childColumns = ["type_id"]
        ),
        ForeignKey(
            entity = Attending::class,
            parentColumns = ["id"],
            childColumns = ["attending_id"]
        )
    ]
)
data class Entry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val quantity: Int,
    val notes: String,
    val age: Int,
    val gender: String,
    @ColumnInfo(name = "attending_id", index = true)
    val attendingId: Int,
    @ColumnInfo(name = "entry_date")
    val entryDate: Long = 0L,
    @ColumnInfo(name = "type_id", index = true)
    val typeId: Int,
)
