package com.vikaspogu.logit.data.repository

import com.vikaspogu.logit.data.dao.RegionalTypeDao
import com.vikaspogu.logit.data.model.RegionalType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OfflineRegionalTypeRepository @Inject constructor(private val regionalTypeDao: RegionalTypeDao): RegionalTypeRepository {
    override fun getAllRegionalType(): Flow<List<RegionalType>> = regionalTypeDao.getAllRegionalType()

    override suspend fun insertRegionalType(regionalType: RegionalType) = regionalTypeDao.insert(regionalType)

    override suspend fun updateRegionalType(regionalType: RegionalType) = regionalTypeDao.update(regionalType)

    override suspend fun deleteRegionalType(id: Int) = regionalTypeDao.delete(id)

    override suspend fun getRegionalTypeIdByName(name: String): Flow<Int>  = regionalTypeDao.getRegionalTypeIdByName(name)
}