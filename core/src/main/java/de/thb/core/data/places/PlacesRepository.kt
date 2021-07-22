package de.thb.core.data.places

import de.thb.core.domain.place.PlaceEntity
import kotlinx.coroutines.flow.Flow

interface PlacesRepository {

    fun getAll(): Flow<List<PlaceEntity>>

    suspend fun insert(place: PlaceEntity)
}
