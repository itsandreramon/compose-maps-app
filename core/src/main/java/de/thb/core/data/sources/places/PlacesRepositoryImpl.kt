package de.thb.core.data.sources.places

import android.util.Log
import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import de.thb.core.data.sources.places.local.PlacesLocalDataSource
import de.thb.core.data.sources.places.remote.PlacesRemoteDataSource
import de.thb.core.domain.place.PlaceEntity
import de.thb.core.domain.place.PlaceResponse
import de.thb.core.util.PlaceUtils.toEntity
import de.thb.core.util.responseToEntityIfExistsElseResponse
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

class PlacesRepositoryImpl(
    private val placesLocalDataSource: PlacesLocalDataSource,
    private val placesRemoteDataSource: PlacesRemoteDataSource,
) : PlacesRepository {

    companion object {
        const val TAG = "PlacesRepository"
    }

    private val getAllStore = StoreBuilder.from(
        fetcher = Fetcher.of { placesRemoteDataSource.getAll().also { Log.e(TAG, "fetched: $it") } },
        sourceOfTruth = SourceOfTruth.of(
            reader = { placesLocalDataSource.getAll() },
            writer = { _, input -> insert(input) },
        )
    ).build()

    private val getByIdStore = StoreBuilder.from(
        fetcher = Fetcher.of { id: String -> placesRemoteDataSource.getById(id) },
        sourceOfTruth = SourceOfTruth.Companion.of(
            reader = { id: String -> placesLocalDataSource.getById(id) },
            writer = { _, input -> placesLocalDataSource.insertOrUpdate(input) }
        )
    ).build()

    override fun getAll() = flow<List<PlaceEntity>> {
        getAllStore.stream(StoreRequest.cached(key = "all", refresh = true)).collect { response ->
            when (response) {
                is StoreResponse.Data -> emit(response.value)
                else -> emit(emptyList())
            }
        }
    }

    override fun getById(id: String) = flow {
        getByIdStore.stream(StoreRequest.cached(id, refresh = true)).collect { response ->
            when (response) {
                is StoreResponse.Data -> emit(response.value)
                else -> emit(null)
            }
        }
    }

    override suspend fun insert(place: PlaceEntity) {
        placesLocalDataSource.insert(place)
    }

    private suspend fun insert(placesResponse: List<PlaceResponse>) {
        responseToEntityIfExistsElseResponse(
            responseData = placesResponse,
            localData = placesLocalDataSource.getAllOnce(),
            predicate = { response, entity -> response.id == entity.id },
            updater = { response, entity -> response.toEntity(entity) },
            mapper = { response -> response.toEntity() },
            onUpdateRequested = { places ->
                Log.e(TAG, "updating places: $places")
                placesLocalDataSource.insert(places)
            },
            onInsertRequested = { places ->
                Log.e(TAG, "inserting places: $places")
                placesLocalDataSource.insert(places)
            }
        )
    }
}
