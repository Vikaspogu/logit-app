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

    @Query("SELECT CASE WHEN sum(entries.quantity)= 0 THEN COUNT(*) ELSE SUM(entries.quantity) END as total,types.type, types.id as type_id FROM entries Inner join types on entries.type_id=types.id GROUP by type_id")
    fun getSummary(): Flow<List<Summary>>

    @Query("SELECT * FROM entries")
    fun getAllEntries(): Flow<List<Entry>>

    @Query("SELECT entries.id as entry_id,entries.quantity,entries.gender,entries.age,entries.asa,entries.clinical,entries.cvc,regional_type.name as regional_type,entries.notes,attending.name as attending_name,entries.entry_date,entries.type_id,types.type FROM entries INNER JOIN types ON entries.type_id=types.id INNER JOIN attending ON entries.attending_id=attending.id LEFT JOIN regional_type ON entries.regional_id=regional_type.id AND entries.regional_id IS NOT NULL ORDER BY types.type ASC")
    fun getAllEntriesWithTypes(): Flow<List<EntryType>>

    @Query("SELECT entries.id as entry_id,entries.quantity,entries.gender,entries.age,entries.asa,entries.clinical,entries.cvc,regional_type.name as regional_type,entries.notes,attending.name as attending_name,entries.entry_date,entries.type_id,types.type FROM entries INNER JOIN types ON entries.type_id=types.id INNER JOIN attending ON entries.attending_id=attending.id LEFT JOIN regional_type ON entries.regional_id=regional_type.id AND entries.regional_id IS NOT NULL WHERE type_id = :typeId ORDER BY types.type ASC")
    fun getEntriesByType(typeId: Int): Flow<List<EntryType>>

    @Query("SELECT * from entries WHERE id = :id")
    fun getEntry(id: Int): Flow<Entry>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(entry: Entry)

    @Update
    suspend fun update(entry: Entry)

    @Query("DELETE FROM entries WHERE id = :id")
    suspend fun delete(id: Int)
}