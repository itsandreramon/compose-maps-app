package de.thb.core.data.sources.categories.local

import de.thb.core.domain.category.CategoryEntity
import de.thb.core.domain.category.CategoryResponse
import kotlinx.coroutines.flow.Flow

interface CategoriesLocalDataSource {

    suspend fun insert(categories: List<CategoryEntity>)

    suspend fun insert(category: CategoryEntity)

    suspend fun insertOrUpdate(categoryResponse: CategoryResponse)

    suspend fun getByIdOnce(id: Long): CategoryEntity?

    suspend fun getAllOnce(): List<CategoryEntity>

    fun getAll(): Flow<List<CategoryEntity>>
}
