package de.thb.core.data.sources.places

import de.thb.core.domain.place.PlaceEntity
import kotlinx.coroutines.flow.Flow

interface PlacesRepository {

    fun getById(id: String): Flow<PlaceEntity?>

    fun getAll(): Flow<List<PlaceEntity>>

    suspend fun insert(place: PlaceEntity)
}
