package de.thb.core.data.example

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import de.thb.core.data.example.local.ExampleLocalDataSource
import de.thb.core.data.example.remote.ExampleRemoteDataSource
import de.thb.core.domain.ExampleEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ExampleRepositoryImpl(
    private val exampleLocalDataSource: ExampleLocalDataSource,
    private val exampleRemoteDataSource: ExampleRemoteDataSource,
) : ExampleRepository {

    private val store: Store<Any, List<ExampleEntity>> = StoreBuilder.from(
        fetcher = Fetcher.of {
            exampleRemoteDataSource.getAll()
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { exampleLocalDataSource.getAll() },
            writer = { _, input -> exampleLocalDataSource.insert(input) }
        )
    ).build()

    override fun getAll(): Flow<List<ExampleEntity>> {
        return flow<List<ExampleEntity>> {
            store.stream(StoreRequest.cached(key = "all", refresh = true))
                .flowOn(Dispatchers.IO)
                .collect { response ->
                    when (response) {
                        is StoreResponse.Loading -> emit(emptyList())
                        is StoreResponse.Error -> emit(emptyList())
                        is StoreResponse.Data -> emit(response.value)
                        is StoreResponse.NoNewData -> emit(emptyList())
                    }
                }
        }.flowOn(Dispatchers.IO)
    }
}
