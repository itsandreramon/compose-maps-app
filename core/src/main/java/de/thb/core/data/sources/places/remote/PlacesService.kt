package de.thb.core.data.sources.places.remote

import de.thb.core.domain.place.PlaceResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface PlacesService {

    @GET("/places")
    suspend fun getAll(): List<PlaceResponse>

    @GET("/places/{id}")
    suspend fun getById(
        @Path("id") id: String
    ): PlaceResponse
}
