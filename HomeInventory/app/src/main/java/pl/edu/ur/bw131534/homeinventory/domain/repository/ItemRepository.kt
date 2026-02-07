package pl.edu.ur.bw131534.homeinventory.domain.repository

import kotlinx.coroutines.flow.Flow
import pl.edu.ur.bw131534.homeinventory.data.local.entity.ItemEntity


interface ItemRepository {
    suspend fun saveItem(item: ItemEntity): Long
    suspend fun deleteItem(item: ItemEntity)

    suspend fun getCountByModelId(modelId: String): Int
    suspend fun getItemsCount(): Int
    fun getAllItems(): Flow<List<ItemEntity>>
    suspend fun getItemById(itemId: Long): ItemEntity?
    fun searchItems(query: String): Flow<List<ItemEntity>>

}