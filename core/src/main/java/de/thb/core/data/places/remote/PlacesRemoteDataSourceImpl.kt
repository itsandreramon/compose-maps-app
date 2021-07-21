package de.thb.core.data.places.remote

import de.thb.core.domain.place.PlaceResponse
import de.thb.core.util.CoroutinesDispatcherProvider
import kotlinx.coroutines.withContext

class PlacesRemoteDataSourceImpl(
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val placesService: PlacesService
) : PlacesRemoteDataSource {

    companion object {
        const val TAG = "PlacesRemoteDataSource"
    }

    override suspend fun getAll(): List<PlaceResponse> {
        return withContext(dispatcherProvider.io()) {
            val result = placesService.getAll()
            result
        }
    }
}
