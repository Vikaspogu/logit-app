package com.vikaspogu.logit.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attending")
data class Attending(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)
