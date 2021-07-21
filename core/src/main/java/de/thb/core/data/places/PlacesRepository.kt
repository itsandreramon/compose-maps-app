package de.thb.core.data.places

import de.thb.core.domain.place.PlaceEntity
import de.thb.core.domain.place.PlaceResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow

interface PlacesRepository {

    fun getAll(): Flow<List<PlaceEntity>>

    suspend fun insert(placesResponse: List<PlaceResponse>): Job

    suspend fun insert(place: PlaceEntity)
}
