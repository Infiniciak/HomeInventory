package pl.edu.ur.bw131534.homeinventory.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pl.edu.ur.bw131534.homeinventory.data.local.entity.LocationEntity


@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLocation(location: LocationEntity): Long

    @Query("SELECT * FROM locations ORDER BY name ASC")
    fun getAllLocations(): Flow<List<LocationEntity>>

    @Query("SELECT COUNT(*) FROM locations")
    suspend fun getLocationsCount(): Int

    @Delete
    suspend fun deleteLocation(location: LocationEntity)
}