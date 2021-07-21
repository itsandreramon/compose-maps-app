package de.thb.core.data.places

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import de.thb.core.data.places.local.PlacesLocalDataSource
import de.thb.core.data.places.remote.PlacesRemoteDataSource
import de.thb.core.domain.place.PlaceEntity
import de.thb.core.domain.place.PlaceResponse
import de.thb.core.util.PlaceUtils.toEntities
import de.thb.core.util.PlaceUtils.toEntity
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class PlacesRepositoryImpl(
    private val placesLocalDataSource: PlacesLocalDataSource,
    private val placesRemoteDataSource: PlacesRemoteDataSource,
) : PlacesRepository {

    private val store = StoreBuilder.from(
        fetcher = Fetcher.of { placesRemoteDataSource.getAll() },
        sourceOfTruth = SourceOfTruth.of(
            reader = { placesLocalDataSource.getAll() },
            writer = { _, input -> insert(input) },
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

    override suspend fun insert(place: PlaceEntity) {
        placesLocalDataSource.insert(place)
    }

    override suspend fun insert(placesResponse: List<PlaceResponse>) = coroutineScope {
        val localPlaces = placesLocalDataSource.getAllOnce()

        val (toUpdate, toInsert) = placesResponse.partition { response ->
            localPlaces.any { entity ->
                response.id == entity.id
            }
        }

        launch {
            toUpdate.mapNotNull { response ->
                val localPlaceMaybe = localPlaces.firstOrNull { entity -> response.id == entity.id }

                localPlaceMaybe?.let { entity ->
                    response.toEntity(entity)
                }
            }.let { updatedPlaces ->
                placesLocalDataSource.insert(updatedPlaces)
            }
        }

        launch {
            placesLocalDataSource.insert(toInsert.toEntities())
        }
    }
}
