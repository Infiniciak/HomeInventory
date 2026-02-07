package pl.edu.ur.bw131534.homeinventory.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.edu.ur.bw131534.homeinventory.data.dao.InitialItemDto
import pl.edu.ur.bw131534.homeinventory.data.local.entity.ItemEntity
import pl.edu.ur.bw131534.homeinventory.domain.repository.ItemRepository
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val itemRepository: ItemRepository
) : ViewModel() {

    private val _saveState = MutableStateFlow<String?>(null)
    val saveState = _saveState.asStateFlow()
    fun resetSaveState() {
        _saveState.value = null
    }
    fun saveScannedItem(jsonCode: String) {
        viewModelScope.launch {
            try {
                val dto = Gson().fromJson(jsonCode, InitialItemDto::class.java)
                val newItem = ItemEntity(
                    modelId = dto.modelId,
                    serialNumber = dto.serialNumber.ifBlank { "SN-${UUID.randomUUID().toString().take(8)}" },
                    name = dto.name,
                    description = dto.description,
                    price = dto.price,
                    categoryId = null,
                    locationId = null,
                    dateAdded = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date()),
                    imageUri = null
                )
                itemRepository.saveItem(newItem)
                _saveState.value = "Zapisano: ${dto.name}"
            } catch (e: Exception) {
                _saveState.value = "Błąd: Niepoprawny format kodu QR"
            }

        }
    }
}