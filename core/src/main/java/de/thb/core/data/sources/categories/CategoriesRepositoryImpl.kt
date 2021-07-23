package de.thb.core.data.sources.categories

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import de.thb.core.data.sources.categories.local.CategoriesLocalDataSource
import de.thb.core.data.sources.categories.remote.CategoriesRemoteDataSource
import de.thb.core.domain.category.CategoryEntity
import de.thb.core.domain.category.CategoryResponse
import de.thb.core.util.CategoryUtils.toEntity
import de.thb.core.util.responseToEntityIfExistsElseResponse
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

class CategoriesRepositoryImpl(
    private val categoriesLocalDataSource: CategoriesLocalDataSource,
    private val categoriesRemoteDataSource: CategoriesRemoteDataSource,
) : CategoriesRepsitory {

    companion object {
        const val TAG = "CategoriesRepsitory"
    }

    private val store = StoreBuilder.from(
        fetcher = Fetcher.of { categoriesRemoteDataSource.getAll() },
        sourceOfTruth = SourceOfTruth.of(
            reader = { categoriesLocalDataSource.getAll() },
            writer = { _, input -> insert(input) },
        )
    ).build()

    override fun getAll() = flow<List<CategoryEntity>> {
        store.stream(StoreRequest.cached(key = "all", refresh = true)).collect { response ->
            when (response) {
                is StoreResponse.Loading -> emit(emptyList())
                is StoreResponse.Error -> emit(emptyList())
                is StoreResponse.Data -> emit(response.value)
                is StoreResponse.NoNewData -> emit(emptyList())
            }
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
