package com.vikaspogu.logit.data.repository

import com.vikaspogu.logit.data.dao.AttendingDao
import com.vikaspogu.logit.data.model.Attending
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OfflineAttendingRepository @Inject constructor(private val attendingDao: AttendingDao): AttendingRepository {
    override fun getAllAttending(): Flow<List<Attending>> = attendingDao.getAllAttending()

    override suspend fun insertAttending(attending: Attending) = attendingDao.insert(attending)

    override suspend fun updateAttending(attending: Attending) = attendingDao.update(attending)

    override suspend fun deleteAttending(id: Int) = attendingDao.delete(id)

    override suspend fun getAttendingIdByName(name: String): Flow<Int>  = attendingDao.getAttendingIdByName(name)
}