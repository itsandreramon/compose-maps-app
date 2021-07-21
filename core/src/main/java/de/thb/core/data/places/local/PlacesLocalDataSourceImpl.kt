package de.thb.core.data.places.local

import de.thb.core.domain.place.PlaceEntity
import de.thb.core.domain.place.PlaceResponse
import de.thb.core.util.CoroutinesDispatcherProvider
import de.thb.core.util.PlaceUtils.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class PlacesLocalDataSourceImpl(
    private val placesRoomDao: PlacesRoomDao,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
) : PlacesLocalDataSource {

    companion object {
        const val TAG = "PlacesLocalDataSource"
    }

    override suspend fun insert(places: List<PlaceEntity>) {
        withContext(dispatcherProvider.database()) {
            placesRoomDao.insert(places)
        }
    }

    override suspend fun insert(place: PlaceEntity) {
        withContext(dispatcherProvider.database()) {
            placesRoomDao.insert(place)
        }
    }

    override suspend fun insertOrUpdate(placeResponse: PlaceResponse) {
        val existingPlaceMaybe = getByIdOnce(placeResponse.id)

        if (existingPlaceMaybe != null) {
            if (existingPlaceMaybe != placeResponse.toEntity()) {
                insert(placeResponse.toEntity(existingPlaceMaybe))
            }
        } else {
            insert(placeResponse.toEntity())
        }
    }

    override suspend fun getByIdOnce(id: String): PlaceEntity? {
        return withContext(dispatcherProvider.database()) {
            placesRoomDao.getByIdOnce(id)
        }
    }

    override fun getAll(): Flow<List<PlaceEntity>> {
        return placesRoomDao.getAll()
            .flowOn(dispatcherProvider.database())
    }

    override suspend fun getAllOnce(): List<PlaceEntity> {
        return withContext(dispatcherProvider.database()) {
            placesRoomDao.getAllOnce()
        }
    }

    override fun getById(id: String): Flow<PlaceEntity> {
        return placesRoomDao.getById(id)
            .flowOn(dispatcherProvider.database())
    }
}
