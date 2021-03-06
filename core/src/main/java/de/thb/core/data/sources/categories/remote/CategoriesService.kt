package de.thb.core.data.sources.categories.remote

import de.thb.core.domain.category.CategoryResponse
import retrofit2.http.GET

interface CategoriesService {

    @GET("/categories")
    suspend fun getAll(): List<CategoryResponse>
}
