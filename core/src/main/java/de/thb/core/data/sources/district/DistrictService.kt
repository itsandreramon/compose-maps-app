package de.thb.core.data.sources.district

import de.thb.core.domain.district.DetailsResponse
import de.thb.core.domain.district.ReverseResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface DistrictService {

    @GET("/reverse")
    suspend fun getByLatLng(
        @Query("lat") lat: String,
        @Query("lon") lng: String,
        @Query("format") format: String,
    ): ReverseResponse

    @GET("/reverse")
    suspend fun getByOsm(
        @Query("osmType") osmType: String,
        @Query("osmId") osmId: String,
        @Query("format") format: String,
    ): DetailsResponse
}
