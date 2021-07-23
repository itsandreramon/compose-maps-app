package de.thb.core.data.sources.places.local

import de.thb.core.domain.place.PlaceEntity
import de.thb.core.domain.place.PlaceResponse
import de.thb.core.util.CoroutinesDispatcherProvider
import de.thb.core.util.PlaceUtils.toEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlacesLocalDataSourceImpl(
    private val applicationScope: CoroutineScope,
    private val placesRoomDao: PlacesRoomDao,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
) : PlacesLocalDataSource {

    override suspend fun insert(places: List<PlaceEntity>) {
        withContext(dispatcherProvider.database()) {
            applicationScope.launch {
                placesRoomDao.insert(places)
            }.join()
        }
    }

    override suspend fun insert(place: PlaceEntity) {
        withContext(dispatcherProvider.database()) {
            applicationScope.launch {
                placesRoomDao.insert(place)
            }.join()
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
            applicationScope.async {
                placesRoomDao.getByIdOnce(id)
            }.await()
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
