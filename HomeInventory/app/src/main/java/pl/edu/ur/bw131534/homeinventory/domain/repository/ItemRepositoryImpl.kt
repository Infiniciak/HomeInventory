package pl.edu.ur.bw131534.homeinventory.domain.repository

import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import pl.edu.ur.bw131534.homeinventory.data.local.dao.ItemDao
import pl.edu.ur.bw131534.homeinventory.data.local.entity.ItemEntity


class ItemRepositoryImpl @Inject constructor(
    private val itemDao: ItemDao
) : ItemRepository {



    override suspend fun saveItem(item: ItemEntity): Long {
        return itemDao.insertItem(item)
    }


    override suspend fun getCountByModelId(modelId: String): Int {
        return itemDao.getCountByModelId(modelId)
    }

    override suspend fun getItemsCount(): Int {
        return itemDao.getItemsCount()
    }

    override suspend fun deleteItem(item: ItemEntity) {
        itemDao.deleteItem(item)
    }

    override fun getAllItems(): Flow<List<ItemEntity>> {
        return itemDao.getAllItems()
    }

    override suspend fun getItemById(itemId: Long): ItemEntity? {
        return itemDao.getItemById(itemId)
    }


    override fun searchItems(query: String): Flow<List<ItemEntity>> {
        return itemDao.searchItems(query)
        }
    }