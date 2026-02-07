package pl.edu.ur.bw131534.homeinventory.domain.repository

import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import pl.edu.ur.bw131534.homeinventory.data.local.dao.WarrantyDao
import pl.edu.ur.bw131534.homeinventory.data.local.entity.WarrantyEntity


class WarrantyRepositoryImpl @Inject constructor(
    private val dao: WarrantyDao
) : WarrantyRepository {
    override suspend fun saveWarranty(warranty: WarrantyEntity) = dao.insertWarranty(warranty)
    override suspend fun getWarrantyForItem(itemId: Long): WarrantyEntity? = dao.getWarrantyForItem(itemId)
    override fun getAllWarranties(): Flow<List<WarrantyEntity>> = dao.getAllWarranties()
    override suspend fun deleteWarranty(warranty: WarrantyEntity) = dao.deleteWarranty(warranty)
}