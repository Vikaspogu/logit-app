package com.vikaspogu.logit.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.vikaspogu.logit.data.model.RegionalType
import kotlinx.coroutines.flow.Flow

@Dao
interface RegionalTypeDao {
    @Query("SELECT * FROM regional_type ORDER BY regional_type.name ASC")
    fun getAllRegionalType(): Flow<List<RegionalType>>

    @Query("SELECT id from regional_type WHERE lower(name) = :name")
    fun getRegionalTypeIdByName(name: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(type: RegionalType)

    @Update
    suspend fun update(type: RegionalType)

    @Query("DELETE FROM regional_type WHERE id = :id")
    suspend fun delete(id: Int)
}