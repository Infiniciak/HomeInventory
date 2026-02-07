package pl.edu.ur.bw131534.homeinventory.domain.repository

import kotlinx.coroutines.flow.Flow
import pl.edu.ur.bw131534.homeinventory.data.local.entity.WarrantyEntity

interface WarrantyRepository {
    suspend fun saveWarranty(warranty: WarrantyEntity): Long
    suspend fun getWarrantyForItem(itemId: Long): WarrantyEntity?
    fun getAllWarranties(): Flow<List<WarrantyEntity>>
    suspend fun deleteWarranty(warranty: WarrantyEntity)
}