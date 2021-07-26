package de.thb.core.data.sources.boundaries.remote

import de.thb.core.domain.boundary.BoundaryResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface BoundariesService {

    @GET("/search.php")
    suspend fun getByName(
        @Query("q") name: String,
        @Query("polygon_geojson") geoJson: Int,
        @Query("format") format: String,
    ): List<BoundaryResponse>
}
