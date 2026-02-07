package pl.edu.ur.bw131534.homeinventory.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation


data class ItemWithDetails(
    @Embedded val item: ItemEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "itemId"
    )

    val reminders: List<ReminderEntity>,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: CategoryEntity?
)