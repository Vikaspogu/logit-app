package com.vikaspogu.logit.data.repository

import com.vikaspogu.logit.data.dao.EntryDao
import com.vikaspogu.logit.data.model.Entry
import com.vikaspogu.logit.data.model.EntryType
import com.vikaspogu.logit.data.model.Summary
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OfflineEntryRepository @Inject constructor(private val entryDao: EntryDao) : EntryRepository {
    override fun getSummary(): Flow<List<Summary>> = entryDao.getSummary()
    override fun getEntries(): Flow<List<Entry>> = entryDao.getAllEntries()
    override fun getEntriesWithTypes(): Flow<List<EntryType>> = entryDao.getAllEntriesWithTypes()
    override fun getEntriesByType(typeId: Int): Flow<List<EntryType>> = entryDao.getEntriesByType(typeId)
    override fun getEntry(id: Int): Flow<Entry> = entryDao.getEntry(id)
    override suspend fun insertEntry(entry: Entry) = entryDao.insert(entry)
    override suspend fun updateEntry(entry: Entry) = entryDao.update(entry)
    override suspend fun deleteEntry(id: Int) = entryDao.delete(id)
}