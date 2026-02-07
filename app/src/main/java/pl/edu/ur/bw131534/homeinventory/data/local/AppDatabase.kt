package pl.edu.ur.bw131534.homeinventory.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import pl.edu.ur.bw131534.homeinventory.data.local.dao.*
import pl.edu.ur.bw131534.homeinventory.data.local.entity.*
@Database(
    entities = [
        ItemEntity::class,
        CategoryEntity::class,
        ReminderEntity::class,
        LocationEntity::class,
        WarrantyEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao
    abstract fun categoryDao(): CategoryDao
    abstract fun reminderDao(): ReminderDao

    abstract fun locationDao(): LocationDao

    abstract fun warrantyDao(): WarrantyDao


}