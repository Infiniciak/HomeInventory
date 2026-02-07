package pl.edu.ur.bw131534.homeinventory.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pl.edu.ur.bw131534.homeinventory.data.local.entity.ItemEntity
import pl.edu.ur.bw131534.homeinventory.data.local.entity.LocationEntity
import pl.edu.ur.bw131534.homeinventory.domain.repository.ItemRepository
import pl.edu.ur.bw131534.homeinventory.domain.repository.LocationRepository
import pl.edu.ur.bw131534.homeinventory.domain.repository.WarrantyRepository
import javax.inject.Inject

enum class SortType { NAZWA, CENA, DATA }

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    private val locationRepository: LocationRepository,
    private val warrantyRepository: WarrantyRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _sortType = MutableStateFlow(SortType.NAZWA)
    val sortType = _sortType.asStateFlow()

    private val _selectedLocationId = MutableStateFlow<Long?>(null)
    val selectedLocationId = _selectedLocationId.asStateFlow()

    val locations = locationRepository.getAllLocations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val items = itemRepository.getAllItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expiringWarranties = warrantyRepository.getAllWarranties()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredItems = combine(items, _searchQuery, _sortType, _selectedLocationId) { allItems, query, sort, locId ->
        var list = if (query.isBlank()) allItems
        else allItems.filter { it.name.contains(query, ignoreCase = true) }

        if (locId != null) list = list.filter { it.locationId == locId }

        when (sort) {
            SortType.NAZWA -> list.sortedBy { it.name.lowercase() }
            SortType.CENA -> list.sortedBy { it.price ?: 0.0 }
            SortType.DATA -> list.sortedByDescending { it.dateAdded }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChanged(q: String) { _searchQuery.value = q }
    fun onSortTypeChanged(s: SortType) { _sortType.value = s }
    fun toggleLocationFilter(id: Long?) { _selectedLocationId.value = if (_selectedLocationId.value == id) null else id }


    fun addLocation(location: LocationEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            locationRepository.saveLocation(location)
        }
    }
    fun updateLocation(location: LocationEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            locationRepository.saveLocation(location)
        }
    }

    fun deleteLocation(location: LocationEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            locationRepository.deleteLocation(location)
        }
    }

    fun updateItemImage(item: ItemEntity, uri: String) {
        viewModelScope.launch(Dispatchers.IO) { itemRepository.saveItem(item.copy(imageUri = uri)) }
    }

    fun updateItem(item: ItemEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            itemRepository.saveItem(item)
        }
    }

    fun deleteItem(item: ItemEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            itemRepository.deleteItem(item)
        }
    }

    }
