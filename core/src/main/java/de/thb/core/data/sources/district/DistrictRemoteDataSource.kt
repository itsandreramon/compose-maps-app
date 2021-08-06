package de.thb.core.data.sources.district

import de.thb.core.domain.district.DistrictResponse

interface DistrictRemoteDataSource {

    suspend fun getByLatLng(
        name: String
    ): List<DistrictResponse>
}
