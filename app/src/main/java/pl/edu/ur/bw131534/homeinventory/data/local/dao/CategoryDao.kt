package pl.edu.ur.bw131534.homeinventory.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pl.edu.ur.bw131534.homeinventory.data.local.entity.CategoryEntity

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCategoriesCount(): Int

    @Query("SELECT * FROM categories WHERE name = :name LIMIT 1")
    suspend fun getCategoryByName(name: String): CategoryEntity?
}