package pl.edu.ur.bw131534.homeinventory.domain.repository

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import pl.edu.ur.bw131534.homeinventory.data.local.AppDatabase
import pl.edu.ur.bw131534.homeinventory.data.local.dao.*

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "home_inventory_db"

        )
            .fallbackToDestructiveMigration(true)
            .fallbackToDestructiveMigrationOnDowngrade(true)
            .build()
    }



    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    fun provideItemDao(database: AppDatabase): ItemDao {
        return database.itemDao()
    }

    @Provides
    fun provideReminderDao(database: AppDatabase): ReminderDao {
        return database.reminderDao()
    }

    @Provides
    fun provideLocationDao(database: AppDatabase): LocationDao {
        return database.locationDao()
    }

    @Provides
    fun provideWarrantyDao(database: AppDatabase): WarrantyDao {
        return database.warrantyDao()
    }
}