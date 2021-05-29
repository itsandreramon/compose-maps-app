package de.thb.core.data.places

import de.thb.core.domain.PlaceEntity
import kotlinx.coroutines.flow.Flow

interface PlacesRepository {
    fun getAll(): Flow<List<PlaceEntity>>
}
