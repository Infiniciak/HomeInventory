package pl.edu.ur.bw131534.homeinventory.domain.repository

import kotlinx.coroutines.flow.Flow
import pl.edu.ur.bw131534.homeinventory.data.local.entity.LocationEntity

interface LocationRepository {
    suspend fun saveLocation(location: LocationEntity): Long
    fun getAllLocations(): Flow<List<LocationEntity>>
    suspend fun getLocationsCount(): Int
    suspend fun deleteLocation(location: LocationEntity)
}

