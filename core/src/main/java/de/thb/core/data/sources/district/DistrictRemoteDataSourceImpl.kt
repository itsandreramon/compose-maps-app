package de.thb.core.data.sources.district

import de.thb.core.util.CoroutinesDispatcherProvider
import de.thb.core.util.LatLng
import kotlinx.coroutines.withContext

class DistrictRemoteDataSourceImpl(
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val districtService: DistrictService,
) : DistrictRemoteDataSource {

    override suspend fun getByLatLng(latLng: LatLng): String {
        return withContext(dispatcherProvider.io()) {
            val reverseResponse = districtService.getByLatLng(
                lat = latLng.lat.toString(),
                lng = latLng.lng.toString(),
                format = "json"
            )

            val osmType = reverseResponse.osm_type[0].uppercase()
            val osmId = reverseResponse.osm_id
            val state = reverseResponse.address.state

            val detailsResponse = districtService.getByOsm(
                osmType = osmType,
                osmId = osmId,
                format = "json",
            )

            var location = state

            for (item in detailsResponse.address ?: emptyList()) {
                if (item.admin_level == 6) {
                    location = item.localname
                    break
                }
            }

            location
        }
    }
}
