package com.vikaspogu.logit.data.repository

import com.vikaspogu.logit.data.model.Attending
import kotlinx.coroutines.flow.Flow

interface AttendingRepository {
    fun getAllAttending(): Flow<List<Attending>>
    suspend fun insertAttending(attending: Attending)
    suspend fun updateAttending(attending: Attending)
    suspend fun deleteAttending(id: Int)
}