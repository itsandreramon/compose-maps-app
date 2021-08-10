package de.thb.core.data.sources.route

import de.thb.core.domain.route.RouteRequest
import de.thb.core.util.CoroutinesDispatcherProvider
import de.thb.core.util.MapLatLng
import kotlinx.coroutines.withContext
import okio.IOException

class RouteRemoteDataSourceImpl(
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val routeService: RouteService,
) : RouteRemoteDataSource {

    override suspend fun getRoute(
        originLatLng: MapLatLng,
        destinationPlaceId: String
    ) = withContext(dispatcherProvider.io()) {
        try {
            routeService.getByPlaceIds(
                RouteRequest(
                    origin = listOf(originLatLng.latitude, originLatLng.longitude),
                    destination = destinationPlaceId,
                )
            )
        } catch (e: IOException) {
            null
        }
    }
}