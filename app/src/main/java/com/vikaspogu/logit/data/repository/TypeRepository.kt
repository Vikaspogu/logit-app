package com.vikaspogu.logit.data.repository

import com.vikaspogu.logit.data.model.Type
import kotlinx.coroutines.flow.Flow

interface TypeRepository {
    fun getAllTypes(): Flow<List<Type>>
    suspend fun insertType(type: Type)
    suspend fun updateType(type: Type)
    suspend fun deleteType(id: Int)
}