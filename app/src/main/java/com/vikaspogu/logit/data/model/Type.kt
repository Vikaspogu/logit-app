package com.vikaspogu.logit.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "types", indices = [Index(value = ["type"],unique = true)])
data class Type(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "type")
    val type: String
)
