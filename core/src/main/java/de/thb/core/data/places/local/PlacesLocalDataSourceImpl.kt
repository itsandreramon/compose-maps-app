package de.thb.core.data.places.local

import de.thb.core.domain.PlaceEntity
import de.thb.core.util.CoroutinesDispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class PlacesLocalDataSourceImpl(
    private val placesRoomDao: PlacesRoomDao,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
) : PlacesLocalDataSource {

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

    override fun getAll(): Flow<List<PlaceEntity>> {
        return placesRoomDao.getAll()
            .flowOn(dispatcherProvider.database())
    }

    override fun getByUuid(uuid: String): Flow<PlaceEntity> {
        return placesRoomDao.getByUuid(uuid)
            .flowOn(dispatcherProvider.database())
    }
}
