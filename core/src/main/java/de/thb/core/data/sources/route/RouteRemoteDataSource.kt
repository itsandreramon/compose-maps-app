package de.thb.core.data.sources.route

import de.thb.core.domain.route.RouteResponse
import de.thb.core.util.MapLatLng

interface RouteRemoteDataSource {
    suspend fun getRoute(
        originLatLng: MapLatLng,
        destinationPlaceId: String,
    ): RouteResponse
}