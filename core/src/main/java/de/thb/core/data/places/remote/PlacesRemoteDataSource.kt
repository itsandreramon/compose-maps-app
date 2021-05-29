package de.thb.core.data.places.remote

import de.thb.core.domain.PlaceResponse

interface PlacesRemoteDataSource {
    suspend fun getAll(): List<PlaceResponse>
}
