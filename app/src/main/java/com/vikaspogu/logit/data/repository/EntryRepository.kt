package com.vikaspogu.logit.data.repository

import com.vikaspogu.logit.data.model.Entry
import com.vikaspogu.logit.data.model.EntryType
import com.vikaspogu.logit.data.model.Summary
import kotlinx.coroutines.flow.Flow

interface EntryRepository {
    fun getSummary(): Flow<List<Summary>>
    fun getEntries(): Flow<List<Entry>>
    fun getEntriesWithTypes(): Flow<List<EntryType>>
    fun getEntriesByType(typeId: Int): Flow<List<Entry>>
    fun getEntry(id: Int): Flow<Entry>
    suspend fun insertEntry(entry: Entry)
    suspend fun updateEntry(entry: Entry)
    suspend fun deleteEntry(id: Int)
}