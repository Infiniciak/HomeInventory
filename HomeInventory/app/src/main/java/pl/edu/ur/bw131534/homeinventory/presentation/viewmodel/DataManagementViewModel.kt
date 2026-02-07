package pl.edu.ur.bw131534.homeinventory.presentation.viewmodel

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import pl.edu.ur.bw131534.homeinventory.data.local.entity.CategoryEntity
import pl.edu.ur.bw131534.homeinventory.data.local.entity.ItemEntity
import pl.edu.ur.bw131534.homeinventory.data.local.entity.LocationEntity
import pl.edu.ur.bw131534.homeinventory.data.local.entity.WarrantyEntity
import pl.edu.ur.bw131534.homeinventory.data.dao.InitialItemDto
import pl.edu.ur.bw131534.homeinventory.data.dao.InitialWarrantyDto
import pl.edu.ur.bw131534.homeinventory.domain.repository.CategoryRepository
import pl.edu.ur.bw131534.homeinventory.domain.repository.ItemRepository
import pl.edu.ur.bw131534.homeinventory.domain.repository.LocationRepository
import pl.edu.ur.bw131534.homeinventory.domain.repository.WarrantyRepository
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class DataManagementViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val locationRepository: LocationRepository,
    private val itemRepository: ItemRepository,
    private val warrantyRepository: WarrantyRepository,
    private val application: Application
) : ViewModel() {

    private val _statusMessage = MutableStateFlow("")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            setupInitialData()
            if (itemRepository.getItemsCount() == 0) {
                loadJsonFromAssets()
            }
        }
    }


    fun exportDatabaseToJson() {
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val itemsList = itemRepository.getAllItems().first()
                val gson = GsonBuilder().setPrettyPrinting().create()
                val jsonString = gson.toJson(itemsList)

                val file = File(application.cacheDir, "inventory_export.json")
                file.writeText(jsonString)

                shareFile(file)
                _statusMessage.value = "Eksport zakończony sukcesem"
            } catch (e: Exception) {
                Log.e("EXPORT_ERROR", "Błąd eksportu: ${e.message}")
                _statusMessage.value = "Błąd eksportu: ${e.message}"
            }
        }
    }

    private fun shareFile(file: File) {
        val uri = FileProvider.getUriForFile(
            application,
            "${application.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(intent, "Udostępnij bazę przedmiotów")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        application.startActivity(chooser)
    }


    private suspend fun loadJsonFromAssets() {
        try {
            val existingCategories = categoryRepository.getAllCategories().first()
            val existingLocations = locationRepository.getAllLocations().first()

            val itemsJson = application.assets.open("initial_data.json").bufferedReader().use { it.readText() }
            val warrantiesJson = application.assets.open("initial_warranties.json").bufferedReader().use { it.readText() }

            val gson = Gson()
            val itemType = object : TypeToken<List<InitialItemDto>>() {}.type
            val warrantyType = object : TypeToken<List<InitialWarrantyDto>>() {}.type

            val itemsList: List<InitialItemDto> = gson.fromJson(itemsJson, itemType)
            val warrantiesList: List<InitialWarrantyDto> = gson.fromJson(warrantiesJson, warrantyType)

            val formattedDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())

            itemsList.forEach { dto ->
                val catId = existingCategories.find { it.name.equals(dto.category, true) }?.id ?: 0L
                var locId = existingLocations.find { it.name.equals(dto.location, true) }?.id

                if (locId == null && !dto.location.isNullOrBlank()) {
                    val newLoc = LocationEntity(
                        name = dto.location.trim(),
                        floor = "Nieokreślone"
                    )
                    locId = locationRepository.saveLocation(newLoc)
                }

                val newItem = ItemEntity(
                    modelId = dto.modelId,
                    serialNumber = dto.serialNumber.ifBlank { "SN-${dto.modelId}" },
                    name = dto.name,
                    description = dto.description,
                    price = dto.price,
                    categoryId = catId,
                    locationId = locId?: 0L,
                    dateAdded = formattedDate,
                    imageUri = null
                )

                val generatedId = itemRepository.saveItem(newItem)


                warrantiesList.find { it.modelId == dto.modelId }?.let { wDto ->
                    warrantyRepository.saveWarranty(WarrantyEntity(
                        itemId = generatedId,
                        expiryDate = wDto.expiryDate,
                        provider = wDto.provider ?: "Producent",
                        notes = wDto.notes ?: "Import automatyczny"
                    ))
                }
            }
        } catch (e: Exception) {
            Log.e("IMPORT_ERROR", "Błąd importu: ${e.message}")
        }
    }

    private suspend fun setupInitialData() {
        if (categoryRepository.getCategoriesCount() == 0) {
            val categoriesMap = mapOf(
                "Narzędzia" to "Sprzęt warsztatowy i narzędzia ręczne",
                "Sport i Hobby" to "Sprzęt sportowy, rowery i akcesoria rekreacyjne",
                "Elektronika" to "Komputery, telefony i inne gadżety elektroniczne",
                "AGD" to "Urządzenia gospodarstwa domowego i sprzęt kuchenny",
                "Dom" to "Elementy wyposażenia wnętrz i akcesoria domowe",
                "Rozrywka" to "Konsole, gry i sprzęt audio-wideo",
                "Edukacja" to "Książki, materiały naukowe i podręczniki"
            )

            categoriesMap.forEach { (name, desc) ->
                categoryRepository.saveCategory(CategoryEntity(name = name, description = desc))
            }
        }

        if (locationRepository.getLocationsCount() == 0) {
            val locations = listOf(
                LocationEntity(name = "Salon", floor = "Parter"),
                LocationEntity(name = "Kuchnia", floor = "Parter"),
                LocationEntity(name = "Garaż", floor = "Poziom 0"),
                LocationEntity(name = "Piwnica", floor = "Poziom -1")
            )
            locations.forEach { locationRepository.saveLocation(it) }
        }
    }
}