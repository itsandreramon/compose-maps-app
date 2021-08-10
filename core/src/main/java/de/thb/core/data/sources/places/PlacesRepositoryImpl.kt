package de.thb.core.data.sources.places

import android.util.Log
import de.thb.core.data.sources.places.local.PlacesLocalDataSource
import de.thb.core.data.sources.places.remote.PlacesRemoteDataSource
import de.thb.core.domain.place.PlaceEntity
import de.thb.core.domain.place.PlaceResponse
import de.thb.core.util.PlaceUtils.toEntity
import de.thb.core.util.responseToEntityIfExistsElseResponse
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import okio.IOException

class PlacesRepositoryImpl(
    private val placesLocalDataSource: PlacesLocalDataSource,
    private val placesRemoteDataSource: PlacesRemoteDataSource,
) : PlacesRepository {

    companion object {
        const val TAG = "PlacesRepository"
    }

    override fun getAll() = flow {
        val places = try {
            placesRemoteDataSource.getAll()
        } catch (e: IOException) {
            emptyList()
        }

        insert(places)

        placesLocalDataSource.getAll().collect {
            emit(it)
        }
    }

    override fun getById(id: String) = flow {
        val place = try {
            placesRemoteDataSource.getById(id)
        } catch (e: IOException) {
            null
        }

        if (place != null) {
            placesLocalDataSource.insertOrUpdate(place)
        }

        placesLocalDataSource.getById(id).collect {
            emit(it)
        }
    }

    override suspend fun insert(place: PlaceEntity) {
        Log.e("REPO", "$place")
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
