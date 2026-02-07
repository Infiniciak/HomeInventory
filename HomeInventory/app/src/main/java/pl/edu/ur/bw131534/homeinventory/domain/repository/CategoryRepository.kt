package pl.edu.ur.bw131534.homeinventory.domain.repository

import kotlinx.coroutines.flow.Flow
import pl.edu.ur.bw131534.homeinventory.R
import pl.edu.ur.bw131534.homeinventory.data.local.entity.CategoryEntity

interface CategoryRepository {
    suspend fun saveCategory(category: CategoryEntity): Long

    val PREDEFINED_CATEGORIES: List<CategoryEntity>
        get() = listOf(
            CategoryEntity(name = "Elektronika", description = "Komputery, telefony, sprzęt RTV i akcesoria",iconResId = R.drawable.ic_electronics),
            CategoryEntity(name = "Narzędzia", description = "Sprzęt warsztatowy, budowlany i naprawczy",iconResId = R.drawable.ic_tools,),
            CategoryEntity(name = "Dom", description = "Meble, dekoracje, tekstylia i oświetlenie",iconResId = R.drawable.ic_house),
            CategoryEntity(name = "Ogród", description = "Narzędzia ogrodnicze, rośliny i meble tarasowe",iconResId = R.drawable.ic_garden),
            CategoryEntity(name = "Sport", description = "Sprzęt treningowy, rowery i odzież sportowa",iconResId = R.drawable.ic_sport)
        )

    fun getAllCategories(): Flow<List<CategoryEntity>>

    suspend fun getCategoriesCount(): Int

    suspend fun getCategoryByName(name: String): CategoryEntity?
}