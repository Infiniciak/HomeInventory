package pl.edu.ur.bw131534.homeinventory.domain.repository

import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import pl.edu.ur.bw131534.homeinventory.data.local.dao.LocationDao
import pl.edu.ur.bw131534.homeinventory.data.local.entity.LocationEntity


class LocationRepositoryImpl @Inject constructor(
    private val dao: LocationDao
) : LocationRepository {
    override suspend fun saveLocation(location: LocationEntity) = dao.saveLocation(location)
    override fun getAllLocations(): Flow<List<LocationEntity>> = dao.getAllLocations()
    override suspend fun getLocationsCount(): Int = dao.getLocationsCount()
    override suspend fun deleteLocation(location: LocationEntity) = dao.deleteLocation(location)
}