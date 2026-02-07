package pl.edu.ur.bw131534.homeinventory.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import pl.edu.ur.bw131534.homeinventory.data.local.entity.CategoryEntity
import pl.edu.ur.bw131534.homeinventory.domain.repository.CategoryRepository
import pl.edu.ur.bw131534.homeinventory.domain.repository.ItemRepository
import javax.inject.Inject


@HiltViewModel
class StatsViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val itemRepository: ItemRepository
) : ViewModel() {

    val categoryStats: StateFlow<Map<CategoryEntity, Int>> = combine(
        categoryRepository.getAllCategories(),
        itemRepository.getAllItems()
    ) { categories, items ->
        val stats = categories.associateWith { category ->
            items.count { it.categoryId == category.id }
        }.toMutableMap()

        val uncategorizedCount = items.count { it.categoryId == null }
        if (uncategorizedCount > 0) {
            stats[CategoryEntity(id = -1, name = "Nieprzypisane", description = "")] = uncategorizedCount
        }
        stats
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())
}