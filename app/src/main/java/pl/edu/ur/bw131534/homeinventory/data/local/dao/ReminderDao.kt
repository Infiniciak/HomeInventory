package pl.edu.ur.bw131534.homeinventory.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pl.edu.ur.bw131534.homeinventory.data.local.entity.ReminderEntity

@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity): Long

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)

    @Query("SELECT * FROM reminders WHERE isActive = 1 ORDER BY scheduleTime ASC")
    fun getActiveReminders(): Flow<List<ReminderEntity>>
}