package de.thb.core.data.sources.places.remote

import de.thb.core.domain.place.PlaceResponse

interface PlacesRemoteDataSource {
    suspend fun getAll(): List<PlaceResponse>

    suspend fun getById(id: String): PlaceResponse
}
