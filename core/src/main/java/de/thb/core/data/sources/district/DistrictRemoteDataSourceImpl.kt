package de.thb.core.data.sources.district

import de.thb.core.domain.district.DetailsResponse
import de.thb.core.domain.district.ReverseResponse
import de.thb.core.util.CoroutinesDispatcherProvider
import de.thb.core.util.LatLng
import kotlinx.coroutines.withContext

class DistrictRemoteDataSourceImpl(
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val districtService: DistrictService,
) : DistrictRemoteDataSource {

    override suspend fun getByLatLng(latLng: LatLng): ReverseResponse {
        return withContext(dispatcherProvider.io()) {
            districtService.getByLatLng(
                lat = latLng.lat.toString(),
                lng = latLng.lng.toString(),
                format = "json"
            )
        }
    }

    override suspend fun getByOsm(osmType: String, osmId: String): DetailsResponse {
        return withContext(dispatcherProvider.io()) {
            districtService.getByOsm(
                osmType = osmType,
                osmId = osmId,
                format = "json"
            )
        }
    }
}
