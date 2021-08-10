package de.thb.core.data.sources.categories

import de.thb.core.data.sources.categories.local.CategoriesLocalDataSource
import de.thb.core.data.sources.categories.remote.CategoriesRemoteDataSource
import de.thb.core.domain.category.CategoryEntity
import de.thb.core.domain.category.CategoryResponse
import de.thb.core.util.CategoryUtils.toEntity
import de.thb.core.util.responseToEntityIfExistsElseResponse
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import okio.IOException

class CategoriesRepositoryImpl(
    private val categoriesLocalDataSource: CategoriesLocalDataSource,
    private val categoriesRemoteDataSource: CategoriesRemoteDataSource,
) : CategoriesRepsitory {

    companion object {
        const val TAG = "CategoriesRepsitory"
    }

    override fun getAll() = flow {
        val categories = try {
            categoriesRemoteDataSource.getAll()
        } catch (e: IOException) {
            emptyList()
        }

        insert(categories)

        categoriesLocalDataSource.getAll().collect {
            emit(it)
        }
    }

    override suspend fun insert(category: CategoryEntity) {
        categoriesLocalDataSource.insert(category)
    }

    private suspend fun insert(categoriesResponse: List<CategoryResponse>) {
        responseToEntityIfExistsElseResponse(
            responseData = categoriesResponse,
            localData = categoriesLocalDataSource.getAllOnce(),
            predicate = { response, entity -> response.id == entity.id },
            updater = { response, entity -> response.toEntity(entity) },
            mapper = { response -> response.toEntity() },
            onUpdateRequested = { categories ->
                categoriesLocalDataSource.insert(categories)
            },
            onInsertRequested = { categories ->
                categoriesLocalDataSource.insert(categories)
            }
        )
    }
}
