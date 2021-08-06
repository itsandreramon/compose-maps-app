package de.thb.core.data.sources.district

import de.thb.core.domain.district.DistrictResponse
import de.thb.core.util.CoroutinesDispatcherProvider
import kotlinx.coroutines.withContext

class DistrictRemoteDataSourceImpl(
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val districtService: DistrictService,
) : DistrictRemoteDataSource {

    override suspend fun getByLatLng(name: String): List<DistrictResponse> {
        return withContext(dispatcherProvider.io()) {
            // TODO
            emptyList()
        }
    }
}
