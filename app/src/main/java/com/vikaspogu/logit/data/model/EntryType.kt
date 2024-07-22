package com.vikaspogu.logit.data.model

import androidx.room.ColumnInfo

data class EntryType(
    @ColumnInfo(name = "entry_id")
    val entryId: Int = 0,
    val quantity: Int,
    val notes: String,
    val age: Int,
    @ColumnInfo(name = "attending_name")
    val attendingName: String,
    @ColumnInfo(name = "entry_date")
    val entryDate: Long = 0L,
    @ColumnInfo(name = "type_id")
    val typeId: Int,
    val type: String
)
