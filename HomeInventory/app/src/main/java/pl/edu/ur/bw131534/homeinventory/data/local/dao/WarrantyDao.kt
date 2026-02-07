package pl.edu.ur.bw131534.homeinventory.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pl.edu.ur.bw131534.homeinventory.data.local.entity.WarrantyEntity

@Dao
interface WarrantyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWarranty(warranty: WarrantyEntity): Long

    @Query("""
        SELECT * FROM warranties 
        WHERE itemId = :itemId LIMIT 1
    """)
    suspend fun getWarrantyForItem(itemId: Long): WarrantyEntity?

    @Query("SELECT * FROM warranties ORDER BY expiryDate ASC")
    fun getAllWarranties(): Flow<List<WarrantyEntity>>

    @Delete
    suspend fun deleteWarranty(warranty: WarrantyEntity)
}