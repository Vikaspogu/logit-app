package com.vikaspogu.logit.data.repository

import com.vikaspogu.logit.data.model.RegionalType
import kotlinx.coroutines.flow.Flow

interface RegionalTypeRepository {
    fun getAllRegionalType(): Flow<List<RegionalType>>
    suspend fun insertRegionalType(regionalType: RegionalType)
    suspend fun updateRegionalType(regionalType: RegionalType)
    suspend fun deleteRegionalType(id: Int)
    suspend fun getRegionalTypeIdByName(name: String): Flow<Int>
}