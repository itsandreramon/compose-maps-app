package de.thb.core.data.sources.district

import de.thb.core.domain.boundary.BoundaryResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface DistrictService {

    @GET("/search.php")
    suspend fun getByName(
        @Query("q") name: String,
    ): List<BoundaryResponse>
}
