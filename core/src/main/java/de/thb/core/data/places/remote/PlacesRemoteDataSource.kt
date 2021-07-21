package de.thb.core.data.places.remote

import de.thb.core.domain.place.PlaceResponse

interface PlacesRemoteDataSource {
    suspend fun getAll(): List<PlaceResponse>
}
