package de.thb.core.data.sources.route

import de.thb.core.domain.route.RouteRequest
import de.thb.core.domain.route.RouteResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface RouteService {

    @POST("/routing")
    suspend fun getByPlaceIds(
        @Body request: RouteRequest,
    ): RouteResponse

    @POST("/routing")
    suspend fun getByOriginLatLngDestinationPlaceId(
        @Body request: RouteRequest,
    ): RouteResponse
}