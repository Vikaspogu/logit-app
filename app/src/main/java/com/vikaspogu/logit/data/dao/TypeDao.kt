package com.vikaspogu.logit.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.vikaspogu.logit.data.model.Type
import kotlinx.coroutines.flow.Flow

@Dao
interface TypeDao {

    @Query("SELECT * FROM types ORDER BY types.type ASC")
    fun getAllTypes(): Flow<List<Type>>

    @Query("SELECT * from types WHERE id = :id")
    fun getType(id: Int): Flow<Type>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(type: Type)

    @Update
    suspend fun update(type: Type)

    @Query("DELETE FROM types WHERE id = :id")
    suspend fun delete(id: Int)
}