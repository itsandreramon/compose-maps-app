package de.thb.core.data.sources.categories

import de.thb.core.domain.category.CategoryEntity
import kotlinx.coroutines.flow.Flow

interface CategoriesRepsitory {
    fun getAll(): Flow<List<CategoryEntity>>

    suspend fun insert(category: CategoryEntity)
}
