package de.thb.core.data.sources.boundaries.remote

import de.thb.core.domain.boundary.BoundaryResponse
import de.thb.core.util.MapLatLng

interface BoundariesRemoteDataSource {

    suspend fun getByName(
        name: String
    ): List<BoundaryResponse>

    suspend fun getBoundariesPolyline(
        name: String
    ): List<MapLatLng>
}
