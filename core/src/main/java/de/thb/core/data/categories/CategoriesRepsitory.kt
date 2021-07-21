package de.thb.core.data.categories

import de.thb.core.domain.category.CategoryEntity
import kotlinx.coroutines.flow.Flow

interface CategoriesRepsitory {
    fun getAll(): Flow<List<CategoryEntity>>
}
