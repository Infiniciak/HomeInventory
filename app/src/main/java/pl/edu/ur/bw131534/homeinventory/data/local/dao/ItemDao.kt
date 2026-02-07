package pl.edu.ur.bw131534.homeinventory.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import pl.edu.ur.bw131534.homeinventory.data.local.entity.ItemEntity
import pl.edu.ur.bw131534.homeinventory.data.local.entity.ItemWithDetails

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ItemEntity): Long

    @Query("SELECT * FROM items WHERE id = :id")
    suspend fun getItemById(id: Long): ItemEntity?

    @Update
    suspend fun updateItem(item: ItemEntity)

    @Delete
    suspend fun deleteItem(item: ItemEntity)

    @Query("SELECT * FROM items ORDER BY dateAdded DESC")
    fun getAllItems(): Flow<List<ItemEntity>>


    @Query("SELECT * FROM items WHERE name LIKE '%' || :query || '%'")
    fun searchItems(query: String): Flow<List<ItemEntity>>

    @Transaction
    @Query("SELECT * FROM items ORDER BY dateAdded DESC")
    fun getAllItemsWithDetails(): Flow<List<ItemWithDetails>>

    @Query("SELECT COUNT(*) FROM items")
    suspend fun getItemsCount(): Int

    @Transaction
    @Query("SELECT * FROM items WHERE id = :itemId")
    suspend fun getItemWithDetailsById(itemId: Long): ItemWithDetails?


    @Query("SELECT COUNT(*) FROM items WHERE modelId = :modelId")
    suspend fun getCountByModelId(modelId: String): Int

    @Transaction
    @Query("""
        SELECT * FROM items 
        WHERE name LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%'
        ORDER BY dateAdded DESC
    """)
    fun searchItemsWithDetails(query: String): Flow<List<ItemWithDetails>>
}