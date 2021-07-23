package de.thb.core.data.sources.categories.remote

import de.thb.core.domain.category.CategoryResponse
import de.thb.core.util.CoroutinesDispatcherProvider
import kotlinx.coroutines.withContext

class CategoriesRemoteDataSourceImpl(
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val categoriesService: CategoriesService,
) : CategoriesRemoteDataSource {

    override suspend fun getAll(): List<CategoryResponse> {
        return withContext(dispatcherProvider.io()) {
            categoriesService.getAll()
        }
    }
}
