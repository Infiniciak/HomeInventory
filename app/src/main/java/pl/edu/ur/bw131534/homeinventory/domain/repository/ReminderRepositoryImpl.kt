package pl.edu.ur.bw131534.homeinventory.domain.repository

import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import pl.edu.ur.bw131534.homeinventory.data.local.dao.ReminderDao
import pl.edu.ur.bw131534.homeinventory.data.local.entity.ReminderEntity


class ReminderRepositoryImpl @Inject constructor(private val dao: ReminderDao) : ReminderRepository {

    override suspend fun saveReminder(reminder: ReminderEntity): Long {
        return dao.insertReminder(reminder)
    }

    override suspend fun deleteReminder(reminder: ReminderEntity) {
        dao.deleteReminder(reminder)
    }

    override fun getActiveReminders(): Flow<List<ReminderEntity>> {
        return dao.getActiveReminders()
    }


    override suspend fun updateReminderStatus(reminderId: Long, isActive: Boolean) {

    }
}