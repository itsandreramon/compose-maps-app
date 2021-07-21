package de.thb.core.data.categories

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import de.thb.core.data.categories.local.CategoriesLocalDataSource
import de.thb.core.data.categories.remote.CategoriesRemoteDataSource
import de.thb.core.domain.category.CategoryEntity
import de.thb.core.util.CategoryUtils.toEntities
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

class CategoriesRepositoryImpl(
    private val categoriesLocalDataSource: CategoriesLocalDataSource,
    private val categoriesRemoteDataSource: CategoriesRemoteDataSource,
) : CategoriesRepsitory {

    private val store = StoreBuilder.from(
        fetcher = Fetcher.of { categoriesRemoteDataSource.getAll().toEntities() },
        sourceOfTruth = SourceOfTruth.of(
            reader = { categoriesLocalDataSource.getAll() },
            writer = { _, input -> categoriesLocalDataSource.insert(input) },
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
}
