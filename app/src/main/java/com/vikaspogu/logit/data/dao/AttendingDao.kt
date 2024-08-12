package com.vikaspogu.logit.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.vikaspogu.logit.data.model.Attending
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendingDao {
    @Query("SELECT * FROM attending ORDER BY attending.name ASC")
    fun getAllAttending(): Flow<List<Attending>>

    @Query("SELECT * from attending WHERE id = :id")
    fun getType(id: Int): Flow<Attending>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(type: Attending)

    @Update
    suspend fun update(type: Attending)

    @Query("DELETE FROM attending WHERE id = :id")
    suspend fun delete(id: Int)
}