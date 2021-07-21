package de.thb.core.data.categories.local

import de.thb.core.domain.category.CategoryEntity
import kotlinx.coroutines.flow.Flow

interface CategoriesLocalDataSource {

    suspend fun insert(categories: List<CategoryEntity>)

    suspend fun insert(category: CategoryEntity)

    fun getAll(): Flow<List<CategoryEntity>>
}
