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
            childColumns = ["type_id"],
            onDelete      = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Attending::class,
            parentColumns = ["id"],
            childColumns = ["attending_id"],
            onDelete      = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = RegionalType::class,
            parentColumns = ["id"],
            childColumns  = ["regional_id"],
            onDelete      = ForeignKey.SET_NULL
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
    val attendingId: Int? = null,
    @ColumnInfo(name = "entry_date")
    val entryDate: Long = 0L,
    @ColumnInfo(name = "type_id", index = true)
    val typeId: Int? = null,
    val asa: Int,
    val clinical: String,
    val cvc: String,
    @ColumnInfo(name = "regional_id", index = true)
    val regionalId: Int? = null,
)
