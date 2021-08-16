package de.thb.core.data.sources.places

import de.thb.core.data.sources.places.local.PlacesLocalDataSource
import de.thb.core.data.sources.places.remote.PlacesRemoteDataSource
import de.thb.core.domain.place.PlaceEntity
import de.thb.core.domain.place.PlaceResponse
import de.thb.core.util.PlaceUtils.toEntity
import de.thb.core.util.responseToEntityIfExistsElseResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okio.IOException

class PlacesRepositoryImpl(
    private val placesLocalDataSource: PlacesLocalDataSource,
    private val placesRemoteDataSource: PlacesRemoteDataSource,
) : PlacesRepository {

    companion object {
        const val TAG = "PlacesRepository"
    }

    override fun getAll() = channelFlow {
        val places = async {
            try {
                placesRemoteDataSource.getAll()
            } catch (e: IOException) {
                emptyList()
            }
        }

        launch {
            insert(places.await())
        }

        launch {
            placesLocalDataSource.getAll().collect {
                send(it)
            }
        }
    }

    override fun getById(id: String) = channelFlow {
        val placeAsync = async {
            try {
                placesRemoteDataSource.getById(id)
            } catch (e: IOException) {
                null
            }
        }

        launch {
            placeAsync.await()?.let {
                placesLocalDataSource.insertOrUpdate(it)
            }
        }

        launch {
            placesLocalDataSource.getById(id).collect {
                send(it)
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
                placesLocalDataSource.insert(places)
            },
            onInsertRequested = { places ->
                placesLocalDataSource.insert(places)
            }
        )
    }
}
