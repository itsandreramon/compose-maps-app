package de.thb.core.data.sources.district

import de.thb.core.util.LatLng

interface DistrictRemoteDataSource {

    suspend fun getByLatLng(
        latLng: LatLng
    ): String
}
