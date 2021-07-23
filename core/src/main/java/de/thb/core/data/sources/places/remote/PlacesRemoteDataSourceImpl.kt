package de.thb.core.data.sources.places.remote

import de.thb.core.domain.place.PlaceResponse
import de.thb.core.util.CoroutinesDispatcherProvider
import kotlinx.coroutines.withContext

class PlacesRemoteDataSourceImpl(
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val placesService: PlacesService,
) : PlacesRemoteDataSource {

    override suspend fun getById(id: String): PlaceResponse {
        return withContext(dispatcherProvider.io()) {
            placesService.getById(id)
        }
    }

    override suspend fun getAll(): List<PlaceResponse> {
        return withContext(dispatcherProvider.io()) {
            placesService.getAll()
        }
    }
}
