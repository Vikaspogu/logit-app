package com.vikaspogu.logit.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "types")
data class Type(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val type: String
)
