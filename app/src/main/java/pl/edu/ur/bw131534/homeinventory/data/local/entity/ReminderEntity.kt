package pl.edu.ur.bw131534.homeinventory.data.local.entity

import androidx.room.*

@Entity(tableName = "reminders",
    foreignKeys = [
        ForeignKey(
            entity = ItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("itemId")]
)
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val itemId: Long?,
    val scheduleTime: Long,
    val type: String,
    val description: String?,
    val isActive: Boolean = true
)