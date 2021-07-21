package de.thb.core.data.places.local

import de.thb.core.domain.place.PlaceEntity
import de.thb.core.domain.place.PlaceResponse
import kotlinx.coroutines.flow.Flow

interface PlacesLocalDataSource {

    suspend fun insert(places: List<PlaceEntity>)

    suspend fun insert(place: PlaceEntity)

    suspend fun insertOrUpdate(placeResponse: PlaceResponse)

    suspend fun getByIdOnce(id: String): PlaceEntity?

    suspend fun getAllOnce(): List<PlaceEntity>

    fun getAll(): Flow<List<PlaceEntity>>

    fun getById(id: String): Flow<PlaceEntity>
}
