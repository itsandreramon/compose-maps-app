package de.thb.core.data.sources.categories.remote

import de.thb.core.domain.category.CategoryResponse

interface CategoriesRemoteDataSource {
    suspend fun getAll(): List<CategoryResponse>
}
