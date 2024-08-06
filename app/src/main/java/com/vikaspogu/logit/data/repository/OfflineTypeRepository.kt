package com.vikaspogu.logit.data.repository

import com.vikaspogu.logit.data.dao.TypeDao
import com.vikaspogu.logit.data.model.Type
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OfflineTypeRepository @Inject constructor(private val typeDao: TypeDao) : TypeRepository {
    override fun getAllTypes(): Flow<List<Type>> = typeDao.getAllTypes()

    override suspend fun insertType(type: Type) = typeDao.insert(type)

    override suspend fun updateType(type: Type) = typeDao.update(type)

    override suspend fun deleteType(id: Int) = typeDao.delete(id)

}