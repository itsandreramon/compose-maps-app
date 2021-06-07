package de.thb.core.data.places

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import de.thb.core.data.places.local.PlacesLocalDataSource
import de.thb.core.data.places.remote.PlacesRemoteDataSource
import de.thb.core.domain.PlaceEntity
import de.thb.core.util.CoroutinesDispatcherProvider
import de.thb.core.util.toEntities
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

class PlacesRepositoryImpl(
    private val placesLocalDataSource: PlacesLocalDataSource,
    private val placesRemoteDataSource: PlacesRemoteDataSource,
) : PlacesRepository {

    private val store = StoreBuilder.from(
        fetcher = Fetcher.of { placesRemoteDataSource.getAll().toEntities() },
        sourceOfTruth = SourceOfTruth.of(
            reader = { placesLocalDataSource.getAll() },
            writer = { _, input -> placesLocalDataSource.insert(input) },
        )
    ).build()

    override fun getAll() = flow<List<PlaceEntity>> {
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
