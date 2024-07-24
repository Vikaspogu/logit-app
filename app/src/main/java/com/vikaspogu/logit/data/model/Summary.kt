package com.vikaspogu.logit.data.model

import androidx.room.ColumnInfo

data class Summary(
    val total: Int,
    val type: String,
    @ColumnInfo(name = "type_id")
    val typeId: Int
)
