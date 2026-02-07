package pl.edu.ur.bw131534.homeinventory.domain.repository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindCategoryRepository(
        impl: CategoryRepositoryImpl
    ): CategoryRepository


    @Singleton
    @Binds
    abstract fun bindItemRepository(
        impl: ItemRepositoryImpl
    ): ItemRepository

    @Singleton
    @Binds
    abstract fun bindReminderRepository(
        impl: ReminderRepositoryImpl
    ): ReminderRepository

    @Binds
    @Singleton
    abstract fun bindLocationRepository(
        impl: LocationRepositoryImpl
    ): LocationRepository

    @Binds
    @Singleton
    abstract fun bindWarrantyRepository(
        impl: WarrantyRepositoryImpl
    ): WarrantyRepository
}
