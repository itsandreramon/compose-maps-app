package de.thb.core.data.sources.boundaries.remote

import de.thb.core.domain.boundary.BoundaryResponse
import de.thb.core.domain.boundary.Geojson
import de.thb.core.util.CoroutinesDispatcherProvider
import de.thb.core.util.MapLatLng
import kotlinx.coroutines.withContext

class BoundariesRemoteDataSourceImpl(
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val boundariesService: BoundariesService,
) : BoundariesRemoteDataSource {

    override suspend fun getByName(name: String): List<BoundaryResponse> {
        return withContext(dispatcherProvider.io()) {
            boundariesService.getByName(
                name = name,
                geoJson = 1,
                format = "json",
            )
        }
    }

    override suspend fun getBoundaryByName(name: String): List<MapLatLng> {
        val result = getByName(name)

        return result.firstOrNull { it.geojson is Geojson.MultiPolygon }?.let { response ->
            getLatLngsForBoundaryResponse(response)
        } ?: emptyList()
    }

    private fun getLatLngsForBoundaryResponse(response: BoundaryResponse): List<MapLatLng> {
        return (response.geojson as? Geojson.MultiPolygon)?.coordinates
            ?.flatten()
            ?.flatten()
            ?.mapNotNull { coordinates ->
                try {
                    coordinates.let { latLng ->
                        val lat = latLng[1]
                        val lng = latLng[0]
                        MapLatLng(lat, lng)
                    }
                } catch (e: IndexOutOfBoundsException) {
                    null
                }
            } ?: emptyList()
    }
}
