package de.thb.core.data.sources.district

import de.thb.core.domain.district.DetailsResponse
import de.thb.core.domain.district.ReverseResponse
import de.thb.core.util.LatLng

interface DistrictRemoteDataSource {

    suspend fun getByLatLng(
        latLng: LatLng
    ): ReverseResponse

    suspend fun getByOsm(
        osmType: String,
        osmId: String,
    ): DetailsResponse
}
