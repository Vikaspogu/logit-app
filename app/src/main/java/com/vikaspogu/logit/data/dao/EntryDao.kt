package com.vikaspogu.logit.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.vikaspogu.logit.data.model.Entry
import com.vikaspogu.logit.data.model.EntryType
import com.vikaspogu.logit.data.model.Summary
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {

    @Query("SELECT sum(entries.quantity) as total,types.type FROM entries Inner join types on entries.type_id=types.id GROUP by type_id")
    fun getSummary(): Flow<List<Summary>>

    @Query("SELECT * FROM entries")
    fun getAllEntries(): Flow<List<Entry>>

    @Query("SELECT entries.id as entry_id,entries.quantity,entries.age,entries.notes,entries.attending_name,entries.entry_date,entries.type_id,types.type FROM entries INNER JOIN types ON entries.type_id=types.id")
    fun getAllEntriesWithTypes(): Flow<List<EntryType>>

    @Query("SELECT * from entries WHERE id = :id")
    fun getEntry(id: Int): Flow<Entry>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(entry: Entry)

    @Update
    suspend fun update(entry: Entry)

    @Query("DELETE FROM entries WHERE id = :id")
    suspend fun delete(id: Int)
}