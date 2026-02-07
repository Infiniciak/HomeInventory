package pl.edu.ur.bw131534.homeinventory.domain.repository

import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import pl.edu.ur.bw131534.homeinventory.data.local.dao.CategoryDao
import pl.edu.ur.bw131534.homeinventory.data.local.entity.CategoryEntity


class CategoryRepositoryImpl @Inject constructor(
    private val dao: CategoryDao
) : CategoryRepository {

    override suspend fun saveCategory(category: CategoryEntity): Long {
        return dao.insertCategory(category)
    }

    override fun getAllCategories(): Flow<List<CategoryEntity>> {
        return dao.getAllCategories()
        }

    override suspend fun getCategoriesCount(): Int {
        return dao.getCategoriesCount()
    }

    override suspend fun getCategoryByName(name: String): CategoryEntity? {
        return dao.getCategoryByName(name)
    }
}
