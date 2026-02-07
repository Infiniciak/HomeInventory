package pl.edu.ur.bw131534.homeinventory.domain.repository

import kotlinx.coroutines.flow.Flow
import pl.edu.ur.bw131534.homeinventory.data.local.entity.ReminderEntity

interface ReminderRepository {
    suspend fun saveReminder(reminder: ReminderEntity): Long
    suspend fun deleteReminder(reminder: ReminderEntity)
    fun getActiveReminders(): Flow<List<ReminderEntity>>
    suspend fun updateReminderStatus(reminderId: Long, isActive: Boolean)
}