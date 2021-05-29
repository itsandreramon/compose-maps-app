package de.thb.core.data.places.remote

import de.thb.core.domain.PlaceResponse
import retrofit2.http.GET

interface PlacesService {

    @GET("all")
    suspend fun getAll(): List<PlaceResponse>
}
